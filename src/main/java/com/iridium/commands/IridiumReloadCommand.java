package com.iridium.commands;

import com.iridium.Iridium;
import com.iridium.config.IridiumConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class IridiumReloadCommand {

    private static volatile boolean registered = false;

    private IridiumReloadCommand() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(
                        Commands.literal("iridium")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("reload")
                                        .executes(IridiumReloadCommand::executeReload))
                )
        );

        Iridium.LOGGER.info("Comando /iridium reload registrado");
    }

    private static int executeReload(CommandContext<CommandSourceStack> ctx) {
        try {
            IridiumConfig.load();
            ctx.getSource().sendSuccess(
                    () -> Component.translatable("iridium.command.reload.success"),
                    true);
            return Command.SINGLE_SUCCESS;
        } catch (RuntimeException e) {
            Iridium.LOGGER.warn("Falha ao recarregar config via /iridium reload", e);
            ctx.getSource().sendFailure(
                    Component.translatable("iridium.command.reload.failure"));
            return 0;
        }
    }
}
