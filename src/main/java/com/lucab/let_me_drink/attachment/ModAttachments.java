package com.lucab.let_me_drink.attachment;

import com.lucab.let_me_drink.LetMeDrink;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, LetMeDrink.MODID);

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
        DrinkAttachments.register();
    }
}
