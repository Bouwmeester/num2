package server;

import com.nedap.university.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import static com.nedap.university.Packet.HEADERSIZE;
import static com.nedap.university.Packet.DATASIZE;

public class Server extends Thread {

    protected DatagramSocket socket = null;
    protected int port = 5454;
    protected String path;
    protected Send send;

    public Server() throws IOException {
        socket = new DatagramSocket(port);

        if (new File("/home/pi/myDoc").exists()) {
            path = "/home/pi/myDoc";
        } else {
            path = "/Users/mark.oudeveldhuis/Downloads/_kleinmapje/";
        }
    }

    public void run() {
        while (true) {
            try {
                //receive packet
                byte[] totalPacket = new byte[DATASIZE + HEADERSIZE];
                DatagramPacket packet = new DatagramPacket(totalPacket, totalPacket.length);
                socket.receive(packet);
                System.out.println("Receiving input");

                Packet onsPakketje = new Packet(totalPacket);

                //System.out.println("Data received" + dataReceived);

                String msg = new String(onsPakketje.getData(), "UTF-8").trim();
                System.out.println("msg " + msg);
                String[] parts = msg.split(" ");

                if (send != null && onsPakketje.isACK()) {
                    send.setACK();
                } else {
                    Packet responsePacket = null;

                    switch (parts[0]) {
                        case "LIST":     responsePacket = listFiles(); break;
                        case "DOWNLOAD": download(parts[1], packet);   break;

                        default:
                            System.out.println("Unknown command received: " + parts[0]);
                    }

                    if (responsePacket != null) {
                        byte[] bytes = responsePacket.getBytes();

                        DatagramPacket responseDatagramPacket = new DatagramPacket(
                                bytes, bytes.length, packet.getAddress(), packet.getPort());

                        socket.send(responseDatagramPacket);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Packet listFiles() throws IOException {
        List<String> fileList = new ArrayList<>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        //System.out.println(listOfFiles + "list of files");
        for (File file : listOfFiles) {
            if (file.isFile()) {
                fileList.add(file.getName());
            }
        }
        return new Packet(true, 0, false, 0, fileList.toString().getBytes());
    }

    private void download(String fileName, DatagramPacket datagramPacket) {
        send = new Send(path + "/" + fileName, datagramPacket.getAddress(), datagramPacket.getPort(), socket);
        send.start();
    }
}






////                } else if (parts[0].equals(Protocol.Client.UPLOAD)){
////                    String fileName = parts[1];
////                    //instead of sending a file --> receive
////                    byte[] uploaded = new byte[HEADERSIZE + DATASIZE];
////
////                    DatagramPacket uploadPacket = new DatagramPacket(uploaded, uploaded.length);
////                    socket.receive(uploadPacket);
////                    // make client send packets
//
//                } else {
//                    System.out.println("List not found");
//                    //is also printed after incoming ACK
//                }