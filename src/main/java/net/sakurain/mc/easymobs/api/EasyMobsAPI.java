package net.sakurain.mc.easymobs.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

public interface EasyMobsAPI {

    static EasyMobsAPI getInstance() {
        ServicesManager sm = Bukkit.getServer().getServicesManager();
        EasyMobsAPI api = sm.load(EasyMobsAPI.class);
        if (api == null) {
            throw new IllegalStateException("EasyMobs API is not available. Ensure EasyMobs plugin is loaded.");
        }
        return api;
    }

    static boolean isAvailable() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("EasyMobs");
        return plugin != null && plugin.isEnabled();
    }

    @NotNull
    ItemAPI getItemAPI();

    @NotNull
    MobAPI getMobAPI();

    @NotNull
    SpawnAPI getSpawnAPI();

    @NotNull
    SkillAPI getSkillAPI();

    @NotNull
    RegistryAPI getRegistryAPI();

    @NotNull
    String getVersion();
}
