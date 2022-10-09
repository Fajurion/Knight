package ac.knight.user;

import ac.knight.check.Check;
import ac.knight.packet.PacketHandler;
import ac.knight.collision.Hitbox;

import java.util.ArrayList;

import net.minecraft.server.v1_8_R3.SharedConstants;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class User {

    public UserData data;
    private int protocolVersion = ProtocolVersion.V1_8; // Last supported version
    private PacketHandler packetHandler;
    private final Player player;
    public Hitbox hitbox = new Hitbox();
    public boolean alerts = false, exempted = false;
    public ArrayList<Check> checks = new ArrayList<>();

    public long lastAlert = 0;

    public int tpCount = 0;

    public User(Player player) {
        this.player = player;
        data = new UserData(this);
        inject();
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void reset() {
        for(Check check : checks) {
            check.setBuffer(0);
        }
    }

    public void inject() {
        this.getCraftPlayer().getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", "knight_packet_handler", packetHandler = new PacketHandler(this));
    }

    public void uninject() {
        getCraftPlayer().getHandle().playerConnection.networkManager.channel.pipeline().remove("packet_handler");
    }

    public boolean isExempted() {
        return exempted;
    }

    public void setExempted(boolean exempted) {
        this.exempted = exempted;
    }

    public Player getPlayer() {
        return player;
    }

    public PacketHandler getPacketHandler() {
        return packetHandler;
    }

    public CraftPlayer getCraftPlayer() {
        return (CraftPlayer) player;
    }

}
