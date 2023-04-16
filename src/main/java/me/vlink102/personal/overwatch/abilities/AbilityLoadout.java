package me.vlink102.personal.overwatch.abilities;

import me.vlink102.personal.overwatch.abilities.types.PassiveAbility;
import me.vlink102.personal.overwatch.abilities.types.PrimaryAbility;
import me.vlink102.personal.overwatch.abilities.types.SecondaryAbility;
import me.vlink102.personal.overwatch.abilities.types.UltimateAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class AbilityLoadout {
    private final List<Ability> abilities;

    private final PrimaryAbility primaryAbility;
    private final SecondaryAbility secondaryAbility;
    private final Ability e;
    private final Ability lShift;
    private final PassiveAbility passiveAbility;
    private final UltimateAbility ultimateAbility;

    public AbilityLoadout(PrimaryAbility primaryAbility, SecondaryAbility secondaryAbility, Ability e,
                          Ability lShift, PassiveAbility passiveAbility, UltimateAbility ultimateAbility) {
        this.primaryAbility = primaryAbility;
        this.secondaryAbility = secondaryAbility;
        this.e = e;
        this.lShift = lShift;
        this.passiveAbility = passiveAbility;
        this.ultimateAbility = ultimateAbility;
        this.abilities = saveAbilities();
    }

    public PrimaryAbility primaryAbility() {
        return primaryAbility;
    }

    public SecondaryAbility secondaryAbility() {
        return secondaryAbility;
    }

    public Ability e() {
        return e;
    }

    public Ability lShift() {
        return lShift;
    }

    public PassiveAbility passiveAbility() {
        return passiveAbility;
    }

    public UltimateAbility ultimateAbility() {
        return ultimateAbility;
    }

    public Ability get(int id) {
        return switch (id) {
            case 1 -> primaryAbility;
            case 2 -> secondaryAbility;
            case 3 -> e;
            case 4 -> lShift;
            case 5 -> passiveAbility;
            case 6 -> ultimateAbility;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public int getHotbarSlot(int id) {
        return switch (id) {
            case 1 -> 8;
            case 2 -> 5;
            case 3 -> 3;
            case 4 -> 2;
            case 6 -> 4;
            default -> -1;
        };
    }

    public static String getButton(int id) {
        return switch (id) {
            case 1 -> "§8(LMB)";
            case 2 -> "§8(RMB)";
            case 3 -> "§8(E)";
            case 4 -> "§8(LSHIFT)";
            case 5 -> "§8(PASSIVE)";
            case 6 -> "§8(ULTIMATE)";
            default -> "§8";
        };
    }

    private List<Ability> saveAbilities() {
        List<Ability> abilities = new ArrayList<>();
        if (primaryAbility != null) abilities.add(primaryAbility);
        if (secondaryAbility != null) abilities.add(secondaryAbility);
        if (e != null) abilities.add(e);
        if (lShift != null) abilities.add(lShift);
        if (passiveAbility != null) abilities.add(passiveAbility);
        if (ultimateAbility != null) abilities.add(ultimateAbility);
        return abilities;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }
}
