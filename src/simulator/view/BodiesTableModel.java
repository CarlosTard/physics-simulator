package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {
	// ...
	private List<Body> _bodies;

	BodiesTableModel(Controller ctrl) {
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _bodies.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int column) {
		switch(column) {
		case 0: return "id";
		case 1: return "Mass";
		case 2: return "Position";
		case 3: return "Velocity";
		case 4: return "Acceleration";
		default: return "";
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex) {
		case 0: return _bodies.get(rowIndex).getId();
		case 1: return _bodies.get(rowIndex).getMass();
		case 2: return _bodies.get(rowIndex).getPos();
		case 3: return _bodies.get(rowIndex).getVel();
		case 4: return _bodies.get(rowIndex).getAc();
		default: return null;
		}
	}
	
	private void refreshBodies(List<Body> bodies) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_bodies = bodies;
				fireTableStructureChanged();
			}
			
		});
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		refreshBodies(bodies);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		refreshBodies(bodies);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		refreshBodies(bodies);
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		refreshBodies(bodies);
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {

	}
}