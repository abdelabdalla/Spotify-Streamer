package net.ddns.sabr.spotifystreamer;

import android.app.Fragment;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Abdel on 27/07/2015.
 */
public class Top10Fragment extends Fragment{

    private static SongAdapter songAdapter;

    static Toast t;

    static Song[] songs = {new Song("Name","Album","")};

    static String[] namesArtists;
    static String[] imgLocsArtists;
    static String[] idsArtists;

    public Top10Fragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate((savedInstanceState));
        setHasOptionsMenu(true);
    }

}
