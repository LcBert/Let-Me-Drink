package com.lucab.let_me_drink;

import org.slf4j.Logger;

import com.lucab.let_me_drink.attachment.ModAttachments;
import com.lucab.let_me_drink.command.DrunkCommand;
import com.lucab.let_me_drink.effect.ModEffects;
import com.lucab.let_me_drink.item.ItemsRegistry;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(LetMeDrink.MODID)
public class LetMeDrink {
    public static final String MODID = "let_me_drink";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LetMeDrink(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        ItemsRegistry.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModEffects.register(modEventBus);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DrunkCommand.register(event.getDispatcher());
    }
}
