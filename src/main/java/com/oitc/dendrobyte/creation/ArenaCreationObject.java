package com.oitc.dendrobyte.creation;

import org.bukkit.Location;

import java.util.ArrayList;

/**
 * Created by mobki, aka Dendrobyte, on 8/25/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaCreationObject {

    private String name;
    private Location signLoc;
    private ArrayList<Location> locations = new ArrayList<>();

    public ArenaCreationObject(){

    }

    public String getName(){
        return name;
    }

    public Location getSignLoc() {
        return signLoc;
    }

    public ArrayList<Location> getLocations(){
        return locations;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSignLoc(Location signLoc){
        this.signLoc = signLoc;
    }

    public void addLocation(Location loc){
        locations.add(loc);
    }

}
