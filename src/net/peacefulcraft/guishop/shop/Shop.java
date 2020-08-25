package net.peacefulcraft.guishop.shop;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
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
    public int getShopSize() { return inventory.getSize(); }

  private HashMap<Player, InventoryView> activeViews;

  public Shop(String title, int size) {
    this.activeViews = new HashMap<Player, InventoryView>();
    this.inventory = GUIShop._this().getServer().createInventory(null, size, title);
  }

  public Shop(ShopConfiguration config) {
    this.config = config;
    inventory = GUIShop._this().getServer().createInventory(null, InventoryType.CHEST, config.getShopName());
    for(ShopItem item : config) {
      setShopItem(item.getSlot(), item);
    }
    this.activeViews = new HashMap<Player, InventoryView>();
  }

  /**
   * Takes a pre-configured items stack and adds it to the
   * shop inveotry. Does not save item to shop config
   * @param loc The inventory location for the item
   * @param item The itemstack to plave in the inventory
   */
  public void setShopItem(int loc, ItemStack item) {
    inventory.setItem(loc, item);
    this.updateInventoryViews();
  }

  /**
   * Takes a shop item configuration and generates the appropriate itemstack
   * to place at the specified location within the inventory
   * @param loc The inventory location for the item
   * @param item The item configuration used to generate the item stack
   */
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
    this.inventory.setItem(item.getSlot(), displayItem);
      
    config.setShopItem(loc, item);
    this.updateInventoryViews();
  }

  public void updateInventoryViews() {
    if (this.activeViews.size() > 0) {
      this.activeViews.keySet().forEach((p) -> {
        p.updateInventory();
      });
    }
  }

  /**
   * Open the shop inventory for a player
   */
  public void openShop(Player p) {
    this.activeViews.put(p, p.openInventory(inventory));
  }

  public void closeShop(Player p) {
    this.activeViews.remove(p).close();
  }

  public void closeAllInventoryViews() {
    this.activeViews.forEach((p, view) -> {
      view.close();
      p.sendMessage(GUIShop.messagingPrefix + "GUIShop has been updated. You can re-open the shop with /shop");
    });
  }
}