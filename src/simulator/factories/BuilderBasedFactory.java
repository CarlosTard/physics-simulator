package simulator.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {
	private List<Builder<T>> builderList;
	private List<JSONObject> infoList;
	public BuilderBasedFactory(List<Builder<T>> builders) {
		this.builderList = builders;
		infoList = new ArrayList<JSONObject>();
		for(Builder<T> b:builders) {
			infoList.add(b.getBuilderInfo());
		}
	}
	
	@Override
	public T createInstance(JSONObject info) 
			 throws IllegalArgumentException{
		for(Builder<T> b: builderList) {
			T objeto = b.createInstance(info);
			if(objeto != null) return objeto;
		}
		throw new IllegalArgumentException("Esta factoría no admite crear el objeto: " + info.getString("type"));
	}

	@Override
	public List<JSONObject> getInfo() {
		return Collections.unmodifiableList(infoList);
	}
	
}
