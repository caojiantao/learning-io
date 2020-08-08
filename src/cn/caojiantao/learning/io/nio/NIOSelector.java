package cn.caojiantao.learning.io.nio;

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class NIOSelector {

    public static void main(String[] args) throws IOException {
        // 创建 selector
        Selector selector = Selector.open();
        // 创建 channel
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress address = new InetSocketAddress(7000);
        channel.bind(address);
        // 将 channel 注册到 selector 上
        channel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 阻塞监听 channel 事件
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isConnectable()) {
                    // 连接成功
                    System.out.println("连接成功");
                } else if (key.isAcceptable()) {
                    System.out.println("客户端连接成功，将其注册 read 事件");
                    ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = serverChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(key.selector(), SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    System.out.println("读事件");
                    SocketChannel clientChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1);
                    // 获取消息
                    byte[] data = new byte[0];
                    while (clientChannel.read(byteBuffer) != -1) {
                        byteBuffer.flip();
                        byte[] dataPart = byteBuffer.array();
                        int originLength = data.length;
                        data = Arrays.copyOf(data, originLength + dataPart.length);
                        System.arraycopy(dataPart, 0, data, originLength, dataPart.length);
                        byteBuffer.clear();
                    }
                    String info = new String(data).trim();
                    System.out.println("从客户端发送过来的消息是：" + info);
                    clientChannel.close();
                }
            }
        }
    }
}
