package net.ddns.sabr.spotifystreamer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    static Song[] songs = {new Song("Name","Album","")};

    static String[] namesArtists;
    static String[] imgLocsArtists;
    static String[] idsArtists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.top10_container, new Top10Fragment())
                    .commit();

        } else {

            String[] name = savedInstanceState.getStringArray("namesSong");
            String[] imgLocs = savedInstanceState.getStringArray("imgLocsSong");
            String[] album = savedInstanceState.getStringArray("albumsSong");

            namesArtists = savedInstanceState.getStringArray("namesArtists");
            imgLocsArtists  = savedInstanceState.getStringArray("imgLocsArtists");
            idsArtists = savedInstanceState.getStringArray("idsArtists");

            songs = new Song[name.length];
            for (int i = 0; i < name.length; i++) {
                songs[i] = new Song(name[i],album[i], imgLocs[i]);
            }

            /*getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();*/
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
        if(id == android.R.id.home){

                Intent intent = new Intent(getApplicationContext(), MainActivity.class).putExtra("names",namesArtists).putExtra("imgLocs",imgLocsArtists).putExtra("ids",idsArtists);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Top10Fragment extends Fragment {

        public Top10Fragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_top10, container, false);

            t = Toast.makeText(getActivity(), "Unable to fetch top tracks :(", Toast.LENGTH_SHORT);

            if(intent != null && intent.hasExtra("t")){

                String[] artistExtra = intent.getStringArrayExtra("t");

                namesArtists = intent.getStringArrayExtra("names");
                imgLocsArtists = intent.getStringArrayExtra("imgLocs");
                idsArtists = intent.getStringArrayExtra("ids");

                ActionBar bar = ((ActionBarActivity)getActivity()).getSupportActionBar();
                bar.setSubtitle(artistExtra[1]);

                ArrayList<Song> songs = new ArrayList<>();
                //songs.add(new Song("name","album",""));
                songAdapter = new SongAdapter(getActivity(), songs);

                ListView listView = (ListView) rootView.findViewById(R.id.listView);
                listView.setAdapter(songAdapter);

                FetchSongsClass f = new FetchSongsClass();
                f.execute(new myTask(savedInstanceState,artistExtra[0]));

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

        savedInstanceState.putStringArray("namesSong",names);
        savedInstanceState.putStringArray("imgLocsSong",imgLocs);
        savedInstanceState.putStringArray("albumsSong",albums);

        savedInstanceState.putStringArray("namesArtists",namesArtists);
        savedInstanceState.putStringArray("imgLocsArtists",imgLocsArtists);
        savedInstanceState.putStringArray("idsArtists",idsArtists);

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

    static boolean isConnected(){
        //http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm =
                (ConnectivityManager)cont().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    static Application cont(){
        //http://stackoverflow.com/a/12495865/1695220
        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            return (Application) method.invoke(null, (Object[]) null);
        } catch (final ClassNotFoundException e) {
            // handle exception
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
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

            if(params[0].bundle != null){
                list = songs;
            } else if(!isConnected()){
                    songs = new Song[1];
                    songs[0] = new Song("Name","Album","");
                list = songs;
            }
            else {
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
            }


            /*if(params[0].bundle == null) {
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

                String[] name = params[0].bundle.getStringArray("namesSong");
                String[] imgLocs = params[0].bundle.getStringArray("imgLocsSong");
                String[] albums = params[0].bundle.getStringArray("albumsSong");
                list = new Song[name.length];
                for(int i = 0; i < name.length; i++){
                    list[i] = new Song(name[i],albums[i],imgLocs[i]);
                }

            }*/

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