package net.sakurain.mc.easymobs.skill.effect;

import net.sakurain.mc.easymobs.skill.SkillContext;
import net.sakurain.mc.easymobs.util.MessageUtil;
import org.bukkit.entity.Player;

public class TitleEffect extends AbstractSkillEffect {

    public TitleEffect() {
        super("title");
    }

    @Override
    public void execute(SkillContext context) {
        String title = string("title", "");
        String subtitle = string("subtitle", "");
        int fadeIn = integer("fade_in", 10);
        int stay = integer("stay", 70);
        int fadeOut = integer("fade_out", 20);

        Player player = context.getTargetPlayer();
        if (player == null) {
            player = context.getCasterPlayer();
        }
        if (player == null) {
            return;
        }
        MessageUtil.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }
}
