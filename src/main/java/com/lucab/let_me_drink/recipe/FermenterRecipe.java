package com.lucab.let_me_drink.recipe;

import com.lucab.let_me_drink.LetMeDrink;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

public class FermenterRecipe implements Recipe<FermenterRecipeInput> {
    private final List<SizedIngredient> ingredients;
    private final Ingredient container;
    private final FluidStack fluid;
    private final ItemStack result;
    private final int processTime;

    public FermenterRecipe(List<SizedIngredient> ingredients, Ingredient container, FluidStack fluid, ItemStack result,
            int processTime) {
        this.ingredients = ingredients;
        this.container = container;
        this.fluid = fluid;
        this.result = result;
        this.processTime = processTime;
    }

    @Override
    public boolean matches(FermenterRecipeInput input, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        // Check fluid
        if (!input.fluid().is(fluid.getFluid()) || input.fluid().getAmount() < fluid.getAmount()) {
            return false;
        }

        // Check container
        if (!container.test(input.container())) {
            return false;
        }

        // Check SizedIngredients
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>(input.inputs());

        for (SizedIngredient required : ingredients) {
            boolean found = false;
            for (int i = 0; i < inputs.size(); i++) {
                if (required.test(inputs.get(i))) {
                    found = true;
                    // Note: We don't remove count from input list here because inputs are usually
                    // distinct slots
                    // But if multiple ingredients can match same slot...
                    // For now, let's assume we consume the whole slot for match or at least check
                    // availability.
                    // Actually, for shapeless matching where one slot satisfies multiple
                    // ingredients? No.
                    // Each ingredient uses distinct items.
                    // Since SizedIngredient creates a requirement greater than 1, we must ensure
                    // the slot has enough.

                    // Simple logic: One slot satisfies one ingredient requirement.
                    // And that slot must have count >= required.count.
                    // Then we remove that slot from consideration.
                    inputs.remove(i);
                    break;
                }
            }
            if (!found)
                return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(FermenterRecipeInput input, HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public List<SizedIngredient> getSizedIngredients() {
        return ingredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        for (SizedIngredient sizedIngredient : ingredients) {
            list.add(sizedIngredient.ingredient());
        }
        return list;
    }

    public Ingredient getContainer() {
        return container;
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public int getProcessTime() {
        return processTime;
    }

    public static class Type implements RecipeType<FermenterRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "fermenting";
    }

    public static class Serializer implements RecipeSerializer<FermenterRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LetMeDrink.MODID, "fermenting");

        public static final MapCodec<FermenterRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                SizedIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(FermenterRecipe::getSizedIngredients),
                Ingredient.CODEC_NONEMPTY.fieldOf("container").forGetter(FermenterRecipe::getContainer),
                FluidStack.CODEC.fieldOf("fluid").forGetter(FermenterRecipe::getFluid),
                ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
                Codec.INT.fieldOf("processTime").forGetter(FermenterRecipe::getProcessTime))
                .apply(instance, FermenterRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, FermenterRecipe> STREAM_CODEC = StreamCodec.composite(
                SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()),
                FermenterRecipe::getSizedIngredients,
                Ingredient.CONTENTS_STREAM_CODEC,
                FermenterRecipe::getContainer,
                FluidStack.STREAM_CODEC,
                FermenterRecipe::getFluid,
                ItemStack.STREAM_CODEC,
                r -> r.result,
                ByteBufCodecs.INT,
                FermenterRecipe::getProcessTime,
                FermenterRecipe::new);

        @Override
        public MapCodec<FermenterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FermenterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
