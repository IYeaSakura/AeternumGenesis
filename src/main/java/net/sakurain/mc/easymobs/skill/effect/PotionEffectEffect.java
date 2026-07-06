package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectEffect extends AbstractSkillEffect {

    public PotionEffectEffect() {
        super("potion");
    }

    @Override
    public void execute(SkillContext context) {
        String typeName = string("type", "").toUpperCase();
        double durationSeconds = number("duration", 5.0);
        int level = integer("level", 1);
        boolean ambient = bool("ambient", false);
        boolean particles = bool("particles", true);
        boolean icon = bool("icon", true);

        PotionEffectType effectType = PotionEffectType.getByName(typeName);
        if (effectType == null) {
            return;
        }

        LivingEntity target = singleTarget(context);
        if (target == null) {
            return;
        }

        int ticks = (int) (durationSeconds * 20.0);
        int amplifier = Math.max(0, level - 1);
        target.addPotionEffect(new PotionEffect(effectType, ticks, amplifier, ambient, particles, icon));
    }
}
