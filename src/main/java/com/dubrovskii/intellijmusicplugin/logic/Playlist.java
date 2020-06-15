package com.dubrovskii.intellijmusicplugin.logic;

import com.dubrovskii.intellijmusicplugin.ui.PlaylistTree;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.graalvm.compiler.lir.LIRInstruction;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jetbrains.annotations.NotNull;
import org.tritonus.share.sampled.file.TAudioFileFormat;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Playlist implements Iterable<Track>, HavingPopup {

    private String title;
    private ArrayList<Track> tracks;

    public Playlist(String title) {
        this.title = title;
        this.tracks = new ArrayList<>();
    }

    public Playlist(Playlist otherPlaylist) {
        this.title = otherPlaylist.title;
        this.tracks = new ArrayList<>(otherPlaylist.tracks);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public boolean addTrack(Track track) {
        if (tracks.contains(track)) {
            return false;
        }

        return tracks.add(track);
    }

    public boolean removeTrack(Track track) {
        return tracks.remove(track);
    }

    public void clear() {
        tracks.clear();
    }

    public Track getTrackAt(int i) {
        return tracks.get(i);
    }

    public ArrayList<Track> getAllTracks() {
        return tracks;
    }

    public int getSize() {
        return tracks.size();
    }

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(title, playlist.title) &&
                Objects.equals(tracks, playlist.tracks);
    }

    @NotNull
    @Override
    public Iterator<Track> iterator() {
        return tracks.iterator();
    }

    @Override
    public JPopupMenu getPopup(PlaylistTree contextTree) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem addTracks = new JMenuItem("Add tracks");
        JMenuItem deletePlaylist = new JMenuItem("Delete playlist");

        addTracks.setIcon(AllIcons.General.Add);
        deletePlaylist.setIcon(AllIcons.Actions.GC);

        addActionListenerToAddItem(addTracks, contextTree);
        addActionListenerToDeleteItem(deletePlaylist, contextTree);

        popupMenu.add(addTracks);
        popupMenu.addSeparator();
        popupMenu.add(deletePlaylist);

        return popupMenu;
    }

    private void addActionListenerToAddItem(JMenuItem addTracks, PlaylistTree contextTree) {
        addTracks.addActionListener(ae -> {
            FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true,
                    false,false, false, true);
            fileChooserDescriptor.setTitle("Choose Tracks or Folder with Tracks");
            fileChooserDescriptor.withFileFilter(virtualFile -> hasAvailableExtension(virtualFile.getExtension()));

            VirtualFile[] chosenFiles = FileChooser.chooseFiles(fileChooserDescriptor, null, null);
            Playlist playlist = (Playlist) contextTree.getSelectedNode().getUserObject();

            for (VirtualFile chosenFile : chosenFiles) {
                if (chosenFile.isDirectory()) {
                    for (VirtualFile virtualFile : chosenFile.getChildren()) {
                        if (!virtualFile.isDirectory()) {
                            addTrackToPlaylist(virtualFile, playlist, contextTree);
                        }
                    }
                } else {
                    addTrackToPlaylist(chosenFile, playlist, contextTree);
                }
            }
        });
    }

    private void addTrackToPlaylist(VirtualFile virtualFile, Playlist playlist, PlaylistTree contextTree) {
        if (!hasAvailableExtension(virtualFile.getExtension())) {
            return;
        }

        String absolutePath = virtualFile.getPath();
        AudioFile audioFile = null;
        try {
            audioFile = AudioFileIO.read(new File(absolutePath));
        } catch (CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            e.printStackTrace();
        }

        if (audioFile != null && audioFile.getTag() != null) {
            Tag tag = audioFile.getTag();
            String title = tag.getFirst(FieldKey.TITLE);
            String author = tag.getFirst(FieldKey.ARTIST);
            long lengthInMilliseconds = audioFile.getAudioHeader().getTrackLength();
            long lengthInBytes = audioFile.getAudioHeader().getBitRateAsNumber();

            Track newTrack = new Track(absolutePath, author, title, lengthInMilliseconds, lengthInBytes);

            if (playlist.addTrack(newTrack)) {
                DefaultTreeModel treeModel = (DefaultTreeModel) contextTree.getModel();
                DefaultMutableTreeNode playlistNode = contextTree.getSelectedNode();
                treeModel.insertNodeInto(new DefaultMutableTreeNode(newTrack), playlistNode, playlistNode.getChildCount());
            }
        }
    }

    private boolean hasAvailableExtension(String extension) {
        for (AudioFormat availableExtension : AudioFormat.values()) {
            if (Objects.requireNonNull(extension).equalsIgnoreCase(availableExtension.name())) {
                return true;
            }
        }

        return false;
    }

    private void addActionListenerToDeleteItem(JMenuItem deletePlaylist, PlaylistTree contextTree) {
        deletePlaylist.addActionListener(ae -> {
            DefaultMutableTreeNode playlistNode = contextTree.getSelectedNode();

            int result = Messages.showOkCancelDialog("Do you want to delete playlist \"" + playlistNode + "\"?",
                    "Delete", "Delete", Messages.CANCEL_BUTTON, Messages.getQuestionIcon());

            if (result == Messages.OK) {
                DefaultTreeModel treeModel = (DefaultTreeModel) contextTree.getModel();
                Playlist playlist = (Playlist) playlistNode.getUserObject();

                playlist.clear();
                treeModel.removeNodeFromParent(playlistNode);
            }
        });
    }
}
