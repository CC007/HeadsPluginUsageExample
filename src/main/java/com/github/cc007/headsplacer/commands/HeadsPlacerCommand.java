/* 
 * The MIT License
 *
 * Copyright 2015 Rik Schaaf aka CC007 <http://coolcat007.nl/>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.cc007.headsplacer.commands;

import com.github.cc007.headsplacer.HeadsRowPlacer;
import com.github.cc007.headsplugin.bukkit.HeadsPlacer;
import com.github.cc007.headsplugin.bukkit.HeadCreator;
import com.github.cc007.headsplugin.utils.heads.HeadsCategory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Autom
 * @author CC007 (http://coolcat007.nl/)
 */
public class HeadsPlacerCommand implements CommandExecutor {

    private final HeadsRowPlacer plugin;

    public HeadsPlacerCommand(HeadsRowPlacer plugin) {
        this.plugin = plugin;
    }

    private boolean update(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.GREEN + "Updating all categories...");
            plugin.getHeadsUtils().loadCategories();
            sender.sendMessage(ChatColor.GREEN + "Update complete.");
            return true;
        }

        if (plugin.getCategoriesConfig().isInt("predefinedcategories." + args[1]) || plugin.getCategoriesConfig().isInt("customcategories." + args[1] + ".id")) {
            sender.sendMessage(ChatColor.GREEN + "Updating all category: " + args[1] + "...");
            try {
                plugin.getHeadsUtils().loadCategory(args[1]);
            } catch (NullPointerException ex) {
                sender.sendMessage(ChatColor.RED + "Category is empty!");
                return false;
            }
            sender.sendMessage(ChatColor.GREEN + "Update complete.");
            return true;
        }

        sender.sendMessage(ChatColor.RED + "No category found with that name, possible categories: ");
        sendPossibleCategoriesList(sender);
        return false;
    }

    private boolean search(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "You need to specify a head! Use: /heads (search|searchfirst|searchatindex <index>) <head name>");
            return false;
        }

        String[] searchArgs = new String[args.length - 1];
        System.arraycopy(args, 1, searchArgs, 0, searchArgs.length);

        player.sendMessage(ChatColor.GREEN + "Placing heads...");
        try {
            List<ItemStack> heads = HeadCreator.getItemStacks(plugin.getHeadsUtils().getHeads(String.join(" ", searchArgs)));
            for (int i = 0; i < heads.size(); i++) {
                ItemStack head = heads.get(i);
                placeHeadAndGetInv(head, player, i);
            }
            player.sendMessage(ChatColor.GREEN + "Heads placed.");
            return true;
        } catch (NullPointerException ex) {
            player.sendMessage(ChatColor.RED + "No heads found!");
            return false;
        }
    }

    private boolean searchFirst(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "You need to specify a head! Use: /heads (search|searchfirst|searchatindex <index>) <head name>");
            return false;
        }

        String[] searchArgs = new String[args.length - 1];
        System.arraycopy(args, 1, searchArgs, 0, searchArgs.length);

        player.sendMessage(ChatColor.GREEN + "Placing head...");
        try {
            ItemStack head = HeadCreator.getItemStack(plugin.getHeadsUtils().getHead(String.join(" ", searchArgs)));
            placeHeadAndGetInv(head, player, 0);
            player.sendMessage(ChatColor.GREEN + "Head placed.");
            return true;
        } catch (NullPointerException ex) {
            player.sendMessage(ChatColor.RED + "No heads found!");
            return false;
        }
    }

    private boolean searchAtIndex(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "You need to specify an index and a head! Use: /heads (search|searchfirst|searchatindex <index>) <head name>");
            return false;
        }
        if (!HeadsPlacerCommand.isInteger(args[1])) {
            player.sendMessage(ChatColor.RED + "You need to specify an index! Use: /heads (search|searchfirst|searchatindex <index>) <head name>");
            return false;
        }
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "You need to specify a head! Use: /heads (search|searchfirst|searchatindex <index>) <head name>");
            return false;
        }

        String[] searchArgs = new String[args.length - 2];
        System.arraycopy(args, 2, searchArgs, 0, searchArgs.length);

        player.sendMessage(ChatColor.GREEN + "Placing head...");
        try {
            ItemStack head = HeadCreator.getItemStack(plugin.getHeadsUtils().getHead(String.join(" ", searchArgs), Integer.parseInt(args[1])));
            placeHeadAndGetInv(head, player, 0);
            player.sendMessage(ChatColor.GREEN + "Head placed.");
            return true;
        } catch (NullPointerException ex) {
            player.sendMessage(ChatColor.RED + "No heads found!");
            return false;
        }
    }
    
    private boolean allCategoriesSearch(Player player){
            player.sendMessage(ChatColor.GREEN + "Placing heads...");
            List<ItemStack> heads = HeadCreator.getItemStacks(plugin.getHeadsUtils().getAllCategoryHeads());
            for (int i = 0; i < heads.size(); i++) {
                ItemStack head = heads.get(i);
                placeHeadAndGetInv(head, player, i);
            }
            player.sendMessage(ChatColor.GREEN + "The heads have been placed and inventory has been filled");
            return true;
        
    }
    
    private boolean categorySearch(Player player, String[] args){
        // check if given category name exists
        boolean flag = false;
        for (HeadsCategory category : plugin.getHeadsUtils().getCategories().getList()) {
            if (args[0].equalsIgnoreCase(category.getCategoryName())) {
                flag = true;
                break;
            }
        }
        
        if (!flag) {
            player.sendMessage(ChatColor.RED + "No category found with that name, possible categories: ");
            sendCategoriesList(player);
            return false;
        } else {
            player.sendMessage(ChatColor.GREEN + "Placing heads...");
            List<ItemStack> heads = HeadCreator.getItemStacks(plugin.getHeadsUtils().getCategoryHeads(args[0]));
            for (int i = 0; i < heads.size(); i++) {
                ItemStack head = heads.get(i);
                placeHeadAndGetInv(head, player, i);
            }
            player.sendMessage(ChatColor.GREEN + "The heads have been placed and inventory has been filled");
            return true;
        }
    }

    private boolean onPlayerCommand(Player player, Command command, String commandLabel, String[] args) {

        if (!player.hasPermission("heads.place")) {
            return false;
        }

        // return the heads of all categories
        if (args.length == 0) {
            return allCategoriesSearch(player);
        }

        //return the category names
        if (args[0].equalsIgnoreCase("categories")) {
            sendCategoriesList(player);
            return true;
        }

        //head search
        if (args[0].equalsIgnoreCase("search") && player.hasPermission("heads.search")) {
            return search(player, args);
        }

        //search only first head
        if (args[0].equalsIgnoreCase("searchfirst") && player.hasPermission("heads.search")) {
            return searchFirst(player, args);
        }

        //search at a specified index
        if (args[0].equalsIgnoreCase("searchatindex") && player.hasPermission("heads.search")) {
            return searchAtIndex(player, args);
        }
        
        // return the heads of the specified category
        return categorySearch(player, args);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {

        // update heads
        if (args.length > 0 && args[0].equalsIgnoreCase("update") && sender.hasPermission("heads.update")) {
            return update(sender, args);
        }

        // from here on only player commands
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can perform this command");
            return false;
        }
        Player player = (Player) sender;
        return onPlayerCommand(player, command, commandLabel, args);
    }

    private void sendCategoriesList(CommandSender sender) {
        List<HeadsCategory> categories = plugin.getHeadsUtils().getCategories().getList();
        List<String> categoryNames = new ArrayList<>();
        for (HeadsCategory category : categories) {
            categoryNames.add(category.getCategoryName());
        }
        sender.sendMessage(ChatColor.GOLD + StringUtils.join(categoryNames, ", "));
    }

    private void sendPossibleCategoriesList(CommandSender sender) {
        Set<String> categoryNames = plugin.getCategoriesConfig().getConfigurationSection("predefinedcategories").getKeys(false);
        categoryNames.addAll(plugin.getCategoriesConfig().getConfigurationSection("customcategories").getKeys(false));
        sender.sendMessage(ChatColor.GOLD + StringUtils.join(categoryNames, ", "));
    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c <= '/' || c >= ':') {
                return false;
            }
        }
        return true;
    }

    public void placeHeadAndGetInv(ItemStack head, Player player, int locOffset) {
        player.getInventory().addItem(head);
        HeadsPlacer.placeHead(head, locOffset, 63, 0, locOffset, player.getWorld(), plugin.getLogger());
    }
}
