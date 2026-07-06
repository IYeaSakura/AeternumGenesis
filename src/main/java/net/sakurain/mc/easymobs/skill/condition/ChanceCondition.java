package net.sakurain.mc.easymobs.skill.condition;

import net.sakurain.mc.easymobs.skill.SkillContext;

import java.util.concurrent.ThreadLocalRandom;

public class ChanceCondition extends AbstractSkillCondition {

    public ChanceCondition() {
        super("chance");
    }

    @Override
    public boolean test(SkillContext context) {
        double chance = number("value", 50.0);
        return ThreadLocalRandom.current().nextDouble() * 100.0 < chance;
    }
}
