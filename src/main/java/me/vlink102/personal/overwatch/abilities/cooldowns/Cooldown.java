package me.vlink102.personal.overwatch.abilities.cooldowns;

import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.minecraftlink.AbilityListener;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Iterator;

public class Cooldown {
    public static HashMap<Player, HashMap<Ability, AbilityCooldown>> cooldownMap = new HashMap<>();

    public static void add(Player player, Ability ability, long seconds, long systime) {
        if(!cooldownMap.containsKey(player)) cooldownMap.put(player, new HashMap<>());
        if(isCooling(player, ability)) return;
        cooldownMap.get(player).put(ability, new AbilityCooldown(player, ability, seconds * 1000, System.currentTimeMillis()));
    }

    public static boolean isCooling(Player player, Ability ability) {
        if(!cooldownMap.containsKey(player)) return false;
        return cooldownMap.get(player).containsKey(ability);
    }

    public static double getRemaining(Player player, Ability ability) {
        if(!cooldownMap.containsKey(player)) return 0.0;
        if(!cooldownMap.get(player).containsKey(ability)) return 0.0;
        return AbilityCooldown.convert((cooldownMap.get(player).get(ability).getSeconds() + cooldownMap.get(player).get(ability).getSystime()) - System.currentTimeMillis(), AbilityCooldown.TimeUnit.SECONDS, 1);
    }

    public static void removeCooldown(Player player, Ability ability) {
        if(!cooldownMap.containsKey(player)) return;
        if(!cooldownMap.get(player).containsKey(ability)) return;
        cooldownMap.get(player).remove(ability);
    }

    public static void handleCooldowns() {
        if (cooldownMap.isEmpty()) return;
        for (Player key : cooldownMap.keySet()) {
            for (Ability name : cooldownMap.get(key).keySet()) {
                if (getRemaining(key, name) <= 0.0) {
                    removeCooldown(key, name);
                }
            }
        }
    }
}
