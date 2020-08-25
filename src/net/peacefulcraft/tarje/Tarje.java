package net.peacefulcraft.tarje;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.peacefulcraft.tarje.commands.ShopCommand;
import net.peacefulcraft.tarje.config.Configuration;
import net.peacefulcraft.tarje.listeners.InventoryCloseListener;
import net.peacefulcraft.tarje.shop.Shop;
public class Tarje extends JavaPlugin {
  
  public static final String messagingPrefix = ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET;

  private static Tarje _this;
    public static Tarje _this() { return _this; }

  private Configuration configuration;
    public Configuration getConfiguration() { return configuration; }

  private Economy economyService;
    public Economy getEconomyService() { return economyService; }

  private Shop indexShop;
    public Shop getIndexShop() { return indexShop; }

  private HashMap<String, Shop> shops;
    public boolean shopExists(String name) { return shops.containsKey(name); }
    public Shop getShop(String name) { return shops.get(name); }
    public Map<String, Shop> getShops() { return Collections.unmodifiableMap(shops); }
    public void registerShop(Shop shop) {
      shops.put(shop.getConfig().getShopName(), shop);
      generateIndexShop();
    }
    public void removeShop(String shopName) {
      shops.remove(shopName);
    }

  /**
   * Called when Bukkit server enables the plguin
   * For improved reload behavior, use this as if it was the class constructor
   */
  public void onEnable() {
    if (!this.setupDependencies()) {
      this.getServer().getPluginManager().disablePlugin(this);
    }

    this._this = this;
    this.shops = new HashMap<String, Shop>();

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
      this.shops.entrySet().forEach((shop) -> {
        ItemStack shopItem = new ItemStack(shop.getValue().getConfig().getDisplayItem());
        ItemMeta itemMeta = shopItem.getItemMeta();
        itemMeta.setDisplayName(shop.getKey());
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

    private boolean setupDependencies() {
      return this.setupDependencyVault();
    }
    
      private boolean setupDependencyVault() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
          this.logSevere("This plugin requires Vault to function and will now disable itself because Vault is not installed on this server.");
          return false;
        }
        this.economyService = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        return true;
      }

    private void setupCommands() {
      this.getCommand("buy").setExecutor(new ShopCommand());
    }

    private void setupEventListeners() {
      this.getServer().getPluginManager().registerEvents(new InventoryCloseListener(), this);
    }
}