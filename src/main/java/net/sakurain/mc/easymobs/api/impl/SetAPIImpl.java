package net.sakurain.mc.easymobs.api.impl;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.api.SetAPI;
import net.sakurain.mc.easymobs.item.set.ItemSetManager;
import net.sakurain.mc.easymobs.item.set.ItemSetTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SetAPIImpl implements SetAPI {

    private final EasyMobsPlugin plugin;

    public SetAPIImpl(@NotNull EasyMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public Collection<String> getAllSetIds() {
        return plugin.getItemSetManager().getSetIds();
    }

    @Override
    public boolean hasSet(@NotNull String setId) {
        return plugin.getItemSetManager().getSet(setId) != null;
    }

    @Override
    public boolean registerSet(@NotNull String setId, @NotNull ConfigurationSection config) {
        ItemSetTemplate set = ItemSetManager.parseTemplate(setId, config);
        if (set == null) {
            return false;
        }
        return plugin.getItemSetManager().registerSet(set);
    }

    @Override
    public boolean unregisterSet(@NotNull String setId) {
        return plugin.getItemSetManager().unregisterSet(setId);
    }
}
