package net.peacefulcraft.tarje.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.peacefulcraft.tarje.Tarje;

public class ShopCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(Tarje.messagingPrefix + "GUIShops are only openable by players");
      return true;
    }

    if (command.getLabel().equalsIgnoreCase("shop") || command.getLabel().equalsIgnoreCase("buy")) {
      if (Tarje._this().getNumberShopsConfigured() == 0) {
        sender.sendMessage(Tarje.messagingPrefix + "No shops configured to open.");
        return true;
      }

      Tarje._this().getIndexShop().openShop(((Player) sender));
    }
    
    return true;
  } 
}