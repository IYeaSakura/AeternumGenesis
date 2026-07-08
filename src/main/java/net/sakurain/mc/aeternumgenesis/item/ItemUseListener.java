package net.sakurain.mc.aeternumgenesis.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.sakurain.mc.aeternumgenesis.AeternumGenesisPlugin;
import net.sakurain.mc.aeternumgenesis.util.ConfigParseUtil;
import net.sakurain.mc.aeternumgenesis.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Handles item use actions defined in custom item templates.
 */
public final class ItemUseListener implements Listener {

    private final AeternumGenesisPlugin plugin;
    private static final NamespacedKey ITEM_ID_KEY = new NamespacedKey("genesis", "genesis_item_id");

    public ItemUseListener(AeternumGenesisPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null || item.isEmpty()) {
            return;
        }
        CustomItemTemplate template = ItemIdentifier.getTemplate(item);
        if (template == null || template.getUseActions().isEmpty()) {
            return;
        }
        boolean rightClick = true;
        boolean leftClick = false;
        boolean shift = event.getPlayer().isSneaking();
        if (handleUse(event.getPlayer(), item, template, rightClick, leftClick, shift)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        if (item == null || item.isEmpty()) {
            return;
        }
        CustomItemTemplate template = ItemIdentifier.getTemplate(item);
        if (template == null || template.getUseActions().isEmpty()) {
            return;
        }
        // Consumption is handled by the event itself; execute actions and optionally cancel.
        handleUse(event.getPlayer(), item, template, true, false, event.getPlayer().isSneaking());
    }

    private boolean handleUse(Player player, ItemStack item, CustomItemTemplate template,
                              boolean rightClick, boolean leftClick, boolean shift) {
        List<CustomItemTemplate.ItemUseAction> actions = template.getUseActions();
        if (actions.isEmpty()) {
            return false;
        }

        boolean anyMatched = false;
        boolean consume = template.isConsumable();
        for (CustomItemTemplate.ItemUseAction action : actions) {
            if (action.requireRightClick() && !rightClick) {
                continue;
            }
            if (action.requireLeftClick() && !leftClick) {
                continue;
            }
            if (action.requireShift() && !shift) {
                continue;
            }
            anyMatched = true;
            if ("consume".equals(action.type())) {
                consume = true;
                continue;
            }
            executeAction(player, action);
        }

        if (!anyMatched) {
            return false;
        }

        if (consume && !item.getType().isEdible() && !(item.getItemMeta() instanceof PotionMeta)) {
            consumeItem(player, item);
        }
        return true;
    }

    private void consumeItem(Player player, ItemStack item) {
        if (item.getAmount() <= 1) {
            player.getInventory().removeItem(item);
        } else {
            item.setAmount(item.getAmount() - 1);
        }
    }

    private void executeAction(Player player, CustomItemTemplate.ItemUseAction action) {
        Map<String, Object> params = action.parameters();
        switch (action.type()) {
            case "potion" -> executePotion(player, params);
            case "particle" -> executeParticle(player, params);
            case "sound" -> executeSound(player, params);
            case "message" -> executeMessage(player, params);
            case "broadcast" -> executeBroadcast(params);
            case "title" -> executeTitle(player, params);
            case "actionbar" -> executeActionBar(player, params);
            case "execute_command" -> executeCommand(player, params);
            case "start_event_chain" -> executeStartEventChain(player, params);
            default -> plugin.getLogger().warning("Unknown item use action type: " + action.type());
        }
    }

    private void executePotion(Player player, Map<String, Object> params) {
        PotionEffectType type = ConfigParseUtil.parsePotionEffectType(getString(params, "potion_type"));
        if (type == null) {
            return;
        }
        int duration = ConfigParseUtil.toInt(params.get("duration"), 200);
        int amplifier = ConfigParseUtil.toInt(params.get("amplifier"), 0);
        boolean particles = ConfigParseUtil.toBoolean(params.get("show_particles"), true);
        boolean icon = ConfigParseUtil.toBoolean(params.get("show_icon"), true);
        player.addPotionEffect(new PotionEffect(type, Math.max(1, duration), Math.max(0, amplifier), false, particles, icon));
    }

    private void executeParticle(Player player, Map<String, Object> params) {
        Particle particle = ConfigParseUtil.parseParticle(getString(params, "particle"));
        if (particle == null) {
            return;
        }
        Location loc = player.getLocation().clone().add(0, 1, 0);
        int count = ConfigParseUtil.toInt(params.get("count"), 10);
        double offsetX = ConfigParseUtil.toDouble(params.get("offset_x"), 0.5);
        double offsetY = ConfigParseUtil.toDouble(params.get("offset_y"), 0.5);
        double offsetZ = ConfigParseUtil.toDouble(params.get("offset_z"), 0.5);
        player.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ);
    }

    private void executeSound(Player player, Map<String, Object> params) {
        Sound sound = ConfigParseUtil.parseSound(getString(params, "sound"));
        if (sound == null) {
            return;
        }
        float volume = params.get("volume") instanceof Number n ? n.floatValue() : 1.0f;
        float pitch = params.get("pitch") instanceof Number n ? n.floatValue() : 1.0f;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    private void executeMessage(Player player, Map<String, Object> params) {
        String message = getString(params, "message");
        if (message == null || message.isBlank()) {
            return;
        }
        player.sendMessage(MessageUtil.color(message));
    }

    private void executeBroadcast(Map<String, Object> params) {
        String message = getString(params, "message");
        if (message == null || message.isBlank()) {
            return;
        }
        Component component = MessageUtil.prefix(message);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage(component);
        }
        Bukkit.getConsoleSender().sendMessage(component);
    }

    private void executeTitle(Player player, Map<String, Object> params) {
        String title = getString(params, "title");
        String subtitle = getString(params, "subtitle");
        if (title == null && subtitle == null) {
            return;
        }
        int fadeIn = ConfigParseUtil.toInt(params.get("fade_in"), 10);
        int stay = ConfigParseUtil.toInt(params.get("stay"), 70);
        int fadeOut = ConfigParseUtil.toInt(params.get("fade_out"), 10);
        Title.Times times = Title.Times.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L));
        player.showTitle(Title.title(
                MessageUtil.color(title == null ? "" : title),
                MessageUtil.color(subtitle == null ? "" : subtitle),
                times));
    }

    private void executeActionBar(Player player, Map<String, Object> params) {
        String message = getString(params, "message");
        if (message == null || message.isBlank()) {
            return;
        }
        player.sendActionBar(MessageUtil.color(message));
    }

    private void executeCommand(Player player, Map<String, Object> params) {
        String command = getString(params, "command");
        if (command == null || command.isBlank()) {
            return;
        }
        command = command.replace("{player}", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.startsWith("/") ? command.substring(1) : command);
    }

    private void executeStartEventChain(Player player, Map<String, Object> params) {
        String eventId = getString(params, "event_id");
        if (eventId == null || eventId.isBlank()) {
            return;
        }
        plugin.getEventChainManager().startEvent(eventId, player);
    }

    private static String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value == null ? null : value.toString();
    }
}
