package com.lucab.let_me_drink.world.inventory;

import com.lucab.let_me_drink.block.BlocksRegistry;
import com.lucab.let_me_drink.block.fermenter.FermenterEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;

public class FermenterMenu extends AbstractContainerMenu {
    public final FermenterEntity blockEntity;
    private final Level level;

    private final ContainerData data;

    public FermenterMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(2));
    }

    public FermenterMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(MenuRegistry.FERMENTER_MENU.get(), pContainerId);
        checkContainerDataCount(data, 2);
        blockEntity = (FermenterEntity) entity;
        this.level = inv.player.level();
        this.data = data;
        ContainerLevelAccess.create(level, entity.getBlockPos());
        ItemStackHandler inventory = blockEntity.getInventory();

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);

        // Input slots (0-3)
        // 2x2 grid starting at 26, 17
        this.addSlot(new SlotItemHandler(inventory, 0, 44, 30));
        this.addSlot(new SlotItemHandler(inventory, 1, 62, 30));
        this.addSlot(new SlotItemHandler(inventory, 2, 44, 48));
        this.addSlot(new SlotItemHandler(inventory, 3, 62, 48));

        // Container slot (4)
        // Middle bottom? Assuming fuel or catalyst. 80, 53 is typical
        this.addSlot(new SlotItemHandler(inventory, 4, 84, 38));

        // Output slot (5)
        // Right side. 116, 35 is typical
        this.addSlot(new SlotItemHandler(inventory, 5, 133, 38) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        // Sync fluid amount
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return blockEntity.getFluidTank().getFluidAmount();
            }

            @Override
            public void set(int value) {
                blockEntity.getFluidTank()
                        .setFluid(new FluidStack(blockEntity.getFluidTank().getFluid().getFluid(), value));
            }
        });

        // Sync fluid type
        this.addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return BuiltInRegistries.FLUID.getId(blockEntity.getFluidTank().getFluid().getFluid());
            }

            @Override
            public void set(int value) {
                Fluid fluid = BuiltInRegistries.FLUID.byId(value);
                blockEntity.getFluidTank().setFluid(new FluidStack(fluid, blockEntity.getFluidTank().getFluidAmount()));
            }
        });
    }

    public FluidStack getFluidStack() {
        return blockEntity.getFluidTank().getFluid();
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 6; // 4 input + 1 container + 1 output

    public int getProcess() {
        return this.data.get(0);
    }

    public int getMaxProcess() {
        return this.data.get(1);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        Slot sourceSlot = slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem())
            return ItemStack.EMPTY; // EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY; // EMPTY_ITEM
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        // If stack size == 0, the stack was moved completely.
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, BlocksRegistry.FERMENTER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 92 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 150));
        }
    }
}