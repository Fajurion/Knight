package ac.knight.packet;

import ac.knight.event.impl.*;
import ac.knight.check.Check;
import ac.knight.event.Event;
import ac.knight.user.User;
import ac.knight.user.processor.impl.InitializationProcessor;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_8_R3.*;

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

                PacketPlayInFlying flying = (PacketPlayInFlying) packet;

                if(flying.h()) {
                    call(new EventRotation());
                }
                if(flying.g()) {
                    call(new EventMove());
                }

            } else if(packet instanceof PacketPlayInArmAnimation) {
                call(new EventSwing());
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
        InitializationProcessor processor = (InitializationProcessor) user.data.processor(InitializationProcessor.class);

        if(!processor.initialized || user.isExempted()) return;
        for(Check check : user.checks) {
            check.onEvent(event);
        }
    }

}
