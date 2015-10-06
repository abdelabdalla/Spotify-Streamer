package net.ddns.sabr.spotifystreamer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.IOException;

public class PlayerActivity extends ActionBarActivity {

    static SeekBar seek;
    static MediaPlayer mediaPlayer;
    static Handler seekHandler = new Handler();
    static int pos = 0;

    static String[] nameA = {};
    static String[] albumA = {};
    static String[] imgA = {};
    static String[] urlA = {};
    static String artistS = "";

    static TextView artist;
    static TextView album;
    static TextView song;

    static ImageView image;

    static ImageButton play;
    static boolean[] playing = {false};
    static ImageButton next;
    static ImageButton prev;

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
        android.support.v7.app.ActionBar a = getSupportActionBar();
        a.setHomeButtonEnabled(false);
        a.setDisplayHomeAsUpEnabled(false);
        a.setDisplayShowHomeEnabled(false);
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

            artist = (TextView) view.findViewById(R.id.artistText);
            album = (TextView) view.findViewById(R.id.albumText);
            song = (TextView) view.findViewById(R.id.songText);

            image = (ImageView) view.findViewById(R.id.imageView);

            play = (ImageButton) view.findViewById(R.id.playButton);
            next = (ImageButton) view.findViewById(R.id.nextButton);
            prev = (ImageButton) view.findViewById(R.id.prevButton);

            seek = (SeekBar) view.findViewById(R.id.seekBar);


            Intent intent = getActivity().getIntent();

            if(intent != null){
                nameA = intent.getStringArrayExtra("name");
                albumA  = intent.getStringArrayExtra("album");
                imgA  = intent.getStringArrayExtra("img");
                urlA  = intent.getStringArrayExtra("url");
                artistS  = intent.getStringExtra("artist");
                pos = intent.getIntExtra("pos",0);
            }

            setup();

            seekUpdate();

            final int[] pauseat = {0};
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playing[0] ^= true;

                    try {

                        if (playing[0]) {

                            play.setImageResource(android.R.drawable.ic_media_pause);
                            Log.v("playing", "play");
                            if (pauseat[0] < 28) {
                                mediaPlayer.seekTo(pauseat[0]);
                            }
                            mediaPlayer.start();

                        } else {

                            play.setImageResource(android.R.drawable.ic_media_play);
                            Log.v("playing", "pause");
                            pauseat[0] = mediaPlayer.getCurrentPosition();
                            mediaPlayer.pause();

                        }

                    } catch (Exception e) {

                    }
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos--;
                    mediaPlayer.pause();
                    mediaPlayer.release();
                    play.setImageResource(android.R.drawable.ic_media_play);
                    playing[0] = false;
                    pauseat[0] = 0;
                    setup();
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos++;
                    mediaPlayer.pause();
                    mediaPlayer.release();
                    play.setImageResource(android.R.drawable.ic_media_play);
                    playing[0] = false;
                    pauseat[0] = 0;
                    setup();
                }
            });

            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            });


            return view;
        }


        public void seekUpdate(){
            seek.setProgress(mediaPlayer.getCurrentPosition());
            seekHandler.postDelayed(run, 1);
        }

        public void setup() {
            artist.setText(artistS);
            song.setText(nameA[pos]);
            album.setText(albumA[pos]);

            Picasso.with(getActivity()).load(imgA[pos]).into(image);

            String url = urlA[pos];
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            seek.setMax(mediaPlayer.getDuration());
        }

        Runnable run = new Runnable() {
            @Override
            public void run() {
                seekUpdate();
            }
        };
    }
}