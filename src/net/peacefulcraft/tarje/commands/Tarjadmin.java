package net.peacefulcraft.tarje.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.tarje.Tarje;
import net.peacefulcraft.tarje.config.ShopConfiguration;
import net.peacefulcraft.tarje.shop.ShopMenu;

public class Tarjadmin implements CommandExecutor {

  /**
   * TODO: Investigate Async command processing
   *       - Async processing at least for IO work invokers
   */

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (label.equalsIgnoreCase("tarjeadmin")) {
      if (args.length > 0 && args[0].equalsIgnoreCase("createshop")) {
        if (args.length > 1) {
          File shopConfigLocation = new File(Tarje._this().getDataFolder().getPath() + "/shops" + args[1].toLowerCase() + ".yml");
          if (!shopConfigLocation.exists()) {
            ShopConfiguration shopConfig = new ShopConfiguration(args[1]);
            ShopMenu shop = new ShopMenu(shopConfig);
            Tarje._this().registerShop(shop);

            synchronizedMessage(sender,
              Tarje.messagingPrefix + "Shop succesfully created."
            );
          } else {
            synchronizedMessage(sender,
              Tarje.messagingPrefix  + "A shop with the name " + args[1] + " already exists."
            );
          }
        } else {
          synchronizedMessage(sender,
            Tarje.messagingPrefix  + "Please include a name for this shop."
          );
        }
      } else if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
        Tarje._this().getShops().forEach((name, shop) -> {
          synchronizedMessage(sender, Tarje.messagingPrefix + "Shop: " + name);
          shop.getConfig().getItems().forEach((slot, item) -> {
            synchronizedMessage(sender, Tarje.messagingPrefix + slot + ": " + item.getItem());
          });
        });
      } else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
        Tarje._this().reloadPlugin();
        synchronizedMessage(sender,
          Tarje.messagingPrefix  + "Plugin reloaded succesfully"
        );
      } else {
        synchronizedMessage(sender,
          Tarje.messagingPrefix  + "Invalid argument. Valid arguments are " +
          ChatColor.GREEN + "createshop" + ChatColor.GRAY + ", "
        );
      }

      return true;
    }

    return true;
  }
  
  private void synchronizedMessage(CommandSender target, String message) {
    Tarje._this().synchronize(() -> {
      target.sendMessage(message);
    });
  }
}