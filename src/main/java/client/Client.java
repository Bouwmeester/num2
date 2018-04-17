package client;

import java.io.*;
import java.net.*;


public class Client extends Thread {

    private DatagramSocket socket = new DatagramSocket();
    protected int port = 4545;
    static final int DATASIZE = 256;
    static final int HEADERSIZE = 2;
    protected static String path = "/Users/Bente.Bouwmeester/Documents/NedapUniversity/RaspPi";
    //protected String path = "/home/pi/myDoc/";
    private String fileName = "test2";

    public Client() throws IOException {
        socket = new DatagramSocket(port);
    }


    public void run() {
        boolean sending = true;

        while (sending) {
            //sending to server
            System.out.println("Please enter LIST, to get a list of documents and DOWNLOAD-(documentname.txt) to Download ");
            try {
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                String listInput = new String(clientInput.readLine());

                //DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[HEADERSIZE + DATASIZE];
                //InetAddress address = InetAddress.getByName("192.168.1.1");
                InetAddress address = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
                sendPacket.setData(listInput.getBytes());
                socket.send(sendPacket);
                System.out.println("Listinput " + listInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void listen() {
        //receiving from server
        boolean receiving = true;
        int receivedPackets = 0;
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
                    // knip de nullen er af
                    receivedPackets = receivedPackets + 1;
                    //String received = new String(receivedPacket.getData(), 0, receivedPacket.getLength()).trim();
                    seqNrReceivedPkts = (int) receivedPacket.getData()[0];

                    //create byte[][] with all received packets

                    //System.out.println(" Received " + received);
                    System.out.println("Number of packets received  " + receivedPackets);

                    byte[] header = new byte[HEADERSIZE];
                    byte[] data = new byte[DATASIZE];
                    System.arraycopy(reicvPacket, HEADERSIZE, data, 0, DATASIZE);
                    System.arraycopy(reicvPacket, 0, header, 0, HEADERSIZE);

                    String received = new String(data).trim();
                    System.out.println("Received " + received);


                    writeBytesToFile(data);
                    System.out.println("Wrote to file ");

                    if (header[0] == (byte) 1) {
                        lastPacket = true;
                        System.out.println("Last packet received");
                    }

                    //write to file
                    //File filePath = new File(path);
//                    writeBytesToFile(data);
//                    System.out.println("Wrote to file ");

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

    public static void main(String[] args) throws IOException {
        //          2 threads

        Client client = new Client();
        //runs run method, sending
        client.start();

        // receive input
        client.listen();


    }

    public void writeBytesToFile(byte[] bytes) throws IOException {
        // BufferedOutputStream bos = null;
        //FileOutputStream fos = null;
        FileOutputStream fos = new FileOutputStream(fileName, true);
        try {
            //FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(bytes);
//        } finally {
//            fos.flushh;
//        }
//            bos = new BufferedOutputStream(fos);
//            bos.write(bytes);
//
//        } finally {
//            if (bos != null) {
//                try {
//                    bos.flush();
//                    bos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}







