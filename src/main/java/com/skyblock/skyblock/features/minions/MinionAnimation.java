package com.skyblock.skyblock.features.minions;

import com.skyblock.skyblock.Skyblock;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class MinionAnimation {
    private ArmorStand stand;
    private Location targetLocation;
    private BukkitRunnable animationTask;

    public MinionAnimation(ArmorStand stand, Location targetLocation) {
        this.stand = stand;
        this.targetLocation = targetLocation;
    }

    public void start() {
        setHeadPose(targetLocation);

        animationTask = new BukkitRunnable() {
            double rotation = -90;
            int i = 0;

            @Override
            public void run() {
                if (stand == null || stand.isDead()) {
                    cancel();
                    return;
                }

                performAnimation();
            }

            private void performAnimation() {
                if (rotation >= 0) {
                    rotation = -90;
                }

                if (rotation >= 0)
                    rotation = 0;

                stand.setRightArmPose(new EulerAngle(Math.toRadians(rotation), 0, 0));

                rotation += 90d / 4d;
            }
        };

        animationTask.runTaskTimer(Skyblock.getPlugin(), 10, 2);
    }

    public void stop() {
        if (animationTask != null) {
            animationTask.cancel();
            resetArmorStandPose();
        }
    }


    private void setHeadPose(Location targetLocation) {
        stand.teleport(stand.getLocation().setDirection(targetLocation.clone().subtract(stand.getLocation()).toVector()));
        stand.setHeadPose(new EulerAngle(Math.toRadians(stand.getLocation().getPitch()), 0, 0));
    }

    private void resetArmorStandPose() {
        Location resetLocation = stand.getLocation().clone();
        resetLocation.setYaw(0);
        resetLocation.setPitch(0);
        stand.teleport(resetLocation);
        stand.setHeadPose(new EulerAngle(0, 0, 0));
        stand.setRightArmPose(new EulerAngle(0, 0, 0));
    }
}
