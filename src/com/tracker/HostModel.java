package com.tracker;

import java.util.ArrayList;

public class HostModel {

    private String hostName, hostIp;
    private int hostHash, hostPort;

    private ArrayList filesList;


    public HostModel(String hostName, String hostIp, int hostPort, int hostHash) {
        this.hostName = hostName;
        this.hostIp = hostIp;
        this.hostPort = hostPort;
        this.hostHash = hostHash;
        filesList = new ArrayList<File>();
    }

    public String getHostName() {
        return hostName;
    }

    public String getHostIp() {
        return hostIp;
    }

    public int getHostHash() {
        return hostHash;
    }

    public int getHostPort() {
        return hostPort;
    }

    public ArrayList getFilesList() {
        return filesList;
    }

    @Override
    public String toString() {
        return "HostModel{" +
                "hostName='" + hostName + '\'' +
                ", hostIp='" + hostIp + '\'' +
                ", hostHash=" + hostHash +
                ", hostPort=" + hostPort +
                ", filesList=" + filesList +
                '}';
    }
}
