package net.peacefulcraft.guishop.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class GUISAdminTablCompleter implements TabCompleter {

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    List<String> options = new ArrayList<String>();

    if (options.size() > 0) { return options; }
    return null;
  }
  
}