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
 * Created by Abdel on 24/06/2015.
 */
public class ArtistAdapter extends ArrayAdapter<Artists>{

    public ArtistAdapter(Context c, ArrayList<Artists> artists){
        super(c, 0, artists);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){

        Artists artist = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_artist, parent, false);
        }

        TextView name = (TextView) view.findViewById(R.id.artisttextView);
        ImageView image = (ImageView) view.findViewById(R.id.albumimage);

        name.setText(artist.name);
        if(!artist.imgLoc.equals("")) {
            Picasso.with(getContext()).load(artist.imgLoc).into(image);
        } else {
            Picasso.with(getContext()).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png").into(image);
        }


        return view;
    }

}
