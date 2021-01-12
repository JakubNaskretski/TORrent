package com.client;

import com.client.view.ClientView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TestMain {

    public static void main(String[] args) {

        ClientView clientView1 = new ClientView();

        startNewClient(clientView1);

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
            clientView.getIpLabel().setText(client.getCurrentAppHostingIp());
            clientView.getPortLabel().setText(String.valueOf(client.getCurrentAppHostingPort()));

            clientView.setSeeders(client.getSeedersArray());
            clientView.createSeedersJPanelsArray();
            clientView.addSeedersJPanelListToLeftPanel();


            clientView.getReloadHostsList().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    client.loadFilesToShare();

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
                            clientView,
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




