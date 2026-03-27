package com.lucab.let_me_drink.recipe;

import com.lucab.let_me_drink.LetMeDrink;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipesRegistry {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, LetMeDrink.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE,
            LetMeDrink.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FermenterRecipe>> FERMENTING_SERIALIZER = SERIALIZERS
            .register("fermenting", () -> FermenterRecipe.Serializer.INSTANCE);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FermenterRecipe>> FERMENTING_TYPE = TYPES
            .register("fermenting", () -> FermenterRecipe.Type.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
