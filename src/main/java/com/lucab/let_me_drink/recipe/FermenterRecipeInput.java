package com.lucab.let_me_drink.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

public record FermenterRecipeInput(List<ItemStack> inputs, ItemStack container, FluidStack fluid)
        implements RecipeInput {
    @Override
    public ItemStack getItem(int index) {
        if (index < 0)
            return ItemStack.EMPTY;
        if (index < inputs.size())
            return inputs.get(index);
        if (index == inputs.size())
            return container;
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return inputs.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return inputs.isEmpty() && container.isEmpty() && fluid.isEmpty();
    }
}
