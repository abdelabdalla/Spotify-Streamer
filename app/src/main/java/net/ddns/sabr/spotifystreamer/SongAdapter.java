package net.ddns.sabr.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abdel on 07/07/2015.
 */
public class SongAdapter extends ArrayAdapter<Song>{

    public SongAdapter(Context c, ArrayList<Song> songs){super(c, 0, songs);}

    @Override
    public View getView(int position, View view, ViewGroup parent){

        Song song = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_top10, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.song_name);
        TextView album = (TextView) view.findViewById(R.id.album_name);
        ImageView image = (ImageView) view.findViewById(R.id.albumimage);

        name.setText(song.name);
        album.setText(song.album);

        if(!song.imgLoc.equals("")) {
            Picasso.with(getContext()).load(song.imgLoc).into(image);
        } else {
            Picasso.with(getContext()).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png").into(image);
        }

        return view;
    }

}
