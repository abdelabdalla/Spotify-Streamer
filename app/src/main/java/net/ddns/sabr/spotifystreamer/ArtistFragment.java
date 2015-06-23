package net.ddns.sabr.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    private ArrayAdapter<String> adapter;

    public String artistName = "Ellie Goulding";
    String[] dataArray = {
        "Search for an artist above"
    };

    

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
            fetch.execute(artistName);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> fakeData = new ArrayList<>(Arrays.asList(dataArray));

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_artist, R.id.list_item_artist_textview, fakeData);

        ListView listView = (ListView) rootView.findViewById(R.id.artist_list_view);
        listView.setAdapter(adapter);

        return rootView;
    }

        public class FetchArtistClass extends AsyncTask<String, Void, String[]> {

            @Override
            protected String[] doInBackground(String... params) {

               SpotifyApi api = new SpotifyApi();

                SpotifyService spotify = api.getService();

                ArtistsPager results = spotify.searchArtists(params[0]);

                String[] artistList = new String[results.artists.items.size()];

                int i = 0;

                for(ArtistSimple a : results.artists.items){
                    artistList[i] = a.name;
                    Log.v("nameArt", a.name);
                    i++;
                }

                return artistList;
            }

            @Override
            protected void onPostExecute(String[] strings) {

                if(strings != null){
                    adapter.clear();
                    adapter.addAll(strings);
                }

            }
        }

}
