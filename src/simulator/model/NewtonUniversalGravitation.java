package simulator.model;

import java.util.List;
import java.lang.Math;

import simulator.launcher.Main;
import simulator.misc.Vector;

public class NewtonUniversalGravitation implements GravityLaws {
	private static final double newtonG = -6.67E-11;

	@Override
	public void apply(List<Body> bodies) {
		for (Body b1 : bodies) {
			Vector sum = new Vector(Main.DIM);
			if (b1.getMass() <= 0.0) {
				b1.setAc(sum);
				b1.setVel(sum);
			} else {
				for (Body b2 : bodies) {
					if (bodies.indexOf(b1) != bodies.indexOf(b2)) {
						double r = b1.getPos().distanceTo(b2.getPos());
						Vector fuerza = (b1.getPos().minus(b2.getPos())).direction()
								.scale((newtonG * b2.getMass()) / (r * r));
						sum = sum.plus(fuerza);
					}
				}
				b1.setAc(sum);
			}
		}
	}
	public String toString() {
		return "Newton's law of universal gravitation";
	}
}
