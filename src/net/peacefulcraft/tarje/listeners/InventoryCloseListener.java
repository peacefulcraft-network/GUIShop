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
    if (Tarje._this().shopExists(inventoryName)) {
      Tarje._this().getShops().get(inventoryName).onInventoryClosed((Player) ev.getPlayer());
    }
  }  
}