package net.ddns.sabr.spotifystreamer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;

public class ArtistFragment extends Fragment {

    private ArtistAdapter artistAdapter;

    Toast t;

    Artists[] artistList = {new Artists("Enter Name Above","https://upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Aiga_uparrow.svg/500px-Aiga_uparrow.svg.png","")};

    public String artistName = "";
    EditText search;

    public ArtistFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate((savedInstanceState));
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.fragmentmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int id = item.getItemId();
        if(id == R.id.action_refresh){
            FetchArtistClass fetch = new FetchArtistClass();
            fetch.execute(new myTask(null,artistName));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        t = Toast.makeText(getActivity(), "This isn't a real artist :( try again", Toast.LENGTH_SHORT);

        search = (EditText) rootView.findViewById(R.id.artistSearch);

        ArrayList<Artists> a = new ArrayList<>(Arrays.asList(artistList));
        artistAdapter = new ArtistAdapter(getActivity(), a);

        ListView listView = (ListView) rootView.findViewById(R.id.artist_list_view);
        listView.setAdapter(artistAdapter);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (isConnected()) {
                            FetchArtistClass fetch = new FetchArtistClass();
                            myTask m = new myTask(savedInstanceState,s.toString());
                            fetch.execute(m);
                    } else {
                        Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(artistAdapter.getItem(position).name.equals("Enter Name Above")){

                    t.show();
                } else {
                    String t = artistAdapter.getItem(position).id;
                    Intent intent = new Intent(getActivity(), Top10Activity.class).putExtra(Intent.EXTRA_TEXT, t);
                    startActivity(intent);
                }

            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Log.v("f", "at save");

        //savedInstanceState.putBoolean("clear", false);

        String[] names = new String[artistAdapter.getCount()];
        String[] imgLocs = new String[artistAdapter.getCount()];
        String[] id = new String[artistAdapter.getCount()];

        for(int i = 0; i < artistAdapter.getCount(); i++){
            names[i] = artistAdapter.getItem(i).name;
            imgLocs[i] = artistAdapter.getItem(i).imgLoc;
            id[i] = artistAdapter.getItem(i).id;
        }

        savedInstanceState.putStringArray("names", names);
        savedInstanceState.putStringArray("imgLocs",imgLocs);
        savedInstanceState.putStringArray("id",id);

        super.onSaveInstanceState(savedInstanceState);
    }

    boolean isConnected(){
        //http://developer.android.com/training/monitoring-device-state/connectivity-monitoring.html
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private class myTask{
        Bundle bundle;
        String name;
        myTask(Bundle bundle, String name){
            this.bundle = bundle;
            this.name = name;
        }
    }

    public class FetchArtistClass extends AsyncTask<myTask, Void, Artists[]> {

            @Override
            protected Artists[] doInBackground(myTask... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Artists[] list;

                if (params[0].bundle != null) {
                    String[] name = params[0].bundle.getStringArray("names");
                    String[] imgLocs = params[0].bundle.getStringArray("imgLocs");
                    String[] id = params[0].bundle.getStringArray("id");
                    list = new Artists[name.length];
                    for (int i = 0; i < name.length; i++) {
                        list[i] = new Artists(name[i], imgLocs[i], id[i]);
                    }
                    params[0].bundle = null;
                }

                else {
                    SpotifyApi api = new SpotifyApi();

                    SpotifyService spotify = api.getService();

                    ArtistsPager results = spotify.searchArtists(params[0].name);

                    ArrayList<Artists> artistList = new ArrayList<Artists>();

                    int i = 0;

                    for (ArtistSimple a : results.artists.items) {
                        //Log.v("nameArt", a.name);
                        Artist a2 = results.artists.items.get(i);
                        if (a2.images.size() > 0) {
                            //Log.v("picArt", a2.images.get(0).url);
                            artistList.add(new Artists(a.name, a2.images.get(0).url, a.id));
                        } else {
                            artistList.add(new Artists(a.name, "", a.id));
                        }
                        i++;
                    }

                    list = new Artists[artistList.size()];
                    list = artistList.toArray(list);
                }
                return list;
            }

            @Override
            protected void onPostExecute(Artists[] artists) {

                if(artists.length == 0 && artists != null){
                    artistAdapter.clear();
                    t.show();

                } else if(artists != null) {
                    artistAdapter.clear();
                    artistAdapter.addAll(artists);
                }
            }
        }

}
