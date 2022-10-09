package ac.knight.packet;

import ac.knight.Knight;
import ac.knight.event.impl.*;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.event.impl.*;
import ac.knight.user.User;
import ac.knight.util.ReflectionUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.util.Vector;

public class PacketHandler extends ChannelDuplexHandler {

    public User user;

    public PacketHandler(User user) {
        this.user = user;
    }

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

        super.channelRead(channelHandlerContext, o);

        if(!(o instanceof Packet)) return;

        try {
            Packet packet = (Packet) o;
            call(new EventIncoming(packet));

            if(packet instanceof PacketPlayInFlying) {

                if(user.data.breakingBlock && !user.data.swing) {
                    user.data.breakingBlock = false;
                }
                user.data.swing = false;

                user.data.lastLocations.add(user.getPlayer().getLocation());
                if(user.data.lastLocations.size() > 20) user.data.lastLocations.remove(0);

                PacketPlayInFlying flying = (PacketPlayInFlying) packet;
                if(flying.g()) {
                    user.data.handleMove(flying.a(), flying.b(), flying.c(), flying.f());
                    if(user.data.initialized && user.data.teleportTicks > 5) user.hitbox.recalculate(user.data.x-0.4, user.data.y, user.data.z-0.4, user.data.x+0.4, user.data.y+2.1, user.data.z+0.4, user.getPlayer().getWorld());
                } else {
                    user.data.handleTick();
                }
                if(!user.data.initialized) return;

                if(flying.h()) {
                    user.data.handleRotation(flying.d(), flying.e());
                    call(new EventRotation());
                }
                if(flying.g()) {
                    call(new EventMove());
                }

            } else if(packet instanceof PacketPlayInUseEntity) {

                PacketPlayInUseEntity use = (PacketPlayInUseEntity) packet;
                if(use.a().equals(PacketPlayInUseEntity.EnumEntityUseAction.ATTACK)) {
                    user.data.attackTicks = 0;
                }

            } else if(packet instanceof PacketPlayInArmAnimation) {
                user.data.handleSwing();
                call(new EventSwing());
            } else if(packet instanceof PacketPlayInTransaction && !(user.data.velocityCount < 1)) {
                if(((PacketPlayInTransaction) packet).b() >= 0) {
                    int id = ((PacketPlayInTransaction) packet).b();
                    if(!user.data.velocity.containsKey(id)) return;

                    user.data.velocityTicks = 0;
                    call(new EventVelocity());
                    user.data.velocityCount--;
                    Vector velocity = user.data.velocity.get(id);
                    user.data.velocity.remove(id);
                    user.data.remainingVeloX += Math.abs(velocity.getX()) + 0.03;
                    user.data.remainingVeloZ += Math.abs(velocity.getZ()) + 0.03;
                }
            } else if(packet instanceof PacketPlayInBlockDig) {

                PacketPlayInBlockDig dig = (PacketPlayInBlockDig) packet;
                if(dig.c().equals(PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK)) {
                    user.data.breakingBlock = true;
                } else if(dig.c().equals(PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK)) {
                    user.data.breakingBlock = false;
                }

            }

        } catch (Exception ignored) {}

    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {

        super.write(channelHandlerContext, o, channelPromise);

        if(!(o instanceof Packet)) return;

        try {
            Packet packet = (Packet) o;
            call(new EventOutgoing(packet));

            if(packet instanceof PacketPlayOutPosition) {
                PacketPlayOutPosition pos = (PacketPlayOutPosition) packet;
                user.data.teleportX = (double) ReflectionUtil.getField("a", pos);
                user.data.teleportY = (double) ReflectionUtil.getField("b", pos);
                user.data.teleportZ = (double) ReflectionUtil.getField("c", pos);
                user.data.teleportYaw = (float) ReflectionUtil.getField("d", pos);
                user.data.teleportPitch = (float) ReflectionUtil.getField("e", pos);
                user.data.teleported = true;
            }
            else if(packet instanceof PacketPlayOutEntityVelocity) {

                PacketPlayOutEntityVelocity velocity = (PacketPlayOutEntityVelocity) packet;
                int id = (int) ReflectionUtil.getField("a", velocity);
                if(id != user.getPlayer().getEntityId()) return;
                int x = (int) ReflectionUtil.getField("b", velocity);
                int y = (int) ReflectionUtil.getField("c", velocity);
                int z = (int) ReflectionUtil.getField("d", velocity);
                short tid = (short) user.data.handleVelocity(x, y, z);
                user.getCraftPlayer().getHandle().playerConnection.sendPacket(new PacketPlayOutTransaction(0, tid, false));
                user.data.velocityCount += 1;
            }
            else if(packet instanceof PacketPlayOutExplosion) {

                PacketPlayOutExplosion explosion = (PacketPlayOutExplosion) packet;
                double x = (double) ReflectionUtil.getField("a", explosion);
                double y = (double) ReflectionUtil.getField("b", explosion);
                double z = (double) ReflectionUtil.getField("c", explosion);
                short id = (short) (user.data.handleVelocity(x, y, z));
                user.getCraftPlayer().getHandle().playerConnection.sendPacket(new PacketPlayOutTransaction(0, id, false));
                user.data.velocityCount += 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void call(Event event) {
        if(!user.data.initialized || user.exempted) return;
        for(Check check : user.checks) {
            check.onEvent(event);
        }
    }

}
