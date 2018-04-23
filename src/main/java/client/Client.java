package client;

import com.nedap.university.Packet;
import general.Protocol;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Thread {

    private DatagramSocket socket = new DatagramSocket();
    private String downloadLocation;
    private String fileName = "";
    private InetAddress address;
    private int remotePort;
    private FileOutputStream fos;

    public Client(String host, int remotePort, String downloadLocation) throws IOException {
        socket = new DatagramSocket(37642);
        address = InetAddress.getByName(host);
        this.remotePort = remotePort;
        this.downloadLocation = downloadLocation;
    }

    public void run() {
        boolean sending = true;

        while (sending) {
            //sending to server
            System.out.println("Please enter LIST, to get a list of documents and DOWNLOAD (documentname.txt) to Download ");
            try {
                String clientInputTxt = readFromInput("Enter command");

                Packet pakketjeToSend = new Packet(true, 0, false, 0, clientInputTxt.getBytes());

                byte[] bytesToSend = pakketjeToSend.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(bytesToSend, bytesToSend.length, address, remotePort);
                socket.send(sendPacket);

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
        int receivedPackets = 0;
        int expectedSeqNr = 0;
        boolean lastPacket = false;

        while (receiving) {
            System.out.println("Receiving... , give command LIST or DOWNLOAD");
            try {
                byte[] receivedBytes = new byte[Packet.SIZE];
                DatagramPacket receivedPacket = new DatagramPacket(receivedBytes, receivedBytes.length);
                socket.receive(receivedPacket);
                //System.out.println("Receive packet created ");

                if (receivedPacket != null) {
                    receivedPackets = receivedPackets + 1;
                    System.out.println("Number of packets received  " + receivedPackets);

                    Packet receivedPakketje = new Packet(receivedBytes);
                    System.out.println("Received " + new String(receivedPakketje.getData()).trim());

                    //build check based on seq numbers
                    int receivedSeqNr = receivedPakketje.getSeqNr();
                    System.out.println("received seq nr" + receivedSeqNr);

                    //send ACK
                    Packet ackPacket = new Packet(true, 0, true, receivedSeqNr);
                    DatagramPacket sendPacket = new DatagramPacket(ackPacket.getBytes(), Packet.SIZE, address, remotePort);
                    socket.send(sendPacket);

                    if (receivedPakketje.isLastPacket()) {
                        lastPacket = true;
                        receivedPackets = 0;
                        System.out.println("Last packet received");
                    }

                    if (!fileName.isEmpty()) {
                        if (receivedSeqNr == expectedSeqNr){
                            writeBytesToFile(receivedPakketje.getData());
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

    private void writeBytesToFile(byte[] bytes) throws IOException {
        fos.write(bytes);
        fos.flush();
    }

    public static void main(String[] args) throws IOException {
        String host = readFromInput("Enter host: ");
        String portAsString = readFromInput("Enter port to connect to: ");

        int port = Integer.parseInt(portAsString);

        String downloadLocation = "~/Downloads/";
        Client client = new Client(host, port, downloadLocation);

        client.start(); // start thread, be able to send stuff
        client.listen(); // listen for answers
    }

    private static String readFromInput(String requestion) throws IOException {
        System.out.println(requestion);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        return br.readLine();
    }

}







