package ac.knight.user.processor.impl;

import ac.knight.user.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import ac.knight.util.MovementUtil;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class MovementProcessor extends Processor {

    public MovementProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    public double x, lastX, y, lastY, z, lastZ, teleportX, teleportY = -255, teleportZ;
    public double deltaX, lastDeltaX, deltaY, lastDeltaY, deltaZ, lastDeltaZ;
    public double deltaXZ, lastDeltaXZ, remainingVeloX, remainingVeloZ;
    public boolean packetOnGround, onGround, teleported, rotation = false;

    public int speedLvl;

    public int teleportTicks, velocityTicks, liquidTicks, iceTicks, ticksSinceLiquid, stairTicks, verticalTicks, airTicks, groundTicks, attackTicks;

    @Override
    public void handlePacket(Packet<?> packet) {
        if(packet.getClass().equals(PacketPlayInFlying.class)) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;

            if(flying.g()) {
                boolean initialized = ((InitializationProcessor) data.processor(InitializationProcessor.class)).initialized;

                rotation = false;
                speedLvl = data.getSpeedLevel(data.user.getPlayer());
                Location currentLocation = new Location(data.user.getPlayer().getLocation().getWorld(), x, y, z);
                Location teleportLocation = new Location(data.user.getPlayer().getLocation().getWorld(), teleportX, teleportY, teleportZ);
                if(teleported) {

                    if(currentLocation.distance(teleportLocation) < 1) {
                        teleportTicks = 0;
                        teleported = false;
                    }
                } else {
                    teleportTicks = Math.min(10000, teleportTicks + 1);
                }

                velocityTicks = Math.min(10000, velocityTicks + 1);
                attackTicks = Math.min(10000, attackTicks + 1);
                lastX = this.x;
                lastY = this.y;
                lastZ = this.z;
                this.x = flying.a();
                this.y = flying.b();
                this.z = flying.c();
                lastDeltaX = deltaX;
                lastDeltaY = deltaY;
                lastDeltaZ = deltaZ;
                deltaX = this.x - lastX;
                deltaY = this.y - lastY;
                deltaZ = this.z - lastZ;
                lastDeltaXZ = deltaXZ;
                deltaXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
                this.packetOnGround = flying.f();
                this.remainingVeloX *= 0.91;
                this.remainingVeloZ *= 0.91;

                onGround = packetOnGround;
                if(!onGround) {
                    groundTicks = 0;
                    airTicks++;
                } else {
                    groundTicks++;
                    airTicks = 0;
                }

                if(teleportTicks > 5) {
                    if(initialized && MovementUtil.isInWater(data.world(), x, y, z)) {
                        this.liquidTicks++;
                        this.ticksSinceLiquid = 20;
                    } else {
                        this.ticksSinceLiquid = Math.min(0, this.ticksSinceLiquid - 1);
                        this.liquidTicks = 0;
                    }

                    if(initialized && MovementUtil.isOnIce(x, y, z, data.world())) {
                        this.iceTicks = 0;
                    } else {
                        this.iceTicks++;
                    }

                    if(initialized && MovementUtil.stairsNear(x, y, z, data.world())) {
                        this.stairTicks = 0;
                    } else {
                        this.stairTicks++;
                    }

                    if(initialized && MovementUtil.isCollidedVertically(data.world(), x, y, z)) {
                        this.verticalTicks = 0;
                    } else {
                        this.verticalTicks++;
                    }

                }

                if(initialized && teleportTicks > 5) data.user.hitbox.recalculate(x-0.4, y, z-0.4, x+0.4, y+2.1, z+0.4, data.world());
            }
        }
    }
}
