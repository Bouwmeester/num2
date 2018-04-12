package server;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;

public class Server {

  public static void main(String[] args) throws IOException {
      new ServerThread().start();
  }

}
