package simulator.view;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.util.List;

import javax.swing.*;

import org.json.JSONException;

import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ControlPanel extends JPanel implements SimulatorObserver {
	private JButton open;
	private JButton changeGravity;
	private JButton run;
	private JButton stop;
	private JButton exit;
	private JTextField forDT;
	private JSpinner forSteps;
	private JSpinner forDelay;
	private Controller _ctrl;
	private volatile Thread _thread;

	public ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private JButton createButton(String action, String toolTip, String routeIcon) {
		JButton button = new JButton();
		button.setActionCommand(action);
		button.setToolTipText(toolTip);
		Image openIm = new ImageIcon(routeIcon).getImage();
		Image newIm = openIm.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		button.setIcon(new ImageIcon(newIm));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				executeAction(e.getActionCommand());

			}
		});
		return button;
	}

	private void initGUI() {
		this.setLayout(new BorderLayout());
		JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createEmptyBorder());
		open = createButton("load", "Load a file", "resources/icons/open.png");
		changeGravity = createButton("change", "Changes the gravity law", "resources/icons/physics.png");
		run = createButton("run", "Run the simulator for a number of steps.", "resources/icons/run.png");
		stop = createButton("stop", "Stops the simulator", "resources/icons/stop.png");
		exit = createButton("exit", "Exit out of the simulator", "resources/icons/exit.png");
		jp.add(open);
		jp.add(changeGravity);
		jp.add(run);
		jp.add(stop);

		jp.add(new JLabel("Delay: "));
		forDelay = new JSpinner(new SpinnerNumberModel(500, 0, 1000, 1));
		forDelay.setPreferredSize(new Dimension(80, 30));
		jp.add(forDelay);

		jp.add(new JLabel("Steps: "));
		forSteps = new JSpinner(new SpinnerNumberModel(10000, 0, 100000, 500));
		forSteps.setPreferredSize(new Dimension(80, 30));
		jp.add(forSteps);
		jp.add(new JLabel("Delta Time: "));
		forDT = new JTextField(10);
		forDT.setPreferredSize(new Dimension(80, 30));
		jp.add(forDT);
		this.add(jp, BorderLayout.LINE_START);

		this.add(exit, BorderLayout.LINE_END);
	}

	// other private/protected methods
	// ...

	private void enabler(boolean b) {
		open.setEnabled(b);
		changeGravity.setEnabled(b);
		run.setEnabled(b);
		exit.setEnabled(b);
	}

	private void run_sim(int n, long delay) {
		while (n > 0 && !Thread.currentThread().isInterrupted()) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JOptionPane.showMessageDialog(new JOptionPane(), e.getMessage(),
								"Error related to the execution", JOptionPane.OK_OPTION);
					}
				});
				return;
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
			--n;
		}

	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				forDT.setText(Double.toString(dt));
			}
		});

	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String gLawsDesc) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				forDT.setText(Double.toString(dt));
			}
		});
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
	}

	@Override
	public void onDeltaTimeChanged(double dt) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				forDT.setText(Double.toString(dt));
			}
		});
	}

	@Override
	public void onGravityLawChanged(String gLawsDesc) {
		// TODO Auto-generated method stub
	}

	private void loadFile() {
		JFileChooser bodyFile = new JFileChooser();
		bodyFile.setDialogTitle("Choose a file to open");
		bodyFile.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
		if (bodyFile.showOpenDialog(new JOptionPane()) == JOptionPane.OK_OPTION) {
			_ctrl.reset();
			InputStream in;
			try {
				in = new FileInputStream(bodyFile.getSelectedFile().getPath());
				_ctrl.loadBodies(in);
			} catch (FileNotFoundException e1) {
				JOptionPane.showMessageDialog(new JOptionPane(), e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			} catch (JSONException e) {
				JOptionPane.showMessageDialog(new JOptionPane(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void changeGravityLaw() {
		JOptionPane lawPane = new JOptionPane();
		JComboBox changeLaw = new JComboBox();
		changeLaw.addItem("Newton");
		changeLaw.addItem("Falling to center");
		changeLaw.addItem("No Gravity");

		int n = lawPane.showConfirmDialog(lawPane, changeLaw, "Choose a gravity law", JOptionPane.OK_CANCEL_OPTION);
		if (n == JOptionPane.OK_OPTION)
			_ctrl.setGravityLaws(_ctrl.getGravityLawsFactory().getInfo().get(changeLaw.getSelectedIndex()));
	}

	private class toRunSim implements Runnable {
		@Override
		public void run() {
			try {
				_ctrl.setDeltaTime(Double.parseDouble(forDT.getText()));
				int delay = (int) forDelay.getValue();
				int steps = (int) forSteps.getValue();
				run_sim(steps, delay);
				_thread = null;
			} catch (IllegalArgumentException exc) {
				JOptionPane.showMessageDialog(new JPanel(), "Error: " + exc.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				enabler(true);
			}
		}
	}

	private void actionRun() {
		try {
			try {
				enabler(false);
				_thread = new Thread(new toRunSim());
				_thread.start();
			} catch (IllegalArgumentException e) {
				JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, "Error: Invalid dt", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void actionStop() {
		if (_thread != null)
			_thread.interrupt();
	}

	private void actionExit() {
		JOptionPane exitPane = new JOptionPane();
		int n = exitPane.showConfirmDialog(exitPane, "The simulator will close", "Do you want to exit?",
				JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.OK_OPTION)
			System.exit(0);
	}

	private void executeAction(String actionCommand) {
		switch (actionCommand) {
		case "load":
			loadFile();
			break;
		case "change":
			changeGravityLaw();
			break;
		case "run":
			actionRun();
			break;
		case "stop":
			actionStop();
			break;
		case "exit":
			actionExit();
			break;
		}
	}

}
