package simulator.model;

import java.util.List;

public interface GravityLaws {
	public void apply(List<Body> bodies);
	public String toString();
}
