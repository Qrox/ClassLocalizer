package china.qrox.ClassLocalizer;

import china.qrox.ClassLocalizer.classfile.ClassResolver;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.TreePath;

public class Localization extends DefaultTableModel {

    public Localization(InputStream in, long size, FileNode root) throws IOException {
        init(in, size, root);
    }

    public Localization(File file, FileNode root) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            init(in, file.length(), root);
        }
    }

    private void init(InputStream in, long size, FileNode root) throws IOException {
        setDataVector((cres = new ClassResolver(in, size)).getTable(), title);
        this.root = root;
    }
    private FileNode root;
    private ClassResolver cres;

    public FileNode getRoot() {
        return root;
    }

    public ClassResolver getClassResolver() {
        return cres;
    }

    public String[][] getTable() {
        int rows = getRowCount(),
                columns = getColumnCount();
        String[][] ret = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ret[i][j] = (String) getValueAt(i, j);
            }
        }
        return ret;
    }
	
	public void search(SearchPolicy policy, TreePath path, List<searchResult> tp) {
		int rows = getRowCount();
		int columns = getColumnCount();
nextRow:for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				String s = (String) getValueAt(i, j);
				switch (policy.type) {
					case SearchPolicy.NORMAL:
						if ((policy.ignoreCase ? s.toLowerCase() : s).contains((String) policy.pattern)) {
							tp.add(new searchResult(path, i, j, s));
							continue nextRow;
						}
						break;
					case SearchPolicy.FULLTEXT:
						if (policy.ignoreCase ?
								s.equalsIgnoreCase((String) policy.pattern):
								s.equals((String) policy.pattern)) {
							tp.add(new searchResult(path, i, j, s));
							continue nextRow;
						}
						break;
					case SearchPolicy.WILDCARD:
						break;
					case SearchPolicy.REGEX:
						Matcher m = ((Pattern) policy.pattern).matcher(s);
						if (m.find()) {
							tp.add(new searchResult(path, i, j, m.group()));
							continue nextRow;
						}
						break;
				}
			}
		}
	}

    public void save(OutputStream out) throws IOException {
        cres.setTable(getTable());
        cres.save(out);
    }

    public void save(File f) throws IOException {
        try (FileOutputStream out = new FileOutputStream(f)) {
            save(out);
        }
    }

    public void exportText(OutputStream out) throws IOException {
        cres.setTable(getTable());
        cres.exportText(out);
    }

    public void exportText(File f) throws IOException {
        exportText(new FileOutputStream(f));
    }

    public void importText(InputStream in) throws IOException {
        cres.importText(in);
        setDataVector(cres.getTable(), title);
    }

    public void importText(File f) throws IOException {
        importText(new FileInputStream(f));
    }
	
    private static final String[] title = {
        "原文本", "变更文本"
    };

    public boolean isCellEditable(int row, int column) {
        return editable[column];
    }
	
    private static final boolean[] editable = {
        false, true
    };
}