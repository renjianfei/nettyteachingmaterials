package com.netty.renjianfei.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BioTimeClient {

  public static void main(String[] args) {
    int port = 8081;
    if (args != null && args.length > 0) {
      try {
        port = Integer.valueOf(args[0]);
      } catch (Exception e) {
        //采用默认值
      }
    }
    Socket socket = null;
    BufferedReader in = null;
    PrintWriter out = null;
    try {
      socket = new Socket("127.0.0.1",port);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out = new PrintWriter(socket.getOutputStream());
      out.println("QUERY TIME ORDER");
      out.flush();
      Thread.sleep(100L);
      System.out.println("Send order 2 server succeed.");
      String resp = in.readLine();
      System.out.println("Now is : " + resp);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (out != null) {
        out.close();
        out = null;
      }

      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        in = null;
      }
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        socket = null;
      }
    }
  }

}
