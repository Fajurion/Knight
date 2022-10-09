package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.UserData;
import ac.knight.util.MathUtil;
import ac.knight.util.MovementUtil;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;

public class GravityA extends Check {

    public GravityA(UserData userData) {
        super("Gravity", "A", "Checks for modified gravity.", 12, userData);
    }

    private boolean prevSlime = false;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            if(userData.isFlying()
                    || userData.ticksSinceLiquid > 6
                    || userData.velocityTicks < 3
                    || userData.teleportTicks < 3
                    || userData.user.hitbox.climbable
                    || MovementUtil.isCollidedVertically(userData.user.getPlayer().getWorld(), userData.x, userData.y, userData.z))
                return;

            double pred = (userData.lastDeltaY - 0.08f) * 0.9800000190734863D;
            if(userData.user.getProtocolVersion() > 47) {
                pred = (userData.lastDeltaY - 0.08f) * 0.98;
            }
            final boolean exclude = userData.deltaY == 0;
            if(userData.lastDeltaY <= 0 && MathUtil.isRoughly(userData.deltaY, 0.41999998688697815, 0.001)) {
                return;
            }

            boolean prevWasOnSlime = prevSlime;
            boolean wasOnSlime = userData.user.hitbox.touchesGround && userData.user.hitbox.slimeBlocks;
            prevSlime = wasOnSlime;

            if(Math.abs(pred) < 0.005) pred = 0;
            if(MathUtil.isRoughly(userData.lastDeltaY, -0.1552, 0.001)) pred = 0.40444491418477924;
            if(MathUtil.isRoughly(userData.lastDeltaY, 0.40444491418477924, 0.001)) pred = 0.33319999363422426;
            if(MathUtil.isRoughly(userData.lastDeltaY, -0.15233518685055003, 0.001)) pred = 0.395575898673286;
            if(MathUtil.isRoughly(userData.lastDeltaY, 0.395575898673286, 0.001)) pred = 0.33319999363422426;

            if(userData.deltaY > -0.1 && userData.deltaY < -0.06) {
                return;
            }
            if(Math.abs(pred - userData.deltaY) > (userData.user.getProtocolVersion() > 47 ? 0.01D : 0.001D) && userData.deltaY > -0.13 && !exclude && !userData.packetOnGround && !wasOnSlime && !prevWasOnSlime) {
                if(userData.user.getProtocolVersion() > 47 && userData.iceTicks < 3) {
                    return;
                }
                if(userData.verticalTicks < 10 && userData.deltaY < 0.2) {
                    return;
                }
                boolean safeMode = false;
                if(!userData.user.getPlayer().getLocation().clone().add(0, -0.15, 0).getBlock().getType().equals(Material.AIR)) {
                    pred = userData.user.getPlayer().getLocation().clone().add(0, -0.15, 0).getBlock().getY() - userData.lastY + 1;
                    safeMode = true;
                    if(MathUtil.isRoughly(pred, userData.deltaY, 0.001)) {
                        return;
                    }
                }
                if(this.increaseBuffer(safeMode ? 0.5 : 1) > 1) {
                    System.out.println(Math.abs(pred - userData.deltaY) + " | " + pred + " | " + userData.deltaY + " | " + userData.lastDeltaY);
                    this.fail("motionY", MathUtil.round(userData.deltaY) + "", "prediction", MathUtil.round(pred) + "", "difference", MathUtil.round(Math.abs(pred - userData.deltaY)) + "");
                }
            } else this.decreaseBuffer(0.01);

        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new GravityA(data);
    }
}
