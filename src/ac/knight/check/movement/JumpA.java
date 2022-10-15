package ac.knight.check.movement;

import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.EventMove;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.MathUtil;
import ac.knight.user.UserData;
import org.bukkit.Material;

public class JumpA extends Check {

    public JumpA(UserData movement) {
        super("Jump", "A", "Checks for invalid jump motion.", 3, movement);
        setKicking(false);
    }

    private double lastLastDeltaY = 0;

    private MovementProcessor movement;

    @Override
    public void init(UserData data) {
        movement = (MovementProcessor) data.processor(MovementProcessor.class);
    }

    @Override
    public void onEvent(Event event) {
        if(event instanceof EventMove) {

            boolean vanillaStep = movement.deltaY < 0.65 && movement.lastDeltaY < 0.05 && userData.user.hitbox.containsSolidBlocks;
            if(movement.teleportTicks <= 1
                    || movement.velocityTicks < 3
                    || vanillaStep) {
                return;
            }

            if(movement.lastDeltaY <= 0) {
                double jumpLimit = 0.42;
                if(userData.user.getPlayer().getLocation().clone().add(0, -1, 0).getBlock().getType().equals(Material.SLIME_BLOCK)) {
                    jumpLimit = -lastLastDeltaY + 0.02;
                }

                if(movement.deltaXZ < 0.05) {
                    jumpLimit += 0.035;
                }

                jumpLimit = Math.max(0.42, jumpLimit);
                if(movement.deltaY > jumpLimit) {
                    if(this.increaseBuffer(1 + (movement.deltaY - jumpLimit)) > 0) {
                        this.fail("deltaY", MathUtil.round(movement.deltaY).doubleValue() + "", "limit", MathUtil.round(jumpLimit).doubleValue() + "");
                    }
                } else this.decreaseBuffer(0.01);
            }

            lastLastDeltaY = movement.lastDeltaY;
        }
    }

    @Override
    public Check newInstance(UserData data) {
        return new JumpA(data);
    }
}
