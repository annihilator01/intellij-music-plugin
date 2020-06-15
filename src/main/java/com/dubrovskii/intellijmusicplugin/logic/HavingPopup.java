package com.dubrovskii.intellijmusicplugin.logic;

import com.dubrovskii.intellijmusicplugin.ui.PlaylistTree;

import javax.swing.*;

public interface HavingPopup {
    JPopupMenu getPopup(PlaylistTree contextTree);
}
