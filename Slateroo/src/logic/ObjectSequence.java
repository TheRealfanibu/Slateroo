package logic;

import java.util.ArrayList;
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
import items.superClasses.Item;

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
	
	
	private ObjectSequence(){}
	
	public static List<Class<?>> getItemClasses() {
		return getClassesMatchingPredicate(clazz -> Item.class.isAssignableFrom(clazz));
	}
	
	public static List<Class<?>> getClassesMatchingPredicate(Predicate<Class<?>> predicate) {
		return Arrays.stream(GAME_CLASS_ORDER).filter(predicate).collect(Collectors.toList());
	}
	
	public static Class<?>[] getGameClassOrder() {
		return GAME_CLASS_ORDER;
	}
}
