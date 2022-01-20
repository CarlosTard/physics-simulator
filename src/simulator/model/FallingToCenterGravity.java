package simulator.model;

import java.util.List;

public class FallingToCenterGravity implements GravityLaws {
	private static final double acceleration = -9.81;
	@Override
	public void apply(List<Body> bodies) {
		for(Body b:bodies) {
			b.setAc(b.getPos().direction().scale(acceleration));
		}
	}
	public String toString() {
		return "Falling to Center Law";
	}

}
