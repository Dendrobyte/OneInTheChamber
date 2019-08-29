package com.oitc.dendrobyte;

import com.oitc.dendrobyte.timers.ArenaRunningTimer;
import com.oitc.dendrobyte.timers.ArenaStartingTimer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaObject {

    private String name;
    private Location signLoc;
    private ArrayList<Location> spawnLocations = new ArrayList<>();
    private ArrayList<Player> players = new ArrayList<>();
    private ArenaGameState state = ArenaGameState.WAITING;
    private ArenaRunningTimer art;
    private ArenaStartingTimer ast;
    private HashMap<Player, Integer> playerElims = new HashMap<>();
    private HashMap<Player, Integer> playerDeaths = new HashMap<>();

    public ArenaObject(String name, Location signLoc, List<Location> spawnLocations){
        this.name = name;
        this.signLoc = signLoc;
        this.spawnLocations.addAll(spawnLocations);
    }

    public String getName(){
        return name;
    }

    public Location getSignLoc(){
        return signLoc;
    }

    public ArrayList<Location> getSpawnLocations(){
        return spawnLocations;
    }

    public Location getRandomSpawnLocation(){
        Random rand = new Random();
        int i = rand.nextInt(spawnLocations.size());
        Location recLoc = spawnLocations.get(i); // Hoping this fixes weird sign issue?
        return new Location(signLoc.getWorld(), recLoc.getBlockX(), recLoc.getBlockY(), recLoc.getBlockZ());
    }

    public ArrayList<Player> getPlayers(){
        return players;
    }

    public ArenaGameState getState(){
        return state;
    }

    public void setState(ArenaGameState state){
        this.state = state;
    }

    public void setArenaRunningTimer(ArenaRunningTimer art){
        this.art = art;
    }

    public ArenaRunningTimer getArt(){
        return art;
    }

    public HashMap<Player, Integer> getPlayerElims(){
        return playerElims;
    }

    public int incrementPlayerElims(Player player){
        if(!playerElims.containsKey(player)){
            playerElims.put(player, 1);
            return 1;
        } else {
            playerElims.put(player, playerElims.get(player)+1);
            return playerElims.get(player);
        }
    }

    public HashMap<Player, Integer> getPlayerDeaths(){
        return playerDeaths;
    }

    public int incrementPlayerDeaths(Player player){
        if(!playerDeaths.containsKey(player)){
            playerDeaths.put(player, 1);
            return 1;
        } else {
            playerDeaths.put(player, playerDeaths.get(player)+1);
            return playerDeaths.get(player);
        }
    }

    public void setArenaStartingTimer(ArenaStartingTimer ast){
        this.ast = ast;
    }

    public ArenaStartingTimer getAst(){
        return ast;
    }

    public void clearFields(){
        art = null;
        ast = null;
        players.clear();
        playerElims.clear();
        playerDeaths.clear();
        state = ArenaGameState.WAITING;
    }

}
