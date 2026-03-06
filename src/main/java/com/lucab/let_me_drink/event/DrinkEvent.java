package com.lucab.let_me_drink.event;

import com.lucab.let_me_drink.LetMeDrink;
import com.lucab.let_me_drink.attachment.DrinkAttachments;
import com.lucab.let_me_drink.effect.DrunkEffect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = LetMeDrink.MODID)
public class DrinkEvent {
    @SubscribeEvent
    public static void onPlayerDrink(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity().level().isClientSide)
            return;

        ItemStack item = event.getItem();
        if (event.getEntity() instanceof Player player) {
            if (item.getItem() == Items.COOKED_BEEF) {
                DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());
                drinkData.addDrinkLevel();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        DrinkAttachments drinkData = player.getData(DrinkAttachments.DRINK_ATTACHMENTS.get());

        if (drinkData.getDrinkLevel() >= 1) {
            addEffect(player, MobEffects.CONFUSION);
        }

        if (drinkData.getDrinkLevel() >= 3 && drinkData.getDrinkLevel() <= 4) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate());
        } else if (drinkData.getDrinkLevel() >= 5 && drinkData.getDrinkLevel() <= 6) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 1);
        } else if (drinkData.getDrinkLevel() >= 7 && drinkData.getDrinkLevel() <= 8) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 2);
        } else if (drinkData.getDrinkLevel() >= 9 && drinkData.getDrinkLevel() <= 10) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 3);
        }

        if (player.tickCount % DrinkAttachments.DRINK_TICK_DURATION_PER_LEVEL == 0) {
            drinkData.removeDrinkLevel();
        }
    }

    private static void addEffect(Player player, Holder<MobEffect> effect) {
        addEffect(player, effect, 0);
    }

    private static void addEffect(Player player, Holder<MobEffect> effect, int amplifier) {
        if (!player.hasEffect(effect) || player.getEffect(effect).getDuration() <= 100)
            player.addEffect(new MobEffectInstance(effect, 200, amplifier));
    }
}
