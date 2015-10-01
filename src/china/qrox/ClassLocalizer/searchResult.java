package china.qrox.ClassLocalizer;

import java.io.File;
import javax.swing.tree.TreePath;

public class searchResult {
	TreePath path;
	int row, column;
	String text;
	
	public searchResult(TreePath path, int row, int column, String text) {
		this.path = path;
		this.row = row;
		this.text = text;
		this.column = column;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Object o : path.getPath()) {
			sb.append(o.toString()).append(File.separatorChar);
		}
		return sb.deleteCharAt(sb.length() - 1).append(':').append(row + 1).append(column == 0 ? "(原文本)" : "(变更文本)").toString();
	}
}