package net.sakurain.mc.easymobs.spawn.condition;

import net.sakurain.mc.easymobs.spawn.SpawnCondition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class ThunderingCondition implements SpawnCondition {

    private boolean expected = true;

    @Override
    public String getType() {
        return "thundering";
    }

    @Override
    public void parse(String arguments) {
        this.expected = parseBoolean(arguments);
    }

    @Override
    public boolean test(Location location, EntityType originalType) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }
        return world.isThundering() == expected;
    }

    private boolean parseBoolean(String value) {
        return value == null || value.isBlank() || Boolean.parseBoolean(value.trim());
    }
}
