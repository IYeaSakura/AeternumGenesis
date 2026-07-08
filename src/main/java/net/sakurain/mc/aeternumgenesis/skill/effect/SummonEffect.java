package net.sakurain.mc.aeternumgenesis.skill.effect;

import net.sakurain.mc.aeternumgenesis.AeternumGenesisPlugin;
import net.sakurain.mc.aeternumgenesis.mob.CustomMobTemplate;
import net.sakurain.mc.aeternumgenesis.mob.MobSpawner;
import net.sakurain.mc.aeternumgenesis.skill.SkillContext;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class SummonEffect extends AbstractSkillEffect {

    public SummonEffect() {
        super("summon");
    }

    @Override
    public void execute(SkillContext context) {
        String mobId = string("mob", "").toLowerCase();
        String entityTypeName = string("entity_type", "").toLowerCase();
        int amount = Math.max(0, Math.min(integer("amount", 1), 100));
        if (amount <= 0) {
            return;
        }
        Location location = location(context);
        if (location == null || location.getWorld() == null) {
            return;
        }

        if (!mobId.isEmpty()) {
            CustomMobTemplate template = AeternumGenesisPlugin.getInstance().getMobManager().getTemplate(mobId);
            if (template == null) {
                return;
            }
            for (int i = 0; i < amount; i++) {
                LivingEntity spawned = MobSpawner.spawn(template, location);
                if (spawned instanceof org.bukkit.entity.Mob mob && context.getTarget() != null) {
                    mob.setTarget(context.getTarget());
                }
            }
            return;
        }

        if (entityTypeName.isEmpty()) {
            return;
        }
        EntityType entityType = Registry.ENTITY_TYPE.get(NamespacedKey.minecraft(entityTypeName));
        if (entityType == null) {
            return;
        }
        for (int i = 0; i < amount; i++) {
            org.bukkit.entity.Entity spawned = location.getWorld().spawnEntity(location, entityType);
            if (spawned instanceof LivingEntity living && context.getCaster() != null) {
                if (living instanceof org.bukkit.entity.Mob mob && context.getTarget() != null) {
                    mob.setTarget(context.getTarget());
                }
            }
        }
    }
}
