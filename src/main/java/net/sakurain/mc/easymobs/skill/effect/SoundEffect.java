package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class SoundEffect extends AbstractSkillEffect {

    public SoundEffect() {
        super("sound");
    }

    @Override
    public void execute(SkillContext context) {
        String soundName = string("sound", "").toUpperCase();
        if (soundName.isEmpty()) {
            return;
        }
        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            return;
        }
        Location location = location(context);
        if (location == null || location.getWorld() == null) {
            return;
        }
        float volume = (float) number("volume", 1.0);
        float pitch = (float) number("pitch", 1.0);
        SoundCategory category = SoundCategory.MASTER;
        try {
            category = SoundCategory.valueOf(string("category", "MASTER").toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }
        location.getWorld().playSound(location, sound, category, volume, pitch);
    }
}
