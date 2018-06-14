package ai.A3C;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomDistributedGenerator {
	private static final Random generator = new Random(AIConstants.RANDOM_SEED);

    public static int getDistributedRandomNumber(double[] probabilities) {
    	double random = generator.nextDouble();
    	double probabilitySum = 0;
    	for(int i = 0; i < probabilities.length; i++) {
    		probabilitySum += probabilities[i];
    		if(random <= probabilitySum)
    			return i;
    	}
    	throw new RuntimeException("Something went wrong, please debug getDistributedRandomNumber()");
    }
    
    /*
    private Map<Integer, Double> specificProbs = new HashMap<>();
    double probabilitySum = 0;

    public void addNumber(int value, double distribution) {
    if (this.distribution.get(value) != null) {
        distSum -= this.distribution.get(value);
    }
    this.distribution.put(value, distribution);
    distSum += distribution;
}

public int getDistributedChosenNumber() {
    double rand = Math.random();
    double tempDist = 0;
    for (Integer i : distribution.keySet()) {
        tempDist += distribution.get(i);
        if (rand * distSum <= tempDist) {
            return i;
        }
    }
    return 0;
}*/

    
}
