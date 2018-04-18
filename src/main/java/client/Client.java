package client;

import java.io.*;
import java.net.*;

public class Client extends Thread {

    private DatagramSocket socket = new DatagramSocket();
    protected int port = 4545;
    private static final int DATASIZE = 256;
    private static final int HEADERSIZE = 4;
    protected static String path = "/Users/Bente.Bouwmeester/Documents/NedapUniversity/RaspPi";
    //protected String path = "/home/pi/myDoc/";
    private String fileName = "test11"; //aanpassen
    protected InetAddress address = InetAddress.getByName("localhost");
    //protected InetAddress address = InetAddress.getByName("192.168.1.1");

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
                //fileName

                //if clientInputTxt --> upload
                // pak de goede file, packets, send

                //DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[HEADERSIZE + DATASIZE];
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
                sendPacket.setData(clientInputTxt.getBytes());
                socket.send(sendPacket);
                System.out.println("clientInput " + clientInputTxt);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void listen() {
        //receiving from server
        boolean receiving = true;
        int receivedPackets = -1;
        int seqNrReceivedPkts = 0;
        boolean lastPacket = false;

        while (receiving) {
            System.out.println("Receiving... , give command LIST or DOWNLOAD");
            try {
                //DatagramSocket socket = new DatagramSocket();
                byte[] reicvPacket = new byte[HEADERSIZE + DATASIZE];

                DatagramPacket receivedPacket = new DatagramPacket(reicvPacket, reicvPacket.length);
                socket.receive(receivedPacket);
                //System.out.println("Receive packet created ");

                if (receivedPacket != null) {
                    receivedPackets = receivedPackets + 1;

                    //System.out.println(" Received " + received);
                    System.out.println("Number of packets received  " + receivedPackets);

                    byte[] header = new byte[HEADERSIZE];
                    byte[] data = new byte[DATASIZE];
                    System.arraycopy(reicvPacket, HEADERSIZE, data, 0, DATASIZE);
                    System.arraycopy(reicvPacket, 0, header, 0, HEADERSIZE);

                    String received = new String(data);
                    //System.out.println("Received " + received);

                    //build check based on seq numbers
                    int receivedSeqNr = data[1];
                    System.out.println("received seq nr" + receivedSeqNr);

                    //send ACK
                    byte[] buf = new byte[HEADERSIZE + DATASIZE];
                    buf[0] = (byte)1;
                    buf[1] = (byte) seqNrReceivedPkts;
                    buf[2] = (byte)1; //ACK is true
                    buf[3] = (byte) receivedSeqNr;
                    DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
                    socket.send(sendPacket);
                    System.out.println("Packet " + seqNrReceivedPkts + "has been ACK ed" + receivedSeqNr);




                    writeBytesToFile(data);
                    System.out.println("Written to file ");

                    if (header[0] == (byte) 1) {
                        lastPacket = true;
                        System.out.println("Last packet received");
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
        FileOutputStream fos = new FileOutputStream(fileName, true);
        try {
            fos.write(bytes);
            //fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

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







