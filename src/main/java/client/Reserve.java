//package client;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketTimeoutException;
//
//public class Reserve {
//
//    package client;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketTimeoutException;
//
//
//    public class Client {
//
//        public static void main(String[] args) throws IOException {
//
////        if (args.length != 1) {
////            System.out.println("Usage: java Client ieh");
////            return;
////        }
//
//            // get a datagram socket
//            DatagramSocket socket = new DatagramSocket();
//
//            // send request
//            byte[] buf = new byte[25];
//            InetAddress address = InetAddress.getByName("192.168.1.1");
//            //InetAddress address = InetAddress.getByName("localhost");
//            DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
//            //socket.send(packet);
//
//            // get a response
//            boolean keepGoing = true;
//            String received = " ";
//            socket.setSoTimeout(1000);
//
//            while (keepGoing) {
//
//                try {
//                    socket.send(sendPacket);
//                    DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
//                    socket.receive(receivedPacket);
//                    String recv = new String (receivedPacket.getData());
//
//                    //display response
//                    received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
//
//                    if (recv == null || recv.length() == 0){
//                        keepGoing = false;
//                    } else {
//                        received = received + recv;
//                    }
//                } catch (SocketTimeoutException ste) {
//                    if (received.length() > 0) {
//                        System.out.println(" Received " + received);
//                    } else {
//                        System.out.println(" Error: Connection time out");
//                    }
//                }
//                //System.out.println(" Received " + received);
//            }
//            socket.close();
//        }
//    }


//        String datagramSocketInput = " ";
//input commando correct
//        BufferedReader commandoIn = new BufferedReader(new InputStreamReader(System.in));
// out correct point to print it?
// buffered reader best thing for input?
// change path to piepie


//            try {
//                while ((datagramSocketInput = commandoIn.readLine()) != null) {
//                    String[] clientInputs = datagramSocketInput.split(Protocol.General.DELIMITER1);
//                    List<String> fileList = new ArrayList<String>();
//                    if (clientInputs[0].equals(Protocol.Client.LIST)) {
//                        File folder = new File(" /home/pi");
//                        //File folder = new File(" home/Documents/NedapUniversity");
//                        File[] listOfFiles = folder.listFiles();
//
//                        for (File file : listOfFiles) {
//                            if (file.isFile()) {
//                                fileList.add(file.getName());
//
//                            }
//                            System.out.println("List of available documents " + fileList);
//                        }
//
//                    } else if (clientInputs[0].equals(Protocol.Client.DOWNLOAD)){
//                        // doe download dingen
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


//print geen output meer dus iets meer send receive gaat fout

// get a datagram socket
//        DatagramSocket socket = new DatagramSocket();
//
//                // send request
//                byte[] buf = new byte[256];
//                InetAddress address = InetAddress.getByName("192.168.1.1");
//                //InetAddress address = InetAddress.getByName("localhost");
//                DatagramPacket sendPacket = new DatagramPacket(buf, buf.length, address, 5454);
//                //socket.send(packet);
//
//                // get a response
//                boolean keepGoing = true;
//                String received = " ";
//                socket.setSoTimeout(10);
//
//                // keeps waiting for response of client before sending new packet
//
//                // new
//                byte[] fileContents = new byte[256];
//
//                while (keepGoing) {
//                // wat wil je doen
//                // stuur naar server
//                // vanuit gaan dat je antwoord krijgt, tot laatste
//                // terug naar het begin
//
//                try //nieuwe thread  {
//                //                    System.out.println("Please enter LIST, to get a list of documents ");
////                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
//                //altijd luisteren op invoer tot laatste packet (terug naar begin)
//                }
//
//                try {
//                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
//                socket.receive(receivedPacket);
//
//                int numberOfPacketsReceived = 0;
//
//                if (receivedPacket != null) {
//                numberOfPacketsReceived = numberOfPacketsReceived + 1;
//                System.out.println("Received packet," + receivedPacket.getLength());
//                System.out.println("Sendpacket" + sendPacket);
//                socket.send(sendPacket);
//
//                //display response
//                received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
//                System.out.println(" Received " + received);
//
//                System.out.println("Number of packets received " + numberOfPacketsReceived);
//                } else {
//                try {
//                Thread.sleep(10);
//                } catch (InterruptedException e) {
//                e.printStackTrace();
//                keepGoing = false;
//                }
//                }
//
//                //          try {
//
//                // Packet wordt niet meer goed verstuurd
//                //String listInput = " ";
//
////                try {
////                    System.out.println("Please enter LIST, to get a list of documents ");
////                    BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
////                    listInput = clientInput.readLine();
////                    sendPacket.setData(listInput.getBytes());
////                    //System.out.println(listInput);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////
////                int numberOfPacketsReceived = 0;
////                //byte[] storedPackets = new byte[];
////                //byte[] fileContents = new byte[];
////
////                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
////                socket.receive(receivedPacket);
//
//
//
////                //Old
////                // Get user input, LIST
////                System.out.println("Please enter LIST, to get a list of documents ");
////                BufferedReader clientInput = new BufferedReader(new InputStreamReader(System.in));
////                String listInput = " ";
////
////                try {
////                    listInput = clientInput.readLine();
////                    sendPacket.setData(listInput.getBytes());
////                    //System.out.println(listInput);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
//
//
////                socket.send(sendPacket);
////                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
////                socket.receive(receivedPacket);
//                //String recv = new String (receivedPacket.getData());
//
//                //display response
////                received = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
////                System.out.println(" Received " + received);
//
//
//
//                } catch (SocketTimeoutException ste) {
//                if (received.length() > 0) {
//                //System.out.println(" Received " + received);
//                } else {
//                System.out.println(" Error: Connection time out");
//                }
//                }
//                //System.out.println(" Received " + received);
//                }
//
//                socket.close();
//                }
//                }
//}
