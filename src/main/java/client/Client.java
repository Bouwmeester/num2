package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;


public class Client extends Thread {

    private DatagramSocket socket = new DatagramSocket();
    protected int port = 5454 ;

    public Client() throws IOException {
        socket = new DatagramSocket(port);
    }



    public void run() {
        boolean sending = true;

        while(sending) {
            //sending to server
            System.out.println("Please enter LIST, to get a list of documents and DOWNLOAD-(documentname.txt) to Download ");
            try {
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
                String listInput = new String (clientInput.readLine());

                //DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[256];
                InetAddress address = InetAddress.getByName("192.168.1.1");
                //InetAddress address = InetAddress.getByName("localhost");
                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, port);
                sendPacket.setData(listInput.getBytes());
                socket.send(sendPacket);
                System.out.println("Listinput " + listInput);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    public void listen () {
        //receiving from server
        boolean receiving = true;
        int receivedPackets = 0;

        while (receiving) {
            System.out.println("Receving...");
            try {
                //DatagramSocket socket = new DatagramSocket();
                byte[] buf = new byte[256];
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                socket.receive(receivedPacket);
                //System.out.println("Receive packet created ");

                if (receivedPacket != null) {
                    receivedPackets = receivedPackets + 1;
                    String received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                    System.out.println(" Received " + received);
                    System.out.println("Number of packets received  " + receivedPackets);

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
        //         wat wil je doen
//                // stuur naar server
//                // vanuit gaan dat je antwoord krijgt, tot laatste
//                // terug naar het begin



        Client client = new Client();
        //runs run method, sending
        client.start();

        // receive input
        client.listen();







    }


    }


