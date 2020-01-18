package cn.caojiantao.learning.io.bio;

import java.io.*;

/**
 * BIO 文件操作相关
 *
 * @author caojiantao
 */
public class BIOFile {

    public static void main(String[] args) {
        try (InputStream is = new FileInputStream("a.jpg");
             OutputStream os = new FileOutputStream("b.jpg")) {
            byte[] block = new byte[1024];
            int len, sum = 0;
            while ((len = is.read(block)) != -1) {
                os.write(block, 0, len);
                sum += len;
            }
            System.out.println("sum is " + sum);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
