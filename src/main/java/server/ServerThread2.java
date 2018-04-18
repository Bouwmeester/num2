package server;

import com.nedap.university.Packet;
import general.Protocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
                byte[] totalPacket = new byte[DATASIZE + HEADERSIZE];

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
//                } else if (parts[0].equals(Protocol.Client.UPLOAD)){
//                    String fileName = parts[1];
//                    //instead of sending a file --> receive
//                    byte[] uploaded = new byte[HEADERSIZE + DATASIZE];
//
//                    DatagramPacket uploadPacket = new DatagramPacket(uploaded, uploaded.length);
//                    socket.receive(uploadPacket);
//                    // make client send packets


                } else {
                    System.out.println("List not found");
                    //is also printed after incoming ACK
                }

            } catch (IOException e) {
                e.printStackTrace();


            }
        }
    }
}



