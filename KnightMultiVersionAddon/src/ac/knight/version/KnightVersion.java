package ac.knight.version;

import ac.knight.Knight;
import com.viaversion.viaversion.api.Via;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class KnightVersion extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        System.out.println("Knight Multi Version Addon: Loaded!");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        System.out.println("Knight Multi Version Addon: Disabled!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Knight.getInstance().users.get(event.getPlayer()).setProtocolVersion(Via.getAPI().getPlayerVersion(event.getPlayer().getUniqueId()));
    }

}
