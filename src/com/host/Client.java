package com.host;

import java.io.*;
import java.net.Socket;

public class Client {

//    TODO: If no host currently online inform about no seeders



    private Socket socket;
    private OutputStream outputStream;
    private BufferedWriter out;
    private BufferedReader reader;

    public Client(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);

            outputStream = socket.getOutputStream();
            out= new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"));
//                    new FileOutputStream(fileDir), "UTF8"));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Nawiązano połączenie!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        talk();
    }

    private void talk(){

        try {
            out.append("First test text\n");
            out.flush();
            // Send first message
//            dOut.writeByte(1);
//            dOut.writeUTF(String.valueOf(socket.hashCode()));
//            dOut.flush(); // Send off the data
//
//// Send the second message
//            dOut.writeByte(2);
//            dOut.writeUTF("Example name");
//            dOut.flush(); // Send off the data
//
//// Send the third message
//            dOut.writeByte(3);
//            dOut.writeUTF("Example ip");
////            dOut.writeUTF("This is the third type of message (Part 2).");
//            dOut.flush(); // Send off the data
//
//// Send the fourth message
//            dOut.writeByte(4);
//            dOut.writeUTF(String.valueOf(socket.getPort()));
//            dOut.flush(); // Send off the data
//
//// Send the exit message
//            dOut.writeByte(-1);
//            dOut.flush();

            out.close();
            reader.close();
            socket.close();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        new Client("0.0.0.0", 44445);
    }
}