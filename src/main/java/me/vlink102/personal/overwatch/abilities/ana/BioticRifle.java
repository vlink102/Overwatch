package me.vlink102.personal.overwatch.abilities.ana;

import me.vlink102.personal.overwatch.Overwatch;
import me.vlink102.personal.overwatch.abilities.AbilityEnum;
import me.vlink102.personal.overwatch.abilities.types.*;
import me.vlink102.personal.overwatch.minecraftlink.ColorConverter;
import me.vlink102.personal.overwatch.minecraftlink.HitscanListener;
import me.vlink102.personal.overwatch.minecraftlink.Player;
import me.vlink102.personal.overwatch.minecraftlink.TimeUnit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.*;

public class BioticRifle extends PrimaryAbility implements LinearProjectileAbility, Reloadable, Scoped, TimeLimited, RecoilAbility {
    private final Overwatch overwatch;

    public BioticRifle(Overwatch overwatch) {
        this.overwatch = overwatch;
    }

    private final Color healColor = ColorConverter.convertBukkitAWT(BioticGrenade.yellow);

    @Override
    public String abilityName() {
        return "Biotic Rifle";
    }

    @Override
    public void useAbility(Player player, Vector dir) {
        if (!player.isRecovering() && !player.isCasting()) {
            player.setCasting(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setCasting(false);
                    player.useRound();

                    org.bukkit.entity.Player bukkitPlayer = player.getPlayerBind();

                    HitscanListener.defaultHitScan(bukkitPlayer, player, BioticRifle.this, player.isScoped(), healColor, 0.4f, healColor, 0.3f, true);
                    RecoilAbility.performRecoil(bukkitPlayer, recoilUnits());

                    player.setRecovering(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            player.setRecovering(false);
                            player.resetHeldItemSlot();
                        }
                    }.runTaskLater(overwatch, recoveryTime().getTicks());
                }
            }.runTaskLater(overwatch, castingTime().getTicks());
        }
        System.out.println("Used biotic rifle");
    }

    @Override
    public TimeUnit recoveryTime() {
        return TimeUnit.Converter.MILLISECOND.of(800);
    }

    @Override
    public TimeUnit castingTime() {
        return TimeUnit.Converter.TICK.of(0);
    }

    @Override
    public String abilityDescription() {
        return "(LEFT-CLICK) Long-range rifle that heals allies and damages enemies. (RIGHT-CLICK) Hold to zoom in.";
    }

    @Override
    public int abilityID() {
        return 1;
    }

    @Override
    public AbilityEnum abilityBind() {
        return AbilityEnum.ANA_BIOTIC_RIFLE;
    }

    @Override
    public float projectileSpeed() {
        return 125;
    } // unscoped

    @Override
    public float projectilePinpointRadius() {
        return 0.3f;
    }

    @Override
    public int magazineSize() {
        return 15;
    }

    @Override
    public TimeUnit reloadTime() {
        return TimeUnit.Converter.MILLISECOND.of(1500);
    } // 1.5

    @Override
    public TimeUnit scopeInTime() {
        return TimeUnit.Converter.MILLISECOND.of(240);
    } // 0.24

    @Override
    public TimeUnit scopeOutTime() {
        return TimeUnit.Converter.MILLISECOND.of(160);
    } // 0.16

    @Override
    public void scopedAffect(Player shooter, Player shot, boolean headShot) {

    }

    @Override
    public float scopedMovementPenalty() {
        return 0.65f; // - 65%
    }

    @Override
    public TimeUnit timeLimit() {
        return TimeUnit.Converter.MILLISECOND.of(580); // 0.58
    }

    @Override
    public float recoilUnits() {
        return 2;
    }

    @Override
    public void affect(Player shooter, Player shot, boolean headShot) {

    }
}
