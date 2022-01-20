package simulator.factories;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import simulator.misc.Vector;

public abstract class Builder<T> {
	protected JSONObject builderInfo;
	protected String type;
	
	public Builder(String type,String desc) {
		this.type = type;
		builderInfo = new JSONObject();
		builderInfo.put("type", type);
		builderInfo.put("data", createData());
		builderInfo.put("desc", desc);
	}
	
	public T createInstance(JSONObject info) 
			throws IllegalArgumentException{
		if(!info.has("type")) throw new IllegalArgumentException("Trying to parse JSONObject whithout type");
		
			if(info.getString("type").equals(this.type)) {
				if(!info.has("data")) throw new IllegalArgumentException("Trying to parse JSONObject whithout data");
				return createTheInstance(info.getJSONObject("data"));
				
			}else return null;			
	}
	//se implementa en las hijas. Solo se pasa el JSONObject de dentro de data
	protected abstract T createTheInstance(JSONObject data) throws IllegalArgumentException;
	
	protected JSONObject createData() {
		return new JSONObject();
	}
	//Comprueba que la seccion data tenga el formato correcto, y que los valores no sean nulos
	protected void comprobarData(JSONObject data) throws IllegalArgumentException {
		if(!data.keySet().equals(builderInfo.getJSONObject("data").keySet())) 
			throw new IllegalArgumentException("A un objeto le faltan datos de la sección Data");
		for(String key: data.keySet()) {
			if(data.isNull(key))
				throw new IllegalArgumentException(key + " no tiene nada");
		}
	}
	protected Vector parseJsonArray(int dim,JSONObject data,String key) throws IllegalArgumentException{
		JSONArray obj1;
		try {
			obj1 = data.getJSONArray(key);
		}catch(JSONException e) {
			throw new IllegalArgumentException("No hay un vector en la sección " + key);
		}
		if(obj1.length() != dim) throw new IllegalArgumentException("La dimension de " + key  + " no es correcta");
		double[] p = new double[obj1.length()];
		try {
			for(int i = 0; i < p.length; ++i) {
				p[i] = obj1.getDouble(i);
			}
		}
		catch(JSONException exc) {
			throw new IllegalArgumentException("Algún double incorrecto en " + key);
		}
		return new Vector(p);
	}
	
	public JSONObject getBuilderInfo() {
		return new JSONObject(builderInfo.toString());
	}

}
