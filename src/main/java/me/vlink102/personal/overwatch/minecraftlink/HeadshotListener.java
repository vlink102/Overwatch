package me.vlink102.personal.overwatch.minecraftlink;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HeadshotListener implements Listener {
    // https://www.spigotmc.org/threads/headshot.165230/#:~:text=I%20fixed%20it%20and%20checked%20it%2C%20it%20works!

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player shot && event.getDamager() instanceof Projectile arrow) {
            if (arrow.getShooter() instanceof Player shooter) {
                if (arrow.getLocation().getY() - shot.getLocation().getY() > 1.4) {
                    shooter.sendMessage("§cHeadshot!");
                }
            }
        }
    }
}
