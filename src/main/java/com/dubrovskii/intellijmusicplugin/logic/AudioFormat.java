package com.dubrovskii.intellijmusicplugin.logic;

import com.dubrovskii.intellijmusicplugin.ui.MyIcons;

import javax.swing.*;

public enum AudioFormat {
    MP3 (MyIcons.MP3_TRACK),
    WAV (MyIcons.WAV_TRACK),
    OGG (MyIcons.OGG_TRACK);

    private Icon icon;

    AudioFormat(Icon icon) {
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }
}
