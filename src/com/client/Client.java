package com.client;

import com.SeederModel;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

//    TODO: If no host currently online inform about no seeders

//    Wymiana list udostępnianych plików między hostami -  chcemy wiedzieć gdzie (na jakim hoście) jakie pliki się znajdują, wraz z ich sumami kontrolnymi MD5 – wersje (H2H, MH, TCP) (1 pkt.).
//    Przesyłanie plików typu PULL – ściągamy z wybranego hosta plik o zadanej nazwie - wersje (H2H, MH, TCP) (2 pkt.).
//    Przesyłanie plików typu PUSH – wrzucamy na wybrany host pliku o zadanej nazwie – wersje (H2H, MH, TCP) (1 pkt.).
//    Wznawianie transmisji pliku w przypadku jej przerwania lub rozłączenia – wersje (H2H, MH, TCP) (2 pkt.).
//    Ściąganie tego samego pliku (ale różnych jego części) z wielu hostów jednocześnie – wersja (MH, TCP) (2 pkt).
//    Dodatkowo aplikacja powinna pracować również pod nadzorem protokołu UDP (3 pkt.). ten punkt robimy jak poprzednie są zrobione - to są punkty bonusowe.
//    Pomiar czasu pozostałego do końca przesyłania plik (1 pkt.). ten punkt robimy jak poprzednie są zrobione  to są punkty bonusowe.


//  Default app number increasing for each instance of app created
    private static int applicationNumber = 0;
    private int currentAppNumber;

//  Default app hosting port
    private static int hostingPort = 10000;

//  Current app hosting port
    private int currentAppHostingPort;

//  Current app hosting socket
    private ServerSocket currentAppHostingSocket;

//  Current app client socket
    private Socket socketForCommAsAClient;

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

//  If connection with tracker was successful flag
    private boolean connectedToTracker = false;

//  List of files
    ConcurrentHashMap<File, String> hostAppFiles;

//  Locker of the statics within all classes
    static ReentrantLock counterLock = new ReentrantLock(true);

//  List of seeders
    private ArrayList<SeederModel> seedersArray;

    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedWriter out;
    private BufferedReader in;
    private String receivedData, dirToSaveFile;
    private boolean stop;
//    private int receiverPort;


//  Constructor
    public Client(){


//      After creating instance of a class, give it new number and port

        this.currentAppNumber = incrementAppNo();
        this.currentAppHostingPort = 10000+currentAppNumber;

//      Starts socket for current app
        try {
            this.currentAppHostingSocket = new ServerSocket(currentAppHostingPort);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

//        this.hostAppFiles = new ArrayList<>();
        this.hostAppFiles = new ConcurrentHashMap<>();

//      Creates directory to store host files
        this.hostingFilesFolder = "D:\\TORrent_"+currentAppNumber+"\\";
//        this.hostingFilesFolder = "C:\\Users\\jnaskretski\\Desktop\\TORrent\\"+currentAppNumber+"\\";



//      Creates Array for storing seeders list
        seedersArray = new ArrayList<SeederModel>();


//      Tries to connect with the tracker in order to get information about seeders
//        new Thread(() -> {
//            while (true) {
                connectWithTracker();
//                System.out.println("loop");
//                try {
//                    Thread.sleep(4000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();

//      Starts listening for other seeders
            new Thread(() -> {
                startSocketForSeeding();
            }).start();

//      After receiving list of seeders from the tracker and starting hosting
//      Load files from the folders
        loadFilesToShare();
//      Ask all other seeders for files
//        new Thread(() -> {
            askSeedersForFilesList();
//        }).start();
//      print information about seeders with files
//            printSeedersFiles();

    };

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

    public String getFileMD5CheckSum(File file) {
        try {
            //Use MD5 algorithm
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");

            System.out.println("Fiel check sum "+getFileChecksum(md5Digest, file));

            //Get the checksum
            return getFileChecksum(md5Digest, file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


//  Reads files in folder of app and makes map of files on host
    public void loadFilesToShare() {

//          Clears map for reuse of function
            hostAppFiles.clear();

            File folder = new File(hostingFilesFolder);
//          If there is no main app folder
            if (!folder.exists()) {
//              Create folder
                folder.mkdir();
            }
//          Make list out of folder files
            File[] listOfFiles = folder.listFiles();

//      If list of file have been downloaded
        if (listOfFiles != null) {

//          For each file in files list
            for (int i = 0; i < listOfFiles.length; i++) {
//          TODO: verify if this check is needed
//          If found element is file
                if (listOfFiles[i].isFile()) {
//                    this.hostAppFiles.add(listOfFiles[i]);
//                  Adds file with its created check sum to map
                    this.hostAppFiles.put(listOfFiles[i], getFileMD5CheckSum(listOfFiles[i]));
                }
            }
        } else {
            System.out.println("No files to load in folder");
        }
    }

//  Connects current app with tracker
    public void connectWithTracker() {

        counterLock.lock();

        try {
            try {
//          Creates socket to connect with tracker
                this.socketForCommAsAClient = new Socket(trackerIp, trackerPort);

//          Creates output stream buffer for writing data to tracker
                outputStream = socketForCommAsAClient.getOutputStream();
                out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//          Creates input stream buffer for getting data from the tracker
                inputStream = socketForCommAsAClient.getInputStream();
                in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

//          Prints out information if successfully connected with tracker
//            System.out.println("Connection established with Tracker by app: "+Thread.currentThread().getName()+" "+currentAppNumber);

//          Set up connection flag
                connectedToTracker = true;

            } catch (ConnectException e) {
                System.out.println("Could not connect to the Tracker by app: " + currentAppNumber);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//      Calls method responsible for communication with tracker
            getSeedersList();
        } finally {
            counterLock.unlock();
        }
    }


//  Asks for seeders list from tracker (if connected) and gives its own data to update list on tracker
    private void getSeedersList() {

//      Clears seeder array for reuse
        seedersArray.clear();

            if (connectedToTracker) {
                try {
//          Send information about itself in order to add new seeder to the list
                    out.append(currentAppNumber + "\n");
                    out.append(hostingIp + "\n");
                    out.append(currentAppHostingPort + "\n");
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
                    List<String> receivedSeedersList = splitStringToListByNLine(receivedData);

//          For elements in list, split list and add seeders information into the list of SeederModels
//                if (receivedSeedersList.isEmpty()) {
                    for (String element : receivedSeedersList) {
//              Creates tmp list containing base elements of SeederModel
                        List<String> tmp = splitStringToListByDelimiter(element);

//              Writes SeederModel base elements into the tmp variables
                        try {

                            Integer tmpAppNo = Integer.valueOf(tmp.get(0));
                            String tmpHostIp = tmp.get(1);
                            Integer tmpHostPort = Integer.valueOf(tmp.get(2));
                            Integer tmpHostHash = Integer.valueOf(tmp.get(3));

                            //              Checks if client is in list with seeders
//                            boolean containsSeeder = false;

                                for (SeederModel seederModel : seedersArray) {
                                    if ((seederModel.getSeederAppNumber().equals(tmpAppNo) && seederModel.getSeederIp().equals(tmpHostIp) && seederModel.getSeederPort().equals(tmpHostPort))) {
//                                        containsSeeder = true;
//                                      If found in list no need to continue searching
                                        continue;
                                    }
                                }

                                if (tmpAppNo == currentAppNumber) {
//                                    containsSeeder = true;
//                                  If found in list no need to continue searching
                                    continue;
                                }

//                  TODO: Fix null data received on first connection with tracker
//                  If not, add new object with seeder data to the list
//                                if (containsSeeder == false) {
//                      If element is not the same as current app
//                            if (!tmpAppNo.equals(Integer.valueOf(currentAppNumber))) {
//              Creates SeederModel as a store of possible to connect seeders and places them into array
                                    seedersArray.add(new SeederModel(tmpAppNo, tmpHostIp, tmpHostPort, tmpHostHash));
//                            }
//                                }
                        } catch (NumberFormatException E) {
                            System.out.println("No data of seeders have been downloaded from tracker");
                        }
//                }

//          Print out content of the seeders array - control
//                        System.out.println("Printing seeders from app: " + currentAppNumber);
                        for (SeederModel seederModel : seedersArray) {
                            System.out.println(Thread.currentThread().getName()+" "+seederModel.toString());
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//          Closes streams and connection
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
//                            System.out.println("Dissconnecting from socket - app: " + currentAppNumber + "\n");
                            socketForCommAsAClient.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            } else {
                System.out.println("Cannot get seeders list without connection to tracker on the app: " + currentAppNumber);
            }
    }

//  Splits received data by new lines into the List
    public List<String> splitStringToListByNLine(String seedersData){
        return Arrays.asList(seedersData.split("\\s*\\n\\s*"));
    }

//  Splits received data in the list into the new list by "-"
    public List<String> splitStringToListByDelimiter(String seedersList){
        return Arrays.asList(seedersList.split("\\s*-\\s*"));
    }

////  Downloads data from seeder
//    public void downloadFileFromSeeder(String dirToSaveFile, int receiverPort) {
////      Sets directory to save received file
//        setDirToSaveFile(dirToSaveFile);
////      Sets port of the seeder
//        setReceiverPort(receiverPort);
////      Starts downloading method
//        downloadFileThread.start();
//    }


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
                ServerSocket ss = new ServerSocket(currentAppHostingPort);
                do {
                    socket = ss.accept();

                    // public Socket accept() throws
                    // IOException listens for and accepts connections to this socket. This method blocks until it is connected.
//                    System.out.println("Create a socket link");
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


//  Asks each seeder in array for list of files to download and updates files in each seeder
    public void askSeedersForFilesList() {

            for (SeederModel seederModel : seedersArray) {

                try {

                    String tmpSeederIp = seederModel.getSeederIp();
                    Integer tmpSeederPort = seederModel.getSeederPort();

//              Creates socket to connect with seeder
                    this.socketForCommAsAClient = new Socket(tmpSeederIp, tmpSeederPort.intValue());

                    //          Creates output stream buffer for writing data to seeder
                    outputStream = socketForCommAsAClient.getOutputStream();
                    out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//          Creates input stream buffer for getting data from the seeder
                    inputStream = socketForCommAsAClient.getInputStream();
                    in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

//              Prints out information if successfully connected with tracker
//                    System.out.println("Connection established with a seeder " + seederModel.getSeederAppNumber());

//              Sends request to the other seeder
                    out.append("LIST");
                    out.append("\n");
                    out.flush();

                    receivedData = new String();

                    StringBuffer strB = new StringBuffer(receivedData);

                    String currentLine;

                        while ((currentLine = in.readLine()) != null) {
                            strB.append(currentLine);
                            strB.append("\n");
                        }

                        receivedData = strB.toString();

//                  Prevents doing next lines if received filesData is empty
                    if (!receivedData.isEmpty()) {

//              Splits received seeders data into a list
                        List<String> receivedFilesOnSeederList = splitStringToListByNLine(receivedData);

//              For elements in list, split list and add seeders information into the list of SeederModels
                        for (String element : receivedFilesOnSeederList) {

//              Creates tmp list containing base elements of SeederModel
                            List<String> tmp = splitStringToListByDelimiter(element);

                            String tmpFileName = tmp.get(0);
                            String tmpFileSize = tmp.get(1);

                            seederModel.getFilesMap().put(tmpFileName, tmpFileSize);

                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                        if (out != null) {
                            out.close();
                            socketForCommAsAClient.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
    }




//  Asks each seeder in array for list of files to download and updates files in each seeder
    public void askSeederForFileToDownload(String fileName, String host, Integer port) {

        try {
            if (fileName != null) {

//              Creates socket to connect with seeder
                this.socketForCommAsAClient = new Socket(host, Integer.valueOf(port));


//              Creates output stream buffer for writing data to seeder
                outputStream = socketForCommAsAClient.getOutputStream();
                out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//          Creates input stream buffer for getting data from the seeder
                inputStream = socketForCommAsAClient.getInputStream();
                in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));

//              Data input stream for File
                DataInputStream inputStream = new DataInputStream(
                        new BufferedInputStream(socketForCommAsAClient.getInputStream()));

                int bufferSize = 8192;
                byte[] buf = new byte[bufferSize];
                long passedlen = 0;
                long len = 0;


//              Sends request to the other seeder
                out.append("SEND");
                out.append("\n");
                out.flush();

//              Sending file name to the other seeder
                out.append(fileName);
                out.append("\n");
                out.flush();


//              Iterate over files already in app folder
                for (File file : hostAppFiles.keySet()) {

//                  If app already contains file with same name
                    if (file.getName().equals(fileName)) {

//                      Find index of . before downloading file extension
                        int extensionDotIndex = fileName.lastIndexOf('.');

//                      Add -copy before extension .
                        fileName = fileName.substring(0, extensionDotIndex) + "-copy" + fileName.substring(extensionDotIndex);

                    }
                }

                // Get the file name
                String file = hostingFilesFolder + fileName;

                DataOutputStream fileOut = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(file)));

                len = inputStream.readLong();

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

                loadFilesToShare();
            }

        } catch (UnknownHostException unknownHostException) {
            unknownHostException.printStackTrace();
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            unsupportedEncodingException.printStackTrace();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                    socketForCommAsAClient.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



//  Sends file to particular seeder
    public void sendFileToSeeder(String pathToFileWName ,String fileName, String host, Integer port) {

        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {

//          Creates socket to connect with seeder
            this.socketForCommAsAClient = new Socket(host, Integer.valueOf(port));

            reader = new BufferedReader(new InputStreamReader(socketForCommAsAClient.getInputStream(), "UTF8"));

            writer = new BufferedWriter(new OutputStreamWriter(socketForCommAsAClient.getOutputStream(), "UTF8"));

            File fi = new File(pathToFileWName);

            DataInputStream fis = new DataInputStream(new FileInputStream(pathToFileWName));
            DataOutputStream ps = new DataOutputStream(socketForCommAsAClient.getOutputStream());

//          Sends request to the other seeder
            writer.append("DOWNLOAD");
            writer.append("\n");
            writer.flush();


//          Sending file name to the other seeder
            writer.append(fileName);
            writer.append("\n");
            writer.flush();

//          Sending file size
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

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
//                    System.out.println("Closing connection on app: "+currentAppNumber+" for another seeder");
                    reader.close();
                    socketForCommAsAClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



// TODO: Should it be in a different thread ?
    public void startSocketForSeeding() {
        try {
//      Run infinite loop for getting other seeders requests
        while (true) {

//            System.out.println("Started listening on app: "+currentAppNumber+" for seeders requests");

//          Accept socket
            Socket anotherSeeder = currentAppHostingSocket.accept();

//          Create a new thread object
            ClientHandler anotherSeederSocket = new ClientHandler(anotherSeeder);

//              Thread to handle request
            new Thread(anotherSeederSocket).start();
        }
    }
        catch (IOException e) {
        e.printStackTrace();
    }
        finally {
            if (currentAppHostingSocket != null) {
                try {
                    currentAppHostingSocket.close();
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

    //  Constructor
    public ClientHandler(Socket socket) {
        this.client = socket;
    }

    //  TODO: Fix too many Try Catch
    public void run() {

        BufferedWriter writer = null;
        BufferedReader reader = null;
        String fileName = null;
        int bufferSize = 8192;
        byte[] buf = null;

        try {

            reader = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF8"));

            String readLine = reader.readLine();

            switch (readLine) {


//              Another seeder asks for files list
                case "LIST":
//                    reader.close();
                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));
//                  Iterates over file list and prints to the output file names

//                    for (Map.Entry<File, String> element : hostAppFiles.entrySet()) {
                    for (Iterator<File> keys = hostAppFiles.keySet().iterator(); keys.hasNext();) {

                        File key = keys.next();
                        String val = hostAppFiles.get(key);

//                      Get file name
                        writer.append(key.getName());
                        writer.append("-");
//                      Get file check sum
                        writer.append(val);
                        writer.append("\n");
                    }

                    writer.flush();
                    break;

//              Another seeder asks for downloading file
                case "DOWNLOAD":

                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));

//                  Data input stream for File
                    DataInputStream inputStream = new DataInputStream(
                            new BufferedInputStream(client.getInputStream()));

                    buf = new byte[bufferSize];
                    long passedlen = 0;
                    long len = 0;

//                  Get name of the receiving file
                    fileName = reader.readLine();

//                  Iterate over files already in app folder
                    for (File file : hostAppFiles.keySet()) {
//                      If app already contains file with same name
                        if (file.getName().equals(fileName)) {
//                          Find index of . before downloading file extension
                            int extensionDotIndex = fileName.lastIndexOf('.');
//                          Add -copy before extension .
                            fileName = fileName.substring(0, extensionDotIndex) + "-copy" + fileName.substring(extensionDotIndex);
                        }
                    }

//                  Creating file
                    String file = hostingFilesFolder + fileName;

                    DataOutputStream fileOut = new DataOutputStream(
                            new BufferedOutputStream(new FileOutputStream(file)));

                    len = inputStream.readLong();

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

                    loadFilesToShare();

                    break;

//              Another seeder asks for sending file to this app
                case "SEND":

                    writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream(), "UTF8"));

//                  Get name of the file to send
                    fileName = reader.readLine();

                    File fi = new File(hostingFilesFolder + fileName);

                    DataInputStream fis = new DataInputStream(new FileInputStream(hostingFilesFolder + fileName));
                    DataOutputStream ps = new DataOutputStream(client.getOutputStream());

                    ps.writeLong((long) fi.length());
                    ps.flush();

                    buf = new byte[bufferSize];

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
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
//                    System.out.println("Closing connection on app: "+currentAppNumber+" for another seeder");
                    reader.close();
                    client.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

    public void setDirToSaveFile(String dirToSaveFile) {
        this.dirToSaveFile = dirToSaveFile;
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

    public int getCurrentAppNumber() {
        return currentAppNumber;
    }

    public int getCurrentAppHostingPort() {
        return currentAppHostingPort;
    }

    public ArrayList<SeederModel> getSeedersArray() {
        return seedersArray;
    }

    public synchronized void printSeedersFiles() {

            for (SeederModel seederModel : seedersArray) {
                System.out.println( Thread.currentThread().getName()+" "+currentAppNumber+" "+seederModel.toString()+"\n");
            }
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

}

