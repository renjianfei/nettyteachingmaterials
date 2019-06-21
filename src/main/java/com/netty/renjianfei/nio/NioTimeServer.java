package com.netty.renjianfei.nio;

import java.nio.channels.ServerSocketChannel;

public class NioTimeServer {

  public static void main(String[] args) {
    int port = 8081;
    if (args != null && args.length > 0) {
      try {
        port = Integer.valueOf(args[0]);
      } catch (Exception e) {
        e.printStackTrace();
      }

      MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
      new Thread(timeServer,"NIO-MultiplexerTimeServer-001").start();

    }
  }

}
