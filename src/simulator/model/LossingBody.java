package simulator.model;

import simulator.misc.Vector;

public class LossingBody extends Body {
	private double lossFactor;
	private double lossFrequency;
	private double c;

	public LossingBody(String i, Vector v, Vector a, Vector p, double m, double lfac, double lfreq){
		super(i, v, a, p, m);
		lossFactor = lfac;
		lossFrequency = lfreq;
		c = 0.0;
	}
	
	public void move(double t) {
		super.move(t);
		c += t;
		if(c >= lossFrequency) {
			mass = mass - mass*lossFactor;
			c = 0.0;
		}
	}
}
