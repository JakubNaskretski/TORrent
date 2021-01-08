package com.tracker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tracker {

    private ServerSocket serverSocket;
    private ArrayList<SeederModel> seedersList;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String receivedData;

    public Tracker(int port) {
        try {

            seedersList = new ArrayList();

            seedersList.add(new SeederModel(0, "111.1111.111", 234, 2342));

            serverSocket = new ServerSocket(port);
            Socket client = serverSocket.accept();

            writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));
            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));

            receivedData = new String();

            StringBuffer strB = new StringBuffer(receivedData);

//            String currentLine;
//            while ((currentLine = reader.readLine())!= null) {
//                strB.append(currentLine);
//                strB.append("\n");
//            }

//          Read data about Client
            strB.append(reader.readLine());
            strB.append("\n");
            strB.append(reader.readLine());
            strB.append("\n");
            strB.append(reader.readLine());
            strB.append("\n");
            strB.append(reader.readLine());
            strB.append("\n");


//          Retrieves list of seeders (before appending new one to the list)
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

            Integer lecherHashCode = Integer.getInteger(receivedDataList.get(0));
            Integer lecherNumber = Integer.getInteger(receivedDataList.get(1));
            String lecherIp = receivedDataList.get(2);
            Integer lecherPort = Integer.getInteger(receivedDataList.get(3));


//          Checks if client is in list
            boolean containsHost = false;
            for (SeederModel element : seedersList) {
                if (element.getSeederAppNumber() == lecherNumber && element.getSeederIp() == lecherIp) {
                    containsHost = true;
                }
            }
            if (containsHost) {
                seedersList.add(new SeederModel(lecherNumber, lecherIp, lecherPort, lecherHashCode));
            }
            for (String element : receivedDataList) {
                System.out.println(element);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> splitReceivedWelcomeData(String welcomeData){
        return Arrays.asList(welcomeData.split("\\s*\\n\\s*"));
    }


    public static void main(String[] args) {


        new Tracker(10000);

        
    }
}