package com.lucab.let_me_drink.block;

import com.lucab.let_me_drink.LetMeDrink;
import com.lucab.let_me_drink.block.fermenter.Fermenter;
import com.lucab.let_me_drink.block.fermenter.FermenterEntity;
import com.lucab.let_me_drink.item.ItemsRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlocksRegistry {
    public static DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(LetMeDrink.MODID);
    public static DeferredRegister<BlockEntityType<?>> BLOCKS_ENTITIES = DeferredRegister.create(
            Registries.BLOCK_ENTITY_TYPE, LetMeDrink.MODID);

    // Fermenter
    public static DeferredBlock<Block> FERMENTER = BLOCKS.register("fermenter", () -> new Fermenter());

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermenterEntity>> FERMENTER_ENTITY = BLOCKS_ENTITIES
            .register("fermenter", () -> BlockEntityType.Builder.of(FermenterEntity::new, FERMENTER.get()).build(null));

    public static DeferredItem<BlockItem> FERMENTER_ITEM = ItemsRegistry.ITEMS.registerSimpleBlockItem(
            "fermenter", FERMENTER);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCKS_ENTITIES.register(eventBus);
    }
}
