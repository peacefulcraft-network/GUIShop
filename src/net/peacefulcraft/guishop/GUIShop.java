package net.peacefulcraft.guishop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.peacefulcraft.guishop.config.Configuration;
import net.peacefulcraft.guishop.shop.Shop;
public class GUIShop extends JavaPlugin {
  
  public static final String messagingPrefix = ChatColor.GREEN + "[" + ChatColor.BLUE + "PCN" + ChatColor.GREEN + "]" + ChatColor.RESET;

  private static GUIShop _this;
    public static GUIShop _this() { return _this; }

  private static Configuration configuration;
    public static Configuration getConfiguration() { return configuration; }

  private static ArrayList<Shop> shops;
    public static Collection<Shop> getShops() { return Collections.unmodifiableCollection(shops); }
    public static void registerShop(Shop shop) { shops.add(shop); }
    public static void removeShop(String shopName) {
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