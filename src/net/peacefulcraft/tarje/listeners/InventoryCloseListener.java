package net.peacefulcraft.tarje.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.peacefulcraft.tarje.Tarje;

public class InventoryCloseListener implements Listener {

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent ev) {
    String inventoryName = ev.getView().getTitle();

    // Check if index shop
    if (inventoryName.equals("Server Shops")) {
      Tarje._this().synchronize(() -> {
        Tarje._this().getIndexShop().onInventoryClosed((Player) ev.getPlayer());
        Tarje._this().logDebug("Unregistered InventoryView for " + ev.getPlayer().getName() + " on shop " + inventoryName);
      });

    // Check if general item shop
    } else if (Tarje._this().shopExists(inventoryName)) {
      Tarje._this().synchronize(() -> {
        Tarje._this().getShops().get(inventoryName).onInventoryClosed((Player) ev.getPlayer());
        Tarje._this().logDebug("Unregistered InventoryView for " + ev.getPlayer().getName() + " on shop " + inventoryName);
      });
    
    
    // Check if purchase quantity shop
    } else if (inventoryName.substring(0, 8).equals("Purchase")) {
      String shopName = inventoryName.split(" ")[1];
      if (Tarje._this().shopExists(shopName)) {
        Tarje._this().synchronize(() -> {
          Tarje._this().getShop(shopName).onInventoryClosed((Player) ev.getView().getPlayer());
          Tarje._this().logDebug("Unregistered InventoryView for " + ev.getPlayer().getName() + " on shop " + inventoryName);
        });
      }

    // Check if sell menu
    } else if (inventoryName.equals("Sell Items")) {
      Tarje._this().synchronize(() -> {
        Tarje._this().getSellMenu().onClose(ev);
      });
    }
  }  
}