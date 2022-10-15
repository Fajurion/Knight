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

    private final HashMap<String, Processor> processors = new HashMap<>();

    public UserData(User user) {
        this.user = user;

        // Initialize processors
        ArrayList<Processor> toRegister = new ArrayList<>();

        toRegister.add(new InitializationProcessor(this));
        toRegister.add(new ActionProcessor(this));
        toRegister.add(new MovementProcessor(this));
        toRegister.add(new RotationProcessor(this));

        // Remove empty processors
        ArrayList<Processor> remove = new ArrayList<>();

        for(Processor processor : toRegister) {
            if(!processor.isActive()) {
                remove.add(processor);
            }
        }

        toRegister.removeAll(remove);

        // Register processors
        for(Processor processor : toRegister) {
            processors.put(processor.getClass().getSimpleName(), processor);
        }

    }

    public Processor processor(Class<? extends Processor> clazz) {
        return processors.getOrDefault(clazz.getSimpleName(), null);
    }

    public World world() {
        return user.getPlayer().getLocation().getWorld();
    }

    public Player player() {
        return user.getPlayer();
    }

    public void handleIncomingPacket(Packet<?> packet) {
        for(Processor processor : processors.values()) {
            processor.handleIncomingPacket(packet);
        }
    }

    public void handleOutgoingPacket(Packet<?> packet) {
        for(Processor processor : processors.values()) {
            processor.handleOutgoingPacket(packet);
        }
    }

    public boolean isFlying() {
        return user.getPlayer().getAllowFlight() || user.getPlayer().isFlying();
    }

    public float getBaseSpeed(float value) {
        return value + (getSpeedLevel(user.getPlayer()) * 0.12f);
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
