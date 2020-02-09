package com.oitc.dendrobyte.leaderboards;

import com.oitc.dendrobyte.ArenaObject;
import com.oitc.dendrobyte.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * OneInTheChamber created/started by markb (Mobkinz78/Dendrobyte)
 * Please do not use or edit without permission!
 * If you have any questions, reach out to me on Twitter: @Mobkinz78
 * §
 */
public class LeaderboardManager {

    private LeaderboardManager (){}

    private static LeaderboardManager instance = new LeaderboardManager();

    public static LeaderboardManager getInstance(){
        return instance;
    }

    // Retrieve leaderboard standings
    public ArrayList<CompetitorObject> parseConfigData(){
        List<String> configStrings = Main.getInstance().getConfig().getStringList("leaderboard");
        ArrayList<CompetitorObject> result = new ArrayList<>(configStrings.size());

        for(String str : configStrings) {

            int indexEndOfId = str.indexOf(":");
            int indexEndOfWins = str.indexOf(":", indexEndOfId+1);
            int indexEndOfElims = str.indexOf(":", indexEndOfWins+1);
            int wins = Integer.parseInt(str.substring(indexEndOfId+1, indexEndOfWins));
            int elims = Integer.parseInt(str.substring(indexEndOfWins+1, indexEndOfElims));
            int deaths = Integer.parseInt(str.substring(indexEndOfElims+1));

            String id = str.substring(0, indexEndOfId);

            CompetitorObject competitor = new CompetitorObject(id, wins, elims, deaths);
            result.add(competitor);
        }
        return result;
    }

    /* Some sorting methods for the 3 parameters */
    public ArrayList<CompetitorObject> sortByCategory(String category){
        // Only return top 3
        ArrayList<CompetitorObject> result = new ArrayList<>(3);
        result.add(0, null);
        result.add(1, null);
        result.add(2, null);
        // I could make these CompetitorObjects instead and calculate the getWins... but maybe later?
        int first = -1;
        int second = -1;
        int third = -1;

        for(CompetitorObject competitor : parseConfigData()) {
            int cat;
            if(category.equalsIgnoreCase("wins")) {
                cat = competitor.wins;
            } else if (category.equalsIgnoreCase("elims")) {
                cat = competitor.eliminations;
            } else {
                cat = competitor.deaths;
            }
            if(cat > first){
                // Set second to previous first and third to previous second
                result.set(2, result.get(1));
                result.set(1, result.get(0));
                // Set the first place to the given competitor
                result.set(0, competitor);
                // Update the ints
                third = second;
                second = first;
                first = cat;
            }
            else if (cat > second){
                // Set third to second
                result.set(2, result.get(1));
                // Set the second place to the given competitor
                result.set(1, competitor);
                // Update the ints
                third = second;
                second = cat;
            }
            else if (cat > third){
                // Just replace it
                result.set(2, competitor);
                third = cat;
            }
        }

        return result;
    }

    // Retreive a specific player's standings
    public CompetitorObject getSpecificPlayerStandings(String name) {
        for(CompetitorObject object : parseConfigData()){
            if(object.getUsername().equalsIgnoreCase(name)) {
                return object;
            }
        }

        return new CompetitorObject("AA97B177-9383-4934-8543-0F91A7A02836", -1, -1, -1);
    }

    public String getLeaderboardsTitle(String cat){
        return "\n" + ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + cat + ChatColor.GRAY + "]";
    }

    public void sendAllStandings(Player playerToMessage){
        playerToMessage.sendMessage("" + ChatColor.DARK_GRAY + "-+ " + ChatColor.DARK_PURPLE + "OITC Leaderboard" + ChatColor.DARK_GRAY + " +-");
        playerToMessage.sendMessage(getLeaderboardsTitle("" + ChatColor.DARK_PURPLE + "Most Wins"));
        int i = 1;
        StringBuilder mostWins = new StringBuilder();
        for(CompetitorObject winner : sortByCategory("wins")){
            if(winner == null) break;
            mostWins.append("" + ChatColor.GRAY).append(i).append(". ").append(ChatColor.LIGHT_PURPLE).append(winner.getUsername()).append(" - ")
                    .append(winner.getWins()).append("\n");
            i++;
        }
        playerToMessage.sendMessage(mostWins.toString());

        playerToMessage.sendMessage(getLeaderboardsTitle("" + ChatColor.DARK_GREEN + "Most Eliminations"));
        i = 1;
        StringBuilder mostElims = new StringBuilder();
        for(CompetitorObject elims : sortByCategory("elims")){
            if(elims == null) break;
            mostElims.append("" + ChatColor.GRAY).append(i).append(". ").append(ChatColor.GREEN).append(elims.getUsername()).append(" - ")
                    .append(elims.getEliminations()).append("\n");
            i++;
        }
        playerToMessage.sendMessage(mostElims.toString());

        playerToMessage.sendMessage(getLeaderboardsTitle("" + ChatColor.DARK_RED + "Most Deaths"));
        i = 1;
        StringBuilder mostDeaths = new StringBuilder();
        for(CompetitorObject deaths : sortByCategory("elims")){
            if(deaths == null) break;
            mostDeaths.append("" + ChatColor.GRAY).append(i).append(". ").append(ChatColor.RED).append(deaths.getUsername()).append(" - ")
                    .append(deaths.getDeaths()).append("\n");
            i++;
        }
        playerToMessage.sendMessage(mostDeaths.toString());

        String playerName = playerToMessage.getName();
        playerToMessage.sendMessage(getLeaderboardsTitle("" + ChatColor.BLUE + "Your Standings -- " + playerName));
        playerToMessage.sendMessage("" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "Wins: " + ChatColor.DARK_PURPLE + getSpecificPlayerStandings(playerName).getWins());
        playerToMessage.sendMessage("" + ChatColor.GREEN + ChatColor.BOLD + "Elims: " + ChatColor.GREEN + getSpecificPlayerStandings(playerName).getEliminations());
        playerToMessage.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "Deaths: " + ChatColor.RED + getSpecificPlayerStandings(playerName).getDeaths());
    }

    // Update a single player's standings
    public void updatePlayerStandings(String playerId, int incrWins, int incrElims, int incrDeaths){
        // Loop through entire list and if it matches the UUID replace that part of the list then replace the entire list after you have looped through everyone
        List<String> configStrings = Main.getInstance().getConfig().getStringList("leaderboard");
        if(configStrings.size() == 0){
            String newInfo = playerId + ":" + incrWins + ":" + incrElims + ":" + incrDeaths;
            configStrings.add(newInfo);
            Main.getInstance().getConfig().set("leaderboard", configStrings);
            Main.getInstance().saveConfig();
            return;
        }
        int indexCounter = 0;
        for(String str : configStrings) {
            int indexEndOfId = str.indexOf(":");
            int indexEndOfWins = str.indexOf(":", indexEndOfId+1);
            int indexEndOfElims = str.indexOf(":", indexEndOfWins+1);
            int wins = Integer.parseInt(str.substring(indexEndOfId+1, indexEndOfWins));
            int elims = Integer.parseInt(str.substring(indexEndOfWins+1, indexEndOfElims));
            int deaths = Integer.parseInt(str.substring(indexEndOfElims+1));

            String playerIdInConfig = str.substring(0, str.indexOf(":"));
            if (playerIdInConfig.equalsIgnoreCase(playerId)) {

                wins += incrWins;
                elims += incrElims;
                deaths += incrDeaths;

                String updatedInfo = playerId + ":" + wins + ":" + elims + ":" + deaths;

                // This highkey updates config after every player. If there were more people per game, I'd definitely change it to update everyone in memory then update the config.
                configStrings.set(indexCounter, updatedInfo);
                Main.getInstance().getConfig().set("leaderboard", configStrings);
                Main.getInstance().saveConfig();
                return;
            }
            indexCounter++;
        }

        // If ends up being here, we've looped through the entire list and the player is not in it. Thus, update with current info
        String newInfo = playerId + ":" + incrWins + ":" + incrElims + ":" + incrDeaths;
        configStrings.add(newInfo);
        Main.getInstance().getConfig().set("leaderboard", configStrings);
        Main.getInstance().saveConfig();
    }

    // Update the standings of all players in a game
    public void updateGroupStandings(ArenaObject arena, Player victor){
        for(Player player : arena.getPlayers()){
            System.out.println("Arena loop: " + player);
            int elims = arena.getPlayerElims().get(player);
            int deaths = arena.getPlayerDeaths().get(player);
            if(player.equals(victor)){
                updatePlayerStandings(player.getUniqueId().toString(), 1, elims, deaths);
            } else {
                updatePlayerStandings(player.getUniqueId().toString(), 0, elims, deaths);
            }
        }
    }
}
