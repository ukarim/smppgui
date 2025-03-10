package com.ukarim.smppgui.protocol;

import static java.lang.System.Logger.Level.ERROR;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import com.ukarim.smppgui.protocol.pdu.ReqPdu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class SmppClient {

    private static final System.Logger logger = System.getLogger(SmppClient.class.getName());

    private final AtomicInteger seqGen = new AtomicInteger();
    private final AtomicBoolean active = new AtomicBoolean(true);

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private final Map<Integer, CompletableFuture<Pdu>> syncRespFutures = new ConcurrentHashMap<>();

    private final SmppHandler smppHandler;

    private SocketChannel channel;

    public SmppClient(SmppHandler smppHandler) {
        this.smppHandler = smppHandler;
    }

    public void sendReq(ReqPdu req) throws IOException {
        req.setSeqNum(seqGen.incrementAndGet());
        sendPdu(req);
    }

    private void sendPdu(Pdu pdu) throws IOException {
        if (channel == null) {
            throw new IllegalStateException("Smpp client not started");
        }
        var buffer = pdu.toByteBuffer();
        buffer.flip(); // prepare for writing to socket
        channel.write(buffer);
    }

    public Pdu sendReqSync(ReqPdu req, long timeoutMillis) throws IOException, TimeoutException {
        final int seqNum = seqGen.incrementAndGet();
        req.setSeqNum(seqNum);
        var respFuture = new CompletableFuture<Pdu>();
        syncRespFutures.put(seqNum, respFuture);
        try {
            sendPdu(req);
            return respFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unexpected error while waiting response pdu", e);
        } finally {
            // ensure cleanup
            syncRespFutures.remove(seqNum);
        }
    }

    public void connect(String host, int port) throws IOException {
        channel = SocketChannel.open(new InetSocketAddress(host, port));
        active.set(true);
        threadPool.execute(new SmppWorker());
    }

    public void disconnect() {
        try {
            channel.close();
        } catch (Exception e) {
            // don't care
        } finally {
            seqGen.set(0);
            active.set(false);
            channel = null;
        }
    }

    private class SmppWorker implements Runnable {

        @Override
        public void run() {
            var buffer = ByteBuffer.allocate(SmppConstants.MAX_PDU_LEN);
            buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec

            while (active.get()) {
                try {
                    int readC = channel.read(buffer);
                    if (readC == -1) {
                        // channel closed
                        smppHandler.handlePdu(null, new IOException("Connection was unexpectedly closed"));
                        disconnect();
                        return;
                    }
                    if (readC == 0) {
                        continue;
                    }

                    List<Pdu> pdus;
                    try {
                         pdus = PduParser.parsePdu(buffer);
                    } catch (Exception e) {
                        logger.log(ERROR, "Error during PDU parsing", e);
                        smppHandler.handlePdu(null, new SmppException(e, "Pdu parsing error"));
                        disconnect();
                        return;
                    }

                    for (Pdu pdu : pdus) {
                        var syncRespFuture = syncRespFutures.remove(pdu.getSeqNum());
                        if (syncRespFuture != null) {
                            syncRespFuture.complete(pdu);
                        } else {
                            Pdu respPdu = smppHandler.handlePdu(pdu, null);
                            if (respPdu != null) {
                                sendPdu(respPdu);
                            }
                        }
                    }
                } catch (Exception e) {
                    if (e instanceof AsynchronousCloseException) {
                        // NOOP. Channel was closed by `disconnect` method invocation
                        continue;
                    }
                    smppHandler.handlePdu(null, e);
                } finally {
                    buffer.compact(); // move unread bytes to the begging of array
                }
            }
        }
    }
}
