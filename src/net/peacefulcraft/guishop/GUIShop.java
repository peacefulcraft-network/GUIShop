package net.peacefulcraft.guishop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.guishop.config.Configuration;
import net.peacefulcraft.guishop.shop.Shop;
public class GUIShop extends JavaPlugin {
  
  public static final String messagingPrefix = ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET;

  private static GUIShop _this;
    public static GUIShop _this() { return _this; }

  private Configuration configuration;
    public Configuration getConfiguration() { return configuration; }

  private Shop indexShop;
    public Shop getIndexShop() { return indexShop; }

  private ArrayList<Shop> shops;
    public Collection<Shop> getShops() { return Collections.unmodifiableCollection(shops); }
    public void registerShop(Shop shop) {
      shops.add(shop);
      generateIndexShop();
    }
    public void removeShop(String shopName) {
      for(int i=0; i<shops.size(); i++) {
        if (shops.get(i).getConfig().getShopName().equalsIgnoreCase(shopName)) {
          shops.remove(i);
          break;
        }
      }
    }

  /**
   * Called when Bukkit server enables the plguin
   * For improved reload behavior, use this as if it was the class constructor
   */
  public void onEnable() {
    this._this = this;
    this.shops = new ArrayList<Shop>();

    // Save default config if one does not exist, load the configuration into memory
    this.saveDefaultConfig();
    configuration = new Configuration(this.getConfig());

    this.setupCommands();
    this.setupEventListeners();
  }

    private void generateIndexShop() {
      int shopInventorySize = ((this.shops.size() / 9) * 9) + 9;
      if (this.indexShop == null || this.indexShop.getShopSize() > shopInventorySize) {
        if (this.indexShop != null) {
          this.indexShop.closeAllInventoryViews();
        }
        this.indexShop = new Shop("Server Shops", (this.shops.size() / 9 * 9) + 9);
      }

      AtomicReferenceArray<ItemStack> shopItems = new AtomicReferenceArray<>(shops.size());
      AtomicInteger i = new AtomicInteger(0);
      this.shops.forEach((shop) -> {
        ItemStack shopItem = new ItemStack(shop.getConfig().getDisplayItem());
        ItemMeta itemMeta = shopItem.getItemMeta();
        itemMeta.setDisplayName(shop.getConfig().getShopName());
        shopItem.setItemMeta(itemMeta);
        shopItems.set(i.getAndIncrement(), shopItem);
      });

      for (int j=0; j<shopInventorySize; j++) {
        if (j > shopItems.length() - 1) {
          this.indexShop.setShopItem(j, new ItemStack(Material.AIR));
        } else {
          this.indexShop.setShopItem(j, shopItems.get(j));
        }
      }
    }

  public void logDebug(String message) {
    if (configuration.isDebugEnabled()) {
      this.getServer().getLogger().log(Level.INFO, message);
    }
  }
  
  public void logWarning(String message) {
    this.getServer().getLogger().log(Level.WARNING, message);
  }

  public void logSevere(String message) { 
    this.getServer().getLogger().log(Level.SEVERE, message);
  }

  public void reloadPlugin() {
    // Reload configuration
    // Safley reload all shops. respecting existing, open InventoryViews
  }

  /**
   * Called whenever Bukkit server disableds the plugin
   * For improved reload behavior, try to reset the plugin to it's initaial state here.
   */
  public void onDisable () {
    this.getServer().getScheduler().cancelTasks(this);
  }

    private void setupCommands() {
    }

    private void setupEventListeners() {
    }
}