package com.dubrovskii.intellijmusicplugin.ui;

import com.dubrovskii.intellijmusicplugin.logic.HavingPopup;
import com.dubrovskii.intellijmusicplugin.logic.Playlist;
import com.dubrovskii.intellijmusicplugin.logic.Track;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PlaylistTree extends Tree {

    private DefaultMutableTreeNode selectedNode;

    public PlaylistTree() {
        super();
    }

    public PlaylistTree(TreeNode treeNode) {
        super(treeNode);
    }

    public PlaylistTree(TreeModel treeModel) {
        super(treeModel);
    }

    public void setDefaultPlaylistTreeCellRenderer() {
        setCellRenderer(getDefaultPlaylistTreeCellRenderer());
    }

    public void setDefaultPlaylistMouseListener() {
        PlaylistTree thisTree = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath pathToNode = thisTree.getSelectionPath();

                    if (pathToNode != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) pathToNode.getLastPathComponent();
                        if (node.getUserObject() instanceof HavingPopup) {
                            selectedNode = node;
                            JPopupMenu popupMenu = ((HavingPopup) node.getUserObject()).getPopup(thisTree);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }

    public void setPlaylistTreeModel(ArrayList<Playlist> playlists) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        if (playlists != null) {
            for (Playlist playlist : playlists) {
                DefaultMutableTreeNode playlistNode = new DefaultMutableTreeNode(playlist);
                root.add(playlistNode);

                for (Track track : playlist.getAllTracks()) {
                    DefaultMutableTreeNode trackNode = new DefaultMutableTreeNode(track);
                    playlistNode.add(trackNode);
                }
            }
        }

        setModel(new DefaultTreeModel(root));
    }

    public ArrayList<Playlist> getPlaylists() {
        ArrayList<Playlist> playlists = new ArrayList<>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode playlistNode = (DefaultMutableTreeNode) root.getChildAt(i);
            Playlist playlist = (Playlist) playlistNode.getUserObject();
            playlists.add(playlist);
        }

        return playlists;
    }

    private NodeRenderer getDefaultPlaylistTreeCellRenderer() {
        return new NodeRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree jTree, Object value, boolean selected, boolean expanded,
                                              boolean leaf, int row, boolean hasFocus) {
                super.customizeCellRenderer(jTree, value, selected, expanded, leaf, row, hasFocus);
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                if (node.getUserObject() instanceof Playlist) {
                    setIcon(MyIcons.PLAYLIST);
                } else if (node.getUserObject() instanceof Track) {
                    Track track = (Track) node.getUserObject();
                    setIcon(track.getAudioFormat().getIcon());
                }
            }
        };
    }

    public void refreshPlaylistsTracks() {
        DefaultTreeModel treeModel = (DefaultTreeModel) getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();

        ArrayList<DefaultMutableTreeNode> nodesToDelete = new ArrayList<>();
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode playlistNode = (DefaultMutableTreeNode) root.getChildAt(i);
            Playlist playlist = (Playlist) playlistNode.getUserObject();

            for (int j = 0; j < playlistNode.getChildCount(); j++) {
                DefaultMutableTreeNode trackNode = (DefaultMutableTreeNode) playlistNode.getChildAt(j);
                Track track = (Track) trackNode.getUserObject();

                if (!Files.exists(Paths.get(track.getAbsolutePath()))) {
                    nodesToDelete.add(trackNode);
                }
            }
        }

        for (DefaultMutableTreeNode nodeToDelete : nodesToDelete) {
            treeModel.removeNodeFromParent(nodeToDelete);
        }
    }

    public DefaultMutableTreeNode getSelectedNode() {
        return selectedNode;
    }
}
