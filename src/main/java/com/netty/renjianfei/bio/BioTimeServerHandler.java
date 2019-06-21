package com.netty.renjianfei.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class BioTimeServerHandler implements Runnable {

  private Socket socket;

  public BioTimeServerHandler(Socket socket) {
    this.socket = socket;

  }


  /**
   * @see Thread#run()
   */
  public void run() {
    BufferedReader in = null;
    PrintWriter out = null;
    try {
      in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      out = new PrintWriter(this.socket.getOutputStream(), true);
      String currentTime = null;
      String body = null;
      while (true) {
        body = in.readLine();
        if (body != null) {
          break;
        }
      }
      System.out.println("The time server receive order : " + body);
      currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis())
          .toString() : "BAD ORDER";
      out.println(currentTime);


    } catch (Exception e) {
      try {
        in.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    if (out != null) {
      out.close();
      out = null;
    }
    if (this.socket != null) {
      try {
        this.socket.close();

      } catch (IOException e) {
        e.printStackTrace();
      }
      this.socket = null;
    }

  }
}
