package com.host;

import java.io.*;
import java.net.Socket;

public class Client {

//    TODO: If no host currently online inform about no seeders



    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Client(String hostname, int port) {
        try {
            socket = new Socket(hostname, port);
            writer = new PrintWriter(socket.getOutputStream(),true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Nawiązano połączenie!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        talk();
    }

    private void talk(){

        try {
            writer.println(socket.hashCode());
            writer.println("Example name");
            writer.println("Example ip");
            writer.println(socket.getPort());

            String line;
            while( (line = reader.readLine() ) != null ){
                System.out.println(line);
            }

            writer.close();
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