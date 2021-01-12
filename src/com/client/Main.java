package com.client;

import com.client.view.ClientView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Main {
    public static void main(String[] args) {

        ClientView clientView1 = new ClientView();
        ClientView clientView2 = new ClientView();
        ClientView clientView3 = new ClientView();

        startNewClient(clientView1);
        startNewClient(clientView2);
        startNewClient(clientView3);


//        Thread t1 = new Thread(() -> {
//            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
//            Client client1 = new Client();
////            client1.connectWithTracker();
//
//            clientView1.getAppNumberLabel().setText(String.valueOf(client1.getCurrentAppNumber()));
//            clientView1.getIpLabel().setText(client1.getHostingIp());
//            clientView1.getPortLabel().setText(String.valueOf(client1.getCurrentAppHostingPort()));
//
//            clientView1.getReloadHostsList().addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    client1.connectWithTracker();
//                }
//            });
//
//        });
//
//        Thread t2 = new Thread(() -> {
//            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
//            Client client2 = new Client();
////            client2.connectWithTracker();
//
//            clientView2.getAppNumberLabel().setText(String.valueOf(client2.getCurrentAppNumber()));
//            clientView2.getIpLabel().setText(client2.getHostingIp());
//            clientView2.getPortLabel().setText(String.valueOf(client2.getCurrentAppHostingPort()));
//            clientView2.setSeeders(client2.getSeedersArray());
//            clientView2.createSeedersJPanelsArray();
//            clientView2.addSeedersJPanelListToLeftPanel();
//
//
//        });
//
//        Thread t3 = new Thread(() -> {
//            System.out.println("Starting Thread from main: "+Thread.currentThread().getName());
//            Client client3 = new Client();
//
//            clientView3.getAppNumberLabel().setText(String.valueOf(client3.getCurrentAppNumber()));
//            clientView3.getIpLabel().setText(client3.getHostingIp());
//            clientView3.getPortLabel().setText(String.valueOf(client3.getCurrentAppHostingPort()));
//            clientView3.setSeeders(client3.getSeedersArray());
//            clientView3.createSeedersJPanelsArray();
//            clientView3.addSeedersJPanelListToLeftPanel();
//
////            client2.askSeedersForFilesList();
////            client2.printSeedersFiles();
//        });
//
//        t1.start();
//        t2.start();
//        t3.start();


    }

    public void addSeedersToJPanel(ClientView clientView, Client client) {
        clientView.setSeeders(client.getSeedersArray());
        clientView.createSeedersJPanelsArray();
        clientView.addSeedersJPanelListToLeftPanel();
    }



    public static void startNewClient(ClientView clientView) {

        new Thread(() -> {
            System.out.println("Starting Thread from main: " + Thread.currentThread().getName());
            Client client = new Client();

            clientView.getAppNumberLabel().setText(String.valueOf(client.getCurrentAppNumber()));
            clientView.getIpLabel().setText(client.getHostingIp());
            clientView.getPortLabel().setText(String.valueOf(client.getCurrentAppHostingPort()));

            clientView.setSeeders(client.getSeedersArray());
            clientView.createSeedersJPanelsArray();
            clientView.addSeedersJPanelListToLeftPanel();


            clientView.getReloadHostsList().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.connectWithTracker();
                    client.askSeedersForFilesList();

                    clientView.setSeeders(client.getSeedersArray());

                    clientView.getRightFilesPanel().removeAll();
                    clientView.getCheckSumArea().setText("");

                    clientView.createSeedersJPanelsArray();
                    clientView.addSeedersJPanelListToLeftPanel();

                }
            });


            clientView.getDownloadFileButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

//                    TODO: validation of input
//                  Client application asks the seeder who owns currently choosen file for download file
                    client.askSeederForFileToDownload(
//                          Get currently choosen (clicked) file name
                            clientView.getCurrentlyChoosenFileName(),
//                          Get from seeders list (from view), currently selected seeder ip
                            clientView.getSeeders().get(clientView.getCurrentlyChosenSeeder()).getSeederIp(),
//                          And port
                            clientView.getSeeders().get(clientView.getCurrentlyChosenSeeder()).getSeederPort());

                }
            });


            clientView.getSendFileButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setCurrentDirectory(new File(client.getHostingFilesFolder()));
                    int returnVal = chooser.showOpenDialog(clientView.getFrame());
                    if(returnVal == JFileChooser.APPROVE_OPTION) {

                        client.sendFileToSeeder(
//                              Get chosen file DIR
                                chooser.getSelectedFile().getAbsolutePath(),
//                              Get chosen file name
                                chooser.getSelectedFile().getName(),
//                              Get from seeders list (from view), currently selected seeder ip
                                clientView.getSeeders().get(clientView.getCurrentlyChosenSeeder()).getSeederIp(),
//                              And port
                                clientView.getSeeders().get(clientView.getCurrentlyChosenSeeder()).getSeederPort()
                        );
                    }
                }
            });

        }).start();
    }
}
