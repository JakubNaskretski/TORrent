package com.tracker;

import com.client.File;

import java.util.ArrayList;

public class SeederModel {

    private String seederIp;
    private Integer seederHash, seederAppNumber, seederPort;

    private ArrayList filesList;


    public SeederModel(Integer hostAppNumber, String hostIp, int hostPort, int hostHash) {
        this.seederAppNumber = hostAppNumber;
        this.seederIp = hostIp;
        this.seederPort = hostPort;
        this.seederHash = hostHash;
        filesList = new ArrayList<File>();
    }

    public Integer getSeederAppNumber() {
        return seederAppNumber;
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
                "hostName='" + seederAppNumber + '\'' +
                ", hostIp='" + seederIp + '\'' +
                ", hostHash=" + seederHash +
                ", hostPort=" + seederPort +
                ", filesList=" + filesList +
                '}';
    }
}
