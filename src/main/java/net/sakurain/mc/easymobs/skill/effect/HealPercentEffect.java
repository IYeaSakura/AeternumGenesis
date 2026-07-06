package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

public class HealPercentEffect extends AbstractSkillEffect {

    public HealPercentEffect() {
        super("heal_percent");
    }

    @Override
    public void execute(SkillContext context) {
        double percent = number("percent", 10.0);
        LivingEntity target = singleTarget(context);
        if (target == null || target.isDead()) {
            return;
        }
        double maxHealth = target.getAttribute(Attribute.MAX_HEALTH) != null
                ? target.getAttribute(Attribute.MAX_HEALTH).getValue()
                : target.getHealth();
        target.setHealth(Math.min(maxHealth, target.getHealth() + maxHealth * (percent / 100.0)));
    }
}
