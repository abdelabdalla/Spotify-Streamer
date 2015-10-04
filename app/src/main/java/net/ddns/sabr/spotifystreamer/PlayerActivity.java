package net.ddns.sabr.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PlayerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlayerFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlayerFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_player, container, false);

            TextView artist = (TextView) view.findViewById(R.id.artistText);
            TextView album = (TextView) view.findViewById(R.id.albumText);
            TextView song = (TextView) view.findViewById(R.id.songText);

            ImageView image = (ImageView) view.findViewById(R.id.imageView);

            final ImageButton play = (ImageButton) view.findViewById(R.id.playButton);
            final boolean[] playing = {false};
            ImageButton next = (ImageButton) view.findViewById(R.id.nextButton);
            ImageButton prev = (ImageButton) view.findViewById(R.id.prevButton);

            SeekBar seek = (SeekBar) view.findViewById(R.id.seekBar);


            Intent intent = getActivity().getIntent();

            String[] s = {"On My Mind","On My Mind","https://d3rt1990lpmkn.cloudfront.net/original/8e13218039f81b000553e25522a7f0d7a0600f2e","https://p.scdn.co/mp3-preview/b9547ec4daddc23d1fda0347985f574860d64777","Ellie Goulding"};


            if(intent != null){
                s = intent.getStringArrayExtra("songToPlay");
            }

            artist.setText(s[4]);
            song.setText(s[0]);
            album.setText(s[1]);

            Picasso.with(getActivity()).load(s[2]).into(image);


            final String[] finalS = s;
            final int[] pauseat = {0};
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playing[0] ^= true;

                    try {
                        String url = finalS[3]; // your URL here
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(url);
                        mediaPlayer.prepare();

                        if (playing[0]) {

                            play.setImageResource(android.R.drawable.ic_media_pause);
                            //Log.v("play", "play");
                            mediaPlayer.seekTo(pauseat[0]);
                            mediaPlayer.start();

                        } else {

                            play.setImageResource(android.R.drawable.ic_media_play);
                            //Log.v("play", "pause");

                            mediaPlayer.pause();
                            pauseat[0] = mediaPlayer.getCurrentPosition();

                        }

                    } catch (Exception e){

                    }
                }
            });


            return view;
        }
    }
}