package com.tracker;

import com.SeederModel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Tracker {

    private ServerSocket serverSocket;
    private ArrayList<SeederModel> seedersList = new ArrayList();

//  Client number for each connected app
    private static int applicationNumber = 0;
//  Client port for each connecting app
    private static int hostingPort = 10000;

//  Locker of the statics within all classes
static ReentrantLock counterLock = new ReentrantLock(true);

    public Tracker(int port) {

            try {
                serverSocket = new ServerSocket(port);

                TrackerView trackerView = new TrackerView();
                trackerView.getIpLabel().setText("Ip: "+serverSocket.getInetAddress().getHostAddress());
                trackerView.getPortLabel().setText("Port: "+port);

//              Run infinite loop for getting client request
                while (true) {

//              Accept socket
                    Socket client = serverSocket.accept();

//              Create a new thread object
                    ClientHandler clientSocket = new ClientHandler(client);

//              Thread to handle request
                    new Thread(clientSocket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


//  ClientHandler class
    private  class ClientHandler implements Runnable {
        private final Socket client;
        private String receivedData;

//      Constructor
        public ClientHandler(Socket socket) {
            this.client = socket;
        }

        public void run() {
            BufferedWriter writer = null;
            BufferedReader reader = null;

            try {

                writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));
                reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));

                receivedData = new String();

                StringBuffer strB = new StringBuffer(receivedData);

//              Read lines of data about the connecting client from client
                strB.append(reader.readLine());
                strB.append("\n");
                strB.append(reader.readLine());
                strB.append("\n");
                strB.append(reader.readLine());
                strB.append("\n");
                strB.append(reader.readLine());
                strB.append("\n");

                receivedData = strB.toString();

//              Splits input to a list
                List<String> receivedDataList = splitReceivedWelcomeData(receivedData);

                Integer clientNumber = Integer.parseInt(receivedDataList.get(0));
                String clientIp = receivedDataList.get(1);
                Integer clientHashCode = Integer.parseInt(receivedDataList.get(3));
                Integer clientPort = null;

                if (clientNumber == 0) {
                    clientNumber = incrementAppNo();
                    clientPort = 10000+clientNumber;

    //              Sends to app its new number and new port
                    writer.write(String.valueOf(clientNumber));
                    writer.append("\n");
                    writer.flush();

                } else if (clientNumber != 0) {
                    clientPort = Integer.parseInt(receivedDataList.get(2));
                }

//              Retrieves list of seeders (before appending new one to the list)
                for (SeederModel element : seedersList) {
                    writer.append(String.valueOf(element.getSeederAppNumber()));
                    writer.append("---");
                    writer.append(String.valueOf(element.getSeederIp()));
                    writer.append("---");
                    writer.append(String.valueOf(element.getSeederPort()));
                    writer.append("---");
                    writer.append(String.valueOf(element.getSeederHash()));
                    writer.append("\n");

                    System.out.println(element.toString());
                }
                writer.flush();
                writer.close();


//                receivedData = strB.toString();

//              Checks if client is in list with seeders
                boolean containsHost = false;

//                synchronized (seedersList) {

                    for (SeederModel element : seedersList) {
                        if (element.getSeederAppNumber().equals(clientNumber) && element.getSeederIp().equals(clientIp) && element.getSeederPort().equals(clientPort)) {
                            containsHost = true;
//                          If found in list no need to continue searching
                            continue;
                        }
                    }
//                  If not, add new object with client data to the list
                    if (containsHost == false) {


                        System.out.println(clientNumber);
                        System.out.println(clientIp);
                        System.out.println(clientPort);
                        System.out.println(clientHashCode);

                        System.out.println("random");


                        seedersList.add(new SeederModel(clientNumber, clientIp, clientPort, clientHashCode));
//                      Prints out clients data if it is new
                        for (String element : receivedDataList) {
                            System.out.println(element);
                        }
                    }
//                }

//                try {
//                    if (writer != null) {
//                        writer.close();
//                    }
//                    if (reader != null) {
//                        reader.close();
//                        System.out.println("Dissconnecting from: "+client.getPort());
//                        client.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
                        System.out.println("Dissconnecting from: "+client.getPort());
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//          Method to split received data to lsit by new lines
            public List<String> splitReceivedWelcomeData(String welcomeData){
            return Arrays.asList(welcomeData.split("\\s*\\n\\s*"));
        }
    }


    //  Increments App counter and Port number
    static int incrementAppNo(){
        counterLock.lock();

        int tmpAppNo = 0;

        try{

            applicationNumber++;

            tmpAppNo = applicationNumber;

            System.out.println(Thread.currentThread().getName() + ": " + applicationNumber);

        }finally{
            counterLock.unlock();
        }
        return tmpAppNo;
    }


    public ArrayList<SeederModel> getSeedersList() {
        return seedersList;
    }

    public static void main(String[] args) {
//        new TrackerView();
//        new Tracker(10000);
    }
}