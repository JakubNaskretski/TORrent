package com.tracker;

import com.host.File;

import java.util.ArrayList;

public class SeederModel {

    private String seederName, seederIp;
    private Integer seederHash;
    private Integer seederPort;

    private ArrayList filesList;


    public SeederModel(String hostName, String hostIp, int hostPort, int hostHash) {
        this.seederName = hostName;
        this.seederIp = hostIp;
        this.seederPort = hostPort;
        this.seederHash = hostHash;
        filesList = new ArrayList<File>();
    }

    public String getSeederName() {
        return seederName;
    }

    public String getSeederIp() {
        return seederIp;
    }

    public Integer getSeederHash() {
        return seederHash;
    }

    public Integer getSeederPort() {
        return seederPort;
    }

    public ArrayList getFilesList() {
        return filesList;
    }

    @Override
    public String toString() {
        return "HostModel{" +
                "hostName='" + seederName + '\'' +
                ", hostIp='" + seederIp + '\'' +
                ", hostHash=" + seederHash +
                ", hostPort=" + seederPort +
                ", filesList=" + filesList +
                '}';
    }
}
