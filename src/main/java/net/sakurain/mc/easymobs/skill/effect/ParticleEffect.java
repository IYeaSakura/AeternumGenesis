package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleEffect extends AbstractSkillEffect {

    public ParticleEffect() {
        super("particle");
    }

    @Override
    public void execute(SkillContext context) {
        String particleName = string("particle", "").toUpperCase();
        if (particleName.isEmpty()) {
            return;
        }
        Particle particle = parseParticle(particleName);
        if (particle == null) {
            return;
        }

        Location location = location(context);
        if (location == null || location.getWorld() == null) {
            return;
        }

        int count = integer("count", 1);
        double offsetX = number("offset_x", 0.0);
        double offsetY = number("offset_y", 0.0);
        double offsetZ = number("offset_z", 0.0);
        double speed = number("speed", 0.0);

        Object data = null;
        if (particle == Particle.DUST || particle.name().equals("REDSTONE")) {
            Color color = parseColor(string("dust_color", "FFFFFF"));
            float size = (float) number("dust_size", 1.0);
            data = new Particle.DustOptions(color, size);
        }

        location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, data);
    }

    private Particle parseParticle(String name) {
        if (name.equals("REDSTONE")) {
            try {
                return Particle.valueOf("DUST");
            } catch (IllegalArgumentException ignored) {
                // fall through to REDSTONE
            }
        }
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Color parseColor(String hex) {
        try {
            if (hex.startsWith("#")) {
                hex = hex.substring(1);
            }
            int rgb = Integer.parseInt(hex, 16);
            return Color.fromRGB(rgb);
        } catch (Exception e) {
            return Color.RED;
        }
    }
}
