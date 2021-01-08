package com.tracker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker {

    private ServerSocket serverSocket;
    private static ArrayList<SeederModel> seedersList;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String receivedData;

    public Tracker(int port) {
        try {
            seedersList = new ArrayList();
            seedersList.add(new SeederModel(0, "111.1111.111", 234, 2342));
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
    private static class ClientHandler implements Runnable {
        private final Socket client;
        private String receivedData;

        // Constructor
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

                Integer clientHashCode = Integer.getInteger(receivedDataList.get(0));
                Integer clientNumber = Integer.getInteger(receivedDataList.get(1));
                String clientIp = receivedDataList.get(2);
                Integer clientPort = Integer.getInteger(receivedDataList.get(3));


    //          Checks if client is in list with seeders
                boolean containsHost = false;

                synchronized (seedersList) {

                    for (SeederModel element : seedersList) {
                        if (element.getSeederAppNumber() == clientNumber && element.getSeederIp() == clientIp && element.getSeederPort() == clientPort) {
                            containsHost = true;
                        }
                    }
//                  If not, add new object with client data to the list
                    if (containsHost) {
                        seedersList.add(new SeederModel(clientNumber, clientIp, clientPort, clientHashCode));
//                      Prints out clients data if it is new
                        for (String element : receivedDataList) {
                            System.out.println(element);
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
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


    public static void main(String[] args) {
        new Tracker(10000);
    }
}