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
package com.github.cc007.headsplacer;

import com.github.cc007.headsplacer.commands.HeadsPlacerCommand;
import com.github.cc007.headsplacer.commands.HeadsPlacerTabCompleter;
import com.github.cc007.headsplugin.HeadsPlugin;
import com.github.cc007.headsutils.HeadsUtils;
import com.github.cc007.headsutils.heads.HeadsCategory;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author Rik Schaaf aka CC007 <http://coolcat007.nl/>
 */
public class HeadsRowPlacer extends HeadsPlugin {

    private Plugin vault = null;
    private Permission permission = null;
    private HeadsUtils headsUtils;
    Logger log;

    @Override
    public void onEnable() {
        /* Setup the utils */
        log = getLogger();
        headsUtils = HeadsUtils.getInstance(log);
        headsUtils.loadCategories();
        
        /* Setup plugin hooks */
        vault = getPlugin("Vault");
        if (vault != null) {
            setupPermissions();
        }
        /* Register commands */
        getCommand("customheads").setExecutor(new HeadsPlacerCommand(this));
        getCommand("customheads").setTabCompleter(new HeadsPlacerTabCompleter(headsUtils));

        /* Config stuffs */
        this.getCategoriesConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        for (HeadsCategory category : headsUtils.getCategories().getList()) {
            category.clear();
        }
        headsUtils.getCategories().clear();
        headsUtils = null;
        vault = null;
        permission = null;
    }

    /**
     * Setup permissions
     *
     * @return True: Setup correctly, Didn't setup correctly
     */
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);

        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }

        if (permission == null) {
            getLogger().log(Level.WARNING, "Could not hook Vault!");
        } else {
            getLogger().log(Level.WARNING, "Hooked Vault!");
        }

        return (permission != null);
    }

    /**
     * Get the vault
     *
     * @return the vault
     */
    public Plugin getVault() {
        return vault;
    }

    /**
     * Get the permissions
     *
     * @return the permissions
     */
    public Permission getPermission() {
        return permission;
    }

    /**
     * Get the HeadUtils instance
     *
     * @return the HeadUtils instance
     */
    public HeadsUtils getHeadsUtils() {
        return headsUtils;
    }
}