package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DropItemEffect extends AbstractSkillEffect {

    public DropItemEffect() {
        super("drop_item");
    }

    @Override
    public void execute(SkillContext context) {
        String materialName = string("material", "").toUpperCase();
        if (materialName.isEmpty()) {
            return;
        }
        Material material;
        try {
            material = Material.valueOf(materialName);
        } catch (IllegalArgumentException e) {
            return;
        }
        int amount = integer("amount", 1);
        Location location = location(context);
        if (location == null || location.getWorld() == null) {
            return;
        }
        location.getWorld().dropItemNaturally(location, new ItemStack(material, Math.max(1, amount)));
    }
}
