package net.peacefulcraft.guishop.config;

import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ShopConfigurationIterator implements Iterator<ShopItem> {

  private FileConfiguration c;

  private int i = 0;

  public ShopConfigurationIterator(FileConfiguration c) {
    this.c = c;
  }

  @Override
  public boolean hasNext() {
    return c.contains((i + 1) + "");
  }

  @Override
  public ShopItem next() {
    ConfigurationSection subSection = c.getConfigurationSection(i + "");
    Material material = Material.valueOf(subSection.getString("item").toUpperCase());

    double buyPrice = -1.00;
    if (c.isDouble("buy")) {
      buyPrice = c.getDouble("buy");
    }
    
    double sellPrice = -1.00;
    if (c.isDouble("sell")) {
      sellPrice = c.getDouble("sell");
    }

    i++;

    if (sellPrice < 0 && buyPrice < 0) {
      return new ShopItem(material, false, false);
    } else if (sellPrice < 0 && buyPrice >= 0) {
      return new ShopItem(material, buyPrice, false);
    } else if (sellPrice >= 0 && buyPrice >= 0) {
      return new ShopItem(material, false, sellPrice);
    } else {
      return new ShopItem(material, buyPrice, sellPrice)
    }
  }
  
}