package net.peacefulcraft.tarje.shop;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.peacefulcraft.tarje.Tarje;
import net.peacefulcraft.tarje.config.ShopConfiguration;
import net.peacefulcraft.tarje.config.ShopItem;

public class Shop {
  private ShopConfiguration config;
    public ShopConfiguration getConfig() { return config; }
  
  public boolean isEnabled() { return config.isShopEnabled(); }

  private Inventory inventory;
    public int getShopSize() { return inventory.getSize(); }

  private HashMap<Player, InventoryView> activeViews;

  public Shop(String title, int size) {
    this.activeViews = new HashMap<Player, InventoryView>();
    this.inventory = Tarje._this().getServer().createInventory(null, size, title);
  }

  public Shop(ShopConfiguration config) {
    this.config = config;
    inventory = Tarje._this().getServer().createInventory(null, InventoryType.CHEST, config.getShopName());
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
   * @param p The player to open the inventory for
   */
  public void openShop(Player p) {
    this.activeViews.put(p, p.openInventory(inventory));
  }

  /**
   * Close the InventoryView for this player if they have the shop open
   * @param p The player who's shop InventoryView to try and close
   */
  public void closeShop(Player p) {
    if (this.activeViews.containsKey(p)) {
      this.activeViews.get(p).close();
    }
  }

  /**
   * Called when a player clicks on an InventoryView of this shop. Will generate a purchase quantitiy menu
   * and switch the player's open inventory to that menu for selecting how many items they want to purchase.
   * @param p The player who clicked on the InventoryView
   * @param slotNumber Slot number in the inventory where the click occured
   * @param item The item that was clicked on
   */
  public void onShopInventoryClick(Player p, int slotNumber, ItemStack item) {
    ShopItem shopItem = this.config.getItems().get(slotNumber);
    if (!shopItem.isPurchasable()) {
      p.sendMessage(Tarje.messagingPrefix + "Sorry, " + shopItem.getItem() + " is not purchasable.");
      return;
    }

    Inventory purchaseQuantityMenu = this.generatePurchaseQuantityMenu(shopItem);
    this.closeShop(p);
    this.activeViews.put(p, p.openInventory(purchaseQuantityMenu));
  }

    /**
     * Generates an inventory menu for the player to select how many of an item they want to purchase
     * @param item The shop item being purchased
     * @return An inventory that the player can use to select the quantity of items they want to purchase
     */
    private Inventory generatePurchaseQuantityMenu(ShopItem item) {
      Inventory inv = Tarje._this().getServer().createInventory(null, 9, "Purchase " + config.getShopName() + " " + item.getSlot());
      int[] purchaseQuantities = { 1, 4, 8, 16, 24, 32, 64 };
      for(int i=0; i<purchaseQuantities.length; i++) {
        ItemStack purchaseItem = new ItemStack(item.getItem());
        
        ItemMeta purchaseItemMeta = purchaseItem.getItemMeta();
        ArrayList<String> purchaseLore = new ArrayList<String>();
        purchaseLore.add("Buy " + purchaseQuantities[i] + " for $" + purchaseQuantities[i] * item.getBuyPrice());
        purchaseItemMeta.setLore(purchaseLore);
        purchaseItem.setItemMeta(purchaseItemMeta);
        
        inv.setItem(i + 1, purchaseItem);
      }

      return inv;
    }

  /**
   * Called when a player clicks on a purchase quantity menu generate by this shop
   * @param p The player who clicked on the inventory
   * @param title The title of the inventory. Contains serialized information to get the shop item
   * @param item The item in the inventory that was clicked
   */
  public void onPurchaseQuantityInventoryClick(Player p, String title, ItemStack item) {
    ShopItem shopItem = config.getItems().get(Integer.valueOf(title.split(" ")[2]));
    int purchaseQuantity = item.getAmount();
    double purcahsePrice = shopItem.getBuyPrice() * purchaseQuantity;

    if (!Tarje._this().getEconomyService().has(p, purcahsePrice)) {
      double playerBalance = Tarje._this().getEconomyService().getBalance(p);
      p.sendMessage(Tarje.messagingPrefix + "Sorry, " + shopItem.getItem() + " costs $" + purcahsePrice + " and your account balance is only $" + playerBalance + ".");
      return;
    }

    Tarje._this().getEconomyService().withdrawPlayer(p, purcahsePrice);
    p.getInventory().addItem(new ItemStack(shopItem.getItem()));
    p.sendMessage(Tarje.messagingPrefix + "You bought " + purchaseQuantity + " " + shopItem.getItem() + " for $" + purcahsePrice + ".");
  }

  /**
   * Mark the InventoryView as closed
   */
  public void onInventoryClosed(Player p) {
    this.activeViews.remove(p);
  }

  /**
   * Close all open InventoryViews for this shop
   */
  public void closeAllInventoryViews() {
    this.activeViews.forEach((p, view) -> {
      view.close();
      p.sendMessage(Tarje.messagingPrefix + "GUIShop has been updated. You can re-open the shop with /shop");
    });
  }
}