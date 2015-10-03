package net.ddns.sabr.spotifystreamer;

public class Song {

    public String name;
    public String album;
    public String imgLoc;
    public String url;


    public Song(String name, String album, String imgLoc, String url){
        this.name = name;
        this.album = album;
        this.imgLoc = imgLoc;
        this.url = url;
    }

    public String getName(){
        return name;
    }

    public String getAlbum(){
        return album;
    }

    public String getImgLoc(){
        return imgLoc;
    }

    public String getUrl(){
        return url;
    }
}
