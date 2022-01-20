package simulator.control;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;

// TO-DO: Hacer reset, setDeltaTime y setGravityLaws aquí


public class Controller {
	private PhysicsSimulator ps;
	private Factory<Body> fBody;
	private Factory<GravityLaws> fgl;
	
	public Controller(PhysicsSimulator ps, Factory<Body> bodyFactory, Factory<GravityLaws> glFactory) {
		this.ps = ps;
		fBody = bodyFactory;
		fgl = glFactory;
	}
	
	public void run(int n, OutputStream out) {
		PrintStream show = new PrintStream(out);
		show.println("{\"states\": [");
		for(int i = 0; i < n; ++i) {
			show.println(ps.toString() + ",");
			ps.advance();
		}
		show.println(ps.toString());	
		show.println("] }");
	}
	
	public void run(int n) {
		for(int i = 0; i < n; ++i) 
			ps.advance();
	}
	
	
	public void loadBodies(InputStream in) {
		JSONObject jsonInput = null;
		JSONArray ar1 = null;
		try {
			jsonInput = new JSONObject(new JSONTokener(in));
			ar1 = jsonInput.getJSONArray("bodies");
		}
		catch(JSONException exc) {
			throw new JSONException("Hay errores en el fichero JSON");
		}
		for(int i = 0; i < ar1.length(); ++i) {
			try {
				Body b = fBody.createInstance(ar1.getJSONObject(i));
				ps.addBody(b);
			}
			catch(IllegalArgumentException exc) {
				System.err.println(exc.getMessage());
			}
		}
	}
	
	public void addObserver(SimulatorObserver o) {
		ps.addObserver(o);
	}
	
	public Factory<GravityLaws> getGravityLawsFactory(){
		return fgl;
	}
	
	public void setGravityLaws(JSONObject info) {
		ps.setGravityLaws(fgl.createInstance(info));
	}
	public void reset() {
		ps.reset();
	}
	public void setDeltaTime(double dt) {
		ps.setDeltaTime(dt);
	}

}
