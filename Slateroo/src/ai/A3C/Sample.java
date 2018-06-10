package ai.A3C;

public class Sample {
	
	private double[] s;
	private double[] onehot_a;
	private double r;
	private double[] s_;
	
	public Sample(double[] s, double[] a_cats, double r, double[] s_) {
		this.s = s;
		this.onehot_a = a_cats;
		this.r = r;
		this.s_ = s_;
	}

	public double[] getS(){
		return s;
	}
	
	public double[] getOnehot_A(){
		return onehot_a;
	}
	
	public double getR(){

		return r;
	}
	
	public double[] getS_(){
		return s_;
	}
	
}
