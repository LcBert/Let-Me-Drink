package com.lucab.let_me_drink.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.registries.DeferredHolder;

public class DrunkEffect extends MobEffect {
    public static final DeferredHolder<MobEffect, DrunkEffect> DRUNK_EFFECT = ModEffects.MOB_EFFECTS.register("drunk",
            () -> new DrunkEffect(MobEffectCategory.HARMFUL, 0x55FF00));

    public DrunkEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {
            if (entity.onGround()) {
                double x = (entity.getRandom().nextDouble() - 0.5) * (amplifier + 1) * 0.2;
                double z = (entity.getRandom().nextDouble() - 0.5) * (amplifier + 1) * 0.2;
                entity.push(x, 0, z);
                entity.hurtMarked = true;
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    public static void register() {
    }
}
