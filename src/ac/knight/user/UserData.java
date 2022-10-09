package ac.knight.user;

import ac.knight.util.MathUtil;
import ac.knight.util.MovementUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class UserData {

    public User user;

    public UserData(User user) {
        this.user = user;
    }

    public double x, lastX, y, lastY, z, lastZ, velocityX, velocityY, velocityZ, teleportX, teleportY = -255, teleportZ;
    public double deltaX, lastDeltaX, deltaY, lastDeltaY, deltaZ, lastDeltaZ;
    public double deltaXZ, lastDeltaXZ, remainingVeloX, remainingVeloZ;

    public float yaw, lastYaw, pitch, lastPitch, deltaYaw, lastDeltaYaw, lastLastDeltaYaw, deltaPitch, lastDeltaPitch, lastLastDeltaPitch, teleportYaw, teleportPitch;
    public ArrayList<Number> yawSamples = new ArrayList<>(), pitchSamples = new ArrayList<>(), gcdSamples = new ArrayList<>();

    public boolean packetOnGround, onGround, cinematicRotations, teleported, rotation = false;

    public int velocityCount, speedLvl;

    public int teleportTicks,
            velocityTicks,
            liquidTicks,
            iceTicks,
            ticksSinceLiquid,
            ticksExisted,
            stairTicks,
            verticalTicks,
            cinematicTicks,
            airTicks,
            groundTicks,
            attackTicks;

    public boolean breakingBlock = false;
    public boolean swing = false, initialized = false;

    public ArrayList<Number> cpsSamples = new ArrayList<>();
    public ArrayList<Long> cps = new ArrayList<>();

    public ArrayList<Location> lastLocations = new ArrayList<>();

    public final HashMap<Integer, Vector> velocity = new HashMap<>();

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

    public void handleMove(double x, double y, double z, boolean packetOnGround) {

        rotation = false;
        speedLvl = getSpeedLevel(user.getPlayer());
        Location currentLocation = new Location(user.getPlayer().getWorld(), x, y, z);
        Location teleportLocation = new Location(user.getPlayer().getWorld(), teleportX, teleportY, teleportZ);
        if(teleported) {

            if(currentLocation.distance(teleportLocation) < 1) {
                teleportTicks = 0;
                teleported = false;
            }
        } else {
            teleportTicks = Math.min(10000, teleportTicks + 1);
        }

        ticksExisted = Math.min(10000, ticksExisted + 1);
        velocityTicks = Math.min(10000, velocityTicks + 1);
        attackTicks = Math.min(10000, attackTicks + 1);
        lastX = this.x;
        lastY = this.y;
        lastZ = this.z;
        this.x = x;
        this.y = y;
        this.z = z;
        lastDeltaX = deltaX;
        lastDeltaY = deltaY;
        lastDeltaZ = deltaZ;
        deltaX = this.x - lastX;
        deltaY = this.y - lastY;
        deltaZ = this.z - lastZ;
        lastDeltaXZ = deltaXZ;
        deltaXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        this.packetOnGround = packetOnGround;
        this.remainingVeloX *= 0.91;
        this.remainingVeloZ *= 0.91;

        if(!packetOnGround && ticksExisted < 8) {
            initialized = false;
        } else if(deltaY >= 0.0) initialized = true;

        onGround = packetOnGround;
        if(!onGround) {
            groundTicks = 0;
            airTicks++;
        } else {
            groundTicks++;
            airTicks = 0;
        }

        if(teleportTicks > 5) {
            if(initialized && MovementUtil.isInWater(user.getPlayer().getWorld(), user.data.x, user.data.y, user.data.z)) {
                this.liquidTicks++;
                this.ticksSinceLiquid = 20;
            } else {
                this.ticksSinceLiquid = Math.min(0, this.ticksSinceLiquid - 1);
                this.liquidTicks = 0;
            }

            if(initialized && MovementUtil.isOnIce(x, y, z, user.getPlayer().getWorld())) {
                this.iceTicks = 0;
            } else {
                this.iceTicks++;
            }

            if(initialized && MovementUtil.stairsNear(x, y, z, user.getPlayer().getWorld())) {
                this.stairTicks = 0;
            } else {
                this.stairTicks++;
            }

            if(initialized && MovementUtil.isCollidedVertically(user.getPlayer().getWorld(), x, y, z)) {
                this.verticalTicks = 0;
            } else {
                this.verticalTicks++;
            }

        }
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
