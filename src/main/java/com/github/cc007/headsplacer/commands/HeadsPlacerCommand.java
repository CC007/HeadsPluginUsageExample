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
import com.github.cc007.headsplugin.heads.HeadsPlacer;
import com.github.cc007.headsplugin.heads.HeadCreator;
import com.github.cc007.headsutils.heads.HeadsCategory;
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
 */
public class HeadsPlacerCommand implements CommandExecutor {

    private final HeadsRowPlacer plugin;

    public HeadsPlacerCommand(HeadsRowPlacer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("update") && sender.hasPermission("heads.update")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.GREEN + "Updating all categories...");
                plugin.getHeadsUtils().loadCategories();
                sender.sendMessage(ChatColor.GREEN + "Update complete.");
            } else if (plugin.getCategoriesConfig().isInt("predefinedcategories." + args[1]) || plugin.getCategoriesConfig().isInt("customcategories." + args[1] + ".id")) {
                sender.sendMessage(ChatColor.GREEN + "Updating all category: " + args[1] + "...");
                plugin.getHeadsUtils().loadCategory(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Update complete.");
            } else {
                sender.sendMessage(ChatColor.RED + "No category found with that name, possible categories: ");
                sendPossibleCategoriesList(sender);
                return true;
            }
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can perform this command");
            return true;
        }

        Player player = (Player) sender;

        if (player.hasPermission("heads.place")) {

            sender.sendMessage(ChatColor.GREEN + "Placing heads and filling inventory...");
            if (args.length == 0) {
                List<ItemStack> heads = HeadCreator.getItemStacks(plugin.getHeadsUtils().getAllCategoryHeads());
                for (int i = 0; i < heads.size(); i++) {
                    ItemStack head = heads.get(i);
                    player.getInventory().addItem(head);
                    HeadsPlacer.placeHead(head, i, 63, 0, i, player.getWorld(), plugin.getLogger());
                }
                player.sendMessage(ChatColor.GREEN + "The heads have been placed and inventory has been filled");
                return true;
            }

            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("categories")) {
                    sendCategoriesList(sender);
                    return true;
                } else {
                    boolean flag = false;
                    for (HeadsCategory category : plugin.getHeadsUtils().getCategories().getList()) {
                        if (args[0].equalsIgnoreCase(category.getCategoryName())) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        List<ItemStack> heads = HeadCreator.getItemStacks(plugin.getHeadsUtils().getCategoryHeads(args[0]));
                        for (int i = 0; i < heads.size(); i++) {
                            ItemStack head = heads.get(i);
                            player.getInventory().addItem(head);
                            HeadsPlacer.placeHead(head, i, 63, 0, i, player.getWorld(), plugin.getLogger());
                        }
                        player.sendMessage(ChatColor.GREEN + "The heads have been placed and inventory has been filled");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.RED + "No category found with that name, possible categories: ");
                sendCategoriesList(sender);
                return true;
            }
        }
        return false;
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
}
