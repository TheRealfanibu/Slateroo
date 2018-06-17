package ai.A3C;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomDistributedGenerator {
	private static final Random generator = new Random();

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
    private Map<Integer, Double> numberProbs = new HashMap<>();
    double probabilitySum = 0;

    public void addNumber(int value, double distribution) {
    if (this.numberProbs.get(value) != null) {
        distSum -= this.numberProbs.get(value);
    }
    this.numberProbs.put(value, distribution);
    probabilitySum += distribution;
}

public int getDistributedChosenNumber() {
    double rand = Math.random();
    double tempDist = 0;
    for (Integer i : distribution.keySet()) {
        tempDist += distribution.get(i);
        if (rand * probabilitySum <= tempDist) {
            return i;
        }
    }
    return 0;
}*/

    
}
