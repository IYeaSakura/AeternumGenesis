package net.sakurain.mc.easymobs.api.impl;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.api.ItemAPI;
import net.sakurain.mc.easymobs.api.event.CustomItemSpawnEvent;
import net.sakurain.mc.easymobs.item.CustomItemManager;
import net.sakurain.mc.easymobs.item.CustomItemTemplate;
import net.sakurain.mc.easymobs.item.ItemBuilder;
import net.sakurain.mc.easymobs.item.ItemIdentifier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class ItemAPIImpl implements ItemAPI {

    private final EasyMobsPlugin plugin;
    private final NamespacedKey itemIdKey;

    public ItemAPIImpl(@NotNull EasyMobsPlugin plugin) {
        this.plugin = plugin;
        this.itemIdKey = new NamespacedKey(plugin, "ezmobs_item_id");
    }

    @Override
    public boolean isCustomItem(@Nullable ItemStack item) {
        return ItemIdentifier.isCustomItem(item);
    }

    @Override
    @Nullable
    public String getItemTemplateId(@Nullable ItemStack item) {
        return ItemIdentifier.getTemplateId(item);
    }

    @Override
    public boolean hasTemplate(@NotNull String templateId) {
        return plugin.getItemManager().hasTemplate(templateId);
    }

    @Override
    @NotNull
    public Optional<ItemStack> buildItem(@NotNull String templateId) {
        return buildItem(templateId, 1);
    }

    @Override
    @NotNull
    public Optional<ItemStack> buildItem(@NotNull String templateId, int amount) {
        CustomItemManager manager = plugin.getItemManager();
        CustomItemTemplate template = manager.getTemplate(templateId);
        if (template == null) {
            return Optional.empty();
        }
        ItemStack item = ItemBuilder.build(template);
        if (amount > 0) {
            item.setAmount(Math.min(amount, item.getMaxStackSize()));
        }
        return Optional.of(item);
    }

    @Override
    public boolean giveItem(@NotNull Player player, @NotNull String templateId) {
        return giveItem(player, templateId, 1);
    }

    @Override
    public boolean giveItem(@NotNull Player player, @NotNull String templateId, int amount) {
        Optional<ItemStack> opt = buildItem(templateId, amount);
        if (opt.isEmpty()) {
            return false;
        }
        ItemStack item = opt.get();
        CustomItemSpawnEvent event = new CustomItemSpawnEvent(templateId, player, item);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        player.getInventory().addItem(event.getItem()).values().forEach(drop ->
                player.getWorld().dropItemNaturally(player.getLocation(), drop));
        return true;
    }

    @Override
    @NotNull
    public Optional<ItemStack> dropItem(@NotNull Location location, @NotNull String templateId, int amount) {
        Optional<ItemStack> opt = buildItem(templateId, amount);
        if (opt.isEmpty()) {
            return Optional.empty();
        }
        ItemStack item = opt.get();
        CustomItemSpawnEvent event = new CustomItemSpawnEvent(templateId, null, item);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return Optional.empty();
        }
        location.getWorld().dropItemNaturally(location, event.getItem());
        return Optional.of(event.getItem());
    }

    @Override
    @NotNull
    public Collection<String> getAllTemplateIds() {
        return plugin.getItemManager().getTemplateIds();
    }

    @Override
    @NotNull
    public Optional<String> getDisplayName(@NotNull String templateId) {
        CustomItemTemplate template = plugin.getItemManager().getTemplate(templateId);
        return template != null ? Optional.ofNullable(template.getName()) : Optional.empty();
    }

    @Override
    @NotNull
    public NamespacedKey getItemIdKey() {
        return itemIdKey;
    }
}
