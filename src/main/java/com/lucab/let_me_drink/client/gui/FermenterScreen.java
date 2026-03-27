package com.lucab.let_me_drink.client.gui;

import com.lucab.let_me_drink.LetMeDrink;
import com.lucab.let_me_drink.world.inventory.FermenterMenu;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;
import java.util.List;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

public class FermenterScreen extends AbstractContainerScreen<FermenterMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(LetMeDrink.MODID,
            "textures/gui/fermenter.png");

    public FermenterScreen(FermenterMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageHeight = 174;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFluid(guiGraphics, x + 8, y + 22, 16, 50, this.menu.getFluidStack());
    }

    private void renderFluid(GuiGraphics guiGraphics, int x, int y, int width, int height, FluidStack fluidStack) {
        if (fluidStack.isEmpty())
            return;

        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture(fluidStack);
        if (stillTexture == null)
            return;

        TextureAtlasSprite sprite = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);
        int color = fluidTypeExtensions.getTintColor(fluidStack);

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShaderColor(r, g, b, a);

        int fluidHeight = (int) (height * ((float) fluidStack.getAmount() / 8000f));
        int yOffset = height - fluidHeight;

        guiGraphics.blit(x, y + yOffset, 0, width, fluidHeight, sprite);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        if (isHovering(8, 22, 16, 50, mouseX, mouseY)) {
            renderFluidTooltip(guiGraphics, mouseX, mouseY, this.menu.getFluidStack());
        }
    }

    private void renderFluidTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, FluidStack fluidStack) {
        if (fluidStack.isEmpty()) {
            guiGraphics.renderTooltip(this.font, Component.literal("Empty"), mouseX, mouseY);
        } else {
            guiGraphics.renderTooltip(this.font, List.of(
                    fluidStack.getHoverName(),
                    Component.literal(fluidStack.getAmount() + " / 8000 mB")), Optional.empty(), mouseX, mouseY);
        }
    }
}