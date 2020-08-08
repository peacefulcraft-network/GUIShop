package net.peacefulcraft.guishop.config;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.peacefulcraft.guishop.GUIShop;
import net.peacefulcraft.guishop.shop.Shop;

public class Configuration {
  private FileConfiguration c;

  private HashMap<String, Shop> configuredShops;
    public Map<String, Shop> getConfiguredShops() { return Collections.unmodifiableMap(configuredShops); }
  private HashMap<String, Shop> enabledShops;
    public Map<String, Shop> getEnabledShops() { return Collections.unmodifiableMap(enabledShops); }

  public Configuration(FileConfiguration c) {
    this.c = c;
    configuredShops = new HashMap<String, Shop>();
    enabledShops = new HashMap<String, Shop>();

    /**
     * Load the default plugin configration and use it's values as fallbacks if user-supplied configuration is incomplete.
     * This will also copy the default values for any missing configuration directives into the user's configuration.
     */
    URL defaultConfigurationURI = getClass().getClassLoader().getResource("config.yml");
    File defaultConfigurationFile = new File(defaultConfigurationURI.toString());
    YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defaultConfigurationFile);
    c.setDefaults(defaultConfiguration);
    saveConfiguration();

    loadShopConfigurations();
  }

  private boolean debugEnabled;
    public void setDebugEnabled(boolean v) {
      // Avoid blocking disk work if we can
      if (v != debugEnabled) {
        debugEnabled = v;
        c.set("debug", v);
        saveConfiguration();
      }
    }
    public boolean isDebugEnabled() { return debugEnabled; }

  public void saveConfiguration() { GUIShop._this().saveConfig(); }

  private void loadShopConfigurations() {
    File shopDataDir = new File(GUIShop._this().getDataFolder().toPath() + "/shops");
    String[] shopFileNames = shopDataDir.list(new YAMLFileFilter());
    if(shopFileNames == null || shopFileNames.length == 0) {
      GUIShop._this().logWarning("No shop configurations found in GUIShop/shops");
      return;
    }
    for(String shopName : shopFileNames) {
      if (configuredShops.containsKey(shopName)) {
        GUIShop._this().logSevere("Attempted to load shop " + shopName + ", but a shop with that name already exists. Shop names must be unique.");
        continue;
      }
      Shop configuredShop = new Shop(new ShopConfiguration(shopName));
      configuredShops.put(shopName, configuredShop);
      if (configuredShop.isEnabled()) {
        enabledShops.put(shopName, configuredShop);
      }
    }
  }
}