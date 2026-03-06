package com.lucab.let_me_drink.command;

import com.lucab.let_me_drink.attachment.DrinkAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class DrinkCommand {
    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("drink")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 10))
                                        .executes(DrinkCommand::setDrink)))

                        .then(Commands.literal("add")
                                .executes(DrinkCommand::addDrink)
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(DrinkCommand::addDrink)))

                        .then(Commands.literal("remove")
                                .executes(DrinkCommand::removeDrink)
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(DrinkCommand::removeDrink)))

                        .then(Commands.literal("get")
                                .executes(DrinkCommand::getDrink))));
    }

    private static int setDrink(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());
            int value = IntegerArgumentType.getInteger(context, "value");

            drinkData.setDrinkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Set %s drink data to %d", player.getDisplayName().getString(), value)), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int addDrink(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());
            int tempValue;
            try {
                tempValue = IntegerArgumentType.getInteger(context, "value");
            } catch (IllegalArgumentException e) {
                tempValue = 1;
            }
            final int value = tempValue;

            drinkData.addDrinkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Added %d drink value to %s", value, player.getDisplayName().getString())), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int removeDrink(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());
            int tempValue;
            try {
                tempValue = IntegerArgumentType.getInteger(context, "value");
            } catch (IllegalArgumentException e) {
                tempValue = 1;
            }
            final int value = tempValue;

            drinkData.removeDrinkLevel(value);

            source.sendSuccess(() -> Component.literal(
                    String.format("Removed %d drink value to %s", value, player.getDisplayName().getString())), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private static int getDrink(CommandContext<CommandSourceStack> context) {
        try {
            CommandSourceStack source = context.getSource();
            Player player = EntityArgument.getPlayer(context, "player");
            DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());
            int value = drinkData.getDrinkLevel();

            source.sendSuccess(() -> Component.literal(
                    String.format("%s has %d drink value", player.getDisplayName().getString(), value)), true);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

}
