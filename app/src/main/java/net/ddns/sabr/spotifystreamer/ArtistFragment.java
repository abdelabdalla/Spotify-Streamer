package net.ddns.sabr.spotifystreamer;

import android.content.Intent;
import android.media.Image;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistFragment extends Fragment {

    private ArrayAdapter<String> adapterText;
    private ArrayAdapter<Image> adapterImage;


    public String artistName = "Ellie Goulding";
    String[] dataArray = {
        "Search for an artist above"
    };

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
            fetch.execute(artistName);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        search = (EditText) rootView.findViewById(R.id.artistSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty()){
                    FetchArtistClass fetch = new FetchArtistClass();
                    fetch.execute(s.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ArrayList<String> fakeData = new ArrayList<>(Arrays.asList(dataArray));

        adapterText = new ArrayAdapter<String>(getActivity(), R.layout.list_item_artist, R.id.artisttextView, fakeData);
        //adapterImage = new ArrayAdapter<Image>(getActivity(), R.layout.list_item_artist, R.id.artistImage, );

        ListView listView = (ListView) rootView.findViewById(R.id.artist_list_view);
        listView.setAdapter(adapterText);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String t = adapterText.getItem(position);
                Intent intent = new Intent(getActivity(), Top10Activity.class).putExtra(Intent.EXTRA_TEXT, t);
                startActivity(intent);

            }
        });

        return rootView;
    }

        public class FetchArtistClass extends AsyncTask<String, Void, String[]> {

            @Override
            protected String[] doInBackground(String... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


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

                fetchimages f = new fetchimages();
                f.execute(results);

                return artistList;
            }

            @Override
            protected void onPostExecute(String[] strings) {

                if(strings != null){
                    adapterText.clear();
                    adapterText.addAll(strings);
                }

            }
        }

    public class fetchimages extends AsyncTask<ArtistsPager, Void, String[]>{

        @Override
        protected String[] doInBackground(ArtistsPager... params) {

            for(Artist a : params[0].artists.items){
                if(a.images.size() > 0){
                    Log.v("picArt", a.images.get(0).url);
                }
            }


            return new String[0];
        }
    }

}
