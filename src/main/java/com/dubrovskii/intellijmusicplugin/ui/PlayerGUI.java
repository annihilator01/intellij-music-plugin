package com.dubrovskii.intellijmusicplugin.ui;

import com.dubrovskii.intellijmusicplugin.logic.Playlist;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlayerGUI {

    private BasicPlayer basicPlayer;
    private Playlist currentPlaylist;
    private int currentTrackId;
    private int isPaused; // 0 - not paused, 1 - paused, 2 - pre-state
    private JLabel infoLabel;

    private static PlayerGUI singleton;

    private PlayerGUI() {
        basicPlayer = new BasicPlayer();
        isPaused = 2;
    }

    public static PlayerGUI getInstance() {
        if (singleton == null) {
            singleton = new PlayerGUI();
        }
        return singleton;
    }

    public void loadPlaylist(Playlist existingPlaylist) {
        currentPlaylist = new Playlist(existingPlaylist);
        currentTrackId = 0;
    }

    public void setCurrentTrackId(int currentTrackId) {
        this.currentTrackId = currentTrackId;
    }

    public void setInfoLabel(JLabel infoLabel) {
        this.infoLabel = infoLabel;
    }

    public void play() {
        if (currentPlaylist == null) {
            return;
        }

        if (!Files.exists(Paths.get(currentPlaylist.getTrackAt(currentTrackId).getAbsolutePath()))) {
            playNext();
        } else {
            try {
                basicPlayer.stop();
            } catch (BasicPlayerException e) {
                e.printStackTrace();
            }

            try {
                basicPlayer.open(new File(currentPlaylist.getTrackAt(currentTrackId).getAbsolutePath()));
            } catch (BasicPlayerException e) {
                e.printStackTrace();
            }

            try {
                basicPlayer.play();
                infoLabel.setText(currentPlaylist.getTrackAt(currentTrackId).toString());
                isPaused = 0;
            } catch (BasicPlayerException e) {
                e.printStackTrace();
            }
        }
    }

    public void playNext() {
        if (currentPlaylist == null) {
            return;
        }

        for (int i = currentTrackId + 1; i < currentPlaylist.getSize(); i++) {
            if (Files.exists(Paths.get(currentPlaylist.getTrackAt(currentTrackId).getAbsolutePath()))) {
                currentTrackId = i;
                play();
                break;
            }
        }
    }

    public void playPrevious() {
        if (currentPlaylist == null) {
            return;
        }

        for (int i = currentTrackId - 1; i >= 0; i--) {
            if (Files.exists(Paths.get(currentPlaylist.getTrackAt(currentTrackId).getAbsolutePath()))) {
                currentTrackId = i;
                play();
                break;
            }
        }
    }

    public void pause() {
        if (currentPlaylist == null) {
            return;
        }

        try {
            basicPlayer.pause();
            isPaused = 1;
        } catch (BasicPlayerException bpe) {
            bpe.printStackTrace();
        }
    }

    public void resume() {
        if (currentPlaylist == null) {
            return;
        }

        try {
            basicPlayer.resume();
            isPaused = 0;
        } catch (BasicPlayerException bpe) {
            bpe.printStackTrace();
        }
    }

    public int isPaused() {
        return isPaused;
    }

    public boolean isAbleToPlay() {
        return currentPlaylist != null && currentPlaylist.getAllTracks() != null && !currentPlaylist.getAllTracks().isEmpty();
    }
}
