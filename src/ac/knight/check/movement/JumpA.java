package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.util.MathUtil;
import ac.knight.user.UserData;
import org.bukkit.Material;

public class JumpA extends Check {

    public JumpA(UserData userData) {
        super("Jump", "A", "Checks for invalid jump motion.", 3, userData);
        setKicking(false);
    }

    private double lastLastDeltaY = 0;

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            boolean vanillaStep = userData.deltaY < 0.65 && userData.lastDeltaY < 0.05 && userData.user.hitbox.containsSolidBlocks;
            if(userData.teleportTicks <= 1
                    || userData.velocityTicks < 3
                    || vanillaStep) {
                return;
            }

            if(userData.lastDeltaY <= 0) {
                double jumpLimit = 0.42;
                if(userData.user.getPlayer().getLocation().clone().add(0, -1, 0).getBlock().getType().equals(Material.SLIME_BLOCK)) {
                    jumpLimit = -lastLastDeltaY + 0.02;
                }

                if(userData.deltaXZ < 0.05) {
                    jumpLimit += 0.035;
                }

                jumpLimit = Math.max(0.42, jumpLimit);
                if(userData.deltaY > jumpLimit) {
                    if(this.increaseBuffer(1 + (userData.deltaY - jumpLimit)) > 0) {
                        this.fail("deltaY", MathUtil.round(userData.deltaY).doubleValue() + "", "limit", MathUtil.round(jumpLimit).doubleValue() + "");
                    }
                } else this.decreaseBuffer(0.01);
            }

            lastLastDeltaY = userData.lastDeltaY;
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new JumpA(data);
    }
}
