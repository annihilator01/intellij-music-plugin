package com.dubrovskii.intellijmusicplugin.logic;

import java.nio.file.Paths;

public class Track {

    private final String absolutePath;
    private final String fileName;
    private final AudioFormat audioFormat;
    private final String title;
    private final String author;
    private final int lengthInBytes;
    private final int lengthInMilliseconds;

    public Track(String absolutePath, String title, String author, int lengthInBytes, int lengthInMilliseconds) {
        this.absolutePath = absolutePath;
        this.fileName = Paths.get(absolutePath).getFileName().toString();
        this.audioFormat = AudioFormat.valueOf(getFileExtension(fileName).toUpperCase());
        this.title = title;
        this.author = author;
        this.lengthInBytes = lengthInBytes;
        this.lengthInMilliseconds = lengthInMilliseconds;
    }

    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public String getFileName() {
        return fileName;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getLengthInBytes() {
        return lengthInBytes;
    }

    public int getLengthInMilliseconds() {
        return lengthInMilliseconds;
    }
}
