package net.sakurain.mc.easymobs.api;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface SkillEffect {

    @NotNull
    String getType();

    void loadParameters(@NotNull Map<String, Object> params);

    void execute(@NotNull SkillContext context);
}
