package net.peacefulcraft.guishop.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.guishop.GUIShop;

public class GUISAdmin implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (label.equalsIgnoreCase("guisadmin")) {
      if (args.length < 1) {
        if (args[0].equalsIgnoreCase("createshop")) {
        
        }
      }

      sender.sendMessage(
        GUIShop.messagingPrefix + ChatColor.GRAY + "Invalid argument. Valid arguments are " +
        ChatColor.GREEN + "createshop" + ChatColor.GRAY + ", "
      );
      return true;
    }

    return true;
  }
  
}