package items.superClasses;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Consumer;

import items.manage.ItemEffectMode;
import logic.Snake;

public abstract class MultiplePlayerEffectItem extends Item{
	private static final Color BG_EVERY = Color.WHITE; //new Color(255,250,205)
	private static final Color BG_EXCEPT = new Color(205,92,92);
	
	public static final int THIS_CHANCE = 50; // in percent
	public static final int EVERY_CHANCE = 30;
	
	protected final ItemEffectMode effectMode;
	protected final boolean thisMode, everyMode;
	
	private Color bgColor;
	
	public MultiplePlayerEffectItem(int x, int y, ItemEffectMode mode, String iconName) {
		super(x, y, iconName);
		
		this.effectMode = mode;
		thisMode = mode == ItemEffectMode.THIS;
		everyMode = mode == ItemEffectMode.EVERY;
		
		if(!thisMode)
			initBGColor();
	}
	
	protected abstract void effect(Snake snake);

	@Override
	protected void intersectionHandling(Snake snake) {
		executeOnSnakeDependingOnMode(snake, snk -> effect(snk));
	}
	
	private void initBGColor() {
		if(everyMode)
			bgColor = BG_EVERY;
		else
			bgColor = BG_EXCEPT;
	}
	
	@Override
	public void render(Graphics g) {
		if(thisMode)
			super.render(g);
		else {
			if(!effectActivated)
				g.drawImage(icon, drawX, drawY, LENGTH, LENGTH, bgColor, null);
		}
	}
	
	protected void executeOnSnakeDependingOnMode(Snake snake, Consumer<? super Snake> action) {
		if(thisMode)
			action.accept(snake);
		else if(everyMode)
			snakeManager.getSnakes().forEach(action);
		else
			snakeManager.getSnakesExceptThisSnake(snake).forEach(action);
	}
	
	public ItemEffectMode getEffectMode() {
		return effectMode;
	}

}
