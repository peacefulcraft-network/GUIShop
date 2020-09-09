package net.peacefulcraft.tarje.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
    items = new HashMap<Integer, ShopItem>();

    File configFile = new File(Tarje._this().getDataFolder().getPath() + "/shops/" + name.toLowerCase() + ".yml");
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
    
    // Create new shop configuration from default
    } else {
      try {
        // Copy default config from jar resource to actaul file
        configFile.createNewFile();
        InputStream defaultShopConfigration = getClass().getClassLoader().getResourceAsStream("default_shop_config.yml");
        FileWriter defaultShopConfigrationCopier = new FileWriter(configFile);
        String defaultConfigString = new String(defaultShopConfigration.readAllBytes(), "UTF-8");
        defaultShopConfigrationCopier.write(defaultConfigString);
        defaultShopConfigration.close();
        defaultShopConfigrationCopier.close();

        c.load(configFile);
        c.set("name", this.shopName);
        c.save(configFile);
      } catch (IOException | InvalidConfigurationException e) {
        e.printStackTrace();
        Tarje._this().logSevere(
            "Unable to save configuration file for new shop " + name + ". Reference the above error for details.");
      }
    }

    this.loadValues();
    Tarje._this().logDebug("Shop " + name + " is " + (isEnabled ? "enabled" : "disabled"));
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

      // Load buy price, or set to disabled value if it is invalid
      if (c.contains(i + ".buy")) {
        if (c.isDouble(i + ".buy")) {
          buyPrice = c.getDouble(i + ".buy");
        } else {
          buyPrice = -1.0;
        }
      }

      // Load sell price, or set to disabled value
      if (c.contains(i + ".sell")) {
        if (c.isDouble(i + ".sell")) {
          sellPrice = c.getDouble(i + ".sell");
        } else {
          sellPrice = -1.0;
        }
      }

      // configure the shop item with buy price, sell price, or with one of them disabled
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