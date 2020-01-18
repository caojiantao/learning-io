package cn.caojiantao.learning.io.nio;

import java.nio.ByteBuffer;

/**
 * @author caojiantao
 */
public class NIOByteBuffer {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        String fmt = "capacity:%s limit:%s position:%s";
        System.out.println("================ init ================");
        System.out.println(String.format(fmt, byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position()));
        for (int i = 0; i < 3; i++) {
            byteBuffer.put((byte) i);
        }
        System.out.println("================ finish ================");
        System.out.println(String.format(fmt, byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position()));
        byteBuffer.flip();
        System.out.println("================ flip ================");
        System.out.println(String.format(fmt, byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position()));
        // 读取数据
        StringBuilder builder = new StringBuilder();
        while (byteBuffer.hasRemaining()){
            byte b = byteBuffer.get();
            builder.append(b).append("\t");
        }
        System.out.println("缓冲区：" + builder.toString());
        byteBuffer.clear();
        System.out.println("================ clear ================");
        System.out.println(String.format(fmt, byteBuffer.capacity(), byteBuffer.limit(), byteBuffer.position()));
    }
}
