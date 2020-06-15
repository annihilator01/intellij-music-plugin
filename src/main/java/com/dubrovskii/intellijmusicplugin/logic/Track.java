package com.dubrovskii.intellijmusicplugin.logic;

import com.dubrovskii.intellijmusicplugin.ui.PlaylistTree;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.nio.file.Paths;
import java.util.Objects;

public class Track implements HavingPopup {

    private final String absolutePath;
    private final String fileName;
    private final AudioFormat audioFormat;
    private final String title;
    private final String author;
    private final long lengthInBytes;
    private final long lengthInMilliseconds;

    public Track(String absolutePath, String author, String title, long lengthInMilliseconds, long lengthInBytes) {
        this.absolutePath = absolutePath;
        this.fileName = getFileNameWithoutExtension(absolutePath);
        this.audioFormat = AudioFormat.valueOf(getFileExtension(Paths.get(absolutePath).getFileName().toString()).toUpperCase());
        this.author = author;
        this.title = title;
        this.lengthInMilliseconds = lengthInMilliseconds;
        this.lengthInBytes = lengthInBytes;
    }

    private String getFileNameWithoutExtension(String absolutePath) {
        String fileNameWithExtension = Paths.get(absolutePath).getFileName().toString();
        return fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf("."));
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

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public long getLengthInMilliseconds() {
        return lengthInMilliseconds;
    }

    public long getLengthInBytes() {
        return lengthInBytes;
    }

    @Override
    public String toString() {
        if (!author.equals("") && !title.equals("")) {
            return author + " - " + title;
        }

        if (author.equals("") && !title.equals("")) {
            return title;
        }

        if (!author.equals("")) {
            return author;
        }

        return "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return lengthInBytes == track.lengthInBytes &&
                lengthInMilliseconds == track.lengthInMilliseconds &&
                Objects.equals(absolutePath, track.absolutePath) &&
                Objects.equals(fileName, track.fileName) &&
                audioFormat == track.audioFormat &&
                Objects.equals(title, track.title) &&
                Objects.equals(author, track.author);
    }

    @Override
    public JPopupMenu getPopup(PlaylistTree contextTree) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem playTrack = new JMenuItem("Play track");
        JMenuItem deleteTrack = new JMenuItem("Delete track");

        playTrack.setIcon(AllIcons.RunConfigurations.TestState.Run);
        deleteTrack.setIcon(AllIcons.Actions.GC);

        addActionListenerToPlayItem(playTrack, contextTree);
        addActionListenerToDeleteItem(deleteTrack, contextTree);

        popupMenu.add(playTrack);
        popupMenu.addSeparator();
        popupMenu.add(deleteTrack);

        return popupMenu;
    }

    private void addActionListenerToPlayItem(JMenuItem playTrack, PlaylistTree contextTree) {
        playTrack.addActionListener(ae -> {

        });
    }

    private void addActionListenerToDeleteItem(JMenuItem deleteTrack, PlaylistTree contextTree) {
        deleteTrack.addActionListener(ae -> {
            DefaultMutableTreeNode trackNode = contextTree.getSelectedNode();

            int result = Messages.showOkCancelDialog("Do you want to delete track \"" + trackNode + "\"?",
                    "Delete", "Delete", Messages.CANCEL_BUTTON, Messages.getQuestionIcon());

            if (result == Messages.OK) {
                DefaultMutableTreeNode playlistNode = (DefaultMutableTreeNode) trackNode.getParent();

                Track track = (Track) trackNode.getUserObject();
                Playlist playlist = (Playlist) playlistNode.getUserObject();

                if (playlist.removeTrack(track)) {
                    DefaultTreeModel treeModel = (DefaultTreeModel) contextTree.getModel();
                    treeModel.removeNodeFromParent(contextTree.getSelectedNode());
                }
            }
        });
    }
}
