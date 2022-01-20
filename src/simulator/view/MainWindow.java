package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {
	// ...
	Controller _ctrl;
	private int preferredWidth = 800;

	public MainWindow(Controller ctrl) {
		super("Physics Simulator");
		_ctrl = ctrl;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		initGUI();
		//Para que siempre aparezca en el centro
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		ControlPanel cp = new ControlPanel(_ctrl);
		StatusBar sb = new StatusBar(_ctrl);
		BodiesTable bt = new BodiesTable(_ctrl);
		Viewer v = new Viewer(_ctrl);
		this.getContentPane().add(cp,BorderLayout.PAGE_START);
		this.getContentPane().add(sb,BorderLayout.PAGE_END);
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
		this.getContentPane().add(jp,BorderLayout.CENTER);
		jp.add(bt);
		jp.add(v);
		
		cp.setPreferredSize(new Dimension(preferredWidth,40));
		bt.setPreferredSize(new Dimension(preferredWidth,100));
		v.setPreferredSize(new Dimension(preferredWidth,400));
		v.setMinimumSize(new Dimension(preferredWidth,400));
		v.setSize(new Dimension(preferredWidth,400));
		sb.setPreferredSize(new Dimension(preferredWidth,30));
		this.setMinimumSize(new Dimension(preferredWidth,670));
		this.pack();
		this.setVisible(true);
		
	}
	// other private/protected methods
	// ...
}