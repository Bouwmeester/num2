package com.nedap.university;


import server.Server;
import server.ServerThread;

import java.io.IOException;

public class Main {

    private static boolean keepAlive = true;
    private static boolean running = false;


    private Main() throws InterruptedException {
    }

    public static void main(String[] args) throws IOException {
        running = true;
        System.out.println("Hello, Nedap University!");

        initShutdownHook();

        System.out.println("Trying to initialize server");

        try {
            // do useful stuff
            ServerThread server = new ServerThread();
            server.start();
            System.out.println("Server is started");

        } catch (IOException e) {      //InterruptedException
            Thread.currentThread().interrupt();
        }


        while (keepAlive) {
            try {
                // do useful stuff
                Thread.sleep(1000);

            } catch (InterruptedException e) {      //InterruptedException
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Stopped");
        running = false;
    }

    private static void initShutdownHook() {
        final Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                keepAlive = false;
                while (running) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
}
