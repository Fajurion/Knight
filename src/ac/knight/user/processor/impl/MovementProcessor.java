package ac.knight.user.processor.impl;

import ac.knight.user.processor.ProtocolVersion;
import ac.knight.user.UserData;
import ac.knight.user.processor.Processor;
import ac.knight.util.MovementUtil;
import ac.knight.util.ReflectionUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class MovementProcessor extends Processor {

    public MovementProcessor(UserData data) {
        super(data, ProtocolVersion.V1_8);
    }

    public double x, lastX, y, lastY, z, lastZ, velocityX, velocityY, velocityZ, teleportX, teleportY = -255, teleportZ;
    public double deltaX, lastDeltaX, deltaY, lastDeltaY, deltaZ, lastDeltaZ;
    public double deltaXZ, lastDeltaXZ, remainingVeloX, remainingVeloZ;

    public float teleportYaw, teleportPitch;

    public boolean packetOnGround, onGround, teleported, rotation = false;

    public int velocityCount, speedLvl;

    public int teleportTicks, velocityTicks, liquidTicks, iceTicks, ticksSinceLiquid, stairTicks, verticalTicks, airTicks, groundTicks, attackTicks;

    public ArrayList<Location> lastLocations = new ArrayList<>();

    public final HashMap<Integer, Vector> velocity = new HashMap<>();

    @Override
    public void handleIncomingPacket(Packet<?> packet) {
        if(packet.getClass().equals(PacketPlayInFlying.class)) {
            PacketPlayInFlying flying = (PacketPlayInFlying) packet;
            boolean initialized = ((InitializationProcessor) data.processor(InitializationProcessor.class)).initialized;

            lastLocations.add(data.player().getLocation());
            if(lastLocations.size() > 20) lastLocations.remove(0);

            if(flying.g()) {

                rotation = false;
                speedLvl = data.getSpeedLevel(data.player());
                Location currentLocation = new Location(data.player().getLocation().getWorld(), x, y, z);
                Location teleportLocation = new Location(data.player().getLocation().getWorld(), teleportX, teleportY, teleportZ);
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
            }

            if(initialized && teleportTicks > 5) data.user.hitbox.recalculate(x-0.4, y, z-0.4, x+0.4, y+2.1, z+0.4, data.world());
        } else if(packet.getClass().equals(PacketPlayInTransaction.class) && !(velocityCount < 1)) {
            PacketPlayInTransaction transaction = (PacketPlayInTransaction) packet;

            if(transaction.b() >= 0) {
                int id = transaction.b();
                if(!velocity.containsKey(id)) return;

                velocityTicks = 0;
                velocityCount--;
                Vector velo = velocity.get(id);
                velocity.remove(id);
                remainingVeloX += Math.abs(velo.getX()) + 0.03;
                remainingVeloZ += Math.abs(velo.getZ()) + 0.03;
            }
        }
    }

    @Override
    public void handleOutgoingPacket(Packet<?> packet) {
        if(packet instanceof PacketPlayOutPosition) {
            PacketPlayOutPosition pos = (PacketPlayOutPosition) packet;
            teleportX = (double) ReflectionUtil.getField("a", pos);
            teleportY = (double) ReflectionUtil.getField("b", pos);
            teleportZ = (double) ReflectionUtil.getField("c", pos);
            teleportYaw = (float) ReflectionUtil.getField("d", pos);
            teleportPitch = (float) ReflectionUtil.getField("e", pos);
            teleported = true;
        }
        else if(packet instanceof PacketPlayOutEntityVelocity) {

            PacketPlayOutEntityVelocity velocity = (PacketPlayOutEntityVelocity) packet;
            int id = (int) ReflectionUtil.getField("a", velocity);
            if(id != data.user.getPlayer().getEntityId()) return;
            int x = (int) ReflectionUtil.getField("b", velocity);
            int y = (int) ReflectionUtil.getField("c", velocity);
            int z = (int) ReflectionUtil.getField("d", velocity);
            handleVelocity(x, y, z);
        }
        else if(packet instanceof PacketPlayOutExplosion) {

            PacketPlayOutExplosion explosion = (PacketPlayOutExplosion) packet;
            double x = (double) ReflectionUtil.getField("a", explosion);
            double y = (double) ReflectionUtil.getField("b", explosion);
            double z = (double) ReflectionUtil.getField("c", explosion);
            handleVelocity(x, y, z);
        }
    }

    public void handleVelocity(double x, double y, double z) {

        int id = 0;
        int freeCount = ThreadLocalRandom.current().nextInt(0, 100);
        while(velocity.containsKey(freeCount)) {

            if(id > 20) {
                velocity.clear();
            }

            freeCount = ThreadLocalRandom.current().nextInt(0, 100);
            id++;
        }

        velocity.put(freeCount, new Vector(x, y, z));
        this.velocityX = x;
        this.velocityY = y;
        this.velocityZ = z;
        this.velocityCount++;

        data.user.getCraftPlayer().getHandle().playerConnection.sendPacket(new PacketPlayOutTransaction(0, (short) freeCount, false));
    }

}
