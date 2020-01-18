package cn.caojiantao.learning.io.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * @author caojiantao
 */
public class NIOSocketChannel {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(7000);
        serverSocketChannel.bind(address);
        // 设置为非阻塞模式，accept 会立即返回
        serverSocketChannel.configureBlocking(true);
        while (true) {
            System.out.println("等待连接...");
            SocketChannel socketChannel = serverSocketChannel.accept();
            boolean blocking = socketChannel.isBlocking();
            System.out.println("建立连接 block: " + blocking);
            ByteBuffer byteBuffer = ByteBuffer.allocate(5);
            while (socketChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                byte[] array = byteBuffer.array();
                byte[] bytes = Arrays.copyOfRange(array, 0, byteBuffer.limit());
                String temp = new String(bytes);
                System.out.println(temp);
                byteBuffer.clear();
            }
        }
    }
}
