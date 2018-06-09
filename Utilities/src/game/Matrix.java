package game;

import java.util.Arrays;
import java.util.Random;

public class Matrix {
	private double[][] matrix;
	
	private int rows, columns;
	
	public Matrix (int rows, int cols) {
		matrix = new double[rows][cols];
		this.rows = rows;
		this.columns = cols;
	}
	
	private Matrix (double[][] newMatrix) {
		matrix = newMatrix;
		this.rows = newMatrix.length;
		this.columns = newMatrix[0].length;
	}
	
	private Matrix(double[] newMatrix) {
		matrix = new double[1][newMatrix.length];
		for(int i = 0; i < newMatrix.length; i++) {
			matrix[0][i] = newMatrix[i];
		}
		this.rows = 1;
		this.columns = newMatrix.length;
	}
	
	public static Matrix fromArray(double[] array) {
		return new Matrix(array);
	}
	
	public static Matrix fromArray(double[][] array) {
		return new Matrix(array);
	}
	
	public static Matrix add(Matrix matrix, Matrix otherMatrix) {
		requireEqualMatrixDimensions(matrix, otherMatrix, "addition");
		
		double[][] newMatrix = new double[matrix.getRows()][matrix.getColumns()];
		for(int i = 0; i < matrix.getRows(); i++) {
			for(int j = 0; j < matrix.getColumns(); j++) {
				newMatrix[i][j] = matrix.getValue(i, j) + otherMatrix.getValue(i, j);
			}
		}
		return new Matrix(newMatrix);
	}
	
	public static Matrix subtract(Matrix matrix, Matrix otherMatrix) {
		requireEqualMatrixDimensions(matrix, otherMatrix, "subtraction");
		
		double[][] newMatrix = new double[matrix.getRows()][matrix.getColumns()];
		for(int i = 0; i < matrix.getRows(); i++) {
			for(int j = 0; j < matrix.getColumns(); j++) {
				newMatrix[i][j] = matrix.getValue(i, j) - otherMatrix.getValue(i, j);
			}
		}
		return new Matrix(newMatrix);
		
	}
	
	public static Matrix multiply(Matrix matrix, Matrix otherMatrix) {
		if(matrix.getColumns() != otherMatrix.getRows()/* || matrix.getRows() == 1 && otherMatrix.getColumns() == 1*/)
			throwIllegalDimensionsException("Matrix multiply: Matrix 1 columns has to equal Matrix 2 rows"
					+ getInfoFromMatrices(matrix, otherMatrix));
		
		double[][] newMatrix = new double[matrix.getRows()][otherMatrix.getColumns()];
		for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < otherMatrix.getColumns(); j++) {
                for (int k = 0; k < matrix.getColumns(); k++) {
                    newMatrix[i][j] += matrix.getValue(i, k) * otherMatrix.getValue(k, j);
                }
            }
        }
		return new Matrix(newMatrix);
	}
	
	public static Matrix multiplyElementwise(Matrix matrix, Matrix otherMatrix) {
		requireEqualMatrixDimensions(matrix, otherMatrix, "multiply elementwise");
		
		int rows = matrix.getRows();
		int columns = matrix.getColumns();
		double[][] newVector = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				newVector[i][j] = matrix.getValue(i, j) * otherMatrix.getValue(i, j);
			}
		}
		return new Matrix(newVector);
	}
	
	private static void requireEqualMatrixDimensions(Matrix matrix, Matrix otherMatrix, String operation) {
		if(matrix.getRows() != otherMatrix.getRows() || matrix.getColumns() != otherMatrix.getColumns())
			throwIllegalDimensionsException("Matrix " + operation + ": Matrix dimensions are not equal"
					+ getInfoFromMatrices(matrix, otherMatrix));
	}
	
	private static String getInfoFromMatrices(Matrix matrix, Matrix otherMatrix) {
		return "\nMatrix 1: " + matrix.getRows() + " Rows " + matrix.getColumns() + " Columns\n"
				+ "Matrix 2: " + otherMatrix.getRows() + " Rows " + otherMatrix.getColumns() + " Columns\n";
	}
	
	private static void throwIllegalDimensionsException(String text) {
		throw new IllegalDimensionsException(text);
	}

	
	public Matrix scale(double factor) {
		double[][] newMatrix = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				newMatrix[i][j] = matrix[i][j] * factor;
			}
		}
		return new Matrix(newMatrix);
	}
	
	
	public void randomize(double lowest, double highest) {
		Random random = new Random();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				double randomNumber = random.nextDouble() * (highest-lowest) + lowest;
				matrix[i][j] = randomNumber;
			}
		}
	}
	
	public void randomizeNormal(double deviation){
		Random random = new Random();
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				double randomNormalNumber = random.nextGaussian() * deviation;
				matrix[i][j] = randomNormalNumber;
			}
		}
	}
	
	public Matrix transpose() {
		double[][] newMatrix = new double[columns][rows];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				newMatrix[j][i] = matrix[i][j];
			}
		}
		return new Matrix(newMatrix);
	}
	
	public double[] getColumn(int column) {
		double[] columnNumbers = new double[rows];
		for(int i = 0; i < rows; i++) {
			columnNumbers[i] = matrix[i][column];
		}
		return columnNumbers;
	}
	

	public double getValue(int row, int column) {
		return matrix[row][column];
	}
	
	public void setValue(int row, int column, double newValue) {
		matrix[row][column] = newValue;
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getColumns() {
		return columns;
	}

	@Override
	public String toString() {
		String matrixString = "";
		for(int i = 0; i < rows; i++) {
				matrixString += Arrays.toString(matrix[i]) + "\n";
		}
		return matrixString;
	}

	public int getIndexOfLargestValueInColumn(int column) {
		int index = 0;
		double maxValue = Double.MIN_VALUE;
		for(int i = 0; i < rows; i++) {
			double rowValue = matrix[i][column];
			if(rowValue > maxValue) {
				maxValue = rowValue;
				index = i;
			}
		}
		return index;
	}
}
