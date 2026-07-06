package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;

/**
 * Delay is handled by {@link net.sakurain.mc.easymobs.skill.SkillExecutor}.
 * This class exists so the effect type can be registered.
 */
public class DelayEffect extends AbstractSkillEffect {

    public DelayEffect() {
        super("delay");
    }

    @Override
    public void execute(SkillContext context) {
        // No-op; scheduling is handled by the executor.
    }
}
