package net.ddns.sabr.spotifystreamer;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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
            String filename = artist.imgLoc.replace("/","\\");
            File f = new File(getContext().getFilesDir().getPath() + "/" + filename);
            Uri uri = Uri.fromFile(f);
            if(f.exists()){
                Picasso.with(getContext()).load(uri);
            } else {
                FetchImages fetch = new FetchImages();
                fetch.execute(artist.imgLoc);
            }

            Picasso.with(getContext()).load(artist.imgLoc).into(image);
        } else {
            Picasso.with(getContext()).load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/300px-No_image_available.svg.png").into(image);
        }


        return view;
    }

    class FetchImages extends AsyncTask<String, Void, Void>{


        @Override
        protected Void doInBackground(String... params) {

            try{

                String buffer;
                URLConnection conn = new URL(params[0]).openConnection();
                conn.setUseCaches(false);
                conn.connect();
                InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(isr);

                String name = params[0].replace("/","\\");

                FileOutputStream fos = getContext().openFileOutput(name, Context.MODE_PRIVATE);

                while ((buffer = br.readLine()) != null) {
                    fos.write(buffer.getBytes());
                }

                fos.close();
                br.close();
                isr.close();

            } catch (IOException e){

            }

            return null;
        }
    }

}
