package com.oitc.dendrobyte.leaderboards;

import org.bukkit.Bukkit;

import java.util.UUID;

/**
 * OneInTheChamber created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class CompetitorObject {

    /* Variables */
    UUID id;
    String username;
    int wins, eliminations, deaths;

    /* Constructor */
    public CompetitorObject(String uuid, int wins, int eliminations, int deaths){
        setId(UUID.fromString(uuid));
        this.wins = wins;
        this.eliminations = eliminations;
        this.deaths = deaths;
    }

    /* Pretty obvious getters and setters */
    public UUID getId() {
        return id;
    }

    // This will also update the username in memory
    public void setId(UUID id) {
        this.id = id;
        try {
            this.username = Bukkit.getPlayer(id).getName();
        } catch (NullPointerException e){
            this.username = Bukkit.getOfflinePlayer(id).getName();
        }
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getEliminations() {
        return eliminations;
    }

    public void setEliminations(int eliminations) {
        this.eliminations = eliminations;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}
