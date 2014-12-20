package com.patrick.recordremoteapplication;

/**
 * Created by pat on 12/19/2014.
 */
public class Song {

    Song(){

    }

    Song(String title, Integer breakNum){

        Title = title;
        BreakNum = breakNum;
    }

    Song(String title, String artist, String album, Integer id, Integer breakNum, byte[] key){

        Title = title;
        Artist = artist;
        Album = album;
        BreakNum = breakNum;
        ID = id;
        Key = key;
    }
    public String Title;
    public String Artist;
    public String Album;
    public Integer BreakNum;
    public Integer ID;
    public byte[] Key;

    @Override
    public String toString(){
       return (BreakNum + 1) + ". " + Title;
    }
}
