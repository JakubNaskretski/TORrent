package com.host;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {

//    TODO: If no host currently online inform about no seeders



    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter out;
    private BufferedReader in;
    private String receivedData;
    private HashMap<String, Integer> seedersMap;

    public Client(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            seedersMap = new HashMap<>();

            outputStream = socket.getOutputStream();
            out= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));
//                    new FileOutputStream(fileDir), "UTF8"));
            inputStream = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));
            System.out.println("Nawiązano połączenie!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        talk();
    }

    private void talk(){

        try {
            out.append(socket.hashCode()+"\n");
            out.append("Example name\n");
            out.append("Example ip\n");
            out.append(socket.getPort()+"\n");
            out.flush();

//            out.close();


//            Read list of seeders
            receivedData = new String();

            StringBuffer strB = new StringBuffer(receivedData);

            String currentLine;

//            strB.append(in.readLine());

            while ((currentLine = in.readLine())!= null) {
                strB.append(currentLine);
                strB.append("\n");
            }

            receivedData = strB.toString();

//            Splits input to a list
            List<String> receivedSeedersList = splitReceivedSeedersData(receivedData);

            for (String element : receivedSeedersList) {

                List<String> tmp = splitSeedersListByIpPort(element);

                seedersMap.put(tmp.get(0), Integer.valueOf(tmp.get(1)));

            }

            // using for-each loop for iteration over Map.entrySet()
            for (Map.Entry<String, Integer> entry : seedersMap.entrySet())
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " + entry.getValue());

//        out.close();
//            in.close();
            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public List<String> splitReceivedSeedersData(String seedersData){

        return Arrays.asList(seedersData.split("\\s*\\n\\s*"));

    }

    public List<String> splitSeedersListByIpPort(String seedersList){

        return Arrays.asList(seedersList.split("\\s*-\\s*"));

    }


    public static void main(String[] args) {
        new Client("0.0.0.0", 44445);
    }
}