package ac.knight.bot;

import com.mojang.authlib.GameProfile;
import ac.knight.util.ReflectionUtil;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Bot {

    private final GameProfile gameProfile;
    private final Location location;
    private Location lastLocation;
    private final Player player;
    private EntityHuman human;
    private double distance;
    private final double minDistance;

    public Bot(Player player, double distance, double minDistance) {
        this.player = player;
        this.distance = distance;
        this.minDistance = minDistance;
        this.gameProfile = new GameProfile(UUID.randomUUID(), "kindasussy");
        this.location = player.getLocation();
        this.lastLocation = new Location(player.getWorld(), 0.0D, 0.0D, 0.0D);
    }

    public void summon() {
        this.human = new EntityHuman(((CraftWorld)this.location.getWorld()).getHandle(), this.gameProfile) {
            public boolean isSpectator() {
                return false;
            }
        };
        this.human.setHealth(20.0F);
        this.human.setLocation(this.location.getX(), this.location.getY(), this.location.getZ(), this.location.getYaw(), this.location.getPitch());
        PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(this.human);
        this.tabListAction(EnumPlayerInfoAction.ADD_PLAYER);
        this.sendPacket(packet);
    }

    public void destroy() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{this.human.getId()});
        tabListAction(EnumPlayerInfoAction.REMOVE_PLAYER);
        this.sendPacket(packet);
    }

    public void sendPacket(Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void tabListAction(EnumPlayerInfoAction action) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        PacketPlayOutPlayerInfo.PlayerInfoData data = packet.new PlayerInfoData(gameProfile, 1, WorldSettings.EnumGamemode.NOT_SET, CraftChatMessage.fromString(gameProfile.getName())[0]);
        List<PacketPlayOutPlayerInfo.PlayerInfoData> players = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) ReflectionUtil.getField("b", packet);
        players.add(data);
        ReflectionUtil.setField("a", packet, action);
        ReflectionUtil.setField("b", packet, players);
        this.sendPacket(packet);
    }

    public void handleTick() {

        if(distance > minDistance) {
            distance -= ThreadLocalRandom.current().nextDouble(0.24, 0.28);
        } else {
            distance = minDistance + ThreadLocalRandom.current().nextDouble(-0.2, 0.2);
        }
        this.location.setY(player.getLocation().getY());
        moveToNewLocation(distance);

        if(lastLocation != null) {
            updatePosition();
        }
        lastLocation = location.clone();
    }

    public void moveToNewLocation(double distance) {
        Vector vector = player.getLocation().getDirection().multiply(-distance);
        location.setX(player.getLocation().getX() + vector.getX());
        location.setY(player.getLocation().getY() + vector.getY());
        location.setZ(player.getLocation().getZ() + vector.getZ());
    }

    public float[] getNeededRotations(Location vector) {
        Location playerLocation = new Location(vector.getWorld(), vector.getX(), vector.getY(), vector.getZ());
        double x = this.location.getX() - playerLocation.getX();
        double z = this.location.getZ() - playerLocation.getZ();
        float yaw = (float)Math.toDegrees(Math.atan2(z, x)) - 90.0F;
        return new float[]{this.wrapAngleTo180_float(yaw + 180.0F), this.wrapAngleTo180_float(0.0F)};
    }

    public float wrapAngleTo180_float(float value) {
        value %= 360.0F;
        if (value >= 180.0F) {
            value -= 360.0F;
        }

        if (value < -180.0F) {
            value += 360.0F;
        }

        return value;
    }

    public void updatePosition() {
        if (this.isRotation() && this.isMove()) {
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.human.getId(), this.convertCoordinate(this.location.getX()), this.convertCoordinate(this.location.getY()), this.convertCoordinate(this.location.getZ()), this.convertRotation(this.location.getYaw()), this.convertRotation(this.location.getPitch()), true);
            this.sendPacket(packet);
            PacketPlayOutEntityHeadRotation packet2 = new PacketPlayOutEntityHeadRotation();
            ReflectionUtil.setField("a", packet2, this.human.getId());
            ReflectionUtil.setField("b", packet2, this.convertRotation(this.location.getYaw()));
            this.sendPacket(packet2);
        } else if (this.isMove()) {
            PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.human.getId(), this.convertCoordinate(this.location.getX()), this.convertCoordinate(this.location.getY()), this.convertCoordinate(this.location.getZ()), this.convertRotation(this.location.getYaw()), this.convertRotation(this.location.getPitch()), true);
            this.sendPacket(packet);
        } else if (this.isRotation()) {
            PacketPlayOutEntity packet = new PacketPlayOutEntityLook(this.human.getId(), this.convertRotation(this.location.getYaw()), this.convertRotation(this.location.getPitch()), true);
            PacketPlayOutEntityHeadRotation packet2 = new PacketPlayOutEntityHeadRotation();
            ReflectionUtil.setField("a", packet2, this.human.getId());
            ReflectionUtil.setField("b", packet2, this.convertRotation(this.location.getYaw()));
            this.sendPacket(packet);
            this.sendPacket(packet2);
        }

    }

    public boolean isMove() {
        return this.location.getX() != this.lastLocation.getX() || this.location.getY() != this.lastLocation.getY() || this.lastLocation.getZ() != this.lastLocation.getZ();
    }

    public boolean isMove(Location last, Location update) {
        return last.getX() != update.getX() || last.getY() != update.getY() || last.getZ() != update.getZ();
    }

    public boolean isRotation() {
        return this.location.getPitch() != this.lastLocation.getPitch() || this.location.getYaw() != this.lastLocation.getYaw();
    }

    public byte convertRotation(float rotation) {
        return (byte)((int)(rotation * 256.0F / 360.0F));
    }

    public int convertCoordinate(double coordinate) {
        return MathHelper.floor(coordinate * 32.0D);
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }
}
