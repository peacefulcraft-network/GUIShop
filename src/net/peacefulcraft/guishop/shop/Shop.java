package net.peacefulcraft.guishop.shop;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.peacefulcraft.guishop.GUIShop;
import net.peacefulcraft.guishop.config.ShopConfiguration;
import net.peacefulcraft.guishop.config.ShopItem;

public class Shop {
  private ShopConfiguration config;
    public ShopConfiguration getConfig() { return config; }
  
  public boolean isEnabled() { return config.isShopEnabled(); }

  private Inventory inventory;
    public Inventory getInventory() { return inventory; }

  public Shop(ShopConfiguration config) {
    this.config = config;
    inventory = GUIShop._this().getServer().createInventory(null, InventoryType.CHEST, config.getShopName());
    for(ShopItem item : config) {
      setShopItem(item.getSlot(), item);
    }
  }

  public void setShopItem(int loc, ShopItem item) {
    ItemStack displayItem = new ItemStack(item.getItem());
    ItemMeta displayMeta = displayItem.getItemMeta();

    ArrayList<String> lore = new ArrayList<String>();
    if (item.isPurchasable()) {
      // TODO: Probably needs rounded
      lore.add("Buy: $" + item.getBuyPrice());
    } else {
      lore.add("Item can not purchasable");
    }

    if (item.isSellable()) {
      // TODO: Probably needs rounded
      lore.add("Sell: $" + item.getSellPrice());
    } else {
      lore.add("Item can not be sold");
    }

    displayMeta.setLore(lore);
    displayItem.setItemMeta(displayMeta);
    inventory.setItem(item.getSlot(), displayItem);

    config.setShopItem(loc, item);
  }

  /**
   * WARNING: This method is only inteded to be used by the ViewManager,
   * Invoking this method direclty to open a shop window will result in
   * unexpected behavior.
   * @param p
   */
  protected void openShop(Player p) {
    p.openInventory(inventory);
  }
}