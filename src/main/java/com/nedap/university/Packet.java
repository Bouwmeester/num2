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

    public Packet(byte[] receivedData) {
        lastPacket = receivedData[0] == (byte)1;
        seqNr = receivedData[1];
        isACK = receivedData[2] == (byte)1;
        ackedPacket = receivedData[3];

        data = new byte[DATASIZE];
        System.arraycopy(receivedData, HEADERSIZE, data, 0, DATASIZE);
    }

    public Packet(boolean lastPacket, int seqNr, boolean isACK, int ackedPacket) {
        this.lastPacket = lastPacket;
        this.seqNr = seqNr;
        this.isACK = isACK;
        this.ackedPacket = ackedPacket;
        this.data = new byte[DATASIZE];
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

    public boolean isLastPacket() {
        return this.lastPacket;
    }

    public int getSeqNr() {
        return this.seqNr;
    }

    public boolean isACK() {
        return isACK;
    }

    public byte[] getData() {
        return data;
    }

}
