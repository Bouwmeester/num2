package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class Client {

    public static void main(String[] args) throws IOException {
//
//        if (args.length != 1) {
//            System.out.println("Usage: java Client ieh");
//            return;
//        }

        // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName("192.168.1.1");
        //InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
        //socket.send(packet);

        // get a response
        boolean keepGoing = true;
        String received = " ";
        socket.setSoTimeout(10);

        // keeps waiting for response of client before sending new packet

       while (keepGoing) {

            try {
                // Get user input, LIST
                System.out.println("Please enter LIST, to get a list of documents ");
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                String listInput = " ";

                try {
                    listInput = clientInput.readLine();
                    sendPacket.setData(listInput.getBytes());
                    //System.out.println(listInput);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                socket.send(sendPacket);
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivedPacket);
                //String recv = new String (receivedPacket.getData());

                //display response
                received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                System.out.println(" Received " + received);

//                if (recv == null || recv.length() == 0){
//                    keepGoing = false;
//                } else {
//                    received = received + recv;
//                }
            } catch (SocketTimeoutException ste) {
                if (received.length() > 0) {
                    //System.out.println(" Received " + received);
                } else {
                    System.out.println(" Error: Connection time out");
                }
            }
            //System.out.println(" Received " + received);
        }

        socket.close();
    }
}

