package me.vlink102.personal.overwatch;


public class EntityStats {
    private final float health;
    private final float armor;
    private final float shields;

    public EntityStats(float health, Float armor, Float shields) {
        this.health = health;
        this.armor = armor;
        this.shields = shields;
    }

    public EntityStats(float health) {
        this.health = health;
        this.armor = 0;
        this.shields = 0;
    }

    public float getHealth() {
        return health;
    }

    public Float getArmor() {
        return armor;
    }

    public Float getShields() {
        return shields;
    }
}
