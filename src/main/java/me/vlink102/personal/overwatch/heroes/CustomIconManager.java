package me.vlink102.personal.overwatch.heroes;

import me.vlink102.personal.overwatch.abilities.AbilityEnum;

import java.awt.*;
import java.util.HashMap;

public class CustomIconManager {
    public static final HashMap<AbilityEnum, CharIcon> ABILITY_MAP = new HashMap<>() {{
        this.put(AbilityEnum.ANA_BIOTIC_GRENADE, new CharIcon('\ue239', new Dimension(100, 100), 8, 20));
        this.put(AbilityEnum.ANA_BIOTIC_RIFLE, new CharIcon('\ue23a', new Dimension(100, 34), 8, 20));
        this.put(AbilityEnum.ANA_NANO_BOOST, new CharIcon('\ue23b', new Dimension(100, 100), 8, 20));
        this.put(AbilityEnum.ANA_SLEEP_DART, new CharIcon('\ue23c', new Dimension(128, 128), 8, 20));
    }};

    public static final HashMap<HeroEnum, CharIcon> HERO_MAP = new HashMap<>() {{
        this.put(HeroEnum.ANA, new CharIcon('\ue238', new Dimension(120, 120), 8, 64));
    }};

    public static class CharIcon extends Icon {
        private final Character character;

        public CharIcon(Character character, Dimension d, int ascent, int height) {
            super(d, ascent, height);
            this.character = character;
        }

        public Character getCharacter() {
            return character;
        }
    }

    public static class HeroIcon extends Icon {
        private final HeroEnum heroEnum;

        public HeroIcon(HeroEnum heroEnum, Dimension d, int ascent, int height) {
            super(d, ascent, height);
            this.heroEnum = heroEnum;
        }

        public HeroEnum getHeroEnum() {
            return heroEnum;
        }
    }

    public static class AbilityIcon extends Icon {
        private final AbilityEnum abilityEnum;

        public AbilityIcon(AbilityEnum abilityEnum, Dimension d, int ascent, int height) {
            super(d, ascent, height);
            this.abilityEnum = abilityEnum;
        }

        public AbilityEnum getAbilityEnum() {
            return abilityEnum;
        }
    }

    public static class Icon {
        private final int ascent;
        private final Dimension d;
        private final int height;

        public Icon(Dimension d, int ascent, int height) {
            this.d = d;
            this.ascent = ascent;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public Dimension getDimension() {
            return d;
        }

        public int getAscent() {
            return ascent;
        }

        public int getScaledWidth() {
            return (height / d.height) * d.width;
        }

        public int getHeightLines() {
            return height / 8;
        }

        public int getWidthSpaces() {
            return getScaledWidth() / 4;
        }
    }
}
