package cn.caojiantao.learning.io.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NIOSelector2 {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        SocketChannel clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.connect(new InetSocketAddress(7000));
        clientChannel.register(selector, SelectionKey.OP_CONNECT);
        while (true){
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    // 连接成功
                    System.out.println("连接成功");
                    SocketChannel channel = (SocketChannel) key.channel();
                    if (channel.isConnectionPending()){
                        channel.finishConnect();
                    }
                    channel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.clear();
                    buffer.put("曹建涛".getBytes());
                    buffer.flip();
                    channel.write(buffer);
                    channel.close();
                } else if (key.isAcceptable()) {
                    System.out.println("客户端连接");
                }
            }
        }
    }
}
