package ac.knight.command;

import ac.knight.user.User;
import ac.knight.Knight;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnticheatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {

        if(cs instanceof Player) {

            Player player = (Player) cs;
            if(player.hasPermission("knight.command")) {

                if(args.length == 0) {
                    sendHelp(player);
                } else if(args[0].equalsIgnoreCase("testmode")) {
                    Knight.testmode = !Knight.testmode;
                    String status = Knight.testmode ? "on" : "off";
                    player.sendMessage(String.format("%s§7Turned §btestmode §7%s.", Knight.PREFIX, status));
                } else if(args[0].equalsIgnoreCase("alerts")) {
                    User user = Knight.getInstance().users.get(player);
                    user.alerts = !user.alerts;
                    String status = user.alerts ? "on" : "off";
                    player.sendMessage(String.format("%s§7Turned §balerts §7%s.", Knight.PREFIX, status));
                } else sendHelp(player);

            } else {

                player.sendMessage(" ");
                player.sendMessage("§7This server is §bprotected §7by §b§lKnight§7.");
                player.sendMessage("§7Not getting §blegit flagged §7since AAC!");
                player.sendMessage("§7~§b§lUnbreathable");
                player.sendMessage(" ");

            }

        } else cs.sendMessage("No console support!");

        return false;
    }

    public void sendHelp(Player player) {

        player.sendMessage(" ");
        player.sendMessage("§b/knight alerts §8-> §7Turn on alerts.");
        player.sendMessage("§b/knight testmode §8-> §7Turn on testmode.");
        player.sendMessage(" ");

    }

}
