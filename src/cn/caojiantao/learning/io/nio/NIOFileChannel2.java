package cn.caojiantao.learning.io.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author caojiantao
 */
public class NIOFileChannel2 {

    public static void main(String[] args) {
        try (FileInputStream fis = new FileInputStream("a.txt");
             FileChannel fisChannel = fis.getChannel();
             FileOutputStream fos = new FileOutputStream("b.txt");
             FileChannel fosChannel = fos.getChannel()) {
            fosChannel.transferFrom(fisChannel, 0, fisChannel.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
