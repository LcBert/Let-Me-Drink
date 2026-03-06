package com.lucab.let_me_drink.command;

import com.lucab.let_me_drink.attachment.DrunkAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class DrunkCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("drunk")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 10))
                                        .executes(DrunkCommand::set)))

                        .then(Commands.literal("add")
                                .executes(DrunkCommand::add)
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(DrunkCommand::add)))

                        .then(Commands.literal("remove")
                                .executes(DrunkCommand::remove)
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(DrunkCommand::remove)))

                        .then(Commands.literal("get")
                                .executes(DrunkCommand::get))));
    }

    private static int set(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
            int value = IntegerArgumentType.getInteger(context, "value");

            drinkData.setDrunkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Set %s drunk data to %d", player.getDisplayName().getString(), value)), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int add(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
            int tempValue;
            try {
                tempValue = IntegerArgumentType.getInteger(context, "value");
            } catch (IllegalArgumentException e) {
                tempValue = 1;
            }
            final int value = tempValue;

            drinkData.addDrunkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Added %d drunk value to %s", value, player.getDisplayName().getString())), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
            int tempValue;
            try {
                tempValue = IntegerArgumentType.getInteger(context, "value");
            } catch (IllegalArgumentException e) {
                tempValue = 1;
            }
            final int value = tempValue;

            drinkData.removeDrunkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Removed %d drunk value to %s", value, player.getDisplayName().getString())), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int get(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
            int value = drinkData.getDrunkLevel();

            source.sendSuccess(() -> Component.literal(
                    String.format("%s has %d drunk value", player.getDisplayName().getString(), value)), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}
