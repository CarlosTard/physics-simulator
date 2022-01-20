package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TO-DO: setGravityLaws, reset y setDeltaTime con notificaciones

public class PhysicsSimulator {
	private GravityLaws gl;
	private double dt;
	private double actualTime;
	private List<Body> bodyList;
	private List<SimulatorObserver> observerList;
	
	public PhysicsSimulator(GravityLaws g, double dt) throws IllegalArgumentException {
		if(g == null)
			throw new IllegalArgumentException("No se ha aplicado ninguna ley de gravedad.");
		if(dt <= 0) throw new IllegalArgumentException("dt invalido");
		this.gl = g;
		this.dt = dt;
		this.actualTime = 0.0;
		bodyList = new ArrayList<Body>();
		observerList = new ArrayList<SimulatorObserver>();
	}
	
	public void advance() {
		gl.apply(this.bodyList);
		for (Body b1 : this.bodyList)
			b1.move(this.dt);
		this.actualTime += this.dt;
		for(SimulatorObserver so : observerList)
			so.onAdvance(Collections.unmodifiableList(bodyList), actualTime);
	}
	
	public void addBody(Body b) throws IllegalArgumentException{
		for(Body b1: this.bodyList)
			if(b.equals(b1))
				throw new IllegalArgumentException("Este cuerpo está repetido: " + b.getId());
		this.bodyList.add(b);
		for(SimulatorObserver so : observerList)
			so.onBodyAdded(Collections.unmodifiableList(bodyList), b);
	}
	public String toString() {
		StringBuilder str = new StringBuilder();
		
		str.append("{ \"time\": " + this.actualTime + ", \"bodies\": [ ");
		
		for(Body b1 : this.bodyList) {
			str.append(b1.toString());
			if(this.bodyList.indexOf(b1) < this.bodyList.size()-1)
				str.append(",");
			str.append(" ");
		}
		str.append(" ] }");
		
		return str.toString();
	}
	public void reset() {
		this.actualTime = 0.0;
		bodyList = new ArrayList<Body>();
		for(SimulatorObserver so: observerList) {
			so.onReset(Collections.unmodifiableList(bodyList), actualTime, dt, gl.toString());
		}
	}
	
	public void setDeltaTime(double dt) throws IllegalArgumentException{
		if(dt <= 0.0) throw new IllegalArgumentException("Invalid dt");
		this.dt = dt;
		for(SimulatorObserver so: observerList) {
			so.onDeltaTimeChanged(dt);
		}
	}
	public void setGravityLaws(GravityLaws gravityLaws) {
		if(gravityLaws == null) throw new IllegalArgumentException("Invalid gravityLaws");
		this.gl = gravityLaws;
		
		for(SimulatorObserver so: observerList) {
			so.onGravityLawChanged(gl.toString());
		}
	}
	
	public void addObserver(SimulatorObserver so) {
		if(!observerList.contains(so)) {
			observerList.add(so);
			so.onRegister(Collections.unmodifiableList(bodyList), actualTime, dt, gl.toString());
		}

	}

}
