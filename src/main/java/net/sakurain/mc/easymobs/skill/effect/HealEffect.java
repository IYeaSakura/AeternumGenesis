package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class HealEffect extends AbstractSkillEffect {

    public HealEffect() {
        super("heal");
    }

    @Override
    public void execute(SkillContext context) {
        double amount = number("amount", 1.0);
        LivingEntity target = singleTarget(context);
        if (target == null || target.isDead()) {
            return;
        }
        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH) != null
                ? target.getAttribute(Attribute.MAX_HEALTH).getValue()
                : target.getHealth();
        target.setHealth(Math.min(maxHealth, target.getHealth() + amount));
    }
}
