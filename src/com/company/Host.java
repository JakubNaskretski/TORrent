package com.company;

import java.util.ArrayList;

public class Host {

    private String hostName, hostIp, hostPort;

    private ArrayList filesList;


    public Host(String hostName, String hostIp, String hostPort) {
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        filesList = new ArrayList<File>();
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public String getHostPort() {
        return hostPort;
    }

    public ArrayList getFilesList() {
        return filesList;
    }



}
