package net.peacefulcraft.guishop.config;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ShopItem implements ConfigurationSerializable {
  private int slot;
    public int getSlot() { return slot; }

  private Material item;
    public Material getItem() { return item; }

  private boolean isPurchasable;
    public boolean isPurchasable() { return isPurchasable; }
  private double buyPrice;
    public double getBuyPrice() { return buyPrice; }

  private boolean isSellable;
    public boolean isSellable() { return isSellable; }
  private double sellPrice;
    public double getSellPrice() { return sellPrice; }

  public ShopItem(Material item, int slot, double buyPrice, double sellPrice) {
    this.item = item;
    this.slot = slot;
    this.isPurchasable = true;
    this.buyPrice = buyPrice;
    this.isSellable = true;
    this.sellPrice = sellPrice;
  }

  public ShopItem(Material item, int slot, boolean buyPrice, double sellPrice) {
    this.item = item;
    this.slot = slot;
    this.isPurchasable = true;
    this.buyPrice = 0.00;
    this.isSellable = true;
    this.sellPrice = sellPrice;
  }

  public ShopItem(Material item, int slot, double buyPrice, boolean sellPrice) {
    this.item = item;
    this.slot = slot;
    this.isPurchasable = true;
    this.buyPrice = buyPrice;
    this.isSellable = true;
    this.sellPrice = 0.00;
  }

  public ShopItem(Material item, int slot, boolean buyboolean, boolean sellPrice) {
    this.item = item;
    this.slot = slot;
    this.isPurchasable = true;
    this.buyPrice = 0.00;
    this.isSellable = true;
    this.sellPrice = 0.00;
  }

  @Override
  public Map<String, Object> serialize() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("item", item);

    if (isPurchasable) {
      map.put("buy", buyPrice);
    } else {
      map.put("buy", false);
    }

    if (isSellable) {
      map.put("sell", sellPrice);
    } else {
      map.put("sell", false);
    }

    return map;
  }
}