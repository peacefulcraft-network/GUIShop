package net.peacefulcraft.tarje.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.peacefulcraft.tarje.Tarje;

public class ShopConfiguration implements Iterable<ShopItem> {
  private FileConfiguration c;

  private String shopName;
  public String getShopName() { return shopName; }
  public void setShopName(String name) {
    c.set("name", name);
    shopName = name;
  }

  private boolean isEnabled;
  public boolean isShopEnabled() { return isEnabled; }
  public void setEnabled(boolean enabled) {
    c.set("enabled", enabled);
    isEnabled = enabled;
  }

  private Material displayItem;
  public Material getDisplayItem() { return displayItem; }
  public void setDisplayItem(Material item) {
    c.set("display_item", item);
    displayItem = item;
  }

  private HashMap<Integer, ShopItem> items;
  public HashMap<Integer, ShopItem> getItems() { return items; }
  public void setItem(int index, ShopItem item) { items.put(index, item); }

  public ShopConfiguration(String name) {
    shopName = name;

    File configFile = new File(Tarje._this().getDataFolder().getPath() + "/shops" + name.toLowerCase() + ".yml");
    c = new YamlConfiguration();

    // Load existing shop configuration
    if (configFile.exists()) {
      try {
        c.load(configFile);
        Tarje._this().logDebug("Loaded shop " + name);
      } catch (IOException | InvalidConfigurationException e) {
        e.printStackTrace();
        Tarje._this().logSevere("Error loading shop configuration " + name + ". Is this a valid YAML file?");
      }
      loadValues();
    
    // Create new shop configuration from default
    } else {
      URL defaultShopConfigLocation = getClass().getClassLoader().getResource("default_shop_config.yml");
      File defaultConfigurationFile = new File(defaultShopConfigLocation.toString());
      YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defaultConfigurationFile);
      c.setDefaults(defaultConfiguration);
      try {
        c.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
        Tarje._this().logSevere(
            "Unable to save configuration file for new shop " + name + ". Reference the above error for details.");
      }
    }
  }

  private void loadValues() {
    isEnabled = c.getBoolean("enabled");
    displayItem = Material.valueOf(c.getString("display_item").toUpperCase());

    int i = 1;
    while (c.contains("" + i)) {
      Material material = Material.BARRIER;
      double buyPrice = -1.00;
      double sellPrice =  -1.00;

      if (c.contains(i + ".enabled") && !c.getBoolean(i + ".enabled")) {
        i++;
        continue;
      }

      if (c.contains(i + ".item")) {
        material = Material.valueOf(c.getString(i + ".item"));
      } else {
        Tarje._this().logWarning("Item " + i + " of shop " + shopName + " has no set material and will not be loaded.");
        i++;
        continue;
      }

      if (c.contains(i + ".buy")) {
        buyPrice = c.getDouble(i + ".buy");
      }

      if (c.contains(i + ".sell")) {
        sellPrice = c.getDouble(i + ".sell");
      }

      ShopItem item = null;
      if (buyPrice > 0 && sellPrice > 0) {
        item = new ShopItem(material, i, buyPrice, sellPrice);
      } else if (buyPrice > 0 && sellPrice < 0) {
        item = new ShopItem(material, i, buyPrice, false);
      } else if (buyPrice < 0 && sellPrice > 0) {
        item = new ShopItem(material, i, false, sellPrice);
      } else {
        Tarje._this().logWarning("Item " + material + " of " + shopName + " has neither a buy price or a sell price. This item will be disabled");
        i++;
        continue;
      }

      setItem(i, item);
      i++;
    }
  }

  public void setShopItem(int loc, ShopItem item) {
    items.put(loc, item);
    c.set(loc + "", item);
  }

  @Override
  public Iterator<ShopItem> iterator() {
    return items.values().iterator();
  }
}