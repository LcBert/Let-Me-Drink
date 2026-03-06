package com.lucab.let_me_drink.effect;

import com.lucab.let_me_drink.LetMeDrink;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister
            .create(Registries.MOB_EFFECT, LetMeDrink.MODID);

    public static void register(IEventBus bus) {
        MOB_EFFECTS.register(bus);
        DrunkEffect.register();
    }
}
