package china.qrox.ClassLocalizer;

import java.io.File;
import java.io.IOException;
import javax.swing.tree.MutableTreeNode;

public interface FileNode extends MutableTreeNode{

    public void save(File f) throws IOException;
    
    public void exportText(File f) throws IOException;
    
    public void importText(File f) throws IOException;

    public File getFile();
    
    public File getLastTextDir();
    
    public boolean isModified();
    
    public void onClose();
}