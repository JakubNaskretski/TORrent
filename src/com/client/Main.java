package com.client;

import com.client.view.ClientView;

public class Main {
    public static void main(String[] args) {
        ClientView clientView = new ClientView();

        Client client = new Client();
        client.connectWithTracker();
        Client client1 = new Client();
        client1.connectWithTracker();

        clientView.getIpLabel().setText(client.getHostingIp());
        clientView.getPortLabel().setText(String.valueOf(client.getHostingPort()));


    }

    public void addSeedersToJPanel() {

    }

}
