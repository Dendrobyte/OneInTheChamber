package com.oitc.dendrobyte;

import com.oitc.dendrobyte.creation.ArenaCreationObject;
import com.oitc.dendrobyte.creation.CreationStates;
import com.oitc.dendrobyte.timers.ArenaRunningTimer;
import com.oitc.dendrobyte.timers.ArenaStartingTimer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Level;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class ArenaManager {

    private static ArenaManager instance = new ArenaManager();
    private String prefix = Main.getInstance().getPrefix();

    private ArenaManager() {}

    public static ArenaManager getInstance() {
        return instance;
    }

    private ArrayList<ArenaObject> currentArenas = new ArrayList<>();
    public ArrayList<ArenaObject> getCurrentArenas(){
        return currentArenas;
    }

    public void loadArenas(){
        Bukkit.getLogger().log(Level.INFO, "Now loading OITC arenas...");
        FileConfiguration config = Main.getInstance().getConfig();
        ConfigurationSection arenas = config.getConfigurationSection("arenas");
        try {
            for (String arenaName : arenas.getKeys(false)) {
                World world = Bukkit.getWorld(arenas.getString(arenaName + ".world"));
                Location signLoc = new Location(world, arenas.getInt(arenaName + ".signLoc.x"), arenas.getInt(arenaName + ".signLoc.y"), arenas.getInt(arenaName + ".signLoc.z"));
                ArrayList<Location> spawnLocations = new ArrayList<>();
                for (String spawnLocString : arenas.getStringList(arenaName + ".spawnpoints")) {
                    int x = Integer.parseInt(spawnLocString.substring(0, spawnLocString.indexOf(";")));
                    int y = Integer.parseInt(spawnLocString.substring(spawnLocString.indexOf(";") + 1, spawnLocString.indexOf("=")));
                    int z = Integer.parseInt(spawnLocString.substring(spawnLocString.indexOf("=") + 1));
                    Location newLoc = new Location(world, x, y, z);
                    spawnLocations.add(newLoc);
                }
                ArenaObject newArena = new ArenaObject(arenaName, signLoc, spawnLocations);
                currentArenas.add(newArena);
            }
            Bukkit.getLogger().log(Level.INFO, "All OITC arenas loaded!");
        } catch (NullPointerException e){
            Bukkit.getLogger().log(Level.WARNING, "No OITC arenas to load!");
        }
    }

    public Location getLobbyLocation(){
        FileConfiguration config = Main.getInstance().getConfig();
        return new Location(Bukkit.getWorld(Objects.requireNonNull(config.getString("lobby.world"))), config.getInt("lobby.x"),
                config.getInt("lobby.y"), config.getInt("lobby.z"));
    }

    private void reloadArenas(){
        currentArenas.clear();
        // TODO: If a game is running, don't clear it. Or kick everyone...
        loadArenas();
    }

    public int maxPlayers = 10; // No needed players. Will start 60 seconds after 2 people join, and skip to 20 seconds after 4 people join
    int killsToWin = 20;

    ArrayList<Player> playersInGames = new ArrayList<>();
    public boolean isInGame(Player player){
        return playersInGames.contains(player);
    }

    private HashMap<Player, ArenaObject> playerArenas = new HashMap<>();
    public ArenaObject getPlayersArena(Player player){
        if(!isInGame(player)) return null;
        return playerArenas.get(player);
    }

    // Also used to reset inventory items
    public void givePlayerItems(Player player){
        player.getInventory().clear();
        player.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
        player.getInventory().addItem(new ItemStack(Material.BOW, 1));
        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));

    }

    public void addPlayerToGame(ArenaObject arena, Player player){
        // Add player to the game, teleport them to the arena with no items
        player.teleport(arena.getRandomSpawnLocation());
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setSaturation(0);
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0D);

        // Add player to the arena list
        arena.getPlayers().add(player);
        int currentPlayers = arena.getPlayers().size();
        playerArenas.put(player, arena);
        playersInGames.add(player);

        // Update the arena sign
        Sign sign = (Sign) arena.getSignLoc().getBlock().getState(); // Should work. If not, the sign was broken and we have another problem
        // Third line (i = 2) is X/10
        sign.setLine(2, currentPlayers + "/" + maxPlayers);
        sign.update();

        player.sendMessage(prefix + ChatColor.GREEN + "Added to " + arena.getName() + "!");
        if(currentPlayers == 1){
            player.sendMessage(prefix + ChatColor.ITALIC + "Waiting for at least one more player...");
            if(arena.getAst() != null){
                arena.getAst().cancel();
                player.sendMessage(prefix + ChatColor.RED + "An existing timer for this arena has been canceled.");
            }

        }

        if(currentPlayers == 2){
            // Set timer to 60
            ArenaStartingTimer ast = new ArenaStartingTimer(60, arena);
            ast.runTaskTimer(Main.getInstance(), 0, 20L);
            arena.setArenaStartingTimer(ast);

            // Tell everyone
            for(Player playerInGame : arena.getPlayers()){
                playerInGame.sendMessage(prefix + "2 players joined! Game starting in 60 seconds!");
            }
        }

        else if(currentPlayers == 3 || currentPlayers >= 5){
            player.sendMessage(prefix + "There are " + ChatColor.DARK_PURPLE + arena.getAst().getSeconds() + ChatColor.GRAY + " seconds remaining.");
        }

        else if(currentPlayers == 4){
            // Set timer to 15
            ArenaStartingTimer ast = arena.getAst();
            ast.setSeconds(15);

            // Tell everyone
            for(Player playerInGame : arena.getPlayers()){
                playerInGame.sendMessage(prefix + "4 players joined! Game starting in 15 seconds!");
            }
        }
    }

    public void removePlayerFromGame(Player player, ArenaObject arena){
        // Remove the player from the game
        arena.getPlayers().remove(player);
        playerArenas.remove(player);
        playersInGames.remove(player);

        // Edit the sign player amount (i = 2)
        Sign arenaSign = (Sign)arena.getSignLoc().getBlock().getState();
        int curr = arena.getPlayers().size();
        arenaSign.setLine(2, curr + "/10");
        arenaSign.update();

        // Teleport them back to world spawn location
        player.getInventory().clear();
        for(PotionEffect effect : player.getActivePotionEffects()){
            player.removePotionEffect(effect.getType());
        }
        player.teleport(getLobbyLocation());
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4.0D);
        player.sendMessage(prefix + "Thanks for playing!");
    }

    public void kickPlayerFromGame(Player player){
        ArenaObject currentArena = getPlayersArena(player);
        removePlayerFromGame(player, currentArena);
        currentArena.getPlayers().remove(player);
        if(currentArena.getPlayers().size() == 1){
            if(currentArena.getState() == ArenaGameState.WAITING){
                currentArena.getAst().cancel();
                currentArena.getPlayers().get(0).sendMessage(prefix + "You are the only one left in the game. Timer reset.");
            } else {
                endGame(currentArena, currentArena.getPlayers().get(0));
            }
        }
        player.sendMessage(prefix + "You have left the game.");
    }

    public void startGame(ArenaObject arena){
        // Give all the players the proper items and teleport them to locations
        for(Player player : arena.getPlayers()){
            givePlayerItems(player);
            player.teleport(arena.getRandomSpawnLocation());
            player.sendMessage(prefix + "Items added to inventory, and teleported to a new spawnpoint!");
        }

        // Start timer
        ArenaRunningTimer art = new ArenaRunningTimer(15, arena);
        art.runTaskTimer(Main.getInstance(), 0L, 60*20L); // Go every minute
        arena.setArenaRunningTimer(art);

        // Set the state, send messages, update sign
        arena.setState(ArenaGameState.RUNNING);

        // Update the arena sign
        Sign sign = (Sign) arena.getSignLoc().getBlock().getState(); // Should work. If not, the sign was broken and we have another problem
        // Fourth line (i = 3) is state
        sign.setLine(3, "" + ChatColor.GREEN + ChatColor.ITALIC + "RUNNING");
        sign.update();
    }

    public void endGame(ArenaObject arena, Player winner){
        // Get who has the most deaths
        Player playerWithMostDeaths = arena.getPlayers().get(0);
        int mostDeaths = 0;

        for(Player player1 : arena.getPlayers()) {
            if(arena.getPlayerDeaths().get(player1) > mostDeaths) {
                mostDeaths = arena.getPlayerDeaths().get(player1);
                playerWithMostDeaths = player1;
            }
        }

        // Remove the players
        ArrayList<Player> tempPlayersInGame = arena.getPlayers(); // Avoid concurrent modification exception
        for(Player playerInGame : tempPlayersInGame){
            if(playerInGame == null) continue;
            playerInGame.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "GAME OVER!"
                    + ChatColor.GRAY + " The winner, with " + killsToWin + " kills, is " + ChatColor.GREEN + ChatColor.ITALIC + winner.getName() + "!"
                    + ChatColor.GRAY + " The award for most deaths goes to " + ChatColor.RED + ChatColor.ITALIC + playerWithMostDeaths.getName() + "!");

            playerInGame.sendMessage(prefix + ChatColor.GRAY + "You had " + ChatColor.GREEN + arena.getPlayerElims().get(playerInGame) + " eliminations," +
                    ChatColor.GRAY + " and " + ChatColor.RED + arena.getPlayerDeaths().get(playerInGame) + " kills" + ChatColor.GRAY + ".");

            removePlayerFromGame(playerInGame, arena);
            lastHits.remove(playerInGame);
        }

        // Reset the arena settings
        arena.getArt().cancel();
        arena.setArenaRunningTimer(null);
        resetArenaSettings(arena);
    }

    // This method for the ArenaRunningTimer, when time has run out
    public void endGame(ArenaObject arena){
        // Remove the players
        for(Player playerInGame : arena.getPlayers()){
            playerInGame.sendMessage(prefix + ChatColor.GOLD + ChatColor.BOLD + "GAME OVER!"
                    + ChatColor.DARK_PURPLE + ChatColor.ITALIC + " Time ran out!");
            removePlayerFromGame(playerInGame, arena);
        }
        // Reset the arena settings
        resetArenaSettings(arena);
    }

    // For use in the endGame methods above
    private void resetArenaSettings(ArenaObject arena){
        // Clear the arena fields
        arena.clearFields();

        // Set the state and update the signs
        arena.setState(ArenaGameState.WAITING);
        // Update the arena sign
        Sign sign = (Sign) arena.getSignLoc().getBlock().getState(); // Should work. If not, the sign was broken and we have another problem
        // Third line (i = 2) is player count
        sign.setLine(2, "0/10");
        // Fourth line (i = 3) is state
        sign.setLine(3, "" + ChatColor.DARK_GRAY + ChatColor.ITALIC + "WAITING");
        sign.update();
    }

    public void eliminatePlayer(Player player, Player shooter){
        ArenaObject currentArena = playerArenas.get(player);
        // Respawn player and reset inventory
        spawnPlayer(player, playerArenas.get(player));
        givePlayerItems(player);

        // Give shooter an arrow
        shooter.getInventory().addItem(new ItemStack(Material.ARROW, 1));
        // TODO: Donor perk -- Chance of spectral arrow (rip that player though, haha woops)

        // Announcement and elim/death tally
        int elims = currentArena.incrementPlayerElims(shooter);
        int deaths = currentArena.incrementPlayerDeaths(player);

        for(Player playerInGame : currentArena.getPlayers()){
            playerInGame.sendMessage(prefix + ChatColor.GOLD + player.getName() +
                    ChatColor.GRAY + " has been " + ChatColor.DARK_RED + "eliminated" +
                    ChatColor.GRAY + " by " + ChatColor.GOLD + shooter.getName() + ChatColor.GRAY + ", " +
                    "now with " + ChatColor.GREEN + ChatColor.BOLD + currentArena.getPlayerElims().get(shooter) + ChatColor.GRAY + " eliminations!");
        }

        shooter.sendMessage(prefix + ChatColor.GOLD + ChatColor.ITALIC + "Nice Shot!" + ChatColor.GREEN + ChatColor.ITALIC + " Total Elims: " + ChatColor.GRAY + currentArena.getPlayerElims().get(shooter));
        shooter.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 20));

        player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Total Deaths: " + ChatColor.GRAY + deaths);

        // If elims reaches 20 (or greater because that would totally be possible...) end the game
        if(elims >= killsToWin){
            endGame(getPlayersArena(player), shooter);
        }

    }

    public void eliminatePlayer(Player player, String reason) {
        ArenaObject currentArena = playerArenas.get(player);
        // Respawn player and reset inventory
        spawnPlayer(player, playerArenas.get(player));
        givePlayerItems(player);

        // Announcement and elim/death tally
        int deaths = currentArena.incrementPlayerDeaths(player);

        for (Player playerInGame : currentArena.getPlayers()) {
            playerInGame.sendMessage(prefix + reason);
        }

        player.sendMessage(prefix + ChatColor.RED + ChatColor.ITALIC + "Total Deaths: " + ChatColor.GRAY + deaths);

        // Increment elims and win condition check if there is a player registered as the killer
        if (lastHits.get(player) != null) {
            int elims = currentArena.incrementPlayerElims(lastHits.get(player));

            if (elims >= killsToWin) {
                endGame(getPlayersArena(player), lastHits.get(player));
            }

            lastHits.remove(player);
        }
    }

    public void spawnPlayer(Player player, ArenaObject arena){
        // Generate a random spawn location from the list
        int size = arena.getSpawnLocations().size();
        Random r = new Random();
        int spawnLocIndex = r.nextInt(size);

        // Send a player to that location
        Location spawnLoc = arena.getSpawnLocations().get(spawnLocIndex);
        player.teleport(spawnLoc);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*3, 20));
        player.setFoodLevel(100);
    }

    public boolean arenaExists(String arenaName){
        for(ArenaObject arena : currentArenas){
            if(arena.getName().equalsIgnoreCase(arenaName)){
                return true;
            }
        }
        return false;
    }

    public ArenaObject getArenaFromName(String arenaName){
        for(ArenaObject arena : currentArenas){
            if(arena.getName().equalsIgnoreCase(arenaName)){
                return arena;
            }
        }
        return null;
    }

    /* Command things and creation methods */
    public void stopArenaTimer(ArenaObject arena){
        arena.getArt().cancel();
        for(Player player : arena.getPlayers()){
            player.sendMessage(prefix + ChatColor.RED + ChatColor.BOLD + "Heads up!" + ChatColor.GOLD + ChatColor.ITALIC + " An admin stopped this arena's timer.");
        }
    }

    public void forcestartArena(ArenaObject arena, Player player){
        if(arena.getPlayers().size() == 1){
            player.sendMessage(prefix + "You can't force start an arena with one person.");
            return;
        }
        arena.getAst().setSeconds(3);
        for(Player playerInGame : arena.getPlayers()){
            playerInGame.sendMessage(prefix + "This game has been force started by " + ChatColor.DARK_PURPLE + ChatColor.BOLD + player.getName() + "!");
        }
    }

    public void forcestopArena(ArenaObject arena, Player player){
        if(arena.getState() == ArenaGameState.WAITING){
            player.sendMessage(prefix + "You can't force stop an arena that hasn't started.");
            return;
        }
        for(Player playerInGame : arena.getPlayers()){
            playerInGame.sendMessage(prefix + "This game has been force stopped by " + ChatColor.DARK_PURPLE + ChatColor.BOLD + player.getName() + "!");
        }
        arena.getArt().forceEndGame();
    }

    // Creation methods start here
    private HashMap<Player, ArenaCreationObject> playerCreatingArena = new HashMap<>(2);
    private HashMap<Player, CreationStates> playerCreationStates = new HashMap<>(2);

    public boolean isPlayerCreating(Player player){
        return playerCreationStates.containsKey(player);
    }

    public void initiatePlayerCreation(Player player){
        playerCreationStates.put(player, CreationStates.SIGNLOC);
        player.sendMessage(prefix + ChatColor.GREEN + ChatColor.ITALIC + "Welcome to creation!");
        ArenaCreationObject aco = new ArenaCreationObject();
        playerCreatingArena.put(player, aco);
        player.sendMessage(prefix + "Let's make this quick. " + ChatColor.GREEN + ChatColor.BOLD + "With the name on the first line, " + ChatColor.GREEN + "click the join sign");
    }

    public void abandonCreation(Player player) {
        playerCreationStates.remove(player);
        playerCreatingArena.remove(player);
        player.sendMessage(prefix + "All set! Type the command again to re-enter creation.");
    }

    public void finishCreation(Player player){
        player.sendMessage(prefix + "Wrapping everything up...");
        ArenaCreationObject arenaToCreate = getPlayerCreationArena(player);
        Main.getInstance().getConfig().set("arenas." + arenaToCreate.getName() + ".world", arenaToCreate.getSignLoc().getWorld().getName());
        Main.getInstance().getConfig().set("arenas." + arenaToCreate.getName() + ".signLoc.x", arenaToCreate.getSignLoc().getX());
        Main.getInstance().getConfig().set("arenas." + arenaToCreate.getName() + ".signLoc.y", arenaToCreate.getSignLoc().getY());
        Main.getInstance().getConfig().set("arenas." + arenaToCreate.getName() + ".signLoc.z", arenaToCreate.getSignLoc().getZ());
        ArrayList<String> locStrings = new ArrayList<>();
        for(Location loc : arenaToCreate.getLocations()){
            locStrings.add(loc.getBlockX() + ";" + loc.getBlockY() + "=" + loc.getBlockZ());
        }
        Main.getInstance().getConfig().set("arenas." + arenaToCreate.getName() + ".spawnpoints", locStrings);
        Main.getInstance().saveConfig();

        player.sendMessage(prefix + "Arena info added to config. Reloading arenas...");
        reloadArenas();

        playerCreatingArena.remove(player);
        playerCreationStates.remove(player);
        player.sendMessage(prefix + "Arenas reloaded and creation finished! Have fun :D");
    }

    public ArenaCreationObject getPlayerCreationArena(Player player){
        return playerCreatingArena.get(player);
    }

    public CreationStates getPlayerCreationState(Player player){
        return playerCreationStates.get(player);
    }

    public void setPlayerCreationState(Player player, CreationStates state){
        playerCreationStates.put(player, state);
    }

    // Some methods for storing player damage to get a kill by knocking someone off

    private HashMap<Player, Player> lastHits = new HashMap<>();

    public void addHit(Player defender, Player attacker){
        lastHits.put(defender, attacker);
    }

    public Player getKiller(Player defender){
        return lastHits.get(defender);
    }
}
