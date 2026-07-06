package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import org.bukkit.Bukkit;

public class ExecuteCommandEffect extends AbstractSkillEffect {

    public ExecuteCommandEffect() {
        super("execute_command");
    }

    @Override
    public void execute(SkillContext context) {
        String command = string("command", "");
        if (command.isEmpty()) {
            return;
        }
        if (!command.startsWith("/")) {
            command = "/" + command;
        }
        command = replacePlaceholders(command, context);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring(1));
    }

    private String replacePlaceholders(String command, SkillContext context) {
        if (context.getCaster() != null) {
            command = command.replace("<caster>", context.getCaster().getName());
            command = command.replace("<caster_uuid>", context.getCaster().getUniqueId().toString());
        }
        if (context.getTarget() != null) {
            command = command.replace("<target>", context.getTarget().getName());
            command = command.replace("<target_uuid>", context.getTarget().getUniqueId().toString());
        }
        command = command.replace("<x>", String.valueOf(context.getOrigin() != null ? context.getOrigin().getX() : 0));
        command = command.replace("<y>", String.valueOf(context.getOrigin() != null ? context.getOrigin().getY() : 0));
        command = command.replace("<z>", String.valueOf(context.getOrigin() != null ? context.getOrigin().getZ() : 0));
        return command;
    }
}
