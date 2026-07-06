package net.sakurain.mc.easymobs.command;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadSubCommand implements SubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        EasyMobsPlugin plugin = EasyMobsPlugin.getInstance();
        plugin.getConfigManager().reloadAll();
        plugin.getItemSetManager().reload(plugin.getConfigManager().getSetConfigs());
        plugin.getItemManager().reload();
        plugin.getMobManager().reload();
        plugin.getSkillManager().reload();
        plugin.getSpawnManager().reload();
        sender.sendMessage(MessageUtil.success("Configuration reloaded!"));
        return true;
    }

    @Override
    public String getPermission() {
        return "easymobs.reload";
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}
