package simulator.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import simulator.control.Controller;

public class BodiesTable extends JPanel {
	private BodiesTableModel _modelo;
	private JTable _eventsTable;
	private Controller _ctrl;
	BodiesTable(Controller ctrl) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Bodies",
				TitledBorder.LEFT, TitledBorder.TOP));
		_ctrl = ctrl;
		this.initGUI();
	}
	private void initGUI() {
		_modelo = new BodiesTableModel(_ctrl);
		_eventsTable = new JTable(_modelo);
		_eventsTable.setShowGrid(false);
		_eventsTable.setCellSelectionEnabled(true);
		this.add(new JScrollPane(_eventsTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
	}
	
}
