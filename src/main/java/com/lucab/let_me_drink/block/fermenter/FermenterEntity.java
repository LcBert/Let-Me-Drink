package com.lucab.let_me_drink.block.fermenter;

import com.lucab.let_me_drink.block.BlocksRegistry;
import com.lucab.let_me_drink.recipe.FermenterRecipe;
import com.lucab.let_me_drink.recipe.FermenterRecipeInput;
import com.lucab.let_me_drink.recipe.RecipesRegistry;
import com.lucab.let_me_drink.recipe.SizedIngredient;
import com.lucab.let_me_drink.world.inventory.FermenterMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FermenterEntity extends BlockEntity implements MenuProvider {
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    public FermenterEntity(BlockPos pos, BlockState blockState) {
        super(BlocksRegistry.FERMENTER_ENTITY.get(), pos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FermenterEntity.this.progress;
                    case 1 -> FermenterEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> FermenterEntity.this.progress = pValue;
                    case 1 -> FermenterEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    private final FluidTank fluidTank = new FluidTank(8000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }
    };

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    private final ItemStackHandler inventory = new ItemStackHandler(6) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    };

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.let_me_drink.fermenter");
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FermenterMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.put("Tank", fluidTank.writeToNBT(registries, new CompoundTag()));
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory"))
            inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        if (tag.contains("Tank"))
            fluidTank.readFromNBT(registries, tag.getCompound("Tank"));
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide()) {
            return;
        }

        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(level, pos, state);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        this.progress = 0;
        this.maxProgress = 0;
    }

    private void increaseCraftingProgress() {
        this.progress++;
    }

    private boolean hasProgressFinished() {
        return this.progress >= this.maxProgress;
    }

    private boolean hasRecipe() {
        Optional<RecipeHolder<FermenterRecipe>> recipe = getCurrentRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        this.maxProgress = recipe.get().value().getProcessTime();

        ItemStack resultItem = recipe.get().value().getResultItem(level.registryAccess());
        ItemStack outputSlotItem = this.inventory.getStackInSlot(5);

        return outputSlotItem.isEmpty() ||
                (outputSlotItem.getItem() == resultItem.getItem() &&
                        outputSlotItem.getCount() + resultItem.getCount() <= outputSlotItem.getMaxStackSize());
    }

    private Optional<RecipeHolder<FermenterRecipe>> getCurrentRecipe() {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            inputs.add(this.inventory.getStackInSlot(i));
        }

        FermenterRecipeInput input = new FermenterRecipeInput(
                inputs,
                this.inventory.getStackInSlot(4),
                this.fluidTank.getFluid());

        return this.level.getRecipeManager().getRecipeFor(RecipesRegistry.FERMENTING_TYPE.get(), input, level);
    }

    private void craftItem() {
        Optional<RecipeHolder<FermenterRecipe>> recipeOptional = getCurrentRecipe();
        if (recipeOptional.isPresent()) {
            FermenterRecipe recipe = recipeOptional.get().value();
            ItemStack result = recipe.getResultItem(level.registryAccess());
            ItemStack outputSlotItem = this.inventory.getStackInSlot(5);

            // Output Handling
            if (outputSlotItem.isEmpty()) {
                this.inventory.setStackInSlot(5, result.copy());
            } else {
                outputSlotItem.grow(result.getCount());
            }

            // Fluid Handling
            this.fluidTank.drain(recipe.getFluid().getAmount(),
                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);

            // Container Handling
            this.inventory.extractItem(4, 1, false);

            // Input Handling (Simple consumption logic as defined in recipe match, assuming
            // 1 item per ingredient matched)
            // But since Recipe logic is "shapeless match", we need to consume items that
            // matched.
            // A simple implementation: try to match again and remove items found

            // For now, let's assume we just iterate and remove first matching like the
            // recipe verify does.
            // Or better: Iterate recipe ingredients and find match in inventory to remove.
            List<ItemStack> currentInputs = new ArrayList<>();
            // We need to operate on actual inventory slots now
            // But to avoid complex logic here which might differ from recipe match, we
            // could rely on recipe.match() side effects if we had passed mutable container.

            // Re-run matching logic to find which slots to decrement
            // Since we know it matches, we satisfy each ingredient.

            // Note: Since we have 4 input slots, checks 0-3
            for (SizedIngredient ingredient : recipe.getSizedIngredients()) {
                for (int i = 0; i < 4; i++) {
                    ItemStack slotItem = inventory.getStackInSlot(i);
                    if (!slotItem.isEmpty() && ingredient.test(slotItem)) {
                        inventory.extractItem(i, ingredient.count(), false);
                        break; // Move to next ingredient
                    }
                }
            }

            setChanged();
        }
    }

    @Override
    public CompoundTag getUpdateTag(Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag, lookupProvider);
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        loadAdditional(tag, lookupProvider);
    }
}
