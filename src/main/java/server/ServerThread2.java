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
    private String fileName = null;
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

                if(fileName != null) {
                    writeBytesToFile(data);
                    Packet ackPacket = new Packet(false, header[1], true, header[1]);
                    DatagramPacket ackDataPacket = new DatagramPacket(ackPacket.getBytes(), Packet.SIZE, address, 4545);
                    socket.send(ackDataPacket);
                    if (header[0] == (byte) 1) {
                        fileName = null;
                        fos.close();
                    }
                } else if (send != null && header[2] == (byte)1) {
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
                    //fileName
                    // if fileName != null --> wegschrijven
                    // remove fileName
                    fos = new FileOutputStream(fileName, true);




                } else {
                    System.out.println("List not found");
                    //is also printed after incoming ACK
                }

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



