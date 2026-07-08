package net.sakurain.mc.aeternumgenesis.block;

import net.sakurain.mc.aeternumgenesis.AeternumGenesisPlugin;
import net.sakurain.mc.aeternumgenesis.util.ConfigParseUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Executes on_interact actions configured for custom blocks.
 */
public final class CustomBlockInteractExecutor {

    private CustomBlockInteractExecutor() {
    }

    public static void execute(AeternumGenesisPlugin plugin, CustomBlockTemplate template, Location location, Player player) {
        for (CustomBlockTemplate.InteractAction action : template.getOnInteract()) {
            executeAction(plugin, template, action, location, player);
        }
    }

    private static void executeAction(AeternumGenesisPlugin plugin, CustomBlockTemplate template,
                                      CustomBlockTemplate.InteractAction action, Location location, Player player) {
        Map<String, Object> params = action.parameters();
        switch (action.type()) {
            case "play_sound" -> {
                Sound sound = ConfigParseUtil.parseSound(getString(params, "sound"));
                if (sound != null && location.getWorld() != null) {
                    float volume = getFloat(params, "volume", 1.0f);
                    float pitch = getFloat(params, "pitch", 1.0f);
                    location.getWorld().playSound(location, sound, volume, pitch);
                }
            }
            case "spawn_particle" -> {
                Particle particle = ConfigParseUtil.parseParticle(getString(params, "particle"));
                if (particle != null && location.getWorld() != null) {
                    int count = getInt(params, "count", 10);
                    double offsetX = getDouble(params, "offset_x", 0.5);
                    double offsetY = getDouble(params, "offset_y", 0.5);
                    double offsetZ = getDouble(params, "offset_z", 0.5);
                    location.getWorld().spawnParticle(particle, location.clone().add(0.5, 0.5, 0.5), count, offsetX, offsetY, offsetZ, 0);
                }
            }
            case "execute_command" -> {
                String command = getString(params, "command");
                if (command != null && !command.isBlank()) {
                    command = command.replace("{player}", player.getName())
                            .replace("{block_id}", template.getId());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }
            case "send_message" -> {
                String message = getString(params, "message");
                if (message != null && !message.isBlank()) {
                    player.sendMessage(net.sakurain.mc.aeternumgenesis.util.MessageUtil.color(message));
                }
            }
            default -> plugin.getLogger().warning("Unknown custom block interact action: " + action.type());
        }
    }

    private static String getString(Map<String, Object> params, String key) {
        Object value = params.get(key);
        return value == null ? null : value.toString();
    }

    private static int getInt(Map<String, Object> params, String key, int def) {
        Object value = params.get(key);
        return ConfigParseUtil.toInt(value, def);
    }

    private static double getDouble(Map<String, Object> params, String key, double def) {
        Object value = params.get(key);
        return ConfigParseUtil.toDouble(value, def);
    }

    private static float getFloat(Map<String, Object> params, String key, float def) {
        Object value = params.get(key);
        return value instanceof Number n ? n.floatValue() : def;
    }
}
