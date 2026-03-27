package com.lucab.let_me_drink.world.inventory;

import com.lucab.let_me_drink.LetMeDrink;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MenuRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU,
            LetMeDrink.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<FermenterMenu>> FERMENTER_MENU = MENUS
            .register("fermenter_menu", () -> IMenuTypeExtension.create(FermenterMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}