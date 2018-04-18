package client;

import com.nedap.university.Packet;
import general.Protocol;

import java.io.*;
import java.net.*;
import java.util.*;


public class Client extends Thread {

    private DatagramSocket socket = new DatagramSocket();
    protected int port = 4545;
    protected static String path = "/Users/Bente.Bouwmeester/Documents/NedapUniversity/RaspPi";
    //protected String path = "/home/pi/myDoc/";
    private String fileName = ""; //aanpassen
    protected InetAddress address = InetAddress.getByName("localhost");
    //protected InetAddress address = InetAddress.getByName("192.168.1.1");
    private FileOutputStream fos;

    public Client() throws IOException {
        socket = new DatagramSocket(port);
    }


    public void run() {
        boolean sending = true;

        while (sending) {
            //sending to server
            System.out.println("Please enter LIST, to get a list of documents and DOWNLOAD (documentname.txt) to Download ");
            try {
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                String clientInputTxt = new String(clientInput.readLine());

                //DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[Packet.HEADERSIZE + Packet.DATASIZE];
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
                sendPacket.setData(clientInputTxt.getBytes());
                socket.send(sendPacket);
                System.out.println("clientInput " + clientInputTxt);

                //fileName
                String[] parts = clientInputTxt.split(" ");
                if (parts[0].equals(Protocol.Client.DOWNLOAD)) {
                    fileName = parts[1];
                    System.out.println("fileName " + fileName);
                    fos = new FileOutputStream(fileName, true);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void listen() {
        //receiving from server
        boolean receiving = true;
        int receivedPackets = -1;
        int expectedSeqNr = 0;
        boolean lastPacket = false;

        while (receiving) {
            System.out.println("Receiving... , give command LIST or DOWNLOAD");
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
                    System.out.println("Received " + received);

                    //build check based on seq numbers
                    int receivedSeqNr = header[1];
                    System.out.println("received seq nr" + receivedSeqNr);

                    //send ACK
                    Packet ackPacket = new Packet(true, 0, true, receivedSeqNr);
                    DatagramPacket sendPacket = new DatagramPacket(ackPacket.getBytes(), Packet.SIZE, address, 5454);
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


    public static void main(String[] args) throws IOException {
        //          2 threads

        Client client = new Client();
        //runs run method, sending
        client.start();

        // receive input
        client.listen();


    }


}







