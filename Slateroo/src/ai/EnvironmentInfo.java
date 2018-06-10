package ai;

import java.util.ArrayList;
import java.util.List;

import items.manage.TimeEffect;
import items.markerInterfaces.IStackableEffectItem;
import items.markerInterfaces.ITimeEffectItem;
import logic.ObjectSequence;
import logic.Snake;

public class EnvironmentInfo {
	public static final double INPUT_AMOUNT = 557;
	private static final double STARVATION_FUNCTION_STRETCH_FACTOR = 30;
	
	private ObjectDetector objectDetector;
	
	public EnvironmentInfo(ObjectDetector objectDetector) {
		this.objectDetector = objectDetector;
	}
	
	public double[] calcAIInputs(Snake snake) {
		List<Double> environmentInputs = objectDetector.calcEnvironmentInputs(snake);
		double starvationInput = calcStarvationInput(snake);
		environmentInputs.add(starvationInput);
		environmentInputs.addAll(calcInputForTimeEffectItems(snake));
		return environmentInputs.stream().mapToDouble(Double::doubleValue).toArray();
	}
	
	private double calcStarvationInput(Snake snake) {
		int tilesAmount = snake.getTiles().size();
		double preciseTileAmount = tilesAmount - snake.calcScaledTimeToNextStarvation();
		return Math.exp(-preciseTileAmount / STARVATION_FUNCTION_STRETCH_FACTOR);
	}
	
	private List<Double> calcInputForTimeEffectItems(Snake snake) {
		List<Double> timeEffectInputs = new ArrayList<>();
		List<Class<?>> timeEffectClasses = ObjectSequence.getTimeEffectClasses();
		for(Class<?> timeEffectClass : timeEffectClasses) {
			List<TimeEffect> timeEffects = snake.getItemClassTimeEffects(timeEffectClass);
			if(timeEffects.isEmpty()) {
				timeEffectInputs.add(0d);
				timeEffectInputs.add(0d);
			} else if(IStackableEffectItem.class.isAssignableFrom(timeEffectClass)){
				int effectAmount = timeEffects.size(); // amount of effects activated right now
				double maxFinishInput = 0; // the effect which will finish first
				for(TimeEffect effect : timeEffects) {
					double finishInput = effect.calcFinishInput();
					if(finishInput > maxFinishInput)
						maxFinishInput = finishInput;
				}
				timeEffectInputs.add((double) effectAmount);
				timeEffectInputs.add(maxFinishInput);
			} else {
				double minFinishInput = 1; // the effect which has started most recent and is going to finish last
				for(TimeEffect effect : timeEffects) {
					double finishInput = effect.calcFinishInput();
					if(finishInput < minFinishInput)
						minFinishInput = finishInput;
				}
				timeEffectInputs.add(1d);
				timeEffectInputs.add(minFinishInput);
			}
		}
		return timeEffectInputs;
	}
}
