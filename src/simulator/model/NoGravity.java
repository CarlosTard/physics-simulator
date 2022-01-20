package simulator.model;

import java.util.List;

public class NoGravity implements GravityLaws {

	@Override
	public void apply(List<Body> bodies) {
	}
	public String toString() {
		return "No gravity";
	}

}
