package items.manage;

import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import items.superClasses.Item;
import items.superClasses.MultiplePlayerEffectItem;
import logic.Arena;
import logic.SnakeManager;

/**
 * This class is responsible for spawning items in the game in certain time intervalls.
 * You need to create an {@code ItemSpawner} object for every {@link Item} in the game.
 * @author jonas.bachmann
 *
 */
public class ItemSpawner {
	/**
	 * The x-range in which an item can spawn.
	 */
	private static final int SPAWN_ROOM_X = Arena.WIDTH - Item.LENGTH;
	/**
	 * The y-range in which an item can spawn.
	 */
	private static final int SPAWN_ROOM_Y = Arena.HEIGHT - Item.LENGTH;
	
	/**
	 * Indicates whether the spawners should spawn items. At the end of the game this will be set to false
	 */
	private static boolean spawning = true;
	/**
	 * The average time it takes to spawn an item object.
	 */
	private double avgSpawnTime;
	/**
	 * The maximum deviation time from the average spawn time.
	 */
	private double maxSpawnDiff;
	/**
	 * Indicates whether this item is an {@link MultiplePlayerEffectItem}
	 */
	private boolean multiplePlayerEffect;
	/**
	 * To generate random numbers
	 */
	private ItemManager itemManager;
	private SnakeManager snakeManager;
	
	private final Random rand = new Random();
	/**
	 * To automize the spawning
	 */
	private final Timer timer;
	/**
	 * The constructor of the item class which this spawner should spawn for reflective instanciation
	 */
	private Constructor<?> itemConstructor;
	
	/**
	 * Creates an item spawner for the specified {@link Item} class
	 * @param itemClass The class of the item that should be spawned
	 */
	public ItemSpawner(ItemManager itemManager, SnakeManager snakeManager, Class<?> itemClass) {
		
		if(!Item.class.isAssignableFrom(itemClass))
			throw new RuntimeException("Itemspawner: " + itemClass.getSimpleName() + " is not a subclass of Item");
		
		this.itemManager = itemManager;
		this.snakeManager = snakeManager;
		initItemAttributes(itemClass);
		timer = new Timer(itemClass.getSimpleName() + "-ItemSpawner");
		
		startTimerForNextSpawn();
	}
	
	private void initItemAttributes(Class<?> itemClass) {
		itemConstructor = itemClass.getConstructors()[0]; // get the only constructor
		
		avgSpawnTime = -1; // to see if the variable is found
		maxSpawnDiff = -1;
		Field[] fields = itemClass.getDeclaredFields(); // get all fields
		for(Field field : fields) {
			try {
				if(field.getName().equals("AVG_SPAWN_TIME")) // attribute must have the name AVG_SPAWN_TIME
					avgSpawnTime = field.getDouble(null);
				else if(field.getName().equals("MAX_SPAWN_DIFF")) // attribute must have the name MAX_SPAWN_DIFF
					maxSpawnDiff = field.getDouble(null);
			} catch(IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Getting the values of a static spawn attributes in Item Class " + itemClass.getSimpleName() + " failed : " + e);
			}
		}
		if(avgSpawnTime < 0) // if not found
			throw new IllegalStateException("Make sure that attribute AVG_SPAWN_TIME in Class " + itemClass.getSimpleName() + " exists and is correctly spelled");
		if(maxSpawnDiff < 0)
			throw new IllegalStateException("Make sure that attribute MAX_SPAWN_DIFF in Class " + itemClass.getSimpleName() + " exists and is correctly spelled");

		multiplePlayerEffect = MultiplePlayerEffectItem.class.isAssignableFrom(itemClass);
	}
	/**
	 * Starts a thread, which spawns an item after a generated time
	 */
	private void startTimerForNextSpawn() {
		if(spawning) {
			long spawnTimeMillis = (long) (generateSpawnTime() * 1000);
			TimerTask task = new TimerTask() {
				public void run() {
					spawnItem();
				}
			};
			timer.schedule(task, spawnTimeMillis);
		}
	}
	/**
	 * Generates random properties such as position and also effect mode if it is an {@link MultiplePlayerEffectItem}
	 * and adds it to the game. Then starts a new spawn process. 
	 */
	private void spawnItem() {
		Item item = createItemInstance(generateItemProperties());
		itemManager.addItem(item);
		startTimerForNextSpawn();
	}
	/**
	 * Generates position and and also effect mode if it is an {@link MultiplePlayerEffectItem}
	 * @return the parameters for the item constructor
	 */
	private Object[] generateItemProperties() {
		Point coords;
		do {
			coords = generateCoordinates();
		} while(itemManager.isItemIntersectingExistingItem(coords)
				|| snakeManager.isItemIntersectingSnakeTile(coords));
		
		Object[] constructorArgs;
		if(multiplePlayerEffect) {
			ItemEffectMode mode;
			int modeDecider = rand.nextInt(100); // for percentage
			if(modeDecider < MultiplePlayerEffectItem.THIS_CHANCE) // "thisChance" percent probability
				mode = ItemEffectMode.THIS;
			else if(modeDecider < MultiplePlayerEffectItem.THIS_CHANCE + MultiplePlayerEffectItem.EVERY_CHANCE) // "everyChance" percent probability
				mode = ItemEffectMode.EVERY;
			else
				mode = ItemEffectMode.EXCEPT;
			
			constructorArgs = new Object[] {(int) coords.getX(), (int) coords.getY(), mode, snakeManager};
		}
		else
			constructorArgs = new Object[] {(int) coords.getX(), (int) coords.getY(), snakeManager};
		
		return constructorArgs;
	}
	/**
	 * Generates a reflective instance of the item class.
	 * @param constructorArgs
	 * @return
	 */
	private Item createItemInstance(Object[] constructorArgs) {
		try {
			return (Item) itemConstructor.newInstance(constructorArgs);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Something went wrong with creating a reflective instance of an Itemobject in ItemSpawner for Item " + itemConstructor.getDeclaringClass().getSimpleName() + ":" + e.getMessage());
		}
	}
	/**
	 * Generates a spawn time for the item depending on {@code avgSpawnTime} and {@code maxSpawnDiff}
	 * @return the generated spawn time in seconds
	 */
	private double generateSpawnTime() {
		double spawnDiff = rand.nextDouble() * maxSpawnDiff;
		spawnDiff = rand.nextBoolean() ? spawnDiff : - spawnDiff;
		return avgSpawnTime + spawnDiff;
	}
	/**
	 * Generates random coordinates for the item.
	 * @return
	 */
	private Point generateCoordinates() {
		int x = rand.nextInt(SPAWN_ROOM_X);
		int y = rand.nextInt(SPAWN_ROOM_Y);
		return new Point(x + Item.LENGTH_HALF, y + Item.LENGTH_HALF); //translate to the middle point
	}
	
	public static void setSpawning(boolean spawn) {
		spawning = spawn;
	}
	
}
