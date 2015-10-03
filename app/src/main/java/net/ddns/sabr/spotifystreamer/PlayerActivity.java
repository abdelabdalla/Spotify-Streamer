package net.ddns.sabr.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PlayerActivity extends FragmentActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.player_container, new PlayerFragment())
                .commit();
    }
}