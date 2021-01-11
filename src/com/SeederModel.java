package com;

import java.util.Comparator;
import java.util.HashMap;

public class SeederModel {

    private String seederIp;
    private Integer seederHash, seederAppNumber, seederPort;

    private HashMap<String, String> filesList;


    public SeederModel(Integer hostAppNumber, String hostIp, int hostPort, int hostHash) {
        this.seederAppNumber = hostAppNumber;
        this.seederIp = hostIp;
        this.seederPort = hostPort;
        this.seederHash = hostHash;
        filesList = new HashMap<>();
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

    public HashMap<String, String> getFilesMap() {
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


    public static Comparator<SeederModel> AppNoComparator = new Comparator<SeederModel>() {

        public int compare(SeederModel m1, SeederModel m2) {

                Integer no1 = m1.getSeederAppNumber();
                Integer no2 = m2.getSeederAppNumber();

                //For ascending order
                return no1-no2;

            }};


}
