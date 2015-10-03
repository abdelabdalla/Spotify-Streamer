package net.ddns.sabr.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PlayerFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);

        TextView artist = (TextView) view.findViewById(R.id.artistText);
        TextView album = (TextView) view.findViewById(R.id.albumText);
        TextView song = (TextView) view.findViewById(R.id.songText);

        ImageView image = (ImageView) view.findViewById(R.id.imageView);

        ImageButton play = (ImageButton) view.findViewById(R.id.playButton);
        ImageButton next = (ImageButton) view.findViewById(R.id.nextButton);
        ImageButton prev = (ImageButton) view.findViewById(R.id.prevButton);

        SeekBar seek = (SeekBar) view.findViewById(R.id.seekBar);


        Intent intent = getActivity().getIntent();

        String[] s = {"On My Mind","On My Mind","https://d3rt1990lpmkn.cloudfront.net/original/8e13218039f81b000553e25522a7f0d7a0600f2e","https://p.scdn.co/mp3-preview/b9547ec4daddc23d1fda0347985f574860d64777"};


        if(intent != null){
            s = intent.getStringArrayExtra("songToPlay");
        }

        artist.setText("");
        album.setText(s[1]);
        song.setText(s[0]);

        Context c = getActivity();


       // Picasso.with(c).load(s[2]).into(image);


        return view;
    }
}
