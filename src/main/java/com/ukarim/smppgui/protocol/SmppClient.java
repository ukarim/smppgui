package com.ukarim.smppgui.protocol;

import com.ukarim.smppgui.protocol.pdu.Pdu;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SmppClient {

    private static final int DEF_BUS_SIZE = 1024;

    private final AtomicBoolean active = new AtomicBoolean(true);

    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    private final SmppHandler smppHandler;

    private SocketChannel channel;

    public SmppClient(SmppHandler smppHandler) {
        this.smppHandler = smppHandler;
    }

    public void sendPdu(Pdu pdu) throws IOException {
        if (channel == null) {
            throw new IllegalStateException("Smpp client not started");
        }
        var buffer = pdu.toByteBuffer();
        buffer.flip(); // prepare for writing to socket
        channel.write(buffer);
    }

    public void connect(String host, int port) throws IOException {
        channel = SocketChannel.open(new InetSocketAddress(host, port));
        active.set(true);
        threadPool.execute(new SmppWorker());
    }

    public void disconnect() throws IOException {
        // TODO send UNBIND pdu
        shutdown();
    }

    private void shutdown() throws IOException {
        active.set(false);
        channel.close();
        channel = null;
    }

    private class SmppWorker implements Runnable {

        @Override
        public void run() {
            var buffer = ByteBuffer.allocate(DEF_BUS_SIZE);
            buffer.order(ByteOrder.BIG_ENDIAN); // according to SMPP spec

            while (active.get()) {
                try {
                    // TODO what if one pdu is larger than buffer's capacity?
                    int readC = channel.read(buffer);
                    if (readC == -1) {
                        // channel closed
                        smppHandler.handlePdu(null, new IOException("Connection was unexpectedly closed"));
                        try {
                            shutdown();
                        } catch (IOException e) {}
                        return;
                    }
                    if (readC == 0) {
                        continue;
                    }

                    for (Pdu pdu : PduParser.parsePdu(buffer)) {
                        Pdu respPdu = smppHandler.handlePdu(pdu, null);
                        if (respPdu != null) {
                            sendPdu(respPdu);
                        }
                    }

                    buffer.compact(); // move unread bytes to the begging of array
                } catch (Exception e) {
                    smppHandler.handlePdu(null, e);
                }
            }
        }
    }
}