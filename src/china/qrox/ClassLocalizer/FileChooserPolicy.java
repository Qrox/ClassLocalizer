package china.qrox.ClassLocalizer;

import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;

public class FileChooserPolicy {

    public FileChooserPolicy(String desc, String[] suffix, boolean multi, boolean save) {
        this.ff = new FileFilter(desc, suffix);
        this.multi = multi;
        this.save = save;
    }
    
    public int show(Component parent, JFileChooser jfc, boolean save) {
		jfc.resetChoosableFileFilters();
        jfc.setFileFilter(ff);
        jfc.setMultiSelectionEnabled(multi);
        return save ? jfc.showSaveDialog(parent) : jfc.showOpenDialog(parent);
    }

    public int show(Component parent, JFileChooser jfc) {
        return show(parent, jfc, save);
    }
    FileFilter ff;
    boolean multi;
    boolean save;
}

class FileFilter extends javax.swing.filechooser.FileFilter {

    public FileFilter(String desc, String[] suffix) {
        this.desc = desc;
        this.suffix = suffix;
    }

    public String getDescription() {
        return desc;
    }

    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String name = f.getName();
        int dot = name.lastIndexOf('.');
        if (dot == -1) {
            return false;
        }
        name = name.substring(dot + 1);
        for (String suffix : this.suffix) {
            if (name.equalsIgnoreCase(suffix)) {
                return true;
            }
        }
        return false;
    }
    String desc;
    String[] suffix;
}