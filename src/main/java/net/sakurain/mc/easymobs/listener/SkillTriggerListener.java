package net.sakurain.mc.easymobs.listener;

import net.sakurain.mc.easymobs.api.event.CustomMobSkillTriggerEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SkillTriggerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSkillTrigger(CustomMobSkillTriggerEvent event) {
        // The actual execution is handled inside SkillManager; this listener is a hook point
        // for other plugins to cancel or observe skill triggers.
    }
}
