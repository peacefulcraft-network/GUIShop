package net.peacefulcraft.tarje.shop;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.peacefulcraft.tarje.Tarje;

public class SellMenu {

  private Player p;

  public SellMenu() {
  }

  /**
 * Open the shop inventory for a player
 * @param p The player to open the inventory for
 */
  public void openMenu(Player p) {
    p.openInventory(Bukkit.getServer().createInventory(null, 45, "Sell Items"));
  }

  /**
   * Don't let players move items into the sell inventory if they're not sellable
   * @param ev
   */
  public void onInventoryClick(InventoryClickEvent ev) {
    if (ev.getCurrentItem() == null) { return; }

    if (!Tarje._this().isItemSellable(ev.getCurrentItem().getType())) {
      ev.setCancelled(true);
      ((Player) ev.getView().getPlayer()).sendMessage(Tarje.messagingPrefix + ev.getCurrentItem().getType() + " is not sellable.");
    }
  }

  public void onClose(InventoryCloseEvent ev) {
    Player p = (Player) ev.getPlayer();
    Inventory inventory = ev.getInventory();

    String confirmationMessage = "You sold ";
    double moneyDue = 0.0;
    for (ItemStack item : inventory.getContents()) {
      if (item == null || item.getType() == Material.AIR) { continue; }

      if (Tarje._this().isItemSellable(item.getType())) {
        Tarje._this().logDebug(item.getType() + " is sellable for " + Tarje._this().getSellableItemPrice(item.getType()));
        moneyDue += Tarje._this().getSellableItemPrice(item.getType()) * item.getAmount();
        confirmationMessage += item.getType() + ", ";
      } else {
        p.sendMessage(Tarje.messagingPrefix + item.getType() + " is not sellable.");
        p.getInventory().addItem(item);
      }
    }

    if (moneyDue > 0) {
      confirmationMessage = confirmationMessage.substring(0, confirmationMessage.length() - 2);
      p.sendMessage(Tarje.messagingPrefix + confirmationMessage + " for $" + moneyDue);
      Tarje._this().getEconomyService().depositPlayer(p, moneyDue);
    }
  }
}
