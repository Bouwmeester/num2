package server;

import com.nedap.university.Packet;
import general.Protocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
    private byte[] fileContents = null;
    protected InetAddress address = InetAddress.getByName("localhost");
    //protected InetAddress address = InetAddress.getByName("192.168.1.1");
    private String fileName = "";
    private FileOutputStream fos;



    public ServerThread2() throws IOException {
        this("ServerThread2");

    }

    public ServerThread2(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(port);      //port 5454
    }

    public void run() {
        Send send = null;
        //secretly receive thread
        while (true) {
            try {
                byte[] totalPacket = new byte[Packet.DATASIZE + Packet.HEADERSIZE];

                //receive packet
                DatagramPacket packet = new DatagramPacket(totalPacket, totalPacket.length);
                socket.receive(packet);
                System.out.println("Receiving input");

                byte[] header = new byte[Packet.HEADERSIZE];
                byte[] data = new byte[Packet.DATASIZE];
                System.arraycopy(totalPacket, 0, data, 0, Packet.DATASIZE);
                System.arraycopy(totalPacket, 0, header, 0, Packet.HEADERSIZE);

                //System.out.println("Data received" + dataReceived);

                String msg = new String(data, "UTF-8").trim();
                System.out.println("msg " + msg);
                String[] parts = msg.split(" ");

                if (send != null && header[2] == (byte)1) {
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
                    Packet listPacket = new Packet(true, 0, false, 0, fileList.toString().getBytes());
                    byte[] bytes = listPacket.getBytes();

                    DatagramPacket filePacket = new DatagramPacket(bytes, bytes.length, packet.getAddress(), packet.getPort());
                    socket.send(filePacket);

                } else if (parts[0].equals(Protocol.Client.DOWNLOAD)) {
                    String fileName = parts[1];
                    send = new Send(fileName, packet.getAddress(), 4545, socket);
                    send.start();
                    //System.out.println("Filecontents" + fileContents);
                } else if (parts[0].equals(Protocol.Client.UPLOAD)){
                    fileName = parts[1];
                    //instead of sending a file --> receive
                    ServerThread2 serverThread = new ServerThread2();
                    serverThread.listen();



                } else {
                    System.out.println("List not found");
                    //is also printed after incoming ACK
                }

            } catch (IOException e) {
                e.printStackTrace();


            }
        }
    }

    public void listen() {
        //receiving from client
        boolean receiving = true;
        int receivedPackets = -1;
        int expectedSeqNr = 0;
        boolean lastPacket = false;

        while (receiving) {
            System.out.println("Receiving... , from client ");
            try {
                //DatagramSocket socket = new DatagramSocket();
                byte[] reicvPacket = new byte[Packet.HEADERSIZE + Packet.DATASIZE];

                DatagramPacket receivedPacket = new DatagramPacket(reicvPacket, reicvPacket.length);
                socket.receive(receivedPacket);
                //System.out.println("Receive packet created ");

                if (receivedPacket != null) {
                    receivedPackets = receivedPackets + 1;

                    System.out.println("Number of packets received  " + receivedPackets);

                    byte[] header = new byte[Packet.HEADERSIZE];
                    byte[] data = new byte[Packet.DATASIZE];
                    System.arraycopy(reicvPacket, Packet.HEADERSIZE, data, 0, Packet.DATASIZE);
                    System.arraycopy(reicvPacket, 0, header, 0, Packet.HEADERSIZE);

                    String received = new String(data);
                    //System.out.println("Received " + received);

                    //build check based on seq numbers
                    int receivedSeqNr = header[1];
                    //System.out.println("received seq nr" + receivedSeqNr);

                    //send ACK
                    Packet ackPacket = new Packet(true, 0, true, receivedSeqNr);
                    DatagramPacket sendPacket = new DatagramPacket(ackPacket.getBytes(), Packet.SIZE, address, 4545);
                    socket.send(sendPacket);
                    //System.out.println("Packet " + seqNrReceivedPkts + " has been ACK ed " + receivedSeqNr);

                    if (header[0] == (byte) 1) {
                        lastPacket = true;
                        System.out.println("Last packet received");
                    }

                    if (!fileName.isEmpty()) {
                        if (receivedSeqNr == expectedSeqNr){
                            writeBytesToFile(data);
                            System.out.println("Written to file ");

                            System.out.println("expectedSeqNr " + expectedSeqNr);
                            //build in sleep/wait till
                        }
                        ++expectedSeqNr;
                        //doesn't work with multiple packets, save somewhere and writebytes per window?
                        //only per 1 task

//                        HashMap<Integer, byte[]>  map = new HashMap<Integer, byte[]>();
//                        map.put(receivedSeqNr,data);

                        //writeBytesToFile(data);
                        //System.out.println("Written to file ");
                        if (lastPacket) {
                            fileName = "";
                            fos.close();
                        }
                    }
                } else {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        receiving = false;
                    }
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void writeBytesToFile(byte[] bytes) throws IOException {
        fos.write(bytes);
        fos.flush();
    }
}



