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
public class DrinkEvent {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        DrunkAttachments drinkData = player.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());

        if (drinkData.getDrunkLevel() >= 1) {
            addEffect(player, MobEffects.CONFUSION);
        }

        if (drinkData.getDrunkLevel() >= 3 && drinkData.getDrunkLevel() <= 4) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate());
        } else if (drinkData.getDrunkLevel() >= 5 && drinkData.getDrunkLevel() <= 6) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 1);
        } else if (drinkData.getDrunkLevel() >= 7 && drinkData.getDrunkLevel() <= 8) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 2);
        } else if (drinkData.getDrunkLevel() >= 9 && drinkData.getDrunkLevel() <= 10) {
            addEffect(player, DrunkEffect.DRUNK_EFFECT.getDelegate(), 3);
        }

        if (player.tickCount % DrunkAttachments.DRuNK_TICK_DURATION_PER_LEVEL == 0) {
            drinkData.removeDrunkLevel();
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
