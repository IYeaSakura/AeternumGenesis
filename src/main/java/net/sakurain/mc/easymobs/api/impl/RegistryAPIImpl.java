package net.sakurain.mc.easymobs.api.impl;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.api.RegistryAPI;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RegistryAPIImpl implements RegistryAPI {

    private final EasyMobsPlugin plugin;

    public RegistryAPIImpl(@NotNull EasyMobsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void registerEffect(@NotNull String type, @NotNull EffectFactory factory) {
        plugin.getSkillManager().registerEffect(type, () -> new SkillAPIImpl.SkillEffectAdapter(factory.create()));
    }

    @Override
    public void registerCondition(@NotNull String type, @NotNull ConditionFactory factory) {
        plugin.getSkillManager().registerCondition(type, () -> new SkillAPIImpl.SkillConditionAdapter(factory.create()));
    }

    @Override
    public void registerSpawnCondition(@NotNull String type, @NotNull SpawnConditionFactory factory) {
        plugin.getSpawnManager().registerCondition(type, factory::create);
    }

    @Override
    public void unregisterEffect(@NotNull String type) {
        plugin.getSkillManager().unregisterEffect(type);
    }

    @Override
    public void unregisterCondition(@NotNull String type) {
        plugin.getSkillManager().unregisterCondition(type);
    }

    @Override
    public void unregisterSpawnCondition(@NotNull String type) {
        plugin.getSpawnManager().unregisterCondition(type);
    }

    @Override
    @NotNull
    public Collection<String> getRegisteredEffectTypes() {
        return plugin.getSkillManager().getRegisteredEffectTypes();
    }

    @Override
    @NotNull
    public Collection<String> getRegisteredConditionTypes() {
        return plugin.getSkillManager().getRegisteredConditionTypes();
    }

    @Override
    @NotNull
    public Collection<String> getRegisteredSpawnConditionTypes() {
        return plugin.getSpawnManager().getRegisteredConditionTypes();
    }
}
