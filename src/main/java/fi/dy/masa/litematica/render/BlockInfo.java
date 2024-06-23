package fi.dy.masa.litematica.render;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.BlockUtils;
import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.litematica.util.ItemUtils;

public class BlockInfo
{
    private final String title;
    private final BlockState state;
    private final ItemStack stack;
    private final String blockRegistryname;
    private final String stackName;
    private final List<String> props;
    private final int totalWidth;
    private final int totalHeight;

    public BlockInfo(BlockState state, String titleKey)
    {
        String pre = GuiBase.TXT_WHITE + GuiBase.TXT_BOLD;
        this.title = pre + StringUtils.translate(titleKey) + GuiBase.TXT_RST;
        this.state = state;
        this.stack = ItemUtils.getItemForState(this.state);

        Identifier rl = Registries.BLOCK.getId(this.state.getBlock());
        this.blockRegistryname = rl.toString();

        this.stackName = this.stack.getName().getString();

        int w = StringUtils.getStringWidth(this.stackName) + 20;
        w = Math.max(w, StringUtils.getStringWidth(this.blockRegistryname));
        w = Math.max(w, StringUtils.getStringWidth(this.title));
        this.props = BlockUtils.getFormattedBlockStateProperties(this.state, " = ");
        this.totalWidth = w + 40;
        this.totalHeight = this.props.size() * (StringUtils.getFontHeight() + 2) + 60;
    }

    public int getTotalWidth()
    {
        return totalWidth;
    }

    public int getTotalHeight()
    {
        return totalHeight;
    }

    public void render(int x, int y, MinecraftClient mc, DrawContext drawContext)
    {
        if (this.state != null)
        {
            RenderUtils.drawOutlinedBox(x, y, this.totalWidth, this.totalHeight, 0xFF000000, GuiBase.COLOR_HORIZONTAL_BAR);

            TextRenderer textRenderer = mc.textRenderer;
            int x1 = x + 10;
            y += 4;

            drawContext.drawText(textRenderer, this.title, x1, y, 0xFFFFFFFF, false);

            y += 12;

            RenderUtils.enableDiffuseLightingGui3D();

            //mc.getRenderItem().zLevel += 100;
            RenderUtils.drawRect(x1, y, 16, 16, 0x20FFFFFF); // light background for the item
            drawContext.drawItem(this.stack, x1, y);
            drawContext.drawItemInSlot(textRenderer, this.stack, x1, y);
            //mc.getRenderItem().zLevel -= 100;

            //RenderSystem.disableBlend();
            RenderUtils.disableDiffuseLighting();

            drawContext.drawText(textRenderer, this.stackName, x1 + 20, y + 4, 0xFFFFFFFF, false);

            y += 20;
            drawContext.drawText(textRenderer, this.blockRegistryname, x1, y, 0xFF4060FF, false);
            y += textRenderer.fontHeight + 4;

            RenderUtils.renderText(x1, y, 0xFFB0B0B0, this.props, drawContext);
        }
    }
}
