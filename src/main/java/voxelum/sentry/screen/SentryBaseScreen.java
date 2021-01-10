package voxelum.sentry.screen;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import voxelum.sentry.Sentry;
import voxelum.sentry.container.SentryBaseContainer;

@SideOnly(Side.CLIENT)
public class SentryBaseScreen extends GuiContainer {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Sentry.MODID, "textures/gui/container/sentry_base.png");

    public SentryBaseScreen(SentryBaseContainer screenContainer) {
        super(screenContainer);
        this.ySize = 133;
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }
}
