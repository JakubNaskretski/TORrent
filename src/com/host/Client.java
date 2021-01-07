package com.host;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;

public class Client {

//    TODO: If no host currently online inform about no seeders


    private ArrayList<File> filesList;

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter out;
    private BufferedReader in;
    private String receivedData, fileDir, hostingAddress;
    private HashMap<String, Integer> seedersMap;
    private boolean stop;
    private int seederPort, hostingPort;

    private String hostingFilePath, hostingFileName;

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
        getSeedersList();
    }

    private void getSeedersList(){

        try {
//          Send information about itself
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

    public void downloadFileFromSeeder(String fileDir, int seederPort) {
        setFileDir(fileDir);
        setSeederPort(seederPort);
        downloadFileThread.start();
    }


    public void seedFileAsHost(String hostingFilePath, String hostingFileName, int hostingPort, String hostingAddress) {
        setHostingFilePath(hostingFilePath);
        setHostingFileName(hostingFileName);
        setHostingPort(hostingPort);
        setHostingAddress(hostingAddress);
        sendFileThread.start();
    }


    Thread downloadFileThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
                ServerSocket ss = new ServerSocket(seederPort);
                do {
                    socket = ss.accept();

                    // public Socket accept() throws
                    // IOException listens for and accepts connections to this socket. This method blocks until it is connected.
                    System.out.println("Create a socket link");
                    DataInputStream inputStream = new DataInputStream(
                            new BufferedInputStream(socket.getInputStream()));

                    // Local save path, the file name will automatically inherit from the server side.
                    int bufferSize = 8192;
                    byte[] buf = new byte[bufferSize];
                    long passedlen = 0;
                    long len = 0;

                    // Get the file name
                    String file = fileDir + inputStream.readUTF();
                    DataOutputStream fileOut = new DataOutputStream(
                            new BufferedOutputStream(new FileOutputStream(file)));
                    len = inputStream.readLong();

                    System.out.println("The length of the file is:" + len + "\n");
                    System.out.println("Start receiving files!" + "\n");

                    while (true) {
                        int read = 0;
                        if (inputStream != null) {
                            read = inputStream.read(buf);
                        }
                        passedlen += read;
                        if (read == -1) {
                            break;
                        }
                        // The following progress bar is made for the prograssBar of the graphical interface. If you are typing a file, you may repeat the same percentage.
                        System.out.println("File Received" + (passedlen * 100 / len)
                                + "%\n");
                        fileOut.write(buf, 0, read);
                    }
                    System.out.println("Receive completed, file saved as" + file + "\n");

                    fileOut.close();
                } while (!stop);
            } catch (Exception e) {
                System.out.println("Receive Message Error" + "\n");
                e.printStackTrace();
                return;
            }
        }
    });


//    ==================


    Thread sendFileThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Socket s = null;
            try {
                s = new Socket(hostingAddress, hostingPort);

                // Select the file to transfer
                File fi = new File(hostingFilePath + hostingFileName);
                System.out.println("file length:" + (int) fi.length());

                DataInputStream fis = new DataInputStream(new FileInputStream(hostingFilePath + hostingFileName));
                DataOutputStream ps = new DataOutputStream(s.getOutputStream());
                ps.writeUTF(fi.getName());
                ps.flush();
                ps.writeLong((long) fi.length());
                ps.flush();

                int bufferSize = 8192;
                byte[] buf = new byte[bufferSize];

                while (true) {
                    int read = 0;
                    if (fis != null) {
                        read = fis.read(buf);
                    }

                    if (read == -1) {
                        break;
                    }
                    ps.write(buf, 0, read);
                }
                ps.flush();
                // Note that the socket link is closed, otherwise the client will wait for the server data to come over.
                // Until the socket times out, the data is incomplete.
                fis.close();
                ps.close();
                s.close();
                System.out.println("File Transfer Complete");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });


//    ==================




        public ArrayList<File> loadFilesToShare(){
        return null;
    }

    public void askSeedersForFilesList(HashMap<String, Integer> seedersMap) {
        for (Map.Entry<String, Integer> entry : seedersMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        }
    }

    public ArrayList<File> askSeederForFile(){

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

        return null;
    }


    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }

    public void setSeederPort(int seederPort) {
        this.seederPort = seederPort;
    }

    public void setHostingAddress(String hostingAddress) {
        this.hostingAddress = hostingAddress;
    }

    public void setHostingPort(int hostingPort) {
        this.hostingPort = hostingPort;
    }

    public void setHostingFilePath(String hostingFilePath) {
        this.hostingFilePath = hostingFilePath;
    }

    public void setHostingFileName(String hostingFileName) {
        this.hostingFileName = hostingFileName;
    }

    public static void main(String[] args) {
        new Client("0.0.0.0", 44445);
    }
}

