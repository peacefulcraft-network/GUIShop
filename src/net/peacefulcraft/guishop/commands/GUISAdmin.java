package net.peacefulcraft.guishop.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.guishop.GUIShop;
import net.peacefulcraft.guishop.config.ShopConfiguration;
import net.peacefulcraft.guishop.shop.Shop;

public class GUISAdmin implements CommandExecutor {

  /**
   * TODO: Investigate Async command processing
   *       - Async processing at least for IO work invokers
   */

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (label.equalsIgnoreCase("guisadmin")) {
      if (args.length > 0 && args[0].equalsIgnoreCase("createshop")) {
        if (args.length > 1) {
          File shopConfigLocation = new File(GUIShop._this().getDataFolder().getPath() + "/shops" + args[1].toLowerCase() + ".yml");
          if (!shopConfigLocation.exists()) {
            ShopConfiguration shopConfig = new ShopConfiguration(args[1]);
            Shop shop = new Shop(shopConfig);
            GUIShop._this().registerShop(shop);

            synchronizedMessage(sender,
              GUIShop.messagingPrefix + ChatColor.GRAY + "Shop succesfully created."
            );
          } else {
            synchronizedMessage(sender,
              GUIShop.messagingPrefix + ChatColor.GRAY + "A shop with the name " + args[1] + " already exists."
            );
          }
        } else {
          synchronizedMessage(sender,
            GUIShop.messagingPrefix + ChatColor.GRAY + "Please include a name for this shop."
          );
        }
      } else if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
        GUIShop._this().reloadPlugin();
        synchronizedMessage(sender,
          GUIShop.messagingPrefix + ChatColor.GRAY + "Plugin reloaded succesfully"
        );
      } else {
        synchronizedMessage(sender,
          GUIShop.messagingPrefix + ChatColor.GRAY + "Invalid argument. Valid arguments are " +
          ChatColor.GREEN + "createshop" + ChatColor.GRAY + ", "
        );
      }

      return true;
    }

    return true;
  }

  private void synchronize(Runnable task) {
    GUIShop._this().getServer().getScheduler().runTask(GUIShop._this(), task);
  }
  
  private void synchronizedMessage(CommandSender target, String message) {
    this.synchronize(() -> {
      target.sendMessage(message);
    });
  }
}