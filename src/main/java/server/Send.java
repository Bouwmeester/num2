package server;

import com.nedap.university.Packet;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;


public class Send extends Thread {


    protected BufferedReader in = null;
    private int chunks = 0;
    private byte[] fileContents = null;
    boolean ACK = false;
    private String filePath;
    private InetAddress address;
    private int clientPort;
    private DatagramSocket socket;
    private int windowSize = Window.WINDOWSIZE;

    public Send(String filePath, InetAddress address, int clientPort, DatagramSocket socket) {
        this.filePath = filePath;
        this.address = address;
        this.clientPort = clientPort;
        this.socket = socket;
    }

    public void run() {
        boolean lastPacket = false;
        int sequenceNumber = -1;

        try {
            in = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        fileContents = getFileContents(filePath);


        while (!lastPacket) {
            try {
                byte[][] packetArray = divideArray(fileContents, Packet.DATASIZE);


                int windowAmount = (packetArray.length + windowSize - 1)/windowSize;
                for (int i = 0; i < windowAmount; i++){
                    byte [][] packetsForWindow = Arrays.copyOfRange(packetArray,i * windowSize,(i * windowSize) + windowSize - 1);
                    Window window = new Window(this, packetsForWindow);
                    window.start();
                    while(!window.isDone()){
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {

                        }
                    }


                }




            }
        }
    }

    public void setACK(int sequenceNumber){
        ACK = true;
        interrupt();
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

    public void sendPacket(Packet packetToSend){
        byte[] bytes = packetToSend.getBytes();
        DatagramPacket filePacket = new DatagramPacket(bytes, bytes.length, address, clientPort);
        try {
            socket.send(filePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
