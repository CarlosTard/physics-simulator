package simulator.model;


import simulator.misc.Vector;

public class Body {
	private String id;
	private Vector vel;
	private Vector ac;
	private Vector pos;
	protected double mass;
	
	public Body(String i, Vector v, Vector acceleration, Vector p, double m) {
		id = i;
		vel = v;
		ac = acceleration;
		pos = p;
		mass = m;
	}
	
	public boolean equals(Body b2) {
		if (this == b2) return true;
		if (b2 == null) return false;
		if (this.getClass() != b2.getClass()) return false;
		Body miclase = (Body) b2;
		return miclase.id.equals(this.id);
	}
	
	public void move(double t) {
		pos = pos.plus(vel.scale(t)).plus(ac.scale(0.5f).scale(t*t));
		vel = vel.plus(ac.scale(t));
	}

	public String toString() {
		//{  "id": "b1", "mass": 1.5E30, "pos": [0.0, 4.5E10], "vel": [10000.0, 0.0], "acc": [0.0, 0.0] }
		StringBuilder s = new StringBuilder();
		s.append("{ \"id\": \"" + id + "\", ");
		s.append("\"mass\": " + mass + ", ");
		s.append("\"pos\": " + pos + ", ");
		s.append("\"vel\": " + vel + ", ");
		s.append("\"acc\": " + ac + " ");
		
		s.append('}');
		return s.toString();
	}
	
	public String getId() {
		return id;
	}
	
	public Vector getVel() {
		return vel;
	}
	
	public Vector getAc() {
		return ac;
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public double getMass() {
		return mass;
	}
	
	void setId(String newId) {
		id = newId;
	}
	
	void setVel(Vector newVel) {
		vel = newVel;
	}
	
	void setAc(Vector newAc) {
		ac = newAc;
	}
	
	void setPos (Vector newPos) {
		pos = newPos;
	}
	
	void setMass(double newMass) {
		mass = newMass;
	}
	
	
}
