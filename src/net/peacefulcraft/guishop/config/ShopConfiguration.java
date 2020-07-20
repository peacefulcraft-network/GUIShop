package net.peacefulcraft.guishop.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import net.peacefulcraft.guishop.GUIShop;

public class ShopConfiguration implements Iterable<ShopItem> {
  private FileConfiguration c;
  private File configFile;

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

  public ShopConfiguration(String name) {
    shopName = name;

    File configFile = new File(GUIShop._this().getDataFolder().getPath() + "/shops" + name);
    c = new YamlConfiguration();

    if (configFile.exists()) {
      try {
        c.load(configFile);
        GUIShop._this().logDebug("Loaded shop " + name);
      } catch (IOException | InvalidConfigurationException e) {
        e.printStackTrace();
        GUIShop._this().logSevere("Error loading shop configuration " + name + ". Is this a valid YAML file?");
      }
      loadValues();
    } else {
      URL defaultShopConfigLocation = getClass().getClassLoader().getResource("default_shop_config.yml");
      File defaultConfigurationFile = new File(defaultShopConfigLocation.toString());
      YamlConfiguration defaultConfiguration = YamlConfiguration.loadConfiguration(defaultConfigurationFile);
      c.setDefaults(defaultConfiguration);
      try {
        c.save(configFile);
      } catch (IOException e) {
        e.printStackTrace();
        GUIShop._this().logSevere(
            "Unable to save configuration file for new shop " + name + ". Reference the above error for details.");
      }
    }
  }

  private void loadValues() {
    isEnabled = c.getBoolean("enabled");
    displayItem = Material.valueOf(c.getString("display_item").toUpperCase());
  }

  public void setShopItem(int loc, ShopItem item) {
    c.set(loc + "", item);
  }

  @Override
  public Iterator<ShopItem> iterator() {
    return new ShopConfigurationIterator(c);
  }
}