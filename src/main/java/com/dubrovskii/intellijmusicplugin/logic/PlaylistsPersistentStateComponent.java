package com.dubrovskii.intellijmusicplugin.logic;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

@State(
        name = "playlists",
        storages = {@Storage("playlists.xml")}
)
public class PlaylistsPersistentStateComponent implements PersistentStateComponent<PlaylistsPersistentStateComponent> {

    private ArrayList<Playlist> playlists;

    @Nullable
    @Override
    public PlaylistsPersistentStateComponent getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PlaylistsPersistentStateComponent state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean hasPersistedData() {
        return (playlists != null);
    }

    public void persistPlaylists(ArrayList<Playlist> playlists) {
        if (!Objects.equals(this.playlists, playlists)) {
            this.playlists = new ArrayList<>(playlists);
        }
    }

    public ArrayList<Playlist> getPersistedPlaylists() {
        return playlists;
    }
}
