package net.ddns.sabr.spotifystreamer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class Top10Activity extends ActionBarActivity {

    private static SongAdapter songAdapter;

    static Toast t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_top10, container, false);

            t = Toast.makeText(getActivity(), "Unable to fetch top tracks :(", Toast.LENGTH_SHORT);

            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){

                String artistName = intent.getStringExtra(Intent.EXTRA_TEXT);
                //((TextView) rootView.findViewById(R.id.artistText)).setText(artistName);

                ArrayList<Song> songs = new ArrayList<>();
                //songs.add(new Song("name","album",""));
                songAdapter = new SongAdapter(getActivity(), songs);

                ListView listView = (ListView) rootView.findViewById(R.id.listView);
                listView.setAdapter(songAdapter);

                FetchSongsClass f = new FetchSongsClass();
                f.execute(new myTask(savedInstanceState,artistName));

            }

            return rootView;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v("f", "at save");

        String[] names = new String[songAdapter.getCount()];
        String[] albums = new String[songAdapter.getCount()];
        String[] imgLocs = new String[songAdapter.getCount()];

        for(int i = 0; i < songAdapter.getCount(); i++){
            names[i] = songAdapter.getItem(i).name;
            imgLocs[i] = songAdapter.getItem(i).imgLoc;
            albums[i] = songAdapter.getItem(i).album;
        }

        savedInstanceState.putStringArray("names",names);
        savedInstanceState.putStringArray("imgLocs",imgLocs);
        savedInstanceState.putStringArray("albums",albums);

        super.onSaveInstanceState(savedInstanceState);
    }

    private static class myTask{
        Bundle bundle;
        String name;
        myTask(Bundle bundle, String name){
            this.bundle = bundle;
            this.name = name;
        }
    }

    public static class FetchSongsClass extends AsyncTask<myTask, Void, Song[]>{


        @Override
        protected Song[] doInBackground(myTask... params) {

            Song[] list;

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(params[0].bundle == null) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                Map<String, Object> m = new HashMap<>();
                if (Locale.getDefault().getCountry().equals("")) {
                    m.put("country", "US");
                } else {
                    m.put("country", Locale.getDefault().getCountry());
                }
                Tracks results = spotify.getArtistTopTrack(params[0].name, m);

                ArrayList<Song> songs = new ArrayList<>();


                for (Track t : results.tracks) {
                    Log.v("songs", t.name);

                    if (t.album.images.size() > 0) {
                        songs.add(new Song(t.name, t.album.name, t.album.images.get(0).url));
                    } else {
                        songs.add(new Song(t.name, t.album.name, ""));
                    }

                }

                list = new Song[songs.size()];
                list = songs.toArray(list);
            } else{

                String[] name = params[0].bundle.getStringArray("names");
                String[] imgLocs = params[0].bundle.getStringArray("imgLocs");
                String[] albums = params[0].bundle.getStringArray("albums");
                list = new Song[name.length];
                for(int i = 0; i < name.length; i++){
                    list[i] = new Song(name[i],albums[i],imgLocs[i]);
                }

            }

            return list;
        }

        @Override
        protected void onPostExecute(Song[] songs) {

            if(songs.length == 0){
                songAdapter.clear();
                t.show();

            } else {
                songAdapter.clear();
                songAdapter.addAll(songs);
            }
        }
        }

}