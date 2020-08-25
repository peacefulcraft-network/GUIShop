package net.peacefulcraft.guishop.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import net.peacefulcraft.guishop.GUIShop;

public class InventoryCloseListener implements Listener {

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent ev) {
    String inventoryName = ev.getView().getTitle();
    if (GUIShop._this().shopExists(inventoryName)) {
      GUIShop._this().getShops().get(inventoryName).onInventoryClosed((Player) ev.getPlayer());
    }
  }  
}