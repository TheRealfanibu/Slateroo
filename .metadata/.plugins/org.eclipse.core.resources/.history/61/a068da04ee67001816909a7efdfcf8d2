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

public class ItemSpawner {
	private static final int SPAWN_ROOM_X = Arena.WIDTH - Item.LENGTH;
	private static final int SPAWN_ROOM_Y = Arena.HEIGHT - Item.LENGTH;
	
	private static ItemManager itemManager;
	private static SnakeManager snakeManager;
	
	private static boolean spawning = true;
	
	private double avgSpawnTime, maxSpawnDiff;
	private boolean multiplePlayerEffect;
	
	private final Random rand = new Random();
	private final Timer timer;
	
	private Constructor<?> itemConstructor;
	
	public ItemSpawner(Class<?> itemClass) {
		if(!Item.class.isAssignableFrom(itemClass))
			throw new RuntimeException("Itemspawner: " + itemClass.getSimpleName() + " is not a subclass of Item");
		
		initItemAttributes(itemClass);
		timer = new Timer(itemClass.getSimpleName() + "-ItemSpawner");
		
		startTimerForNextSpawn();
	}
	
	private void initItemAttributes(Class<?> itemClass) {
		itemConstructor = itemClass.getConstructors()[0];
		
		avgSpawnTime = -1; // to see if the variable is found
		maxSpawnDiff = -1;
		Field[] fields = itemClass.getDeclaredFields();
		for(Field field : fields) {
			try {
				if(field.getName().equals("AVG_SPAWN_TIME"))
					avgSpawnTime = field.getDouble(null);
				else if(field.getName().equals("MAX_SPAWN_DIFF"))
					maxSpawnDiff = field.getDouble(null);
			} catch(IllegalArgumentException | IllegalAccessException e) {
				throw new RuntimeException("Getting the values of a static spawn attributes in Item Class " + itemClass.getSimpleName() + " failed : " + e);
			}
		}
		if(avgSpawnTime < 0)
			throw new IllegalStateException("Make sure that attribute AVG_SPAWN_TIME in Class " + itemClass.getSimpleName() + " exists and is correctly spelled");
		if(maxSpawnDiff < 0)
			throw new IllegalStateException("Make sure that attribute MAX_SPAWN_DIFF in Class " + itemClass.getSimpleName() + " exists and is correctly spelled");

		multiplePlayerEffect = MultiplePlayerEffectItem.class.isAssignableFrom(itemClass);
	}
	
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
	
	private void spawnItem() {
		Item item = createItemInstance(generateItemProperties());
		itemManager.addItem(item);
		startTimerForNextSpawn();
	}
	
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
			
			constructorArgs = new Object[] {(int) coords.getX(), (int) coords.getY(), mode};
		}
		else
			constructorArgs = new Object[] {(int) coords.getX(), (int) coords.getY()};
		
		return constructorArgs;
	}
	
	private Item createItemInstance(Object[] constructorArgs) {
		try {
			return (Item) itemConstructor.newInstance(constructorArgs);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Something went wrong with creating a reflective instance of an Itemobject in ItemSpawner for Item " + itemConstructor.getDeclaringClass().getSimpleName() + ":" + e.getMessage());
		}
	}
	
	private double generateSpawnTime() { // in seconds
		double spawnDiff = rand.nextDouble() * maxSpawnDiff;
		spawnDiff = rand.nextBoolean() ? spawnDiff : - spawnDiff;
		return avgSpawnTime + spawnDiff;
	}
	
	private Point generateCoordinates() {
		int x = rand.nextInt(SPAWN_ROOM_X);
		int y = rand.nextInt(SPAWN_ROOM_Y);
		return new Point(x + Item.LENGTH_HALF, y + Item.LENGTH_HALF);
	}
	
	public static void setItemManager(ItemManager manager) {
		itemManager = manager;
	}
	
	public static void setSnakeManager(SnakeManager manager) {
		snakeManager = manager;
	}
	
	public static void setSpawning(boolean spawn) {
		spawning = spawn;
	}
	
}
