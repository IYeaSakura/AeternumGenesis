package net.sakurain.mc.easymobs.api;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public interface MobAPI {

    boolean isCustomMob(@Nullable Entity entity);

    boolean isCustomMob(@Nullable Entity entity, @NotNull String templateId);

    @NotNull
    Optional<String> getMobTemplateId(@Nullable Entity entity);

    @NotNull
    Optional<LivingEntity> spawnMob(@NotNull String templateId, @NotNull Location location);

    @NotNull
    Optional<LivingEntity> spawnMob(@NotNull String templateId, @NotNull Location location, int level);

    @NotNull
    Collection<LivingEntity> getAllActiveMobs();

    @NotNull
    Collection<LivingEntity> getActiveMobs(@NotNull String templateId);

    @NotNull
    Collection<LivingEntity> getActiveMobs(@NotNull Predicate<LivingEntity> filter);

    @NotNull
    Optional<LivingEntity> getActiveMob(@NotNull UUID uuid);

    @NotNull
    Collection<LivingEntity> getNearbyMobs(@NotNull Location location, double radius);

    @NotNull
    Collection<LivingEntity> getNearbyMobs(@NotNull Location location, double radius, @NotNull String templateId);

    @NotNull
    Optional<String> getTemplateDisplayName(@NotNull String templateId);

    @NotNull
    Optional<Double> getTemplateMaxHealth(@NotNull String templateId);

    @NotNull
    Collection<String> getAllTemplateIds();

    int countNearbyMobs(@NotNull Location location, double radius, @NotNull String templateId);

    int countMobsInChunk(@NotNull Chunk chunk);

    void removeCustomTag(@NotNull LivingEntity entity);

    boolean applyTemplate(@NotNull LivingEntity entity, @NotNull String templateId);

    int getMobLevel(@NotNull LivingEntity entity);

    void setMobLevel(@NotNull LivingEntity entity, int level);
}
