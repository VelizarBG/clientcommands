package net.earthcomputer.clientcommands.command;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

import static dev.xpple.clientarguments.arguments.CItemStackArgumentType.*;
import static net.earthcomputer.clientcommands.command.ClientCommandHelper.*;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class TooltipCommand {

    private static final Flag<Boolean> FLAG_ADVANCED = Flag.ofFlag("advanced").build();

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        var ctooltip = dispatcher.register(literal("ctooltip")
            .then(literal("held")
                .executes(ctx -> showTooltip(ctx.getSource(), ctx.getSource().getPlayer().getMainHandStack(), "held")))
            .then(literal("stack")
                .then(argument("stack", itemStack(registryAccess))
                    .executes(ctx -> showTooltip(ctx.getSource(), getCItemStackArgument(ctx, "stack").createStack(1, false), "stack")))));
        FLAG_ADVANCED.addToCommand(dispatcher, ctooltip, ctx -> true);
    }

    private static int showTooltip(FabricClientCommandSource source, ItemStack stack, String type) {
        source.sendFeedback(Text.translatable("commands.ctooltip.header." + type));

        TooltipContext context = getFlag(source, FLAG_ADVANCED) ? TooltipContext.ADVANCED : TooltipContext.BASIC;

        List<Text> tooltip = stack.getTooltip(source.getPlayer(), context);
        for (Text line : tooltip) {
            source.sendFeedback(line);
        }

        return tooltip.size();
    }
}
