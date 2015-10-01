package china.qrox.ClassLocalizer;

import javax.swing.table.DefaultTableModel;

public class SearchTableModel extends DefaultTableModel {
	public SearchTableModel(String[] s, int i) {
		super(s, i);
	}
	
	public boolean isCellEditable(int row, int col) {
		return false;
	}
}