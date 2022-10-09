package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.util.BlockUtil;
import ac.knight.util.MathUtil;
import ac.knight.util.MovementUtil;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class SpeedA extends Check {

    public SpeedA(UserData userData) {
        super("Speed", "A", "Checks for faster movement than usual.", 12, userData);
    }

    private boolean prevSlime = false;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if (userData.isFlying()
                    || userData.teleportTicks <= 1
                    || userData.ticksExisted < 40
                    || BlockUtil.isVehicleNearby(userData.user.getPlayer())) return;

            final EntityPlayer player = userData.user.getCraftPlayer().getHandle();
            double friction = player.world.getType(new BlockPosition(player.locX, player.locY - 1, player.locZ)).getBlock().frictionFactor * 0.91;
            float frictionLimit = 0;
            float actualLimit = 0;

            boolean prevWasOnSlime = prevSlime;
            prevSlime = userData.user.hitbox.touchesGround && userData.user.hitbox.slimeBlocks;

            if (userData.packetOnGround) {

                frictionLimit += userData.lastDeltaXZ * (0.16277136 / Math.pow(friction, 3));
                if (userData.groundTicks > 0 && userData.groundTicks < 4) frictionLimit += 0.13f;
                actualLimit += 0.3f;
                if (userData.groundTicks > 0 && userData.groundTicks < 10) actualLimit += 0.13f;

            } else {

                actualLimit = 0.37f;
                if (userData.y - userData.lastY > 0.4) {
                    frictionLimit += 1.6f;
                    actualLimit = (float) (userData.lastDeltaXZ + 0.318);
                }
                friction = 0.91f;
                frictionLimit += userData.lastDeltaXZ * friction + 0.026f;
            }

            if (userData.iceTicks < 20) {
                actualLimit += 0.3f;
                frictionLimit += userData.onGround ? 0.6f : 0f;
            }

            if (userData.verticalTicks < 20) {
                frictionLimit += 0.5f;
                if (userData.y - userData.lastY > 0.2) {
                    frictionLimit += 1.6f;
                    actualLimit = (float) (userData.lastDeltaXZ + 0.301);
                }
                actualLimit += 0.05f;
            }


            if (userData.stairTicks < 30) {
                frictionLimit += 0.4f;
                actualLimit += 0.4f;
            }

            if(userData.user.getProtocolVersion() > 47) {
                frictionLimit += 0.0302f;
            }

            final boolean velocity = userData.velocityTicks < 6;

            final double velocityXZ = Math.sqrt(Math.pow(userData.remainingVeloX, 2) + Math.pow(userData.remainingVeloZ, 2));
            actualLimit += velocityXZ;

            frictionLimit = userData.getBaseSpeed(frictionLimit);
            actualLimit = userData.getBaseSpeed(actualLimit);

            boolean fail = false;
            int type = 0;

            if (Math.abs(userData.deltaXZ) > frictionLimit + (userData.onGround ? 0.01f : 0.001f) && !velocity && !prevWasOnSlime) {
                fail = true;
                type = 1;
            } else if (Math.abs(userData.deltaXZ) > actualLimit) {
                fail = true;
                type = 2;
            }

            if (fail && userData.deltaXZ > userData.getBaseSpeed(0.28f)) {
                double diff = Math.abs(userData.deltaXZ - actualLimit);
                if(diff > 20) {
                    return;
                }
                System.out.println(userData.deltaXZ + " | " + actualLimit + " | " + diff);
                if (this.increaseBuffer(1 + diff) > 3) {
                    this.fail("type", (type == 1 ? "friction" : "limit"), "percentage", MathUtil.round((userData.deltaXZ / actualLimit) * 100.0D) + "%");
                }
            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new SpeedA(data);
    }
}
