package me.vlink102.personal.overwatch.abilities.cooldowns;

import me.vlink102.personal.overwatch.abilities.Ability;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;
import me.vlink102.personal.overwatch.minecraftlink.Player;

import java.text.DecimalFormat;

public class AbilityCooldown {
    private final Ability ability;
    private final Player player;
    private final long seconds;
    private final long systime;

    public AbilityCooldown(Player player, Ability ability, long seconds, long systime) {
        this.player = player;
        this.seconds = seconds;
        this.systime = systime;
        this.ability = ability;
    }

    public Ability getAbility() {
        return ability;
    }

    public Player getPlayer() {
        return player;
    }

    public long getSeconds() {
        return seconds;
    }

    public long getSystime() {
        return systime;
    }

    public enum TimeUnit {
        BEST,
        DAYS,
        HOURS,
        MINUTES,
        SECONDS,
    }

    public static double trim(double untrimmed, int decimal) {
        DecimalFormat twoDec = new DecimalFormat("#.#" + "#".repeat(Math.max(0, decimal - 1)));
        return Double.parseDouble(twoDec.format(untrimmed));
    }

    public static double convert(long time, TimeUnit unit, int decPoint) {
        if(unit == TimeUnit.BEST) {
            if(time < 60000L) unit = TimeUnit.SECONDS;
            else if(time < 3600000L) unit = TimeUnit.MINUTES;
            else if(time < 86400000L) unit = TimeUnit.HOURS;
            else unit = TimeUnit.DAYS;
        }
        if(unit == TimeUnit.SECONDS) return trim(time / 1000.0D, decPoint);
        if(unit == TimeUnit.MINUTES) return trim(time / 60000.0D, decPoint);
        if(unit == TimeUnit.HOURS) return trim(time / 3600000.0D, decPoint);
        if(unit == TimeUnit.DAYS) return trim(time / 86400000.0D, decPoint);
        return trim(time, decPoint);
    }
}
