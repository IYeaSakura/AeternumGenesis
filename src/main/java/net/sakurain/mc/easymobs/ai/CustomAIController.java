package net.sakurain.mc.easymobs.ai;

import com.destroystokyo.paper.entity.ai.Goal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.mob.CustomMobTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.*;

public class CustomAIController {

    private static CustomAIController instance;

    private final EasyMobsPlugin plugin;
    private final Map<UUID, AggroTable> aggroTables = new HashMap<>();
    private final Map<UUID, Long> switchCooldowns = new HashMap<>();

    private CustomAIController(@NotNull EasyMobsPlugin plugin) {
        this.plugin = plugin;
    }

    public static synchronized CustomAIController getInstance() {
        if (instance == null) {
            instance = new CustomAIController(EasyMobsPlugin.getInstance());
        }
        return instance;
    }

    public void setupAI(@NotNull Mob mob, @NotNull CustomMobTemplate template) {
        CustomMobTemplate.MobAIConfig ai = template.getAi();
        if (ai == null) return;

        if (ai.isRemoveDefaultGoals()) {
            Bukkit.getMobGoals().removeAllGoals(mob);
        }

        Bukkit.getMobGoals().addGoal(mob, 1, new SmartTargetGoal(mob, template));
        Bukkit.getMobGoals().addGoal(mob, 2, new SmartAttackGoal(mob, template));

        if (ai.getBehavior() != null && ai.getBehavior().isCircleTarget()) {
            Bukkit.getMobGoals().addGoal(mob, 3, new CircleTargetGoal(mob, template));
        }

        CustomMobTemplate.BreakDoorConfig bd = template.getBreakDoor();
        if (bd != null && bd.isEnabled()) {
            Bukkit.getMobGoals().addGoal(mob, 4, new BreakDoorGoal(mob, template));
        }

        CustomMobTemplate.TargetingStrategy strategy = ai.getTargetingStrategy();
        int memory = strategy != null ? strategy.getMaxTargetsMemory() : 5;
        aggroTables.put(mob.getUniqueId(), new AggroTable(memory));
        switchCooldowns.put(mob.getUniqueId(), 0L);
    }

    public void tick(@NotNull LivingEntity entity, @NotNull CustomMobTemplate template) {
        if (!(entity instanceof Mob mob)) return;
        CustomMobTemplate.MobAIConfig ai = template.getAi();
        if (ai == null || !ai.isUseCustomAI()) return;

        AggroTable aggro = aggroTables.get(mob.getUniqueId());
        if (aggro != null) aggro.decay(0.95);

        evaluateTargetSwitch(mob, template);
        checkLeashRange(mob, template);
    }

    public void recordDamage(@NotNull Mob mob, @NotNull LivingEntity attacker, double damage) {
        AggroTable aggro = aggroTables.get(mob.getUniqueId());
        if (aggro != null) aggro.addThreat(attacker.getUniqueId(), damage);
    }

    private void evaluateTargetSwitch(@NotNull Mob mob, @NotNull CustomMobTemplate template) {
        CustomMobTemplate.MobAIConfig ai = template.getAi();
        CustomMobTemplate.TargetingStrategy strategy = ai != null ? ai.getTargetingStrategy() : null;
        if (strategy == null) return;

        long currentTick = Bukkit.getCurrentTick();
        long lastSwitch = switchCooldowns.getOrDefault(mob.getUniqueId(), 0L);
        if (currentTick - lastSwitch < strategy.getSwitchInterval()) return;

        LivingEntity bestTarget = findBestTarget(mob, template);
        LivingEntity currentTarget = mob.getTarget();

        if (bestTarget == null) {
            if (currentTarget != null) mob.setTarget(null);
            return;
        }

        if (currentTarget == null || !currentTarget.equals(bestTarget)) {
            double currentScore = scoreTarget(mob, currentTarget, strategy, template);
            double bestScore = scoreTarget(mob, bestTarget, strategy, template);
            if (bestScore >= currentScore * (1 + strategy.getSwitchThreshold())) {
                mob.setTarget(bestTarget);
                switchCooldowns.put(mob.getUniqueId(), currentTick);
            }
        }
    }

    private LivingEntity findBestTarget(@NotNull Mob mob, @NotNull CustomMobTemplate template) {
        CustomMobTemplate.MobAIConfig ai = template.getAi();
        CustomMobTemplate.TargetingStrategy strategy = ai != null ? ai.getTargetingStrategy() : null;
        if (strategy == null) return null;

        CustomMobTemplate.SenseConfig senses = template.getSenses();
        double range = ai.getTargetRange();
        Location loc = mob.getLocation();
        List<LivingEntity> candidates = new ArrayList<>();

        for (Entity e : mob.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (!(e instanceof LivingEntity target)) continue;
            if (target.equals(mob)) continue;
            if (target.isDead()) continue;
            if (strategy.isPreferPlayers() && !(target instanceof Player)) continue;
            if (senses != null && !SenseSystem.canSense(mob, target, senses)) continue;
            candidates.add(target);
        }
        if (candidates.isEmpty()) return null;

        return switch (strategy.getType()) {
            case NEAREST -> candidates.stream()
                    .min(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(loc))).orElse(null);
            case LOWEST_HP -> candidates.stream()
                    .min(Comparator.comparingDouble(LivingEntity::getHealth)).orElse(null);
            case HIGHEST_THREAT -> {
                AggroTable aggro = aggroTables.get(mob.getUniqueId());
                yield candidates.stream()
                        .max(Comparator.comparingDouble(e -> aggro != null ? aggro.getThreat(e.getUniqueId()) : 0))
                        .orElse(null);
            }
            case RANDOM -> candidates.get(new Random().nextInt(candidates.size()));
            case FIRST_SIGHT -> candidates.get(0);
        };
    }

    private double scoreTarget(@NotNull Mob mob, @Nullable LivingEntity target,
                               @NotNull CustomMobTemplate.TargetingStrategy strategy,
                               @NotNull CustomMobTemplate template) {
        if (target == null || target.isDead()) return 0;
        double score;
        double dist = mob.getLocation().distance(target.getLocation());
        switch (strategy.getType()) {
            case NEAREST -> score = 1.0 / (dist + 1);
            case LOWEST_HP -> score = 1.0 / (target.getHealth() + 1);
            case HIGHEST_THREAT -> {
                AggroTable aggro = aggroTables.get(mob.getUniqueId());
                score = aggro != null ? aggro.getThreat(target.getUniqueId()) : 0;
            }
            case RANDOM -> score = Math.random();
            case FIRST_SIGHT -> score = 1.0;
            default -> score = 0;
        }
        double range = template.getAi().getTargetRange();
        if (dist > range * 0.7) score *= 0.5;
        return score;
    }

    private void checkLeashRange(@NotNull Mob mob, @NotNull CustomMobTemplate template) {
        CustomMobTemplate.BehaviorConfig behavior = template.getAi() != null ? template.getAi().getBehavior() : null;
        if (behavior == null) return;
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        double dist = mob.getLocation().distance(target.getLocation());
        if (dist > behavior.getLeashRange()) {
            mob.setTarget(null);
        }
    }

    public void cleanup(@NotNull UUID uuid) {
        aggroTables.remove(uuid);
        switchCooldowns.remove(uuid);
    }

    public static abstract class BaseGoal implements Goal<Mob> {

        protected final Mob mob;
        protected final CustomMobTemplate template;

        protected BaseGoal(@NotNull Mob mob, @NotNull CustomMobTemplate template) {
            this.mob = mob;
            this.template = template;
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }
    }
}
