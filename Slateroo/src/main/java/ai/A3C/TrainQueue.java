package ai.A3C;

public class TrainQueue {
	
	private double[] s;
	private int a;
	private double r;
	private double[] s_;
	private double[] s_Mask;
	
	public TrainQueue(double[] s, int a, double r, double[] s_, double[] s_Mask) {
		super();
		this.s = s;
		this.a = a;
		this.r = r;
		this.s_ = s_;
		this.s_Mask = s_Mask;
	}

	public double[] getS() {
		return s;
	}

	public int getA() {
		return a;
	}

	public double getR() {
		return r;
	}

	public double[] getS_() {
		return s_;
	}

	public double[] getS_Mask() {
		return s_Mask;
	}
	
}
