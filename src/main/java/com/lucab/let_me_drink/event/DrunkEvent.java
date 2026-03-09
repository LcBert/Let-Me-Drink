package com.lucab.let_me_drink.event;

import com.lucab.let_me_drink.LetMeDrink;
import com.lucab.let_me_drink.attachment.DrunkAttachments;
import com.lucab.let_me_drink.effect.DrunkEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LetMeDrink.MODID)
public class DrunkEvent {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
        int drinkLevel = drinkData.getDrunkLevel();

        if (drinkLevel >= 3) {
            addEffect(player, MobEffects.CONFUSION);
        }

        if (isInRange(drinkLevel, 1, 2)) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 0);
            drinkData.setFaintChance(0.1f);
        } else if (isInRange(drinkLevel, 3, 4)) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 1);
            drinkData.setFaintChance(0.3f);
        } else if (isInRange(drinkLevel, 5, 6)) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 2);
            drinkData.setFaintChance(0.5f);
        } else if (isInRange(drinkLevel, 7, 8)) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 3);
            drinkData.setFaintChance(0.7f);
        } else if (isInRange(drinkLevel, 9, 10)) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 4);
            drinkData.setFaintChance(0.9f);
        }

        if (player.tickCount % DrunkAttachments.DRUNK_TICK_DURATION_PER_LEVEL == 0) {
            drinkData.removeDrunkLevel();
        }
    }

    public static boolean faintPlayer(Player player) {
        DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
        float faintChance = drinkData.getFaintChance();
        if (faintChance > 0.0f && Math.random() < faintChance) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
            drinkData.setDrunkLevel(0);
            drinkData.setFaintChance(0.0f);
        }
        return false;
    }

    private static void addEffect(Player player, Holder<MobEffect> effect) {
        addEffect(player, effect, 0);
    }

    private static void addEffect(Player player, Holder<MobEffect> effect, int amplifier) {
        if (!player.hasEffect(effect) || player.getEffect(effect).getDuration() <= 100)
            player.addEffect(new MobEffectInstance(effect, 200, amplifier));
    }

    private static boolean isInRange(int val, int min, int max) {
        return (val >= min && val <= max);
    }
}
