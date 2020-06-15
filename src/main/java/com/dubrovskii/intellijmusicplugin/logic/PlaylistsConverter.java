package com.dubrovskii.intellijmusicplugin.logic;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsConverter extends Converter<ArrayList<Playlist>> {

    @Nullable
    @Override
    public ArrayList<Playlist> fromString(@NotNull String jsonString) {
        ArrayList<Playlist> finalPlaylists = new ArrayList<>();

        List<LinkedTreeMap> playlists = new Gson().fromJson(jsonString, ArrayList.class);
        for (LinkedTreeMap playlist : playlists) {
            String playlistTitle = ((String) playlist.get("title"));
            Playlist finalPlaylist = new Playlist(playlistTitle);

            List<LinkedTreeMap> tracks = ((List<LinkedTreeMap>) playlist.get("tracks"));
            for (LinkedTreeMap track : tracks) {
                String absolutePath = ((String) track.get("absolutePath"));
                String author = ((String) track.get("author"));
                String trackTitle = ((String) track.get("title"));
                int lengthInMilliseconds = ((Number) track.get("lengthInMilliseconds")).intValue();
                int lengthInBytes = ((Number) track.get("lengthInBytes")).intValue();

                finalPlaylist.addTrack(new Track(absolutePath, author, trackTitle, lengthInMilliseconds, lengthInBytes));
            }

            finalPlaylists.add(finalPlaylist);
        }

        return finalPlaylists;
    }

    @Nullable
    @Override
    public String toString(@NotNull ArrayList<Playlist> playlists) {
        return new Gson().toJson(playlists);
    }
}
