package mathUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import utilities.StringUtils;

public class Calculator {
	private ScriptEngineManager manager = new ScriptEngineManager();
	private ScriptEngine engine = manager.getEngineByName("js");
	
	private String reshapeEquation(String equation) {
		String[] mathFunctions = {"sin", "cos", "tan", "sqrt", "pow", "asin", "acos", "atan", "E", "PI"};
		for(String func : mathFunctions) {
			equation = equation.replace(func, "Math." + func);
		}
		equation = equation.replace("ln", "Math.log");
		
		return equation;
	}
	
	private String addMultiplicationChars(String equation, String varName) {
		int index = -1;
		while((index = equation.indexOf(varName, index + 1)) != -1) {
			if(index != 0 && String.valueOf(equation.charAt(index - 1)).matches("[0-9]"))
				equation = equation.substring(0, index) + equation.substring(index).replaceFirst("[" + varName + "]",
						"*" + varName);
		}
		return equation;
	}
	
	public Object calculate(String equation, String[] varNames, double[] varValues) {
		if(varNames.length != varValues.length)
			throw new RuntimeException("Length of varNames and varValues isn't equal");
		
		equation = reshapeEquation(equation);
		for(int i = 0; i < varNames.length; i++) {
			engine.put(varNames[i], varValues[i]);
			equation = addMultiplicationChars(equation, varNames[i]);
		}
		try {
			return engine.eval(equation);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return Double.NEGATIVE_INFINITY;
	}
	
	public Object calculate(String equation) {
		return calculate(equation, new String[]{}, new double[]{});
	}
	
}
