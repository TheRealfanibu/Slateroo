package ai.A3C;

import java.util.HashMap;
import java.util.Map;

public class DistributedRandomNumberGenerator {
	
	private Map<Integer, Double> distribution;
    private double distSum;

    public DistributedRandomNumberGenerator() {
        distribution = new HashMap<>();
    }
    public int getDistributedRandomNumber(double[] probabilities) {
    	double rand = Math.random();
    	double probabilitySum = 0;
    	for(int i = 0; i < probabilities.length; i++) {
    		probabilitySum += probabilities[i];
    		if(rand <= probabilitySum)
    			return i;
    	}
    	throw new RuntimeException("Something went wrong, please debug getDistributedRandomNumber()");
    }
    
    /*public void addNumber(int value, double distribution) {
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