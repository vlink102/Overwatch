package me.vlink102.personal.overwatch.minecraftlink;

import me.vlink102.personal.overwatch.Overwatch;
import org.apache.commons.lang.WordUtils;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import java.util.Random;

public class SoundListener implements Listener {
    private final Overwatch overwatch;

    public SoundListener(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Random random = new Random();
        String worldName = player.getWorld().getName();
        player.sendMessage((random.nextBoolean() ? "§aNow arriving at §e" : "§aWelcome to §e") + WordUtils.capitalizeFully(worldName.replace("_", " ")));
        switch (worldName) {
            case "ilios" -> player.playSound(player, "map.ilios", SoundCategory.MUSIC, 1, 1);
            case "hanamura" -> player.playSound(player, "map.hanamura." + random.nextInt(1, 4), SoundCategory.MUSIC, 1, 1);
            case "eichenwalde" -> player.playSound(player, "map.eichenwalde", SoundCategory.MUSIC, 1, 1);
            case "hollywood" -> player.playSound(player, "map.hollywood." + random.nextInt(1, 4), SoundCategory.MUSIC, 1, 1);
            case "dorado" -> player.playSound(player, "map.dorado." + random.nextInt(1, 6), SoundCategory.MUSIC, 1, 1);
            case "kings_row" -> player.playSound(player, "map.kings_row." + random.nextInt(1, 5), SoundCategory.MUSIC, 1, 1);
            case "lijang_tower" -> player.playSound(player, "map.lijang_tower", SoundCategory.MUSIC, 1, 1);
            case "nepal" -> player.playSound(player, "map.nepal." + random.nextInt(1, 6), SoundCategory.MUSIC, 1, 1);
            case "numbani" -> player.playSound(player, "map.numbani." + random.nextInt(1, 4), SoundCategory.MUSIC, 1, 1);
            case "route_66" -> player.playSound(player, "map.route_66." + random.nextInt(1, 5), SoundCategory.MUSIC, 1, 1);
            case "temple_of_anubis" -> player.playSound(player, "map.temple_of_anubis." + random.nextInt(1, 6), SoundCategory.MUSIC, 1, 1);
            case "volskaya_industries" -> player.playSound(player, "map.volskaya_industries." + random.nextInt(1, 4), SoundCategory.MUSIC, 1, 1);
            case "watchpoint_gibraltar" -> player.playSound(player, "map.watchpoint_gibraltar." + random.nextInt(1, 4), SoundCategory.MUSIC, 1, 1);
        }
    }
}
