package com.lucab.let_me_drink.attachment;

import java.util.function.Supplier;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class DrinkAttachments implements INBTSerializable<CompoundTag> {
    public static final int DRINK_TICK_DURATION_PER_LEVEL = 2400;

    private int drink_level = 0;

    public int setDrinkLevel(int drink_level) {
        this.drink_level = Math.clamp(drink_level, 0, 10);
        return this.getDrinkLevel();
    }

    public int addDrinkLevel(int value) {
        this.drink_level = Math.min(this.drink_level + value, 10);
        return this.getDrinkLevel();
    }

    public int addDrinkLevel() {
        this.addDrinkLevel(1);
        return this.getDrinkLevel();
    }

    public int removeDrinkLevel(int value) {
        this.drink_level = Math.max(this.drink_level - value, 0);
        return this.getDrinkLevel();
    }

    public int removeDrinkLevel() {
        this.removeDrinkLevel(1);
        return this.getDrinkLevel();
    }

    public int getDrinkLevel() {
        return this.drink_level;
    }

    @Override
    public CompoundTag serializeNBT(Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DrinkLevel", this.drink_level);
        return tag;
    }

    @Override
    public void deserializeNBT(Provider provider, CompoundTag tag) {
        this.drink_level = tag.getInt("DrinkLevel");
    }

    public static final Supplier<AttachmentType<DrinkAttachments>> DRINK_ATTACHMENTS = ModAttachments.ATTACHMENT_TYPES
            .register("drink", () -> AttachmentType.serializable(DrinkAttachments::new).build());

    public static void register() {
    }
}
