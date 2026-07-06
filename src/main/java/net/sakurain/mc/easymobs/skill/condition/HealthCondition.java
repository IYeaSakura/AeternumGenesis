package net.sakurain.mc.easymobs.skill.condition;

import net.sakurain.mc.easymobs.skill.SkillContext;

public class HealthCondition extends AbstractSkillCondition {

    public HealthCondition() {
        super("health");
    }

    @Override
    public boolean test(SkillContext context) {
        if (context.getCaster() == null) return false;
        return compare(context.getCaster().getHealth(), string("operator", "=="), number("value", 1.0));
    }
}
