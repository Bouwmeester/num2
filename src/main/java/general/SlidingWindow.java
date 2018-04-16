//package general;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.nio.ByteBuffer;
//
//public class SlidingWindow implements BasicProtocol {
//
//    //Header info
//    static final int HEADERSIZE = 2; //number of header bytes in each packet
//    static final int DATASIZE = 256; //max number of user data bytes in each packet
//    private int LAR = -1; //Last ACK received
//    private int LFS = -1; //Last Frame Send
//    private int K = 16;  //
//    private int SWS = 4; //Send Window Size
//    private int RWS = 4; //Receive Window Size
//    private int LFR = 0; //Last Frame Received
//    private int LAF = 0; // Largest Acceptable Frame
//    private boolean[] acknowledgements = new boolean[K];
//
//    //
//    protected DatagramSocket socket = null;
//    protected BufferedReader in = null;
//    protected int port = 5454;
//
//    public void sender() throws IOException{
//
//        socket = new DatagramSocket(port);
//        System.out.println("Sending..");
//
//        //read from the input file
//        // update to current file?
//        Integer[] fileContents = Utils.getFileContents(getFileID());
//
//        //keep track of the data
//        int filePointer = 0;
//        int sequenceNumber = 0;
//        int numberOfPacketsSend = 0;
//        boolean lastPacket = false;
//        int datalen = -1;
//
//
//        while(!lastPacket) {
//            while(inSendingWindow) {
//
//                //create new packet of sufficient size
//                datalen = Math.min(DATASIZE, fileContents.length - filePointer);
//                if (datalen < DATASIZE){
//                    lastPacket = true;
//                }
//
//                byte[] buf = new byte[HEADERSIZE + datalen] ; //256
//                DatagramPacket sendingPacket = new DatagramPacket(buf, buf.length);
//
//                //write something in header
//                buf[0] = intToByteArray(sequenceNumber); //is 4 bytes not 1
//                buf[1] = (lastPacket ? (byte)1 : 0);
//
//                // fill the rest of the packet with data
//                // hoe dan
//
//                // send packet
//                socket.send(sendingPacket);
//
//                //Update Window
//                LFS = LFS + 1;
//                sequenceNumber = (sequenceNumber + 1) % K;
//                System.out.println("Sent out packet with header" + buf[0]);
//
//                filePointer = datalen + filePointer;
//            }
//        }
//
//        while (true) {
//            byte[] buffer = new byte[256];
//            DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);
//
//            if(receivedPacket != null){
//                System.out.println("ACK, packet .. received" + receivedPacket);
//
//                if (receivedPacket)
//            }
//
//        }
//
//
//    }
//
//    private boolean inSendingWindow {
//        //filepointer
//        //fileContents.length
//        //LAR SWS
//
//    }
//
//    private byte[] intToByteArray(int i) {
//        byte [] resultBytArr = new byte[4]; // why 4?
//        resultBytArr[0] = (byte) (i >> 24);
//        resultBytArr[1] = (byte) (i >> 16);
//        resultBytArr[2] = (byte) (i >> 8);
//        resultBytArr[3] = (byte) (i);
//
//        return resultBytArr;
//    }
//
//    private int byteArraytoInt(byte[] b) {
//
//        return b[3]  & 0 * 0xFF | (b[2] & 0 * 0xFF) << 8 | (b[1] & 0 * 0xFF) << 16 |
//                (b[0] & 0 * 0xFF) << 24;
//
//    }
//
//
//}
