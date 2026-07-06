package net.sakurain.mc.easymobs.listener;

import net.sakurain.mc.easymobs.EasyMobsPlugin;
import net.sakurain.mc.easymobs.api.event.CustomMobDamageModifyEvent;
import net.sakurain.mc.easymobs.api.event.CustomMobDeathEvent;
import net.sakurain.mc.easymobs.api.event.CustomMobDropEvent;
import net.sakurain.mc.easymobs.mob.CustomMobTemplate;
import net.sakurain.mc.easymobs.mob.MobTracker;
import net.sakurain.mc.easymobs.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;

public class EntityEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;

        if (event.getDamager() instanceof LivingEntity damager && MobTracker.getInstance().isCustomMob(damager)) {
            CustomMobDamageModifyEvent modifyEvent = new CustomMobDamageModifyEvent(victim, damager, event.getDamage(), true);
            Bukkit.getPluginManager().callEvent(modifyEvent);
            if (modifyEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setDamage(modifyEvent.getDamage());
            triggerSkills(damager, victim, "ON_HIT", event.getFinalDamage());
        }

        if (MobTracker.getInstance().isCustomMob(victim)) {
            LivingEntity damager = event.getDamager() instanceof LivingEntity living ? living : null;
            CustomMobDamageModifyEvent modifyEvent = new CustomMobDamageModifyEvent(victim, damager, event.getDamage(), false);
            Bukkit.getPluginManager().callEvent(modifyEvent);
            if (modifyEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setDamage(modifyEvent.getDamage());
            triggerSkills(victim, damager, "ON_HURT", event.getFinalDamage());
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        if (MobTracker.getInstance().isCustomMob(victim)) {
            if (victim.getHealth() - event.getFinalDamage() <= 0) {
                // Death will be handled by EntityDeathEvent
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!MobTracker.getInstance().isCustomMob(entity)) return;

        String templateId = MobTracker.getInstance().getMobTemplateId(entity);
        CustomMobTemplate template = MobTracker.getInstance().getTemplate(entity).orElse(null);
        if (template == null) return;

        Player killer = entity.getKiller();
        Bukkit.getPluginManager().callEvent(new CustomMobDeathEvent(templateId, entity, killer));

        if (template.getDropTable() != null) {
            CustomMobTemplate.DropTable dt = template.getDropTable();
            CustomMobDropEvent dropEvent = new CustomMobDropEvent(templateId, entity, event.getDrops(), event.getDroppedExp());
            Bukkit.getPluginManager().callEvent(dropEvent);
            if (!dropEvent.isCancelled()) {
                event.getDrops().clear();
                event.getDrops().addAll(dropEvent.getDrops());
                event.setDroppedExp(dropEvent.getExperience());
            }
        }

        MobTracker.getInstance().cancelTracking(entity.getUniqueId());
        net.sakurain.mc.easymobs.ai.CustomAIController.getInstance().cleanup(entity.getUniqueId());

        if (killer != null) {
            triggerSkills(entity, killer, "ON_DEATH", 0);
            if (MobTracker.getInstance().isCustomMob(killer)) {
                triggerSkills(killer, entity, "ON_KILL", 0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof LivingEntity caster)) return;
        if (!MobTracker.getInstance().isCustomMob(caster)) return;
        LivingEntity target = event.getTarget() instanceof LivingEntity living ? living : null;
        triggerSkills(caster, target, "ON_TARGET", 0);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityTeleport(EntityTeleportEvent event) {
        if (!(event.getEntity() instanceof LivingEntity caster)) return;
        if (!MobTracker.getInstance().isCustomMob(caster)) return;
        triggerSkills(caster, null, "ON_TELEPORT", 0);
    }

    private void triggerSkills(LivingEntity caster, LivingEntity target, String trigger, double damage) {
        SkillManager manager = EasyMobsPlugin.getInstance().getSkillManager();
        manager.handleTrigger(trigger, caster, target, damage);
    }
}
