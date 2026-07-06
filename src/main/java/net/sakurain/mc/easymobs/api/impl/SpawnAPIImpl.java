package net.sakurain.mc.easymobs.api.impl;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.api.SpawnAPI;
import net.sakurain.mc.easymobs.mob.CustomMobTemplate;
import net.sakurain.mc.easymobs.mob.MobSpawner;
import net.sakurain.mc.easymobs.spawn.SpawnRule;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class SpawnAPIImpl implements SpawnAPI {

    private final EasyMobsPlugin plugin;

    public SpawnAPIImpl(@NotNull EasyMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public Collection<String> getAllRuleIds() {
        return plugin.getSpawnManager().getRules().stream().map(SpawnRule::getId).toList();
    }

    @Override
    public boolean hasRule(@NotNull String ruleId) {
        return plugin.getSpawnManager().getRules().stream().anyMatch(r -> r.getId().equals(ruleId));
    }

    @Override
    @NotNull
    public Optional<LivingEntity> triggerRule(@NotNull String ruleId, @NotNull Location location) {
        SpawnRule rule = plugin.getSpawnManager().getRules().stream()
                .filter(r -> r.getId().equals(ruleId))
                .findFirst().orElse(null);
        if (rule == null) {
            return Optional.empty();
        }
        CustomMobTemplate template = plugin.getMobManager().getTemplate(rule.getType());
        if (template == null) {
            return Optional.empty();
        }
        LivingEntity entity = MobSpawner.spawn(template, location);
        if (entity == null) {
            return Optional.empty();
        }
        int level = rule.getLevel() > 0 ? rule.getLevel() : rule.getRandomLevel();
        if (level > 1) {
            net.sakurain.mc.easymobs.mob.LevelSystem.applyLevel(entity, level, template);
        }
        return Optional.of(entity);
    }

    @Override
    public boolean canSpawn(@NotNull String mobTemplateId, @NotNull Location location) {
        return plugin.getMobManager().hasTemplate(mobTemplateId);
    }

    @Override
    public int countActiveSpawnRules() {
        return plugin.getSpawnManager().getRuleCount();
    }
}
