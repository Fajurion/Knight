package ac.knight.user;

import ac.knight.user.processor.Processor;
import ac.knight.user.processor.impl.*;
import ac.knight.util.MathUtil;
import ac.knight.util.MovementUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class UserData {

    public User user;

    private final ArrayList<Processor> processors = new ArrayList<>();

    public UserData(User user) {
        this.user = user;

        // Initialize processors
        processors.add(new InitializationProcessor(this));
        processors.add(new ActionProcessor(this));
        processors.add(new MovementProcessor(this));
        processors.add(new RotationProcessor(this));

        // Remove empty processors
        ArrayList<Processor> remove = new ArrayList<>();

        for(Processor processor : processors) {
            if(!processor.isActive()) {
                remove.add(processor);
            }
        }

        processors.removeAll(remove);
    }

    public double x, lastX, y, lastY, z, lastZ, velocityX, velocityY, velocityZ, teleportX, teleportY = -255, teleportZ;
    public double deltaX, lastDeltaX, deltaY, lastDeltaY, deltaZ, lastDeltaZ;
    public double deltaXZ, lastDeltaXZ, remainingVeloX, remainingVeloZ;

    public float yaw, lastYaw, pitch, lastPitch, deltaYaw, lastDeltaYaw, lastLastDeltaYaw, deltaPitch, lastDeltaPitch, lastLastDeltaPitch, teleportYaw, teleportPitch;
    public ArrayList<Number> yawSamples = new ArrayList<>(), pitchSamples = new ArrayList<>(), gcdSamples = new ArrayList<>();

    public boolean packetOnGround, onGround, cinematicRotations, teleported, rotation = false;

    public int velocityCount, speedLvl;

    public int teleportTicks, velocityTicks, liquidTicks, iceTicks, ticksSinceLiquid, ticksExisted, stairTicks, verticalTicks, cinematicTicks, airTicks, groundTicks, attackTicks;

    public boolean breakingBlock = false;
    public boolean swing = false, initialized = false;

    public ArrayList<Number> cpsSamples = new ArrayList<>();
    public ArrayList<Long> cps = new ArrayList<>();

    public ArrayList<Location> lastLocations = new ArrayList<>();

    public final HashMap<Integer, Vector> velocity = new HashMap<>();

    public Processor processor(Class<? extends Processor> clazz) {
        for(Processor processor : processors) {
            if(processor.getClass().equals(clazz)) {
                return processor;
            }
        }

        return null;
    }

    public World world() {
        return user.getPlayer().getLocation().getWorld();
    }

    public Player player() {
        return user.getPlayer();
    }

    public void handleIncomingPacket(Packet<?> packet) {
        for(Processor processor : processors) {
            processor.handleIncomingPacket(packet);
        }
    }

    public void handleOutgoingPacket(Packet<?> packet) {
        for(Processor processor : processors) {
            processor.handleOutgoingPacket(packet);
        }
    }

    public int handleVelocity(double x, double y, double z) {

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
        return freeCount;
    }

    public void handleTick() {
        if(initialized && user.data.teleportTicks > 5) user.hitbox.recalculate(user.data.x+user.data.deltaX-0.4, user.data.y, user.data.z+user.data.deltaZ-0.4, user.data.x+user.data.deltaX+0.4, user.data.y+2.1, user.data.z+user.data.deltaZ+0.4, user.getPlayer().getWorld());
    }

    public void handleRotation(float yaw, float pitch) {

        rotation = true;
        lastYaw = this.yaw;
        lastPitch = this.pitch;
        this.yaw = yaw;
        this.pitch = pitch;
        lastLastDeltaYaw = lastDeltaYaw;
        lastLastDeltaPitch = lastDeltaPitch;
        lastDeltaYaw = deltaYaw;
        lastDeltaPitch = deltaPitch;
        deltaYaw = this.yaw - lastYaw;
        deltaPitch = this.pitch - lastPitch;
        yawSamples.add(deltaYaw);
        if(yawSamples.size() > 20) yawSamples.remove(0);
        pitchSamples.add(deltaPitch);
        if(pitchSamples.size() > 20) pitchSamples.remove(0);
        gcdSamples.add(MathUtil.getGcd((long) (Math.abs(this.deltaPitch) * MathUtil.EXPANDER), (long) (Math.abs(this.lastDeltaPitch) * MathUtil.EXPANDER)));
        if(gcdSamples.size() > 20) gcdSamples.remove(0);

        final double accelYaw = MathUtil.getDiffrence(MathUtil.getDiffrence(deltaYaw, lastDeltaYaw), MathUtil.getDiffrence(lastDeltaYaw, lastLastDeltaYaw)).doubleValue();
        final double accelPitch = MathUtil.getDiffrence(MathUtil.getDiffrence(deltaPitch, lastDeltaPitch), MathUtil.getDiffrence(deltaPitch, lastDeltaPitch)).doubleValue();
        final int duplicates = MathUtil.getDuplicates(MathUtil.getDiffrencesOther(user.data.yawSamples));
        if((deltaYaw < 0.05 && deltaYaw > 0) || (deltaPitch < 0.05 && deltaPitch > 0)) cinematicTicks += 10;
        if(accelYaw < 0.05 && accelYaw > 0) cinematicTicks += 3;
        if(accelPitch < 0.05 && accelPitch > 0) cinematicTicks += 3;
        if(cinematicTicks > 0) cinematicTicks -= 1;
        if(duplicates == 0 && accelYaw < 1 && accelYaw > 0 && deltaYaw > 2) cinematicTicks += 5;

        this.cinematicTicks = Math.min(20, this.cinematicTicks);
        this.cinematicRotations = this.cinematicTicks > 1;

    }

    public void handleSwing() {

        swing = true;
        this.cps.add(System.currentTimeMillis());
        this.cps.removeIf(current -> current + 1000 < System.currentTimeMillis());
        this.cpsSamples.add(this.cps.size());
        if(this.cpsSamples.size() > 20) this.cpsSamples.remove(0);

    }

    public boolean isFlying() {
        return user.getPlayer().getAllowFlight() || user.getPlayer().isFlying();
    }

    public float getBaseSpeed(float value) {
        return value + (speedLvl * 0.12f);
    }

    public Vector getDirection(float yaw, float pitch) {
        Vector vector = new Vector();
        float rotX = (float)Math.toRadians(yaw);
        float rotY = (float)Math.toRadians(pitch);
        vector.setY(-Math.sin(rotY));
        double xz = Math.cos(rotY);
        vector.setX(-xz * Math.sin(rotX));
        vector.setZ(xz * Math.cos(rotX));
        return vector;
    }

    public int getSpeedLevel(Player player) {
        for (PotionEffect e : player.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.SPEED)) {
                return e.getAmplifier() == 0 ? 1 : e.getAmplifier();
            }
        }
        return 0;
    }

}
