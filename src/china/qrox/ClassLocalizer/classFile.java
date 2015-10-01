package china.qrox.ClassLocalizer;

import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;

public class classFile extends DefaultMutableTreeNode implements TableModelListener, FileNode, ClassNode {

    public classFile(File file, JComponent menu, FileNamePolicy policy) throws IOException {
        tbl = new Localization(file, this);
        tbl.addTableModelListener(this);
        modified = false;
        if (menu == null) {
            throw new NullPointerException();
        }
        this.file = file;
        this.menu = menu;
        this.policy = policy;
        txtdir = file.getParentFile();
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        if (this.modified ^ modified) {
            this.modified = modified;
            ((JComponent) menu).updateUI();
        }
    }

    public void tableChanged(TableModelEvent e) {
        setModified(true);
    }

    public void save(File file) throws IOException {
        tbl.save(file);
        this.file = file;
        setModified(false);
    }
    private File txtdir;

    public File getLastTextDir() {
        return txtdir;
    }

    public void exportText(File file) throws IOException {
        txtdir = file.getParentFile();
        tbl.exportText(file);
    }

    public void importText(File file) throws IOException {
        txtdir = file.getParentFile();
        tbl.importText(file);
        setModified(true);
    }

    public String toString() {
        String string;
        if (modified) {
            string = modifiedPrefix;
        } else {
            string = "";
        }
        if (policy.policy == FileNamePolicy.SHOW_FULL_NAME) {
            string += file.getAbsolutePath();
        } else if (policy.policy == FileNamePolicy.SHOW_SIMPLE_NAME) {
            string += file.getName();
        }
        return string;
    }

    public Localization getLocalization() {
        return tbl;
    }

    public File getFile() {
        return file;
    }

    public void onClose() {
    }
    private FileNamePolicy policy;
    private Localization tbl;
    private File file;
    private JComponent menu;
    private boolean modified;
    private static final String modifiedPrefix = "*";
}