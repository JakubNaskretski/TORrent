package com.tracker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker {

    private ServerSocket serverSocket;
    private ArrayList<SeederModel> seedersList = new ArrayList();
    private BufferedReader reader;
    private BufferedWriter writer;
    private String receivedData;

    public Tracker(int port) {
        try {
//            seedersList.add(new SeederModel(0, "111.1111.111", 234, 2342));
            serverSocket = new ServerSocket(port);

//          Run infinite loop for getting client request
            while (true) {

//              Accept socket
                Socket client = serverSocket.accept();

//              Create a new thread object
                ClientHandler clientSocket = new ClientHandler(client);

//              Thread to handle request
                new Thread(clientSocket).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                }
                catch (IOException e) {
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


//              Retrieves list of seeders (before appending new one to the list)
                for (SeederModel element : seedersList) {
                    writer.append(String.valueOf(element.getSeederAppNumber()));
                    writer.append("-");
                    writer.append(String.valueOf(element.getSeederIp()));
                    writer.append("-");
                    writer.append(String.valueOf(element.getSeederPort()));
                    writer.append("-");
                    writer.append(String.valueOf(element.getSeederHash()));
                    writer.append("\n");
                }
                writer.flush();
                writer.close();


                receivedData = strB.toString();

//            Splits input to a list
                List<String> receivedDataList = splitReceivedWelcomeData(receivedData);

                Integer clientNumber = Integer.parseInt(receivedDataList.get(0));
                String clientIp = receivedDataList.get(1);
                Integer clientPort = Integer.parseInt(receivedDataList.get(2));
                Integer clientHashCode = Integer.parseInt(receivedDataList.get(3));


//              Checks if client is in list with seeders
                boolean containsHost = false;

//                synchronized (seedersList) {

                    for (SeederModel element : seedersList) {
                        if (element.getSeederAppNumber().equals(clientNumber) && element.getSeederIp().equals(clientIp) && element.getSeederPort().equals(clientPort)) {
                            containsHost = true;
//                          If found in list no need to continue searching
                            break;
                        }
                    }
//                  If not, add new object with client data to the list
                    if (containsHost == false) {
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

    public ArrayList<SeederModel> getSeedersList() {
        return seedersList;
    }

    public static void main(String[] args) {
        new Tracker(10000);
    }
}