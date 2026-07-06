package net.sakurain.mc.easymobs.skill.condition;

import net.sakurain.mc.easymobs.skill.SkillContext;

public class TargetHealthCondition extends AbstractSkillCondition {

    public TargetHealthCondition() {
        super("target_health");
    }

    @Override
    public boolean test(SkillContext context) {
        if (context.getTarget() == null) return false;
        return compare(context.getTarget().getHealth(), string("operator", "=="), number("value", 1.0));
    }
}
