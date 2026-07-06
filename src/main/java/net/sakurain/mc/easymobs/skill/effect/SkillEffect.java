package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;

import java.util.Map;

public interface SkillEffect {

    String getType();

    void loadParameters(Map<String, Object> parameters);

    void execute(SkillContext context);
}
