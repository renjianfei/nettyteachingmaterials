package com.netty.renjianfei.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class MultiplexerTimeServer implements Runnable {

  private Selector selector;
  private ServerSocketChannel serverSocketChannel;
  private volatile boolean stop;

  public MultiplexerTimeServer(int port) {
    try {
      selector = Selector.open();
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
      System.out.println("The time server is start in port : " + port);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public void stop() {
    this.stop = true;
  }

  /**
   * @see Thread#run()
   */
  public void run() {
    while (!stop) {
      try {
        selector.select(1000);
        Set<SelectionKey> selectionKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
          SelectionKey key = iterator.next();
          try {
            handleInput(key);
          } catch (IOException e) {
            if (key != null) {
              key.cancel();
              if (key.channel() != null) {
                key.channel().close();
              }
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //多路复用器关闭后，注册自其上面的Channel和Pipe等资源会自动去除注册并关闭，所以不需要释放资源
    if (selector != null) {
      try {
        selector.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void handleInput(SelectionKey key) throws IOException {

    if (key.isValid()) {

      //处理新接入的请求
      if (key.isAcceptable()) {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
      }

      //处理读操作
      if (key.isReadable()) {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = sc.read(readBuffer);

        if (readBytes > 0) {
          readBuffer.flip();
          byte[] bytes = new byte[readBuffer.remaining()];
          readBuffer.get(bytes);
          String body = new String(bytes, "UTF-8");
          System.out.println("The time server receive order : " + body);
          String currentTime =
              "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis())
                  .toString() : "BAD ORDER";
          doWrite(sc, currentTime);

        } else if (readBytes < 0) {
          key.cancel();
          sc.close();
        } else {
          //读到0字节，忽略
        }

      }
    }

  }

  private void doWrite(SocketChannel channel, String response) throws IOException {
    if (response != null && response.trim().length() > 0) {
      byte[] bytes = response.getBytes();
      ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
      writeBuffer.put(bytes);
      writeBuffer.flip();
      channel.write(writeBuffer);
    }
  }
}
