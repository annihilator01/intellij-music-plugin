package com.dubrovskii.intellijmusicplugin.logic;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist {

    private String title;
    private ArrayList<Track> tracks;

    Playlist(String title) {
        this.title = title;
        this.tracks = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public boolean addTrack(Track track) {
        return tracks.add(track);
    }

    public boolean deleteTrack(Track track) {
        return tracks.remove(track);
    }

    public Track getTrackAt(int i) {
        return tracks.get(i);
    }

    public Iterator<Track> tracks() {
        return tracks.iterator();
    }

    public int getSize() {
        return tracks.size();
    }
}
