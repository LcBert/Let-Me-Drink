package com.lucab.let_me_drink;

import com.lucab.let_me_drink.block.BlocksRegistry;
import com.lucab.let_me_drink.item.ItemsRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
            .create(Registries.CREATIVE_MODE_TAB, LetMeDrink.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> LET_ME_DRINK_TAB = CREATIVE_MODE_TABS
            .register("let_me_drink_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.let_me_drink"))
                    .icon(() -> new ItemStack(ItemsRegistry.BEER.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(BlocksRegistry.FERMENTER_ITEM.get());
                        output.accept(ItemsRegistry.TANKARD.get());
                        output.accept(ItemsRegistry.BEER.get());
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
