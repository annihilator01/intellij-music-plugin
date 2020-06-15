package com.dubrovskii.intellijmusicplugin.ui;

import com.dubrovskii.intellijmusicplugin.logic.Playlist;
import com.dubrovskii.intellijmusicplugin.logic.PlaylistsPersistentStateComponent;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PlaylistsToolWindow {

    private JPanel mainPanel;
    private PlaylistTree playlistTree;
    private JPanel toolbarPanel;
    private JButton addPlaylistButton;
    private JButton refreshPlaylistsButton;
    private JButton pauseResumeButton;
    private JButton previousTrackButton;
    private JButton nextTrackButton;
    private JLabel currentTrack;

    private PlaylistsPersistentStateComponent playlistsService;

    public PlaylistsToolWindow() {
        init();
    }

    private void init() {
        // playlists
        ArrayList<Playlist> playlists = null;

        playlistsService = ServiceManager.getService(PlaylistsPersistentStateComponent.class);
        if (playlistsService.hasPersistedData()) {
            playlists = playlistsService.getPersistedPlaylists();
        }

        //playlistTree.setPlaylistTreeModel(playlists);
        playlistTree.setModel(getTreeModel()); // test
        playlistTree.setRootVisible(false);
        playlistTree.setDefaultPlaylistTreeCellRenderer();
        playlistTree.setDefaultPlaylistMouseListener();
        playlistTree.refreshPlaylistsTracks();

        addPlaylistButton.setIcon(AllIcons.General.Add);
        refreshPlaylistsButton.setIcon(AllIcons.Actions.Refresh);

        pauseResumeButton.setIcon(AllIcons.Actions.Resume);
        nextTrackButton.setIcon(AllIcons.Actions.Rerun);
        previousTrackButton.setIcon(MyIcons.PREVIOUS_TRACK);

        addListenerToAddPlaylistButton();
        addListenerToRefreshPlaylistButton();
    }

    public void addListenerToAddPlaylistButton() {
        addPlaylistButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String title = Messages.showInputDialog("Input title of the playlist", "Add Playlist", Messages.getInformationIcon());
                    Playlist newPlaylist = new Playlist(title);

                    DefaultTreeModel treeModel = (DefaultTreeModel) playlistTree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
                    treeModel.insertNodeInto(new DefaultMutableTreeNode(newPlaylist), root, root.getChildCount());
                }
            }
        });
    }

    public void addListenerToRefreshPlaylistButton() {
        refreshPlaylistsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    playlistTree.refreshPlaylistsTracks();
                }
            }
        });
    }


    private TreeModel getTreeModel() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        root.add(new DefaultMutableTreeNode(new Playlist("best 100")));
        root.add(new DefaultMutableTreeNode(new Playlist("pop")));
        root.add(new DefaultMutableTreeNode(new Playlist("rap")));

        return new DefaultTreeModel(root);
    }

    public JPanel getContent() {
        return mainPanel;
    }
}
