package me.vlink102.personal.overwatch.minecraftlink;

import net.md_5.bungee.api.ChatColor;

import java.awt.*;

public class ColorConverter {
    public static ChatColor convertAWTBungee(Color color) {
        return ChatColor.of(color);
    }

    public static org.bukkit.Color convertAWTBukkit(Color color) {
        return org.bukkit.Color.fromRGB(color.getRGB());
    }

    public static Color convertBukkitAWT(org.bukkit.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
