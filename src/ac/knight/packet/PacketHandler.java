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
            user.data.handleIncomingPacket(packet);
            call(new EventIncoming(packet));

            if(packet instanceof PacketPlayInFlying) {

                if(user.data.breakingBlock && !user.data.swing) {
                    user.data.breakingBlock = false;
                }
                user.data.swing = false;

                PacketPlayInFlying flying = (PacketPlayInFlying) packet;

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
            } else if(packet instanceof PacketPlayInBlockDig) {

                PacketPlayInBlockDig dig = (PacketPlayInBlockDig) packet;
                if(dig.c().equals(PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK)) {
                    user.data.breakingBlock = true;
                } else if(dig.c().equals(PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK)) {
                    user.data.breakingBlock = false;
                }

            }

        } catch (Exception ex) {
            System.out.println(" ");
            System.out.println("Knight | INCOMING PACKET PIPELINE ERROR");
            System.out.println("Please report this error to the developers!");
            System.out.println(" ");
            ex.printStackTrace();
        }

    }

    @Override
    public void write(ChannelHandlerContext channelHandlerContext, Object o, ChannelPromise channelPromise) throws Exception {

        super.write(channelHandlerContext, o, channelPromise);

        if(!(o instanceof Packet)) return;

        try {
            Packet packet = (Packet) o;
            user.data.handleOutgoingPacket(packet);
            call(new EventOutgoing(packet));

        } catch (Exception ex) {
            System.out.println(" ");
            System.out.println("Knight | OUTGOING PACKET PIPELINE ERROR");
            System.out.println("Please report this error to the developers!");
            System.out.println(" ");
            ex.printStackTrace();
        }

    }

    private void call(Event event) {
        if(!user.data.initialized || user.isExempted()) return;
        for(Check check : user.checks) {
            check.onEvent(event);
        }
    }

}
