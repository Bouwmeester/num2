package server;

import com.nedap.university.Packet;

import java.net.DatagramPacket;

public class Window extends Thread {

    public static final int WINDOWSIZE = 5;
    private byte[][] packets;
    private int sequenceNumber = -1;
    private Send send;

    public Window(Send send, byte[][] packets){
        this.packets = packets;
        this.send = send;

    }

    public void run(){

        for (byte[] somePacket : packets) {
            Packet partyPacket = new Packet(false, ++sequenceNumber, false, 0, somePacket);

            // packet is ACK?
            // list un ACK packets
            // ACK --> seq nr --> remove from list
            // list empty --> window is done

            // hoeveelste window

            

            if (sequenceNumber + 1 == chunks) {
                partyPacket.setLastPacket(true);
                lastPacket = true;
            }

            ACK = false;

            while(!ACK){
                send.sendPacket(partyPacket);

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("Interrupted");
                }
            }


        }



    }

    public boolean isDone(){

    }

}
