package ac.knight;

import ac.knight.check.Check;
import ac.knight.check.combat.aim.*;
import ac.knight.check.combat.killaura.KillauraA;
import ac.knight.check.combat.reach.ReachA;
import ac.knight.check.movement.*;
import ac.knight.check.network.SilentA;
import ac.knight.check.network.TimerA;
import ac.knight.check.world.*;
import ac.knight.command.AnticheatCommand;
import ac.knight.user.User;
import ac.knight.user.processor.impl.InitializationProcessor;
import ac.knight.user.processor.impl.MovementProcessor;
import ac.knight.util.MathUtil;
import ac.knight.util.TaskManager;
import ac.knight.check.combat.block.BlockA;
import ac.knight.listener.JoinQuitListener;
import ac.knight.util.BetterRunnable;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Knight extends JavaPlugin {

    public static final String PREFIX = "§8[§b!§8]§r ";
    public static boolean testmode = false;

    private static Knight instance;
    private final ArrayList<Check> checks = new ArrayList<>();
    public final HashMap<Player, User> users = new HashMap<>();
    private TaskManager taskManager;

    @Override
    public void onEnable() {

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("Reload");
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        instance = this;
        taskManager = new TaskManager();
        taskManager.init();

        checks.add(new AimA1(null));
        checks.add(new AimA2(null));
        checks.add(new AimB1(null));
        checks.add(new AimB2(null));
        checks.add(new AimC1(null));
        checks.add(new AimC2(null));

        checks.add(new KillauraA(null));
        checks.add(new ReachA(null));
        checks.add(new BlockA(null));

        checks.add(new SpeedA(null));
        checks.add(new SpeedB1(null));
        checks.add(new SpeedB2(null));
        checks.add(new JumpA(null));
        checks.add(new StrafeA(null));
        checks.add(new SprintA(null));
        checks.add(new FlightA(null));
        checks.add(new FlightB(null));
        checks.add(new GroundA(null));
        checks.add(new GravityA(null));

        checks.add(new ScaffoldA1(null));
        checks.add(new ScaffoldA2(null));
        checks.add(new ScaffoldB(null));
        checks.add(new ScaffoldC1(null));
        checks.add(new ScaffoldC2(null));
        checks.add(new ScaffoldC3(null));
        checks.add(new ScaffoldC4(null));
        checks.add(new ScaffoldE(null));

        checks.add(new TimerA(null));
        checks.add(new SilentA(null));

        this.getCommand("anticheat").setExecutor(new AnticheatCommand());

        taskManager.inject(() -> {

            for(User user : users.values()) {
                InitializationProcessor processor = (InitializationProcessor) user.data.processor(InitializationProcessor.class);
                MovementProcessor movementProcessor = (MovementProcessor) user.data.processor(MovementProcessor.class);

                if(!processor.initialized) {
                    if(movementProcessor.teleportY == -255) continue;
                    if(user.tpCount++ >= 5) {
                        user.getPlayer().teleport(new Location(user.getPlayer().getWorld(), movementProcessor.teleportX, movementProcessor.teleportY, movementProcessor.teleportZ,
                                movementProcessor.teleportYaw, movementProcessor.teleportPitch));
                        user.tpCount = 0;
                    }
                }
            }

        });

        this.getServer().getPluginManager().registerEvents(new JoinQuitListener(), this);

    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public void injectPlayer(Player player) {
        User user = new User(player);
        for(Check check : checks) {
            Check cloned = check.newInstance(user.data);
            cloned.init(user.data);

            user.checks.add(cloned);
        }
        users.put(player, user);
    }

    public void handleFail(Check check, double buffer, User user) {
        this.handleFail(check, buffer, user, "nokeys");
    }

    public void handleFail(Check check, double buffer, User user, String... keys) {

        // Check if plugin message is necessary
        if(buffer > check.getMaxBuffer()/2 && System.currentTimeMillis() - user.lastAlert > 2000) {

            // Build plugin message
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Knight-Alert_");
            out.writeUTF(user.getPlayer().getName() + "_");
            out.writeUTF(check.category + ":" + check.name + ":" + check.description + "_");
            out.writeUTF(MathUtil.round(buffer).doubleValue() + "");

            // Send plugin message
            user.getPlayer().sendPluginMessage(this, "BungeeCord", out.toByteArray());
            user.lastAlert = System.currentTimeMillis();
        }

        if(buffer > check.getMaxBuffer() && !testmode && check.doesKick()) {
            user.reset();
            getTaskManager().inject(new BetterRunnable() {
                @Override
                public void run() {
                    user.getPlayer().kickPlayer("§bUnfair Advantage §8~§b§lKnight");
                    this.uninject();
                }
            });
            return;
        }

        final TextComponent alertComponent = new TextComponent(String.format("%s§b%s §7failed §b%s §8(§b%s§8) §8(§b%s§8)",
                PREFIX, user.getPlayer().getName(), check.category, check.name, MathUtil.round(buffer)));

        final StringBuilder keyBuilder = new StringBuilder();
        String currentKey = "nocurrent";
        for(String s : keys) {
            if(s.equals("nokeys")) continue;

            if(currentKey.equals("nocurrent")) {
                currentKey = s;
            } else {
                keyBuilder.append(" ").append("§8- §b" + currentKey + "§8: §b" + s).append("\n");
                currentKey = "nocurrent";
            }

        }

        alertComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {
                new TextComponent("§8- §b" + check.description + " \n"),
                new TextComponent(keyBuilder.toString()),
                new TextComponent("§8(§7" + user.getCraftPlayer().getHandle().ping + "ms§8) (§7Click to teleport§8)")
        }));

        alertComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/spec " + user.getPlayer().getName()));
        
        for(User u : users.values()) {
            u.getPlayer().spigot().sendMessage(alertComponent);
        }
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public static Knight getInstance() {
        return instance;
    }
}
