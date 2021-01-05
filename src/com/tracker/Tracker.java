package com.tracker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Tracker {

    private ServerSocket serverSocket;
    private ArrayList hostsList;
    private BufferedReader reader;
    private String receivedData;

    public Tracker(int port) {
        try {

            hostsList = new ArrayList<HostModel>();

            serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();

            client.getOutputStream().write("Connection established\r\n".getBytes());

//            DataInputStream dIn = new DataInputStream((client.getInputStream()));

            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));

            receivedData = new String();

            System.out.println(reader.read());

//            StringBuffer strB = new StringBuffer(receivedData);
//            strB.append(reader.readLine());
//
//            receivedData = strB.toString();

            reader.close();

            System.out.println(receivedData);


//                int clientHashCode = Integer.getInteger(reader.readLine());
//                String clientName = reader.readLine();
//                String clientIp = reader.readLine();
//                int clientPort = Integer.getInteger(reader.readLine());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Tracker(44445);
    }
}