package com.nedap.university;

public class Packet {

    public final static int DATASIZE = 256;
    public final static int HEADERSIZE = 4;
    public final static int SIZE = HEADERSIZE + DATASIZE;

    private boolean lastPacket;
    private int seqNr;
    private boolean isACK;
    private int ackedPacket;
    private byte[] data;

    public Packet(boolean lastPacket, int seqNr, boolean isACK, int ackedPacket) {
        this.lastPacket = lastPacket;
        this.seqNr = seqNr;
        this.isACK = isACK;
        this.ackedPacket = ackedPacket;
        this.data = new byte[256];
    }

    public Packet(boolean lastPacket, int seqNr, boolean isACK, int ackedPacket, byte[] data) {
        this.lastPacket = lastPacket;
        this.seqNr = seqNr;
        this.isACK = isACK;
        this.ackedPacket = ackedPacket;
        this.data = data;
    }

    public byte[] getBytes(){
        byte[] dataBytes = new byte[HEADERSIZE + DATASIZE];
        dataBytes[0] = (byte)(lastPacket ? 1 : 0);
        dataBytes[1] = (byte) seqNr;
        dataBytes[2] = (byte)(isACK ? 1 : 0);
        dataBytes[3] = (byte) ackedPacket;
        System.arraycopy(data, 0, dataBytes, HEADERSIZE, data.length);
        return dataBytes;
    }

    public void setLastPacket(boolean lastPacket) {
        this.lastPacket = lastPacket;
    }

}
