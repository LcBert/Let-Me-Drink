package com.lucab.let_me_drink;

import org.slf4j.Logger;

import com.lucab.let_me_drink.attachment.ModAttachments;
import com.lucab.let_me_drink.block.BlocksRegistry;
import com.lucab.let_me_drink.command.DrunkCommand;
import com.lucab.let_me_drink.effect.ModEffects;
import com.lucab.let_me_drink.item.ItemsRegistry;
import com.lucab.let_me_drink.recipe.RecipesRegistry;
import com.lucab.let_me_drink.world.inventory.MenuRegistry;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;

@Mod(LetMeDrink.MODID)
public class LetMeDrink {
    public static final String MODID = "let_me_drink";
    public static final Logger LOGGER = LogUtils.getLogger();

    public LetMeDrink(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerCapabilities);
        BlocksRegistry.register(modEventBus);
        MenuRegistry.register(modEventBus);
        RecipesRegistry.register(modEventBus);

        ItemsRegistry.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModEffects.register(modEventBus);
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                BlocksRegistry.FERMENTER_ENTITY.get(),
                (be, side) -> be.getFluidTank());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        DrunkCommand.register(event.getDispatcher());
    }
}
