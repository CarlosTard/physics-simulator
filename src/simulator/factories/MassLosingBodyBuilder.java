package simulator.factories;

import org.json.JSONException;
import org.json.JSONObject;

import simulator.launcher.Main;
import simulator.misc.Vector;
import simulator.model.Body;
import simulator.model.LossingBody;

public class MassLosingBodyBuilder extends Builder<Body> {
	public MassLosingBodyBuilder() {
		super("mlb","Mass Losing Body");
	}
	@Override
	protected LossingBody createTheInstance(JSONObject data) throws IllegalArgumentException{
		comprobarData(data);
		String id; Double m,lossFactor,lossFrequency;
		try {
			id = data.getString("id");
		}catch(JSONException e) {
			throw new IllegalArgumentException("La sección id no es válida");
		}
		
		Vector pos = parseJsonArray(Main.DIM, data, "pos");
		Vector v = parseJsonArray(Main.DIM, data, "vel");
		
		try {
			m = data.getDouble("mass");
		}catch(JSONException exc) {
			throw new IllegalArgumentException("No hay double válido en mass");
		}
		if(m < 0)throw new IllegalArgumentException("Error: Masa negativa");
		
		try {
			lossFrequency = data.getDouble("freq");
		}catch(JSONException exc) {
			throw new IllegalArgumentException("No hay double válido en lossFrequency");
		}
		if(lossFrequency < 0)throw new IllegalArgumentException("Error: lossFrequency negativa");
		
		try {
			lossFactor = data.getDouble("factor");
		}catch(JSONException exc) {
			throw new IllegalArgumentException("No hay double válido en lossFactor");
		}
		if(lossFactor < 0 || lossFactor > 1)throw new IllegalArgumentException("Error: lossFactor tiene que estar entre 0 y 1");
		
		Vector accel = new Vector(Main.DIM);
		
		return new LossingBody(id, v, accel, pos, m,lossFactor,lossFrequency);
	}
	
	protected JSONObject createData() {
		JSONObject data = new JSONObject();
		data.put("id", "ID del cuerpo");
		data.put("pos", "vector de posicion");
		data.put("vel","vector de velocidad");
		data.put("mass", "masa del objeto");
		data.put("freq", "frecuencia de perdida de masa");
		data.put("factor","factor de perdida de masa");
		return data;
	}

}
