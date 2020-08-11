package net.peacefulcraft.guishop.shop;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ViewManager implements Listener {
  private HashMap<Player, Shop> openViews;

  public ViewManager() {
    openViews = new HashMap<Player, Shop>();
  }

}