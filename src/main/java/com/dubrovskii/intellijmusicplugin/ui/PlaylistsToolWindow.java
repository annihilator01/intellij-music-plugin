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
import java.util.Objects;

public class PlaylistsToolWindow {

    private JPanel mainPanel;
    private PlaylistTree playlistTree;
    private JPanel toolbarPanel;
    private JButton addPlaylistButton;
    private JButton refreshPlaylistsButton;
    private JButton pauseResumeButton;
    private JButton previousTrackButton;
    private JButton nextTrackButton;
    private JLabel currentTrackLabel;

    private PlayerGUI player;

    private PlaylistsPersistentStateComponent playlistsService;

    public PlaylistsToolWindow() {
        init();
    }

    private void init() {
        // player
        player = PlayerGUI.getInstance();
        player.setInfoLabel(currentTrackLabel);

        // playlists
        ArrayList<Playlist> playlists = null;

        playlistsService = ServiceManager.getService(PlaylistsPersistentStateComponent.class);
        if (playlistsService.hasPersistedData()) {
            playlists = playlistsService.getPersistedPlaylists();
        }

        playlistTree.setPlaylistTreeModel(playlists);
        playlistTree.setRootVisible(false);
        //playlistTree.setModel(getTreeModel()); // test
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

        addListenerToResumeButton();
        addListenerToNextTrackButton();
        addListenerTpPreviousTrackButton();
    }

    public void addListenerToAddPlaylistButton() {
        addPlaylistButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    String title = Messages.showInputDialog("Input title of the playlist", "Add Playlist", Messages.getInformationIcon());

                    if (title == null || title.equals("")) {
                        return;
                    }

                    Playlist newPlaylist = new Playlist(title);

                    DefaultTreeModel treeModel = (DefaultTreeModel) playlistTree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
                    treeModel.insertNodeInto(new DefaultMutableTreeNode(newPlaylist), root, root.getChildCount());

                    if (root.getChildCount() == 1) {
                        treeModel.reload();
                    }
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

    public void addListenerToResumeButton() {
        pauseResumeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (player.isPaused() == 0) {
                        player.pause();
                    } else if (player.isPaused() == 1) {
                        player.resume();
                    } else if (player.isPaused() == 2) {
                        if (player.isAbleToPlay()) {
                            player.play();
                        }
                    }
                }
            }
        });
    }

    public void addListenerToNextTrackButton() {
        nextTrackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    player.playNext();
                }
            }
        });
    }

    public void addListenerTpPreviousTrackButton() {
        previousTrackButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    player.playPrevious();
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
