package com.lucab.let_me_drink.attachment;

import java.util.function.Supplier;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class DrunkAttachments implements INBTSerializable<CompoundTag> {
    public static final int DRuNK_TICK_DURATION_PER_LEVEL = 2400;

    private int drunk_level = 0;

    public int setDrunkLevel(int drink_level) {
        this.drunk_level = Math.clamp(drink_level, 0, 10);
        return this.getDrunkLevel();
    }

    public int addDrunkLevel(int value) {
        this.drunk_level = Math.min(this.drunk_level + value, 10);
        return this.getDrunkLevel();
    }

    public int addDrunkLevel() {
        this.addDrunkLevel(1);
        return this.getDrunkLevel();
    }

    public int removeDrunkLevel(int value) {
        this.drunk_level = Math.max(this.drunk_level - value, 0);
        return this.getDrunkLevel();
    }

    public int removeDrunkLevel() {
        this.removeDrunkLevel(1);
        return this.getDrunkLevel();
    }

    public int getDrunkLevel() {
        return this.drunk_level;
    }

    @Override
    public CompoundTag serializeNBT(Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DrunkLevel", this.drunk_level);
        return tag;
    }

    @Override
    public void deserializeNBT(Provider provider, CompoundTag tag) {
        this.drunk_level = tag.getInt("DrunkLevel");
    }

    public static final Supplier<AttachmentType<DrunkAttachments>> DRUNK_ATTACHMENTS = ModAttachments.ATTACHMENT_TYPES
            .register("drunk", () -> AttachmentType.serializable(DrunkAttachments::new).build());

    public static void register() {
    }
}
