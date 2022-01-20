package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import simulator.model.Body;
import simulator.model.SimulatorObserver;
import simulator.control.Controller;

public class StatusBar extends JPanel implements SimulatorObserver {
	// ...
	private JLabel _currTime; // for current time
	private JLabel _currLaws; // for gravity laws
	private JLabel _numOfBodies; // for number of bodies

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));
		_currTime = new JLabel("Time: ");
		_numOfBodies = new JLabel("Bodies: ");
		_currLaws = new JLabel("Laws: ");
		this.add(_currTime);
		this.add(_numOfBodies);
		this.add(_currLaws);
		_currTime.setPreferredSize(new Dimension(150,20));
		_currTime.setVerticalAlignment(SwingConstants.TOP);
		
		_numOfBodies.setPreferredSize(new Dimension(100,20));
		_numOfBodies.setVerticalAlignment(SwingConstants.TOP);
		
		_currLaws.setPreferredSize(new Dimension(400,20));
		_currLaws.setVerticalAlignment(SwingConstants.TOP);
	}


	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
				_currLaws.setText("Laws: " + gLawsDesc);
			}
		});
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
				_currLaws.setText("Laws: " + gLawsDesc);
			}
		});
		
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_numOfBodies.setText("Bodies: " + bodies.size());
			}
		});
		
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_currTime.setText("Time: " + time);
				_numOfBodies.setText("Bodies: " + bodies.size());
			}
		});
		
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				_currLaws.setText("Laws: " + gLawsDesc);
			}
		});
	}
}
