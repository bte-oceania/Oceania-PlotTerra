package github.BTECompanion.commands;

import github.BTECompanion.BTECompanion;
import github.BTECompanion.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMD_ReloadConfig implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("oceania.admin")) {
                BTECompanion.getPlugin().reloadConfig();
                BTECompanion.getPlugin().saveConfig();

                sender.sendMessage(Utils.getInfoMessageFormat("§aSuccessfully reloaded config."));
            }
        }
        return true;
    }
}
