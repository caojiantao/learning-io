package cn.caojiantao.learning.io.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * BIO socket 服务端（线程池）
 *
 * @author caojiantao
 */
public class BIOThreadPoolServer {

    public static void main(String[] args) throws Exception {
        // 创建线程池
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        // 创建 ServerSocket
        ServerSocket serverSocket = new ServerSocket(7000);
        while (true) {
            // 阻塞获取客户端连接
            System.out.println("等待连接...");
            Socket socket = serverSocket.accept();
            System.out.println("连接建立...");
            threadPool.submit(() -> {
                // 线程池提交
                try (InputStream is = socket.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)))) {
                    String line;
                    // 阻塞读取客户端数据
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
