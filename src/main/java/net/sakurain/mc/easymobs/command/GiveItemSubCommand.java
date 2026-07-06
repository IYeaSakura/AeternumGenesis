package net.sakurain.mc.easymobs.command;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.item.CustomItemManager;
import net.sakurain.mc.easymobs.item.CustomItemTemplate;
import net.sakurain.mc.easymobs.item.ItemBuilder;
import net.sakurain.mc.easymobs.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class GiveItemSubCommand implements SubCommand {

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(MessageUtil.error("Usage: /ezmobs give <item-id> [player] [amount]"));
            return true;
        }

        String itemId = args[0];
        CustomItemManager manager = EasyMobsPlugin.getInstance().getItemManager();
        CustomItemTemplate template = manager.getTemplate(itemId);
        if (template == null) {
            sender.sendMessage(MessageUtil.error("Item '&e" + itemId + "&c' does not exist!"));
            return true;
        }

        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(MessageUtil.error("Player '&e" + args[1] + "&c' is not online!"));
                return true;
            }
        } else if (sender instanceof Player) {
            target = (Player) sender;
        } else {
            sender.sendMessage(MessageUtil.error("Console must specify a target player!"));
            return true;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    sender.sendMessage(MessageUtil.error("Amount must be between 1 and 64!"));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(MessageUtil.error("Invalid amount!"));
                return true;
            }
        }

        ItemStack item = ItemBuilder.build(template);
        item.setAmount(amount);
        target.getInventory().addItem(item).values().forEach(drop ->
                target.getWorld().dropItemNaturally(target.getLocation(), drop));

        sender.sendMessage(MessageUtil.success("Gave &e" + target.getName() + " &a" + itemId + " &ax" + amount));
        return true;
    }

    @Override
    public String getPermission() {
        return "easymobs.give";
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        CustomItemManager manager = EasyMobsPlugin.getInstance().getItemManager();
        if (args.length == 1) {
            return manager.getTemplateIds().stream()
                    .filter(id -> id.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
