package com.client;

import com.tracker.SeederModel;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;

public class Client {

//    TODO: If no host currently online inform about no seeders

//    1. Class connects after creating with tracker. Gives its Ip and Port
//    2. Tracker returns list of other Seeders
//    3. Class connects with each class asking for possible to download files
//    4. Class can chose file to download it from one host (if only one has it)
//    5. Class can download parts of the file from different hosts (if file is split for different hosts)
//    6. Class can send files to a host (Starting connection from sender)
//    7. If downloading file was interrupted, there should be possibility to continue sending from the part where it has stopped


    private ArrayList<File> filesList;

//  Default app number increasing for each instance of app created
    private static int applicationNumber = 0;

//  Default app hosting port
    private static int hostingPort = 10000;

//  default ip
    private String hostingIp = "0.0.0.0";

//  Tracker ip
//    TODO: Change for correct data
    private String trackerIp = "0.0.0.0";

//  Tracker port number
    private int trackerPort = 10000;

//  Directory to the file with files of the class
    private String hostingFilesFolder;

//  File to send name
    private String fileToSendName;

//  Verify if connection with tracker was successful
    private boolean connectedToTracker = false;

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter out;
    private BufferedReader in;
    private String receivedData, dirToSaveFile;
    private ArrayList<SeederModel> seedersArray;
    private boolean stop;
    private int receiverPort;

    public Client(){
//      After creating instance of a class, give it new number and port
        this.applicationNumber++;
        this.hostingPort++;

//      Creates directory to store host files
        this.hostingFilesFolder = "D:\\\\TORrent_"+applicationNumber+"\\";

//      Creates HashMap for storing seeders list
        seedersArray = new ArrayList<SeederModel>();

//      Tries to connect with the tracker in order to get information about seeders
        connectWithTracker();

    };

    public void connectWithTracker() {
        try {
//          Creates socket to connect with tracker
            socket = new Socket(trackerIp, trackerPort);

//          Creates output stream buffer for writing data to tracker
            outputStream = socket.getOutputStream();
            out= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//          Creates input stream buffer for getting data from the tracker
            inputStream = socket.getInputStream();
            in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

//          Prints out information if successfully connected with tracker
            System.out.println("Connection established");

//          Set up connection flag
            connectedToTracker = true;

        } catch (ConnectException e) {
            System.out.println("Could not connect to the Tracker");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//      Calls method responsible for communication with tracker
        getSeedersList();
    };


    private void getSeedersList(){

        if (connectedToTracker) {
            try {
//          Send information about itself in order to add new seeder to the list
                out.append(applicationNumber + "\n");
                out.append(hostingIp + "\n");
                out.append(hostingPort + "\n");
                out.append(hashCode() + "\n");
                out.flush();

//          Read list of seeders
                receivedData = new String();
                StringBuffer strB = new StringBuffer(receivedData);
                String currentLine;

//          While output stream on tracker open and sending data, read data
                while ((currentLine = in.readLine()) != null) {
                    strB.append(currentLine);
                    strB.append("\n");
                }

                receivedData = strB.toString();

//          Splits received seeders data into a list
                List<String> receivedSeedersList = splitReceivedSeedersData(receivedData);

//          For elements in list, split list and add seeders information into the list of SeederModels
                for (String element : receivedSeedersList) {
//              Creates tmp list containing base elements of SeederModel
                    List<String> tmp = splitSeedersListByIpPort(element);

//              Writes SeederModel base elements into the tmp variables
                    Integer tmpAppNo = Integer.valueOf(tmp.get(0));
                    String tmpHostIp = tmp.get(1);
                    Integer tmpHostPort = Integer.valueOf(tmp.get(2));
                    Integer tmpHostHash = Integer.valueOf(tmp.get(3));

                    //              Checks if client is in list with seeders
                    boolean containsHost = false;

                    for (SeederModel seederModel : seedersArray) {
                        if (seederModel.getSeederAppNumber().equals(tmpAppNo) && seederModel.getSeederIp().equals(tmpHostIp) && seederModel.getSeederPort().equals(tmpHostPort)) {
                                containsHost = true;
//                          If found in list no need to continue searching
                                break;
                        }
                    }

//                  If not, add new object with seeder data to the list
                    if (containsHost == false) {
//                      If element is not the same as current app
                        if(!tmpAppNo.equals(Integer.valueOf(applicationNumber))) {
//              Creates SeederModel as a store of possible to connect seeders and places them into array
                            seedersArray.add(new SeederModel(tmpAppNo, tmpHostIp, tmpHostPort, tmpHostHash));
                        }
                    }
                }

//          Print out content of the seeders array - control
                System.out.println("Printing from "+applicationNumber);
                for (SeederModel seederModel : seedersArray) {
                    System.out.println(seederModel.toString());
                }

//          Closes streams and connection
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Cannot get seeders list without connection to tracker");
        }
    }

//  Splits received data by new lines into the List
    public List<String> splitReceivedSeedersData(String seedersData){
        return Arrays.asList(seedersData.split("\\s*\\n\\s*"));
    }

//  Splits received data in the list into the new list by "-"
    public List<String> splitSeedersListByIpPort(String seedersList){
        return Arrays.asList(seedersList.split("\\s*-\\s*"));
    }

//  Downloads data from seeder
    public void downloadFileFromSeeder(String dirToSaveFile, int receiverPort) {
//      Sets directory to save received file
        setDirToSaveFile(dirToSaveFile);
//      Sets port of the seeder
        setReceiverPort(receiverPort);
//      Starts downloading method
        downloadFileThread.start();
    }


//  Sends file
    public void seedFileAsHost(String fileToSendName) {
//      Sets name of the file to send
        setFileToSendName(fileToSendName);
//      Starts sending file meth od
        sendFileThread.start();
    }


//  Creates downloading file thread for later start
    Thread downloadFileThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Socket socket = null;
            try {
//              Creates socket to receive connection from file sender
                ServerSocket ss = new ServerSocket(receiverPort);
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
                    String file = dirToSaveFile + inputStream.readUTF();
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

//  Creates sending file thread for later start
    Thread sendFileThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Socket s = null;
            try {
//              Creates socket to send file
                s = new Socket(hostingIp, hostingPort);

                // Select the file to transfer
                File fi = new File(hostingFilesFolder + fileToSendName);
                System.out.println("file length:" + (int) fi.length());

                DataInputStream fis = new DataInputStream(new FileInputStream(hostingFilesFolder + fileToSendName));
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


//  Loads files which may be send
    public ArrayList<File> loadFilesToShare(){
    return null;
    }


//  Asks each seeder in hash map for list of files to download
    public void askSeedersForFilesList(HashMap<String, Integer> seedersMap) {
        for (Map.Entry<String, Integer> entry : seedersMap.entrySet()) {
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue());
        }
    }


//  TODO: Verify if it is needed to create socket from the beginning for each connection
//  Makes connection with particular seeder
    public void connectWithSeeder() {
        try {
//      Creates socket
        socket = new Socket(trackerIp, trackerPort);

//      Creates output stream buffer for writing data to tracker
        outputStream = socket.getOutputStream();
        out= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//      Creates input stream buffer for getting data from the tracker
        inputStream = socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

//      Prints out information if successfully connected with tracker
        System.out.println("Connection established!");
    } catch (IOException e) {
        e.printStackTrace();
    }
//      Calls method responsible for communication with tracker
        getSeedersList();
    };


//  Ask particular seeder for files list
//    public ArrayList<File> askSeederForFile(){
//
//        try {
//            out.append(socket.hashCode()+"\n");
//            out.append("Example name\n");
//            out.append("Example ip\n");
//            out.append(socket.getPort()+"\n");
//            out.flush();
//
////            out.close();
//
//
////            Read list of seeders
//            receivedData = new String();
//
//            StringBuffer strB = new StringBuffer(receivedData);
//
//            String currentLine;
//
////            strB.append(in.readLine());
//
//            while ((currentLine = in.readLine())!= null) {
//                strB.append(currentLine);
//                strB.append("\n");
//            }
//
//            receivedData = strB.toString();
//
////            Splits input to a list
//            List<String> receivedSeedersList = splitReceivedSeedersData(receivedData);
//
//            for (String element : receivedSeedersList) {
//
//                List<String> tmp = splitSeedersListByIpPort(element);
//
//                seedersArray.put(tmp.get(0), Integer.valueOf(tmp.get(1)));
//
//            }
//
//            // using for-each loop for iteration over Map.entrySet()
//            for (Map.Entry<String, Integer> entry : seedersArray.entrySet())
//                System.out.println("Key = " + entry.getKey() +
//                        ", Value = " + entry.getValue());
//
////        out.close();
////            in.close();
//            socket.close();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//
//        return null;
//    }

    public void setDirToSaveFile(String dirToSaveFile) {
        this.dirToSaveFile = dirToSaveFile;
    }

    public void setReceiverPort(int seederPort) {
        this.receiverPort = seederPort;
    }

    public void setFileSenderPort(int hostingPort) {
        this.hostingPort = hostingPort;
    }

    public void setFileToSendName(String fileToSendName) {
        this.fileToSendName = fileToSendName;
    }

    public String getHostingIp() {
        return hostingIp;
    }

    public String getHostingFilesFolder() {
        return hostingFilesFolder;
    }

    public int getHostingPort() {
        return this.hostingPort;
    }


}

