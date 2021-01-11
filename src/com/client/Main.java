package com.client;

import com.client.view.ClientView;

public class Main {
    public static void main(String[] args) {
        ClientView clientView1 = new ClientView();
        ClientView clientView2 = new ClientView();
        ClientView clientView3 = new ClientView();

        Thread t1 = new Thread(() -> {
            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
            Client client1 = new Client();
//            client1.connectWithTracker();

            clientView1.getAppNumberLabel().setText(String.valueOf(client1.getCurrentAppNumber()));
            clientView1.getIpLabel().setText(client1.getHostingIp());
            clientView1.getPortLabel().setText(String.valueOf(client1.getCurrentAppHostingPort()));
            clientView1.setSeeders(client1.getSeedersArray());
            clientView1.createSeedersJPanelsArray();
            clientView1.addSeedersJPanelListToLeftPanel();

        });

        Thread t2 = new Thread(() -> {
            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
            Client client2 = new Client();
//            client2.connectWithTracker();

            clientView2.getAppNumberLabel().setText(String.valueOf(client2.getCurrentAppNumber()));
            clientView2.getIpLabel().setText(client2.getHostingIp());
            clientView2.getPortLabel().setText(String.valueOf(client2.getCurrentAppHostingPort()));
            clientView2.setSeeders(client2.getSeedersArray());
            clientView2.createSeedersJPanelsArray();
            clientView2.addSeedersJPanelListToLeftPanel();


        });

        Thread t3 = new Thread(() -> {
            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
            Client client3 = new Client();

            clientView3.getAppNumberLabel().setText(String.valueOf(client3.getCurrentAppNumber()));
            clientView3.getIpLabel().setText(client3.getHostingIp());
            clientView3.getPortLabel().setText(String.valueOf(client3.getCurrentAppHostingPort()));
            clientView3.setSeeders(client3.getSeedersArray());
            clientView3.createSeedersJPanelsArray();
            clientView3.addSeedersJPanelListToLeftPanel();

//            client2.askSeedersForFilesList();
//            client2.printSeedersFiles();
        });

        t1.start();
        t2.start();
        t3.start();








    }

    public void addSeedersToJPanel() {

    }

}
