## Java I/O 模型

Java I/O 模型，大致分为 BIO、NIO 和 AIO 三种；

 1. BIO
    
    blocking IO，同步阻塞 IO，JDK 1.4 之前唯一选择，面向流 (stream) ；
    
 2. NIO

    即 non-blocking IO，也叫做 new IO，同步非阻塞 IO，JDK 1.4 开始引入，面向缓冲区 (buffer)；

 3. AIO

    Asynchronous IO，异步非阻塞 IO，JDK 1.7 开始引入；

> 同步/异步：A 调用 B，立即返回，且 B 执行完毕通知 A (异步)；
>
> 阻塞/非阻塞：A 调用 B，A 在挂起的时候可以执行其他操作 (非阻塞)；
>
> 参考：http://tieba.baidu.com/p/6104908266

## BIO

### 介绍

![](http://pic.caojiantao.site/learning-io/bio-info.jpg)

基于不同的维度，大致可以将其分为：

1. 字节流

   以 stream 结尾，单个字节传输；

2. 字符流

   以 er 结尾，多个字符传输 (因编码而异，UTF-8 是 3 个字节)；

3. 缓冲流

   有 buffer 标识的，对流包装了一层缓冲区，传输更高效；

4. 输入流

   有 input 标识的，用于获取数据；

5. 输出流

   有 output 标识的，用于输出数据；

6. 文件流

   有 file 表示的，用于文件处理；

> 上述仅仅是初略归类，想要了解更详细信息还请查阅相关资料。

### 文本拷贝

核心代码：

```java
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
```

### Socket 编程

流程图示：

![](http://pic.caojiantao.site/learning-io/bio-socket.jpg)

 核心代码：

```java
// 创建 ServerSocket
ServerSocket serverSocket = new ServerSocket(7000);
while (true) {
    // 阻塞获取客户端连接
    System.out.println("等待连接...");
    Socket socket = serverSocket.accept();
    System.out.println("连接建立...");
    new Thread(() -> {
        // 为每个客户端新建一个线程
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
    }).start();
}
```

> 可以加入线程池优化客户端处理逻辑，见源代码：https://github.com/caojiantao/learning-io/blob/master/cn/caojiantao/learning/io/bio/BIOThreadPoolServer.java

## NIO

### 介绍

![](http://pic.caojiantao.site/learning-io/nio-info.jpg)

Java NIO 的核心组件，简述为：

1. ByteBuffer

    数据读写的缓冲区；

2. Channel

    连接数据的通道；

3. Selector

    选择器，负责监听 Channel 的连接、读和写事件；

### 核心组件

#### ByteBuffer

ByteBuffer 内部维护了一个 byte 数组 hb，用来存放实际的读写数据。同时继承自 Buffer，定义了下列四个属性，来表示当前缓冲区的状态：

1. mark

    标记，默认值为 -1；

2. position

    下一个元素读写的位置索引，默认值为 0；

3. limit

    当前缓冲区的边界 (可修改)，读写不能越界；

4. capacity

    缓冲区容量大小，创建时需要显示声明；

> mark <= position <= limit <= capacity

有几个重要的相关方法，截取源码说明：

```java
// 通常用作切换读模式
public final Buffer flip() {
    limit = position;
    position = 0;
    mark = -1;
    return this;
}

// 重置所有参数（通常用作切换写模式）
public final Buffer clear() {
    position = 0;
    limit = capacity;
    mark = -1;
    return this;
}

// 设置 position 位置为 mark
public final Buffer reset() {
    int m = mark;
    if (m < 0)
        throw new InvalidMarkException();
    position = m;
    return this;
}

// 是否还有读写空间
public final boolean hasRemaining() {
    return position < limit;
}
```

例如往一个大小为 4 的 ByteBuffer 中，写入了 3 个数据后，各个属性概览；

![](http://pic.caojiantao.site/learning-io/nio-bytebuffer-info-1.jpg)

执行 flig() 方法切换为读模式，limit 设置为 position 位置 3，position 重置为 0；

![](http://pic.caojiantao.site/learning-io/nio-bytebuffer-info-2.jpg)

这里有个小案例可以自主体会下；

```java
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
```

根据缓冲区存放的数据类型不同，与 ByteBuffer 同级的还有 IntBuffer、FloatBuffer、DoubleBuffer、CharBuffer、ShortBuffer 和 LongBuffer。

##### MappedByteBuffer

继承自 ByteBuffer，直接在内存（堆外）中读写数据，性能极高。

这里有个小案例可以自主体会下；

```java
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
```

#### Channel

相比 BIO 流，有以下几个区别；

1. channel 支持读写，而 stream 只能读或者只能写；
2. channel 支持异步读写；

常用的 Channel 有：

1. FileChannel

    文件读写；

2. DatagramChannel

    UDP 的数据读写；

3. ServerSocketChannel、SocketChannel

    TCP 的数据读写；

##### FileChannel

用一个 ByteBuffer 进行文件的拷贝；

```java
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
```

> FileChannel 提供 transferFrom 更优雅地实现了文件的拷贝，见源代码：https://github.com/caojiantao/learning-io/blob/master/cn/caojiantao/learning/io/nio/NIOFileChannel2.java

##### SocketChannel

ServerSocketChannel 用来监听 TCP 的连接，相比较 ServerSocket 而言，支持设置非阻塞获取连接模式 configureBlocking，用一段简单的 NIO 服务端代码来看看；

```java
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
```

#### Selector



## AIO

## 总结
