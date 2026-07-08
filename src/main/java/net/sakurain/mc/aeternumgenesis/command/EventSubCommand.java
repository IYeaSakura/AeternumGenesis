package net.sakurain.mc.aeternumgenesis.command;

import net.sakurain.mc.aeternumgenesis.AeternumGenesisPlugin;
import net.sakurain.mc.aeternumgenesis.eventchain.EventChainInstance;
import net.sakurain.mc.aeternumgenesis.eventchain.EventChainTemplate;
import net.sakurain.mc.aeternumgenesis.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventSubCommand implements SubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        AeternumGenesisPlugin plugin = AeternumGenesisPlugin.getInstance();
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("Usage: /genesis event <start|stop|list|templates|info> [args]"));
            return true;
        }
        String action = args[0].toLowerCase();
        switch (action) {
            case "start" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /genesis event start <id>"));
                    return true;
                }
                String id = args[1];
                Player initiator = sender instanceof Player player ? player : null;
                UUID instanceId = plugin.getEventChainManager().startEvent(id, initiator);
                if (instanceId == null) {
                    sender.sendMessage(MessageUtil.error("Unknown event chain: " + id));
                } else {
                    sender.sendMessage(MessageUtil.success("Started event chain &e" + id
                            + " &a(instance: &e" + instanceId + "&a)"));
                }
            }
            case "stop" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /genesis event stop <instance-id>"));
                    return true;
                }
                try {
                    UUID instanceId = UUID.fromString(args[1]);
                    if (plugin.getEventChainManager().stopEvent(instanceId)) {
                        sender.sendMessage(MessageUtil.success("Stopped event chain instance."));
                    } else {
                        sender.sendMessage(MessageUtil.error("Event chain instance not found."));
                    }
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(MessageUtil.error("Invalid UUID: " + args[1]));
                }
            }
            case "stopall" -> {
                plugin.getEventChainManager().stopAllEvents();
                sender.sendMessage(MessageUtil.success("Stopped all active event chains."));
            }
            case "list" -> {
                sender.sendMessage(MessageUtil.success("Active event chains:"));
                for (EventChainInstance instance : plugin.getEventChainManager().getActiveInstances()) {
                    sender.sendMessage(MessageUtil.color("  - &e" + instance.getTemplate().id()
                            + " &7(&f" + instance.getId() + "&7) stage "
                            + instance.getCurrentStageIndex()));
                }
                if (plugin.getEventChainManager().getActiveCount() == 0) {
                    sender.sendMessage(MessageUtil.color("  &7None"));
                }
            }
            case "templates" -> {
                sender.sendMessage(MessageUtil.success("Loaded event chain templates:"));
                for (EventChainTemplate template : plugin.getEventChainManager().getTemplates()) {
                    sender.sendMessage(MessageUtil.color("  - &e" + template.id()));
                }
            }
            case "info" -> {
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.error("Usage: /genesis event info <id>"));
                    return true;
                }
                EventChainTemplate template = plugin.getEventChainManager().getTemplate(args[1]);
                if (template == null) {
                    sender.sendMessage(MessageUtil.error("Unknown event chain: " + args[1]));
                    return true;
                }
                sender.sendMessage(MessageUtil.success("Event chain: &e" + template.id()));
                sender.sendMessage(MessageUtil.color("  &7Trigger: &f" + template.trigger().type()));
                sender.sendMessage(MessageUtil.color("  &7Stages: &f" + template.stages().size()));
                sender.sendMessage(MessageUtil.color("  &7Has end config: &f" + (template.onEnd() != null)));
            }
            default -> sender.sendMessage(MessageUtil.error("Unknown action: " + action));
        }
        return true;
    }

    @Override
    public String getPermission() {
        return "genesis.admin";
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("start", "stop", "stopall", "list", "templates", "info");
        }
        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "start", "info" -> new ArrayList<>(
                        AeternumGenesisPlugin.getInstance().getEventChainManager().getTemplates()
                                .stream().map(EventChainTemplate::id).toList());
                default -> List.of();
            };
        }
        return List.of();
    }
}
