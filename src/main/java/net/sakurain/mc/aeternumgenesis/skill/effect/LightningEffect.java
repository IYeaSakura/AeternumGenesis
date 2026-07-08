package net.sakurain.mc.aeternumgenesis.skill.effect;

import net.sakurain.mc.aeternumgenesis.skill.SkillContext;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class LightningEffect extends AbstractSkillEffect {

    public LightningEffect() {
        super("lightning");
    }

    @Override
    public void execute(SkillContext context) {
        Location location = location(context);
        if (location == null || location.getWorld() == null) {
            return;
        }
        boolean damage = bool("damage", true);
        double fixedDamage = number("damage_amount", -1.0);
        if (fixedDamage > 0) {
            location.getWorld().strikeLightningEffect(location);
            for (LivingEntity entity : context.resolveTargets(target("TARGET"), 3.0)) {
                entity.damage(fixedDamage, context.getCaster());
            }
            return;
        }
        if (damage) {
            location.getWorld().strikeLightning(location);
        } else {
            location.getWorld().strikeLightningEffect(location);
        }
    }
}
