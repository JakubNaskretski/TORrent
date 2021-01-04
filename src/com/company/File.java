package com.company;

public class File {

    private String fileName;
    private byte[] fileControlSum;
    private int howManyFileParts, whichFilePart;

    public File(String fileName, byte[] fileControlSum, int howManyFileParts, int whichFilePart) {
        this.fileName = fileName;
        this.fileControlSum = fileControlSum;
        this.howManyFileParts = howManyFileParts;
        this.whichFilePart = whichFilePart;
    }
}
