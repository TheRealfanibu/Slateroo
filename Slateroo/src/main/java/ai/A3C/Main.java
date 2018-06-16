package ai.A3C;

import java.util.HashMap;
import java.util.Map;

public class Main {
	
	public static void main(String[] args) {
		//Environment testEnv = new Environment(epsStart, epsStop, epsSteps);
		
		/*RandomDistributedGenerator generator = new RandomDistributedGenerator();
		int anzNumbers = 1000000;
		int[] numbers = new int[4];
		
		generator.addNumber(1,0.2);
		generator.addNumber(2,0.3);
		generator.addNumber(3,0.4);
		generator.addNumber(4,0.1);
		
		for(int i = 0; i < anzNumbers; i++) {
			int rdmNumber = generator.getDistributedRandomNumber();
			numbers[rdmNumber-1]++;
		}
		
		for(int i = 0; i < numbers.length; i++) {
			System.out.println((double)numbers[i]/anzNumbers);
		}
		*/
		RandomDistributedGenerator gen = new RandomDistributedGenerator();
		double[] probs = {0.05,0.2,0.1,0.4,0.05,0.2};
		Map<Integer, Integer> count = new HashMap<>();
		int iters = 100000;
		for(int i = 0; i < iters; i++) {
			int rand = RandomDistributedGenerator.getDistributedRandomNumber(probs);
			count.merge(rand, 1, Integer::sum);
		}
		for(int i = 0; i < count.size(); i++) {
			System.out.println(i +  ": " + (count.get(i) / (double) iters) + " expected: " + probs[i]);
		}
	}
	
}