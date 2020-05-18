package com.oitc.dendrobyte.listeners;

import com.oitc.dendrobyte.ArenaManager;
import com.oitc.dendrobyte.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

/**
 * Created by mobki, aka Dendrobyte, on 8/23/2019
 * Written for project OneInTheChamber
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class PlayerHitListener implements Listener {

    private ArenaManager am = ArenaManager.getInstance();
    private String prefix = Main.getInstance().getPrefix();

    // Player contact
    @EventHandler
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player)event.getEntity();
        if(!am.isInGame(player)) return;
        // Player hits them, probably with a sword
        if(event.getDamager() instanceof Player){
            Player damager = (Player)event.getDamager();
            double damage = event.getDamage();
            double playerHealth = player.getHealth();
            if(playerHealth <= damage){
                event.setCancelled(true);
                am.eliminatePlayer(player, damager);
                player.playSound(player.getLocation(), Sound.ENTITY_PIG_HURT, 1, 1);
                damager.playSound(damager.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
            } else {
                // If it's not enough to kill, store the hit
                am.addHit(player, damager);
            }
        }
        // Player is hit with an arrow
        if(event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow)event.getDamager();
            if(!(arrow.getShooter() instanceof Player)) return;
            Player shooter = (Player)arrow.getShooter();
            if(player.getName().equalsIgnoreCase(shooter.getName())){
                player.sendMessage(prefix + "Nice try, bud!");
                event.setDamage(0);
                return;
            }
            event.setCancelled(true);
            am.eliminatePlayer(player, shooter);
        }
    }

    // In case the arrow misses and hits, make it disappear
    @EventHandler
    public void onArrowLand(ProjectileHitEvent event){
        if(!(event.getEntity() instanceof Arrow)) return;
        Arrow arrow = (Arrow)event.getEntity();
        if(!(arrow.getShooter() instanceof Player)) return;
        Player shooter = (Player)arrow.getShooter();
        if(!am.isInGame(shooter)) return;
        if(event.getHitBlock() != null){
            arrow.remove();
        }
    }

}
