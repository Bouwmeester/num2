package server;

import general.Protocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ServerThread2 extends Thread {


    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected int port = 5454;
    protected String path = "/Users/Bente.Bouwmeester/Documents/NedapUniversity/RaspPi";
    //protected String path = "/home/pi/myDoc/";
    //private int chunks = 0;
    static final int HEADERSIZE = 3;
    private int DATASIZE = 256;
    private byte[] fileContents = null;



    public ServerThread2() throws IOException {
        this("ServerThread2");

    }

    public ServerThread2(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(port);
    }

    public void run() {
        Send send = null;
        //secretly receive thread
        while (true) {
            try {
                byte[] buf = new byte[DATASIZE]; //256
                byte[] totalPacket = new byte[buf.length + HEADERSIZE];

                //receive packet
                DatagramPacket packet = new DatagramPacket(totalPacket, totalPacket.length);
                socket.receive(packet);
                System.out.println("Receiving input");

                byte[] header = new byte[HEADERSIZE];
                byte[] data = new byte[DATASIZE];
                System.arraycopy(totalPacket, 0, data, 0, DATASIZE);
                System.arraycopy(totalPacket, 0, header, 0, HEADERSIZE);

                //System.out.println("Data received" + dataReceived);

                String msg = new String(data, "UTF-8").trim();
                System.out.println("msg" + msg);
                String[] parts = msg.split(" ");

                if (header[2] == (byte) 1) {
                    //ACK received
                    send.setACK();


                } else if (parts[0].equals(Protocol.Client.LIST)) {
                    List<String> fileList = new ArrayList<String>();
                    File folder = new File(path);
                    File[] listOfFiles = folder.listFiles();
                    //System.out.println(listOfFiles + "list of files");
                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            fileList.add(file.getName());
                        }
                    }
                } else if (parts[0].equals(Protocol.Client.DOWNLOAD)) {
                    String fileName = parts[1];
                    send = new Send(fileName, packet.getAddress(), 4545, socket);
                    send.start();
                    //System.out.println("Filecontents" + fileContents);
                } else if (parts[0].equals(Protocol.Client.UPLOAD)){
                    String fileName = parts[1];
                    //instead of sending a file --> receive
                    byte[] uploaded = new byte[HEADERSIZE + DATASIZE];

                    DatagramPacket uploadPacket = new DatagramPacket(uploaded, uploaded.length);
                    socket.receive(uploadPacket);
                    // make client send packets


                } else {
                    System.out.println("List not found");
                }

            } catch (IOException e) {
                e.printStackTrace();


            }
        }
    }

//    public void listen() {
//        //receiving from client
//        boolean receiving = true;
//        int receivedPackets = -1;
//        int seqNrReceivedPkts = 0;
//        boolean lastPacket = false;
//
//        while (receiving) {
//            System.out.println("Receiving... , give command LIST or DOWNLOAD");
//            try {
//                //DatagramSocket socket = new DatagramSocket();
//                byte[] reicvPacket = new byte[HEADERSIZE + DATASIZE];
//
//                DatagramPacket receivedPacket = new DatagramPacket(reicvPacket, reicvPacket.length);
//                socket.receive(receivedPacket);
//                //System.out.println("Receive packet created ");
//
//                if (receivedPacket != null) {
//                    receivedPackets = receivedPackets + 1;
//
//                    //System.out.println(" Received " + received);
//                    System.out.println("Number of packets received  " + receivedPackets);
//
//                    byte[] header = new byte[HEADERSIZE];
//                    byte[] data = new byte[DATASIZE];
//                    System.arraycopy(reicvPacket, 0, data, 0, DATASIZE);
//                    System.arraycopy(reicvPacket, 0, header, 0, HEADERSIZE);
//
//                    String received = new String(data).trim();
//                    //System.out.println("Received " + received);
//
//                    //build check based on seq numbers
//                    int receivedSeqNr = data[1];
//                    System.out.println("received seq nr" + receivedSeqNr);
//
//                    //send ACK
//                    byte[] buf = new byte[HEADERSIZE + DATASIZE];
//                    buf[0] = (byte)1;
//                    buf[1] = (byte) seqNrReceivedPkts;
//                    buf[2] = (byte)1; //ACK is true
//                    buf[3] = (byte) receivedSeqNr;
//                    DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
//                    socket.send(sendPacket);
//
//
//
//                    writeBytesToFile(data);
//                    System.out.println("Written to file ");
//
//                    if (header[0] == (byte) 1) {
//                        lastPacket = true;
//                        System.out.println("Last packet received");
//                    }
//
//
//                } else {
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        receiving = false;
//                    }
//                }
//
//            } catch (SocketException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//    }




}



