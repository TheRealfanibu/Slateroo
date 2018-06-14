package game;

import java.util.Arrays;

public class Vector {
	private double[] vector;
	
	private int dimensions;
	
	public Vector(int dimensions) {
		vector = new double[dimensions];
		this.dimensions = dimensions;
	}
	
	public Vector(double... array) {
		vector = array;
		dimensions = array.length;
	}
	
	public static Vector fromArray(double[] array) {
		return new Vector(array);
	}
	
	public static Vector add(Vector v1, Vector v2) {
		requireEqualVectorDimensions(v1, v2, "addition");
		
		int dims = v1.getDimensions();
		double[] newVector = new double[dims];
		for(int i = 0; i < dims; i++) {
			newVector[i] = v1.getValue(i) + v2.getValue(i);
		}
		return fromArray(newVector);
	}
	
	public static double dot(Vector v1, Vector v2) {
		requireEqualVectorDimensions(v1, v2, "dot-product");
		
		double result = 0;
		for(int i = 0; i < v1.getDimensions(); i++) {
			result += v1.getValue(i) * v2.getValue(i);
		}
		return result;
	}
	
	private static void requireEqualVectorDimensions(Vector v1, Vector v2, String operation) {
		if(v1.getDimensions() != v2.getDimensions())
			throw new IllegalDimensionsException("Vector " + operation + ": Vector dimensions are not equal"
					+ getVectorsInfo(v1, v2));
			
	}
	
	private static String getVectorsInfo(Vector v1, Vector v2) {
		return "\nVector 1: " + v1.getDimensions() +
				"\nVector 2: " + v2.getDimensions();
	}
	
	public Vector scale(double factor) {
		double[] newVector = new double[dimensions];
		for(int i = 0; i < dimensions; i++) {
			newVector[i] = vector[i] * factor;
		}
		return fromArray(newVector);
	}
	
	public Vector normalize() {
		return scale(1 / length());
	}
	
	public double length() {
		double squaredDimensionsSum = 0;
		for(int i = 0; i < dimensions; i++) {
			squaredDimensionsSum += vector[i] * vector[i];
		}
		return Math.sqrt(squaredDimensionsSum);
	}
	
	public double squaredLength() {
		double length = length();
		return length * length;
	}
	
	public double getValue(int row) {
		return vector[row];
	}
	
	public int getDimensions() {
		return dimensions;
	}
	
	public String toString() {
		return Arrays.toString(vector);
	}
	
}
