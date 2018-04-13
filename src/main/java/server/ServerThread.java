package server;

import general.Protocol;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
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
        //System.out.println("Working Directory = " +
               // System.getProperty("user.dir"));
        try {
            in = new BufferedReader(new FileReader("/home/pi/myDoc/freestyler.txt"));
            //in = new BufferedReader(new FileReader("/home/pi/myDoc/textFile.txt"));
            //in = new BufferedReader(new FileReader("textFile.txt"));
        } catch (FileNotFoundException e) {
            System.err.println(" Couldn't find file");
        }

    }

    public void run() {

        while (moreLines) {
            try {
                byte[] buf = new byte[256]; //256

                //receive packet
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

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
                    //File folder = new File(" home/Documents/NedapUniversity");
                    File[] listOfFiles = folder.listFiles();
                    //System.out.println(listOfFiles + "list of files");
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
                    in = new BufferedReader(new FileReader("/home/pi/myDoc/fileName"));
                    //in = new BufferedReader(new FileReader("/home/pi/myDoc/fileName"));
                    String fileInfo = " ";
                    fileInfo = getNextQuote();
                    buf = fileInfo.getBytes();
                    InetAddress address = packet.getAddress();
                    DatagramPacket filePacket = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(filePacket);




                } else {
                    System.out.println("List not found");
                }

                //figure out response
                String dString = null;
                if (in == null) {
                    dString = new Date().toString();

                } else {
                    dString = getNextQuote();
                }

                buf = dString.getBytes();

                // send response to the client at " address"  and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();

                moreLines = false;
            }

        }

        socket.close();

    }

    protected String getNextQuote() {
        String returnValue = " ";

        try {
            if ((returnValue = in.readLine()) == null) {
                in.reset();
                moreLines = false;

                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            //returnValue = "IoException occurred in the server";
        }
     return returnValue;

}

}
