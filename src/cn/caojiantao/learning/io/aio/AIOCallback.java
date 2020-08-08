package cn.caojiantao.learning.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class AIOCallback {

    public static void main(String[] args) throws IOException {
        AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open().bind(new InetSocketAddress(7000));
        channel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel result, Object attachment) {
                // 继续监听下一个请求
                channel.accept(attachment, this);
                try {
                    // 阻塞等待客户端接收数据
                    ByteBuffer readBuffer = ByteBuffer.allocate(128);
                    result.read(readBuffer).get();
                    System.out.println(new String(readBuffer.array()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, Object attachment) {

            }
        });
    }
}
