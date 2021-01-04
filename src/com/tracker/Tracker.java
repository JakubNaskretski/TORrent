package com.tracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Tracker {

    private ServerSocket serverSocket;
    private ArrayList hostsList;

    public Tracker(int port) {
        try {

            hostsList = new ArrayList<HostModel>();

            serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();

            client.getOutputStream().write("Connection established\r\n".getBytes());

            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

            System.out.println(reader.readLine());

            try {
//          Gets data from Client about itself
                int clientHashCode = Integer.getInteger(reader.readLine());
                String clientName = reader.readLine();
                String clientIp = reader.readLine();
                int clientPort = Integer.getInteger(reader.readLine());

                HostModel hostModel = new HostModel(clientName, clientIp, clientPort, clientHashCode);


                if (!hostsList.contains(hostModel)) {
                    hostsList.add(hostModel);
                } else {
                    System.out.println("Host już jest na liście");
                }
            } catch (NullPointerException e) {
                System.out.println("Didnt get data");
            }

//            client.getOutputStream().write(hostsList.get(0).toString().getBytes());

            client.getOutputStream().write("test".getBytes());


            reader.close();
//            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Tracker(44445);
    }
}