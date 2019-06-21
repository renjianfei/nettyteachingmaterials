package com.netty.renjianfei.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioTimeServer {

  public static void main(String[] args) {
    //初始化服务端口
    int port = 8081;
    if (args != null && args.length > 0) {
      try {
        port = Integer.valueOf(args[0]);

      } catch (Exception e) {
        // 采用默认值
      }
    }

    ServerSocket server = null;
    try {
      //初始化server socket
      server = new ServerSocket(port);

      System.out.println("The time server is start in port : " + port);

      //声明客户端socket
      Socket socket = null;

      while (true) {
        socket = server.accept();
        new Thread(new BioTimeServerHandler(socket)).start();
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (server != null) {
        System.out.println("The time server close");
        try {
          server.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        server = null;
      }
    }
  }
}
