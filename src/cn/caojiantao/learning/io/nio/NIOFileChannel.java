package cn.caojiantao.learning.io.nio;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author caojiantao
 */
public class NIOFileChannel {

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("a.txt");
             FileChannel fisChannel = fis.getChannel();
             FileOutputStream fos = new FileOutputStream("b.txt");
             FileChannel fosChannel = fos.getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(5);
            while (fisChannel.read(byteBuffer) != -1) {
                byteBuffer.flip();
                fosChannel.write(byteBuffer);
                byteBuffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
