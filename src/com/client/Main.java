package com.client;

import com.client.view.ClientView;

public class Main {
    public static void main(String[] args) {
        ClientView clientView1 = new ClientView();
        ClientView clientView2 = new ClientView();

        Thread t1 = new Thread(() -> {
            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
            Client client1 = new Client();
//            client1.connectWithTracker();

            clientView1.getAppNumberLabel().setText(String.valueOf(client1.getCurrentAppNumber()));
            clientView1.getIpLabel().setText(client1.getHostingIp());
            clientView1.getPortLabel().setText(String.valueOf(client1.getCurrentAppHostingPort()));

//            client1.loadFilesToShare();
//            client1.startSocketForSeeding();
//            client1.printSeedersFiles();
        });

        Thread t2 = new Thread(() -> {
            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
            Client client2 = new Client();
//            client2.connectWithTracker();

            clientView2.getAppNumberLabel().setText(String.valueOf(client2.getCurrentAppNumber()));
            clientView2.getIpLabel().setText(client2.getHostingIp());
            clientView2.getPortLabel().setText(String.valueOf(client2.getCurrentAppHostingPort()));

//            client2.askSeedersForFilesList();
//            client2.printSeedersFiles();
        });

        t1.start();
        t2.start();








    }

    public void addSeedersToJPanel() {

    }

}
