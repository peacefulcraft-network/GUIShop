package net.peacefulcraft.guishop.shop;

import net.peacefulcraft.guishop.config.ShopConfiguration;
import net.peacefulcraft.guishop.config.ShopItem;

public class Shop {
  private ShopConfiguration config;
    public ShopConfiguration getConfig() { return config; }
  
  public boolean isEnabled() { return config.isShopEnabled(); }

  public Shop(ShopConfiguration config) {
    this.config = config;
  }

  public void setShopItem(int loc, ShopItem item) {
    // TOOD: update inventory
    config.setShopItem(loc, item);
  }
}