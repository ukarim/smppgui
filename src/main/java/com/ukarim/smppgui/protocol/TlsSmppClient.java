package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import tlschannel.ClientTlsChannel;
import tlschannel.TlsChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TlsSmppClient {

    private final AtomicBoolean active = new AtomicBoolean(true);

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private final Map<Integer, CompletableFuture<Pdu>> syncRespFutures = new ConcurrentHashMap<>();

    private final SmppHandler smppHandler;

    private TlsChannel tlsChannel;

    public TlsSmppClient(SmppHandler smppHandler) {
        this.smppHandler = smppHandler;
    }

    public void sendPdu(Pdu pdu) throws IOException {
        if (tlsChannel == null) {
            throw new IllegalStateException("Smpp client not started");
        }
        var buffer = pdu.toByteBuffer();
        buffer.flip(); // prepare for writing to socket
        tlsChannel.write(buffer);
    }

    public Pdu sendPduSync(Pdu pdu, long timeoutMillis) throws IOException, TimeoutException {
        int seqNum = pdu.getSeqNum();
        var respFuture = new CompletableFuture<Pdu>();
        syncRespFutures.put(seqNum, respFuture);
        try {
            sendPdu(pdu);
            return respFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unexpected error while waiting response pdu", e);
        } finally {
            // ensure cleanup
            syncRespFutures.remove(seqNum);
        }
    }

    public void connect(String host, int port) throws IOException, NoSuchAlgorithmException, KeyManagementException {

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.configureBlocking(true);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, null, null);
        ClientTlsChannel.Builder builder = ClientTlsChannel.newBuilder(socketChannel, sslContext);

        tlsChannel = builder.build();
        active.set(true);
        threadPool.execute(new SmppWorker());
    }

    public void disconnect() {
        try {
            tlsChannel.close();
        } catch (Exception e) {
            // don't care
        } finally {
            active.set(false);
            tlsChannel = null;
        }
    }

    private class SmppWorker implements Runnable {

        @Override
        public void run() {
            var buffer = ByteBuffer.allocate(SmppConstants.MAX_PDU_LEN);
            buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec

            while (active.get()) {
                try {
                    int readC = tlsChannel.read(buffer);

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
                        smppHandler.handlePdu(null, new SmppException("Pdu parsing error", e));
                        continue;
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

    public static boolean isSSL(String host, int port) throws IOException {
        SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(host, port);
        InputStream in = sslsocket.getInputStream();
        OutputStream out = sslsocket.getOutputStream();
        out.write(1);
        while (in.available() > 0) {
            System.out.print(in.read());
        }
        return true;
    }
}