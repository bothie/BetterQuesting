package betterquesting.api2.client.gui;

import betterquesting.api.storage.BQ_Settings;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.misc.ComparatorGuiDepth;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiCanvas;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.client.BQ_Keybindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiScreenCanvas extends GuiScreen implements IGuiCanvas
{
	private final List<IGuiPanel> guiPanels = new CopyOnWriteArrayList<>();
	private final GuiRectangle transform = new GuiRectangle(0, 0, 0, 0, 0);
	private boolean enabled = true;
	
	public final GuiScreen parent;
	
	public GuiScreenCanvas(GuiScreen parent)
	{
		this.parent = parent;
	}
	
	@Override
	public IGuiRect getTransform()
	{
		return transform;
	}
	
	/**
	 * Use initPanel() for embed support
	 */
	@Override
	public final void initGui()
	{
		super.initGui();
		
		initPanel();
	}
	
	@Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
		
		Keyboard.enableRepeatEvents(false);
    }
	
	@Override
	public void initPanel()
	{
		int marginX = 16;
		int marginY = 16;
		
		if(BQ_Settings.guiWidth > 0)
		{
			marginX = Math.max(16, (this.width - BQ_Settings.guiWidth) / 2);
		}
		
		if(BQ_Settings.guiHeight > 0)
		{
			marginY = Math.max(16, (this.height - BQ_Settings.guiHeight) / 2);
		}
		
		transform.x = marginX;
		transform.y = marginY;
		transform.w = this.width - marginX * 2;
		transform.h = this.height - marginY * 2;
		
		this.guiPanels.clear();
	}
	
	@Override
	public void setEnabled(boolean state)
	{
		// Technically supported if you wanted something like a multiscreen where this isn't actually the root screen
		this.enabled = state;
	}
	
	@Override
	public boolean isEnabled()
	{
		return this.enabled;
	}
	
	/**
	 * Use initPanel() for embed support
	 */
	@Override
	public final void drawScreen(int mx, int my, float partialTick)
	{
		super.drawScreen(mx, my, partialTick);
		
		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.disableDepth();
		
		this.drawPanel(mx, my, partialTick);
		
		List<String> tt = this.getTooltip(mx, my);
		
		if(tt != null && tt.size() > 0)
		{
			this.drawHoveringText(tt, mx, my);
		}
		
		GlStateManager.enableDepth();
		GlStateManager.popMatrix();
	}
	
	/**
	 * Use panel buttons and the event broadcaster
	 */
	@Override
	@Deprecated
	public void actionPerformed(GuiButton button)
	{
	}
	
	// Remembers the last mouse buttons states. Required to fire release events
	private boolean[] mBtnState = new boolean[3];
	
	@Override
	public void handleMouseInput() throws IOException
	{
		super.handleMouseInput();
		
        int i = Mouse.getEventX() * width / mc.displayWidth;
        int j = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        int k = Mouse.getEventButton();
        int SDX = (int)-Math.signum(Mouse.getEventDWheel());
        boolean flag = Mouse.getEventButtonState();
        
        if(k >= 0 && k < 3 && mBtnState[k] != flag)
        {
        	if(flag)
        	{
        		this.onMouseClick(i, j, k);
        	} else
        	{
        		this.onMouseRelease(i, j, k);
        	}
        	mBtnState[k] = flag;
        }
        
        if(SDX != 0)
        {
        	this.onMouseScroll(i, j, SDX);
        }
	}
	
	@Override
    public void keyTyped(char c, int keyCode) throws IOException
    {
        super.keyTyped(c, keyCode);
        
        if(keyCode == 1)
        {
        	return;
        }
        
        this.onKeyTyped(c, keyCode);
    }
	
	@Override
	public void drawPanel(int mx, int my, float partialTick)
	{
		for(IGuiPanel entry : guiPanels)
		{
			if(entry.isEnabled())
			{
				entry.drawPanel(mx, my, partialTick);
			}
		}
	}
	
	@Override
	public boolean onMouseClick(int mx, int my, int click)
	{
		boolean used = false;
		
		ListIterator<IGuiPanel> pnIter = guiPanels.listIterator(guiPanels.size());
		
		while(pnIter.hasPrevious())
		{
			IGuiPanel entry = pnIter.previous();
			
			if(entry.isEnabled() && entry.onMouseClick(mx, my, click))
			{
				used = true;
				break;
			}
		}
		
		return used;
	}
	
	@Override
	public boolean onMouseRelease(int mx, int my, int click)
	{
		boolean used = false;
		
		ListIterator<IGuiPanel> pnIter = guiPanels.listIterator(guiPanels.size());
		
		while(pnIter.hasPrevious())
		{
			IGuiPanel entry = pnIter.previous();
			
			if(entry.isEnabled() && entry.onMouseRelease(mx, my, click))
			{
				used = true;
				break;
			}
		}
		
		return used;
	}
	
	//@Override
	public boolean onMouseScroll(int mx, int my, int scroll)
	{
		boolean used = false;
		
		ListIterator<IGuiPanel> pnIter = guiPanels.listIterator(guiPanels.size());
		
		while(pnIter.hasPrevious())
		{
			IGuiPanel entry = pnIter.previous();
			
			if(entry.isEnabled() && entry.onMouseScroll(mx, my, scroll))
			{
				used = true;
				break;
			}
		}
		
		return used;
	}
	
	@Override
	public boolean onKeyTyped(char c, int keycode)
	{
		boolean used = false;
		
		ListIterator<IGuiPanel> pnIter = guiPanels.listIterator(guiPanels.size());
		
		while(pnIter.hasPrevious())
		{
			IGuiPanel entry = pnIter.previous();
			
			if(entry.isEnabled() && entry.onKeyTyped(c, keycode))
			{
				used = true;
				break;
			}
		}
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if(!used && (BQ_Keybindings.openQuests.isPressed() || mc.gameSettings.keyBindInventory.isPressed()))
		{
			mc.displayGuiScreen(this.parent);
		}
		
		return used;
	}
	
	@Override
	public List<String> getTooltip(int mx, int my)
	{
		ListIterator<IGuiPanel> pnIter = guiPanels.listIterator(guiPanels.size());
		List<String> tt;
		
		while(pnIter.hasPrevious())
		{
			IGuiPanel entry = pnIter.previous();
			
			if(!entry.isEnabled())
			{
				continue;
			}
			
			tt = entry.getTooltip(mx, my);
			
			if(tt != null && tt.size() > 0)
			{
				return tt;
			}
		}
		
		return null;
	}
	
	@Override
	public void addPanel(IGuiPanel panel)
	{
		if(panel == null || guiPanels.contains(panel))
		{
			return;
		}
		
		guiPanels.add(panel);
		guiPanels.sort(ComparatorGuiDepth.INSTANCE);
		panel.getTransform().setParent(getTransform());
		panel.initPanel();
	}
	
	@Override
	public boolean removePanel(IGuiPanel panel)
	{
		return guiPanels.remove(panel);
	}
	
	@Override
	public void resetCanvas()
	{
		guiPanels.clear();
	}
	
	@Override
    public boolean doesGuiPauseGame()
    {
        return false; // Halts packet handling if paused
    }
	
	/**
	 * Should be using PanelButton instead when using a Canvas
	 */
	@Override
	@Deprecated
	public <T extends GuiButton> T addButton(T button)
	{
		return super.addButton(button);
	}
	
	@Override
    protected void renderToolTip(ItemStack stack, int x, int y)
    {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        RenderUtils.drawHoveringText(stack, this.getItemToolTip(stack), x, y, width, height, -1, (font == null ? fontRenderer : font));
    }
	
	@Override
    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
    {
        RenderUtils.drawHoveringText(textLines, x, y, width, height, -1, font);
    }
}
