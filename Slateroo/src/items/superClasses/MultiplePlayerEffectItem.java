package items.superClasses;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Consumer;

import items.manage.ItemEffectMode;
import logic.Snake;

/**
 * This superclass represents items which either have an effect on only the snake which collects the item or every snake
 * existing or every snake except the snake which collected the item.
 * @author Jonas
 * 
 */
public abstract class MultiplePlayerEffectItem extends Item{
	/**
	 * The background color of the item when it has an effect on every snake.
	 */
	private static final Color BG_EVERY = Color.WHITE;
	/**
	 * The background color of the item when it has an effect on every but the snake which collected it.
	 */
	private static final Color BG_EXCEPT = new Color(205,92,92);
	/**
	 * The chance that an item becomes an item that only affects the collector snake in percent
	 */
	public static final int THIS_CHANCE = 50;
	/**
	 * The chance that an item becomes an item that affects every snake
	 */
	public static final int EVERY_CHANCE = 30; // -> EXCEPT_CHANCE = 100 - THIS_CHANCE - EVERY_CHANCE = 20
	/**
	 * The mode, which decides which type of effect this item has
	 */
	protected final ItemEffectMode effectMode;
	/**
	 * Indicates whether this item affects only the collector snake
	 */
	protected final boolean thisMode;	
	/**
	 * Indicates whether this item affects every snake.
	 */
	protected final boolean everyMode;
	/**
	 * The background color of this item. If this item only affects the collector snake, this will be transparent
	 */
	private Color bgColor;
	
	/**
	 * Creates an item object with the specified coordinates, effect mode and icon
	 * @param x The x-coordinate of the middle point of this item
	 * @param y The y-coordinate of the middle point of this item
	 * @param mode The effect mode
	 * @param iconName The name of the image file that contains the look of this item
	 */
	public MultiplePlayerEffectItem(int x, int y, ItemEffectMode mode, String iconName) {
		super(x, y, iconName);
		
		this.effectMode = mode;
		thisMode = mode == ItemEffectMode.THIS;
		everyMode = mode == ItemEffectMode.EVERY;
		
		if(!thisMode)
			initBGColor();
	}
	/**
	 * Every subclass of this class must override this method. This method specifies the behaviour when collecting the item.
	 * @param snake The snake which collected the item
	 */
	protected abstract void effect(Snake snake);

	@Override
	protected void intersectionHandling(Snake snake) {
		executeOnSnakeDependingOnMode(snake, snk -> effect(snk));
	}
	/**
	 * Chooses the background color of this item depending on the effect mode.
	 */
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
		else
			g.drawImage(icon, drawX, drawY, LENGTH, LENGTH, bgColor, null);
	}
	
	/**
	 * Applies the specified operation on either the specified snake or/and the other snakes depending on the effect mode
	 * @param snake The snake which collected the item
	 * @param action The operation which is applied to the snakes
	 */
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
