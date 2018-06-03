package logic;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import items.Food;
import items.HeadToTailItem;
import items.InvulnerableItem;
import items.MegaFood;
import items.ReverseSteeringItem;
import items.SlowItem;
import items.SlowSteerItem;
import items.SpeedItem;
import items.TeleportBorderItem;
import items.ZickZackMovementItem;
import items.markerInterfaces.ITimeEffectItem;
import items.superClasses.Item;

/**
 * This class specifies the classes existing in the game, which is needed for the AI.
 * It also specifies the order of information concerning the objects of those classes in the game, given to the AI
 * @author Jonas
 *
 */

public class ObjectSequence {
	private static final Class<?>[] GAME_CLASS_ORDER = {
			Food.class,
			HeadToTailItem.class,
			InvulnerableItem.class,
			MegaFood.class,
			ReverseSteeringItem.class,
			SlowItem.class,
			SlowSteerItem.class,
			SpeedItem.class,
			TeleportBorderItem.class,
			ZickZackMovementItem.class,
			HeadTile.class,
			SnakeTile.class,
			Arena.class
	};
	
	private static final List<Class<?>> ITEM_CLASSES = getClassesMatchingPredicate(clazz -> Item.class.isAssignableFrom(clazz));
	private static final List<Class<?>> TIME_EFFECT_CLASSES = getClassesMatchingPredicate(clazz -> ITimeEffectItem.class.isAssignableFrom(clazz));
	
	/**
	 * Private constructor to prevent an instanciation of this class
	 */
	private ObjectSequence(){}
	
	/**
	 * 
	 * @param predicate The filter for the game classes
	 * @return All game classes matching this predicate
	 */
	private static List<Class<?>> getClassesMatchingPredicate(Predicate<Class<?>> predicate) {
		return Arrays.stream(GAME_CLASS_ORDER).filter(predicate).collect(Collectors.toList());
	}
	
	public static Class<?>[] getGameClassOrder() {
		return GAME_CLASS_ORDER;
	}
	
	public static List<Class<?>> getItemClasses() {
		return ITEM_CLASSES;
	}

	public static List<Class<?>> getTimeEffectClasses() {
		return TIME_EFFECT_CLASSES;
	}
}
