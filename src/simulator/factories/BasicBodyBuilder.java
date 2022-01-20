package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.launcher.Main;
import simulator.misc.Vector;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body> {
	public BasicBodyBuilder() {
		super("basic","Generic Body");
	}
	
	@Override
	protected Body createTheInstance(JSONObject data) throws IllegalArgumentException{
		comprobarData(data);
		String id; Double m;
		try {
			id = data.getString("id");
		}catch(JSONException e) {
			throw new IllegalArgumentException("La sección id no es válida");
		}
		
		try {
			m = data.getDouble("mass");
		}catch(JSONException exc) {
			throw new IllegalArgumentException("No hay double válido en mass");
		}
		if(m < 0)throw new IllegalArgumentException("Error: Masa negativa");
		
		Vector pos = parseJsonArray(Main.DIM, data, "pos");
		Vector v = parseJsonArray(Main.DIM, data, "vel");
		Vector accel = new Vector(Main.DIM);
		return new Body(id, v, accel, pos, m);
	}
	protected JSONObject createData() {
		JSONObject data = new JSONObject();
		data.put("id", "ID del cuerpo");
		data.put("pos", "vector de posicion");
		data.put("vel","vector de velocidad");
		data.put("mass", "masa del objeto");
		return data;
	}

}
