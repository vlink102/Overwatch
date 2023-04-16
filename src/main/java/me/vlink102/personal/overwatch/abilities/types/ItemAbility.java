package me.vlink102.personal.overwatch.abilities.types;

import me.vlink102.personal.overwatch.minecraftlink.Player;
import org.bukkit.inventory.ItemStack;

public interface ItemAbility {
    ItemStack getAbilityItem(Player player);
}
