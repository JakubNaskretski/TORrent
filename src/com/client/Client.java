package com.client;

import com.SeederModel;
import com.client.view.ClientView;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantLock;

public class Client {

//  Default app number
    private int currentAppNumber;

//  Temporary app port before connecting to the tracker
    private int currentAppTmpPort;

//  Current app hosting port
    private int currentAppHostingPort;

//  Current app hosting socket
    private ServerSocket currentAppHostingSocket;

//  Current app client socket
    private Socket socketForCommAsAClient;

//  default ip
    private String currentAppHostingIp = "0.0.0.0";

//  Tracker ip
    private String trackerIp = "0.0.0.0";

//  Tracker port number
    private int trackerPort = 10000;

//  Directory to the file with files of the class
    private String hostingFilesFolder;

//  File to send name
    private String fileToSendName;

//  If connection with tracker was successful flag
    private boolean connectedToTracker = false;

//  JOptionPane to display errors
    JOptionPane jOptionPane;

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

    ClientView lastUsedView;
    String lastUsedFileName, lastUsedCheckSum;
    ArrayList<SeederModel> lastUsedSeedersList;
    int lastUsedPosition;

//  Constructor
    public Client(JOptionPane jOptionPane) {
        this.jOptionPane = jOptionPane;

//      Gets host app ip out
        try {
             this.currentAppHostingIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

//      App number and port
        this.currentAppNumber = 0;
        this.currentAppHostingPort = 0;

//      After creating instance of a class, give it tmp number and port
        this.currentAppTmpPort = generateRandomPortNo();

//      Starts socket for first connection with tracker
        try {
            this.currentAppHostingSocket = new ServerSocket(currentAppTmpPort);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

//      Creates ConcurrentHashMap in to store app files ready to share
        this.hostAppFiles = new ConcurrentHashMap<>();

//      Creates Array for storing seeders list
        seedersArray = new ArrayList<SeederModel>();

//      Tries to connect with the tracker in order to get information about seeders
        connectWithTracker();

//      Creates new socket after receiving data from tracker
        try {
            this.currentAppHostingSocket = new ServerSocket(currentAppHostingPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

//      Creates directory to store files
        this.hostingFilesFolder = "D:\\TORrent_"+currentAppNumber+"\\";

//      Starts listening for other seeders
            new Thread(() -> {
                startSocketForSeeding();
            }).start();

//      Loads files to share
        loadFilesToShare();

//      Asks seeders for files list
        askSeedersForFilesList();
    }


    /**
     * Generates random port for newly created app
     */
    public int generateRandomPortNo() {
        return ThreadLocalRandom.current().nextInt(10001, 65535 + 1);
    }


    /**
     * Returns checksum for given file
     */
    public String getFileMD5CheckSum(File file) {
        try {
            //Use MD5 algorithm
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            //Get the checksum
            return getFileChecksum(md5Digest, file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Reads files in folder of app and makes map from it
     */
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


    /**
     * Connects current app with the tracker
     */
    public void connectWithTracker() {

//      Locks the thread
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

//              Set up connection flag
                connectedToTracker = true;

            } catch (ConnectException e) {
                System.out.println("Could not connect to the Tracker by app: " + currentAppNumber);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

//          Calls method responsible for communication with tracker
            getSeedersList();

        } finally {
//          Unlocks the thread
            counterLock.unlock();
        }
    }


    /**
     * Sends app data to the tracker
     * If does not have yet app number, receives new app number according to the tracker
     * Receives list of the seeders from the tracker
     */
    private void getSeedersList() {

//      Clears seeder array for reuse
        seedersArray.clear();

            if (connectedToTracker) {
                try {

//                  Send information about itself in order to add new seeder to the list
                    out.append(currentAppNumber + "\n");
                    out.append(currentAppHostingIp + "\n");
                    out.append(currentAppHostingPort + "\n");
                    out.append(hashCode() + "\n");
                    out.flush();

//                  Read list of seeders
                    receivedData = new String();
                    StringBuffer strB = new StringBuffer(receivedData);
                    String currentLine;

//                  If app does not have number yet
                    if (currentAppNumber == 0) {

//                      Read line from tracker which suppose to be new number
                        int receivedNewAppNo = Integer.valueOf(in.readLine());

//                      Assign new number and port to the variables
                        this.currentAppNumber = receivedNewAppNo;
                        this.currentAppHostingPort = 10000+currentAppNumber;

                    }

//                  While output stream on tracker open and sending data, read data
                    while ((currentLine = in.readLine()) != null) {
                        strB.append(currentLine);
                        strB.append("\n");
                    }

                    receivedData = strB.toString();

//                  Splits received seeders data into a list
                    List<String> receivedSeedersList = splitStringToListByNLine(receivedData);

//                  For elements in list, split list and add seeders information into the list of SeederModels
                    for (String element : receivedSeedersList) {

//                      Creates tmp list containing base elements of SeederModel
                        List<String> tmp = splitStringToListByDelimiter(element);

//                      Writes SeederModel base elements into the tmp variables
                        try {

                            Integer tmpAppNo = Integer.valueOf(tmp.get(0));
                            String tmpHostIp = tmp.get(1);
                            Integer tmpHostPort = Integer.valueOf(tmp.get(2));
                            Integer tmpHostHash = Integer.valueOf(tmp.get(3));

//                              Compares tmp variables with seeders array to find if there are dupplicates
                                for (SeederModel seederModel : seedersArray) {
                                    if ((seederModel.getSeederAppNumber().equals(tmpAppNo) &&
                                            seederModel.getSeederIp().equals(tmpHostIp) &&
                                            seederModel.getSeederPort().equals(tmpHostPort))) {

//                                      Than skip that record
                                        continue;
                                    }
                                }

//                              If received data from tracker is about the current app
                                if (tmpAppNo == currentAppNumber) {

//                                  Than skip that record
                                    continue;
                                }

//                              If there is no such record in seeders array, create new out of received data
                                seedersArray.add(new SeederModel(tmpAppNo, tmpHostIp, tmpHostPort, tmpHostHash));

                        } catch (NumberFormatException E) {
                            System.out.println("No data of seeders have been downloaded from tracker");
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
                            socketForCommAsAClient.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                        "Cannot get seeders list without connection to tracker.",
                        "Getting seeders list error",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println("Cannot get seeders list without connection to tracker on the app: " + currentAppNumber);
            }
    }


    /**
     * Splits received data by new lines into the List
     */
    public List<String> splitStringToListByNLine(String seedersData){
        return Arrays.asList(seedersData.split("\\s*\\n\\s*"));
    }


//    TODO: Use new line to separate data
    /**
     * Splits received data in the list into the new list by "---"
     * Used "---" instead of "-" because it is less likely to appear in the file name
     */
    public List<String> splitStringToListByDelimiter(String seedersList){
        return Arrays.asList(seedersList.split("\\s*---\\s*"));
    }


    /**
     * Asks each seeder in array for list of files to download
     * Updates file list in each seeder in array wit hreceived data
     */
    public void askSeedersForFilesList() {

        if (!seedersArray.isEmpty()) {

            for (SeederModel seederModel : seedersArray) {

                try {

                    String tmpSeederIp = seederModel.getSeederIp();
                    Integer tmpSeederPort = seederModel.getSeederPort();

//                  Creates socket to connect with seeder
                    this.socketForCommAsAClient = new Socket(tmpSeederIp, tmpSeederPort.intValue());

//                  Creates output stream buffer for writing data to seeder
                    outputStream = socketForCommAsAClient.getOutputStream();
                    out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//                  Creates input stream buffer for getting data from the seeder
                    inputStream = socketForCommAsAClient.getInputStream();
                    in = new BufferedReader(new InputStreamReader(inputStream, "UTF8"));


//                  Sends request to the other seeder
                    out.append("LIST");
                    out.append("\n");
                    out.flush();

                    receivedData = new String();

//                  Creates string buf for easier data concat
                    StringBuffer strB = new StringBuffer(receivedData);

                    String currentLine;

                    while ((currentLine = in.readLine()) != null) {
                        strB.append(currentLine);
                        strB.append("\n");
                    }

                    receivedData = strB.toString();

//                  Prevents doing next lines if received filesData is empty
                    if (!receivedData.isEmpty()) {

//                      Splits received seeders data into a list
                        List<String> receivedFilesOnSeederList = splitStringToListByNLine(receivedData);

//                      For elements in list, split list and add seeders information into the list of SeederModels
                        for (String element : receivedFilesOnSeederList) {

//                          Creates tmp list containing base elements of SeederModel
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
                } finally {
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
        } else {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                    " No seeders data have been loaded, please use reload button",
                    "No seeders data",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println("No data of any seeder");
        }
    }



    /**
     * Asks particular seeder for file to download
     * Updates files to download in app
     */
    public void askSeedersForFileToDownload(ClientView view, String fileName, String fileCheckSum, ArrayList<SeederModel> seedersList, boolean wasInterrupted) {

        try {

            boolean isFileOnBothHosts = true;

//          How many seeders clicked
            int seedersNumber = seedersList.size();

//          Checks if the same file is on selected seeders
            for (SeederModel seeder : seedersList) {

//              If seeder files contains file
                if (seeder.getFilesMap().containsKey(fileName)) {

//                  Get value which is in map under file name
//                  If this valuer is different than the hash, files are different and cannot be downloaded
                    if (!seeder.getFilesMap().get(fileName).equals(fileCheckSum)) {

                        JOptionPane.showMessageDialog(JOptionPane.getRootFrame(),
                                "Cannot download file mutually from chosen seeders. \nNot all of them have file",
                                "File not on all seeders",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                } else if (!seeder.getFilesMap().containsKey(fileName)) {
                    return;
                }
            }

//          Makes copy of data in case connection was interrupted
            lastUsedView = view;
            lastUsedFileName = fileName;
            lastUsedCheckSum = fileCheckSum;
            lastUsedSeedersList = seedersList;



//      For each seeder run new thread
            for (int i = 0; i < seedersNumber; i++) {

//              Creates socket to connect with seeder
                Socket socketForCommAsAClient = new Socket(seedersList.get(i).getSeederIp(), seedersList.get(i).getSeederPort());

//              Create file download handler thread (Adds i + 1 since i starts from 0)
                DownloadFileHandler downloadFileHandler = new DownloadFileHandler(this ,view, fileName, i + 1, seedersNumber, socketForCommAsAClient, wasInterrupted);

//              Thread to handle request
                new Thread(downloadFileHandler).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DownloadFileHandler implements Runnable {

        private boolean wasInterrupted;
        private Client client;
        private ClientView view;
        private String fileName;
        private int partOfTheFile;
        private int totalFileParts;
        private Socket socketForCommAsAClient;
//        private int lastUsedPosition;

        private DownloadFileHandler(Client client ,ClientView view, String fileName, int partOfTheFile, int totalFileParts, Socket socketForCommAsAClient, boolean wasInterrupted) {
            this.view = view;
            this.fileName = fileName;
            this.partOfTheFile = partOfTheFile;
            this.totalFileParts = totalFileParts;
            this.socketForCommAsAClient = socketForCommAsAClient;
            this.client = client;
            this.wasInterrupted = wasInterrupted;
        }

        public void run() {

            try {

//              Creates variable to add file name
                String newFileName = fileName;

//              Creates output stream buffer for writing data to seeder
                outputStream = socketForCommAsAClient.getOutputStream();
                out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));

//              Creates input stream buffer for getting data from the seeder
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

//              Sending name of the file to download to the other seeder
                out.append(fileName);
                out.append("\n");
                out.flush();

//              Sending for how many parts should be file split
                out.append(String.valueOf(totalFileParts));
                out.append("\n");
                out.flush();

//              Sending which part of the file is needed
                out.append(String.valueOf(partOfTheFile));
                out.append("\n");
                out.flush();

//              Iterate over files already in app folder
                for (File file : hostAppFiles.keySet()) {

//                  If app already contains file with same name
                    if (file.getName().equals(fileName)) {

//                      Find index of . before downloading file extension
                        int extensionDotIndex = fileName.lastIndexOf('.');

//                      Add copy before extension .
                        newFileName = fileName.substring(0, extensionDotIndex) + "copy" + fileName.substring(extensionDotIndex);

                    }
                }

                // Get the file name
                String file = hostingFilesFolder + newFileName;

//              File fi = new File(hostingFilesFolder + fileName);
                RandomAccessFile fi = new RandomAccessFile(file, "rws");

//                DataOutputStream fileOut = new DataOutputStream(
//                        new BufferedOutputStream(new FileOutputStream(file, true)));


//              Gets information about file length
                len = inputStream.readLong();

//              Gets information where to start writing into the file
                long startingPointToWrite = inputStream.readLong();

//              If downloading was interrupted and class was called once again from re-download
//              Skip previously downloaded number of bytes
                if (wasInterrupted) {
                    inputStream.skipBytes(client.lastUsedPosition);
                }

                while (true) {
                    int read = 0;
                    if (inputStream != null) {
                        read = inputStream.read(buf);
                        client.lastUsedPosition = read;
                    }
                    passedlen += read;
                    if (read == -1) {
                        break;
                    }

//                  File progress bar
                    view.changeDownloadFileLabel("Downloaded " + (passedlen / len) * 100 + "% of file");

                    System.out.println("File part - "+partOfTheFile+" - "+new String(buf, StandardCharsets.UTF_8));


//                    fileOut.write(buf, 0, read);

                    if (startingPointToWrite != 0) {
                        fi.seek(startingPointToWrite);
                    }
//                  Skips lastly loaded bites number
                    fi.write(buf,0,read);


                }

//              Creates popup after successful download
                view.createPopUpWindow("File successfully downloaded "+partOfTheFile+" out of "+totalFileParts);
                System.out.println("Receive completed, file saved as" + file + "\n");

                view.changeDownloadFileLabel("% of downloaded file");

//                fileOut.close();
                fi.close();

//              Refresh file list to download from app
                loadFilesToShare();

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


//                  Asks if restart download
                    Object[] options = {"Yes",
                            "No"};
                    int n = JOptionPane.showOptionDialog(view.getFrame(),
                            "Downloading was interrupted \n" +
                                    "Would you like to try again ?",
                            "Downloading exception",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[0]);

//                  If clicked yes
                    if (n == 0) {

//                      Call downloading once again with previous data
                        askSeedersForFileToDownload(client.lastUsedView,
                            client.lastUsedFileName,
                                client.lastUsedCheckSum,
                                client.lastUsedSeedersList,
                         true);

                    }

                }
            }
        }
    }

//  Sends file to particular seeder
    public void sendFileToSeeder(String pathToFileWName ,String fileName, ArrayList<SeederModel> seedersList) {

        try {

//          How many seeders clicked
            int seedersNumber = seedersList.size();

//      For each seeder run new thread
            for (int i = 0; i < seedersNumber; i++) {

//              Creates socket to connect with seeder
                Socket socketForCommAsAClient = new Socket(seedersList.get(i).getSeederIp(), seedersList.get(i).getSeederPort());

//              Create file download handler thread (Adds i + 1 since i starts from 0)
                SendFileHandler sendFileHandler = new SendFileHandler(pathToFileWName, fileName, socketForCommAsAClient);

//              Thread to handle request
                new Thread(sendFileHandler).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class SendFileHandler implements Runnable {

        private String pathToFileWName, fileName;
        private Socket socketForCommAsAClient;

        private SendFileHandler(String pathToFileWName, String fileName, Socket socketForCommAsAClient) {

            this.pathToFileWName = pathToFileWName;
            this.fileName = fileName;
            this.socketForCommAsAClient = socketForCommAsAClient;

        }

        public void run() {

            BufferedReader reader = null;
            BufferedWriter writer = null;

            try {

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
    }



// TODO: Should it be in a different thread ?
    public void startSocketForSeeding() {
        try {
//      Run infinite loop for getting other seeders requests
        while (true) {

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
        int filePart;
        int howManyFileParts;

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
                    for (Iterator<File> keys = hostAppFiles.keySet().iterator(); keys.hasNext(); ) {

                        File key = keys.next();
                        String val = hostAppFiles.get(key);

//                      Get file name
                        writer.append(key.getName());
                        writer.append("---");
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


                    while (true) {
//                  Iterate over files already in app folder
                        for (File file : hostAppFiles.keySet()) {
//                      If app already contains file with same name
                            if (file.getName().equals(fileName)) {
//                          Find index of . before downloading file extension
                                int extensionDotIndex = fileName.lastIndexOf('.');
//                          Add -copy before extension .
                                fileName = fileName.substring(0, extensionDotIndex) + "-copy" + fileName.substring(extensionDotIndex);
                            loadFilesToShare();
                            }
                        }
                        break;
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

//                  File fi = new File(hostingFilesFolder + fileName);
                    RandomAccessFile fi = new RandomAccessFile(hostingFilesFolder + fileName, "r");

//                  Read how many parts of the file should be
                    howManyFileParts = Integer.parseInt(reader.readLine());

//                  Read which part of the file is needed
                    filePart = Integer.parseInt(reader.readLine());

//                  Creates data output stream for sending file
                    DataOutputStream ps = new DataOutputStream(client.getOutputStream());

//                  Get total length of the file
                    long sourceSize = fi.length();
//                  Divide total length of the file into how many file parts should be
                    long sizeOfFilePart = sourceSize / howManyFileParts;
//                  Find ending point to which file should be read
                    long endingPointToRead = sizeOfFilePart * filePart;
//                  Find starting point from which file should be read
                    long startingPointToRead = endingPointToRead - sizeOfFilePart;

//                  Send to the client how much data (size of total file part) will be send
                    ps.writeLong(sizeOfFilePart);
                    ps.flush();

//                  Send to the client where it shold start writing into the file
                    ps.writeLong(startingPointToRead);
                    ps.flush();

//                  Set position of the file reading
                    long position = 0;

//                  Jump in file to the correct starting position
                    fi.seek(startingPointToRead);

//                  While position is not end of the part
                    while (position < endingPointToRead) {

                        int read = 0;
                        read += fi.read();
                        position = fi.getFilePointer();

                        if (read == -1) {
                            break;
                        }
                        ps.write(read);
                    }
                    ps.flush();
                    fi.close();


//                    Solution without dividing into the parts
//                    while (true) {
//                        int read = 0;
//                        if (fis != null) {
//                            read = fis.read(buf);
//                        }
//
//                        if (read == -1) {
//                            break;
//                        }
//                        ps.write(buf, 0, read);
//                    }
//                    ps.flush();
//                    // Note that the socket link is closed, otherwise the client will wait for the server data to come over.
//                    // Until the socket times out, the data is incomplete.
//                    fis.close();

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

    public String getCurrentAppHostingIp() {
        return currentAppHostingIp;
    }

    public String getHostingFilesFolder() {
        return hostingFilesFolder;
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

