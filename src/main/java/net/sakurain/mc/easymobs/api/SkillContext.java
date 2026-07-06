package net.sakurain.mc.easymobs.api;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SkillContext {

    private final LivingEntity caster;
    private final LivingEntity target;
    private final Location origin;
    private final double damage;

    public SkillContext(@NotNull LivingEntity caster, @Nullable LivingEntity target, @Nullable Location origin, double damage) {
        this.caster = caster;
        this.target = target;
        this.origin = origin != null ? origin : caster.getLocation();
        this.damage = damage;
    }

    public SkillContext(@NotNull LivingEntity caster, @Nullable LivingEntity target) {
        this(caster, target, null, 0);
    }

    @NotNull
    public LivingEntity getCaster() {
        return caster;
    }

    @Nullable
    public LivingEntity getTarget() {
        return target;
    }

    @NotNull
    public Location getOrigin() {
        return origin;
    }

    public double getDamage() {
        return damage;
    }

    @NotNull
    public List<LivingEntity> getTargets() {
        return target != null ? List.of(caster, target) : List.of(caster);
    }

    @NotNull
    public SkillContext withTarget(@Nullable LivingEntity newTarget) {
        return new SkillContext(caster, newTarget, origin, damage);
    }

    @NotNull
    public SkillContext withTargets(@NotNull List<LivingEntity> targets) {
        return new SkillContext(caster, targets.isEmpty() ? null : targets.get(0), origin, damage);
    }
}
