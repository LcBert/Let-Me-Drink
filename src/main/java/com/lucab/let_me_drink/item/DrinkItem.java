package com.lucab.let_me_drink.item;

import java.util.List;

import com.lucab.let_me_drink.attachment.DrunkAttachments;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DrinkItem extends Item {
    public DrinkItem() {
        super(new Properties().food(new FoodProperties.Builder()
                .nutrition(0)
                .saturationModifier(0)
                .alwaysEdible()
                .build()));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity.level().isClientSide)
            return stack;

        DrunkAttachments drinkData = livingEntity.getData(DrunkAttachments.DRUNK_ATTACHMENTS.get());
        drinkData.addDrunkLevel();
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {

        tooltipComponents.add(Component.translatable("item.tooltip.let_me_drink.beer")
                .withStyle(style -> style.withColor(ChatFormatting.AQUA).withBold(true)));
    }
}
