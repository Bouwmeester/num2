package server;

import com.nedap.university.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class Send extends Thread {


    protected BufferedReader in = null;
    protected String path = "/Users/Bente.Bouwmeester/Documents/NedapUniversity/RaspPi";
    //protected String path = "/home/pi/myDoc/";
    private int chunks = 0;
    private byte[] fileContents = null;
    boolean ACK = false;
    private String fileName;
    private InetAddress address;
    private int clientPort;
    private DatagramSocket socket;

    public Send(String fileName, InetAddress address, int clientPort, DatagramSocket socket) {
        this.fileName = fileName;
        this.address=address;
        this.clientPort = clientPort;
        this.socket = socket;
    }

    public void run() {
        boolean lastPacket = false;
        int sequenceNumber = -1;

        try {
            in = new BufferedReader(new FileReader(path + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileContents = getFileContents(path + "/" + fileName);


        while (!lastPacket) {
            try {
                byte[][] packetArray = divideArray(fileContents, Packet.DATASIZE);

                //array welke je nog moet sturen --> Ack --> uit de lijst
                // lijst met byte array s

                for (byte[] somePacket : packetArray) {
                    Packet partyPacket = new Packet(false, ++sequenceNumber, false, 0, somePacket);

                    if (sequenceNumber + 1 == chunks) {
                        partyPacket.setLastPacket(true);
                        lastPacket = true;
                        System.out.println("Last packet is to be sent");
                    }
                    byte[] bytes = partyPacket.getBytes();

                    DatagramPacket filePacket = new DatagramPacket(bytes, bytes.length, address, clientPort);
                    socket.send(filePacket);

                    while(!ACK) {
                        //geen ACK binnen < 10 s --> nog een keer sturen
                        try {
                            currentThread().sleep(100);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }

                    }
                    ACK = false;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setACK(){
        ACK = true;
    }




    public byte[][] divideArray(byte[] fileSource, int chunkSize) {


        //get the correct amount of chunk
        chunks = (fileSource.length + chunkSize - 1) / chunkSize;
        System.out.println("Amount of packets to send " + chunks);
        byte[][] arrayOfPackets = new byte[chunks][chunkSize];

        int start = 0;

        //arrays.copyofRange --> copies specified array into new array
        // (orignal, int from, int to)

        for (int i = 0; i < arrayOfPackets.length; i++) {
            arrayOfPackets[i] = Arrays.copyOfRange(fileSource, start, start + chunkSize);
            start = start + chunkSize;
        }
        return arrayOfPackets;
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


}
