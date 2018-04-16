package server;

import general.Protocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected boolean moreLines = true;
    protected int port = 5454;


    public ServerThread() throws IOException {
        this("ServerThread");

    }

    public ServerThread(String name) throws IOException {
        super(name);
        socket = new DatagramSocket(port);
    }

    public void run() {

        while (moreLines) {
            try {
                byte[] buf = new byte[256]; //256

                //receive packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                System.out.println("Receiving input");

                // receive packet --> LIST
                // convert byte[] buf to char

                String msg = new String(buf, "UTF-8").trim();
                String[] parts = msg.split(" ");
                //System.out.println(buf);
                //System.out.println("Byte to chars '" + msg + "'");
                //System.out.println("Test '" + Protocol.Client.LIST + "'");

                // Check if input message is LIST
                // Return list of available documents
                if (parts[0].equals(Protocol.Client.LIST)) {
                    List<String> fileList = new ArrayList<String>();
                    File folder = new File("/home/pi/myDoc");
                    //File folder = new File(" /home/Documents/NedapUniversity/RaspPi");
                    File[] listOfFiles = folder.listFiles();
                    System.out.println(listOfFiles + "list of files");
                    for (File file : listOfFiles) {
                        if (file.isFile()) {
                            fileList.add(file.getName());
                        }
                    }

                    // send list response to client
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();

                    DatagramPacket listPacket = new DatagramPacket(buf, buf.length, address, port);
                    listPacket.setData(fileList.toString().getBytes());
                    socket.send(listPacket);
                    // see output on server
                    System.out.println("List of available documents " + fileList);


                    // download specific file
                } else if (parts[0].equals(Protocol.Client.DOWNLOAD)) {
                    String fileName = parts[1];
                    in = new BufferedReader(new FileReader("/home/pi/myDoc/" + fileName));
                    //in = new BufferedReader(new FileReader("/home/Documents/NedapUniversity/RaspPi" + fileName));

                    byte[] fileContents = getFileContents("/home/pi/myDoc/" + fileName);
                    //System.out.println("Filecontents" + fileContents);

                    int DATASIZE = 256;
                    boolean lastPacket = false;


                    if (fileContents != null) {
                        byte[][] packetArray = divideArray(fileContents, DATASIZE);
                        for (byte[] somePacket : packetArray) {

                            if (somePacket.length < DATASIZE){
                                lastPacket = true;
                                System.out.println("Last packet is send");
                                InetAddress address = packet.getAddress();
                                DatagramPacket packetLast = new DatagramPacket(somePacket, somePacket.length, address, port);
                                socket.send(packetLast);


                            } else {
                                InetAddress address = packet.getAddress();
                                DatagramPacket filePacket = new DatagramPacket(somePacket, somePacket.length, address, port);
                                socket.send(filePacket);

                            }

                        }
                    }

                } else {
                    System.out.println("List not found");
                }

            } catch (IOException e) {
                e.printStackTrace();

                moreLines = false;
            }

        }

        socket.close();

    }


    public static byte[] getFileContents(String fileID) {
        //filename
        File fileToTransmit = new File(fileID);
        try (FileInputStream fileStream = new FileInputStream(fileToTransmit)) {
            byte[] fileContents = new byte[(int) fileToTransmit.length()];

            for (int i = 0; i < fileContents.length; i++) {
                int nextByte = fileStream.read();
                if (nextByte == -1) {
                    throw new Exception("File size smaller than reported");
                }
                fileContents[i] = (byte) nextByte;
            }
            return fileContents;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            return null;
        }
    }

    public byte[][] divideArray(byte[] fileSource, int chunksize) {

        //get the correct amount of chunk
        int chunks = (fileSource.length + chunksize - 1)/chunksize;
        System.out.println("Amount of packets to send " + chunks);
        byte[][] arrayOfPackets = new byte[chunks][chunksize];

        int start = 0;

        //arrays.copyofRange --> copies specified array into new array
        // (orignal, int from, int to)

        for (int i = 0; i < arrayOfPackets.length; i++) {
            arrayOfPackets[i] = Arrays.copyOfRange(fileSource, start, start + chunksize);
            start = start + chunksize;
        }
        return arrayOfPackets;
    }

}
