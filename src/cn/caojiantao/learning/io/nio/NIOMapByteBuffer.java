package cn.caojiantao.learning.io.nio;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author caojiantao
 */
public class NIOMapByteBuffer {

    public static void main(String[] args) throws IOException {
        RandomAccessFile file = new RandomAccessFile("a.txt", "rw");
        System.out.println("文件大小：" + file.length());
        try (FileChannel channel = file.getChannel()) {
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, channel.size());
            // ascii 为字母 a
            int i = 97;
            while (mappedByteBuffer.hasRemaining()) {
                mappedByteBuffer.put((byte) i++);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
