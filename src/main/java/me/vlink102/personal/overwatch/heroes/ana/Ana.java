package me.vlink102.personal.overwatch.heroes.ana;

import me.vlink102.personal.overwatch.EntityStats;
import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.AbilityLoadout;
import me.vlink102.personal.overwatch.abilities.ana.BioticGrenade;
import me.vlink102.personal.overwatch.abilities.ana.BioticRifle;
import me.vlink102.personal.overwatch.abilities.ana.NanoBoost;
import me.vlink102.personal.overwatch.abilities.ana.SleepDart;
import me.vlink102.personal.overwatch.heroes.HeroEnum;
import me.vlink102.personal.overwatch.heroes.properties.HeadshotAble;
import me.vlink102.personal.overwatch.heroes.roles.Support;

public class Ana extends Support implements HeadshotAble {
    private final AbilityLoadout loadout;

    public Ana(Overwatch overwatch) {
        loadout = new AbilityLoadout(new BioticRifle(overwatch), null, new BioticGrenade(overwatch), new SleepDart(overwatch), null, new NanoBoost(overwatch));
    }

    @Override
    public String name() {
        return "Ana";
    }

    @Override
    public EntityStats entityStats() {
        return new EntityStats(200);
    }

    @Override
    public AbilityLoadout abilities() {
        return loadout;
    }

    @Override
    public HeroEnum heroBind() {
        return HeroEnum.ANA;
    }
}
