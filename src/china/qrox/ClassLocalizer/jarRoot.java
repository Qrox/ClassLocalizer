package china.qrox.ClassLocalizer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.swing.JComponent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public final class jarRoot extends jarDir implements TableModelListener, FileNode {
	
	public static final Charset charset_utf8;
	
	static {
		charset_utf8 = Charset.forName("utf8");
	}

    public void onClose() {
        try {
            tmpzfile.close();
            tmpfile.delete();
        } catch (IOException ex) {
        }
    }

    public jarRoot(File file, JComponent menu, FileNamePolicy policy) throws IOException {
        super(file.getName());
        if (file.isDirectory() || !file.getName().endsWith(".jar")) {
            throw new IOException("文件 [" + file.getAbsolutePath() + "] 不是Jar文件!");
        }
        directlycopy = new HashSet<>();
        classes = new HashMap<>();
        try (ZipFile s = new ZipFile(file)) {
            Enumeration<? extends ZipEntry> en = s.entries();
            String name;
            while (en.hasMoreElements()) {
                ZipEntry entry = en.nextElement();
                if (!entry.isDirectory()) {
                    directlycopy.add(entry.getName());
                    if ((name = entry.getName()).endsWith(".class")) {
                        int lastSlash = name.lastIndexOf('/');
                        String current = lastSlash == -1 ? null : name.substring(0, lastSlash);
                        String[] currentPath = lastSlash == -1 ? rootPath : current.split("/");

                        jarDir lastDir = this;
                        for (String str : currentPath) {
                            lastDir = lastDir.dir(str);
                        }
                        try {
                            jarFile jf = new jarFile(name.substring(lastSlash + 1), s.getInputStream(entry), entry.getSize(), entry, this);
                            classes.put(name, jf);
                            lastDir.add(jf);
                            jf.getLocalization().addTableModelListener(this);
                        } catch (IOException ex) {
                        }
                    } else {
                    }
                }
            }
            try {
                tmpfile = File.createTempFile("tmp_", ".jar");
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(tmpfile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                tmpzfile = new ZipFile(tmpfile);
                savepath = file;
            } catch (Exception ex) {
                throw new IOException("创建临时文件失败!");
            }
        }
        this.menu = menu;
        this.policy = policy;
        txtdir = file.getParentFile();
    }

    public synchronized void save(File file) throws IOException {
        save(true, file);
    }

    private synchronized void save(boolean delete, File to) throws IOException {
        if (to != null) {
            if (to.isDirectory()) {
                throw new IOException("[" + to.getAbsolutePath() + "] 是目录!");
            }
        }
        File file = File.createTempFile("tmp_", ".jar");
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file))) {
            out.setLevel(1);
            Enumeration<? extends ZipEntry> en = tmpzfile.entries();
            while (en.hasMoreElements()) {
                ZipEntry entry = en.nextElement();
                if (entry.isDirectory()) {
                    out.putNextEntry(new ZipEntry(entry.getName()));
                } else if (directlycopy.contains(entry.getName())) {
                    InputStream in = tmpzfile.getInputStream(entry);
                    out.putNextEntry(new ZipEntry(entry.getName()));
                    long size = entry.getSize();
                    while (size-- > 0) {
                        out.write(in.read());
                    }
                }//else is valid class file
            }
            for (Entry<String, jarFile> men : classes.entrySet()) {
                String name = men.getKey();
                if (!directlycopy.contains(name)) {
                    jarFile jf = men.getValue();
                    out.putNextEntry(jf.getEntry());
                    jf.save(out);
                    setDirectlyCopy(name, true);
                }
            }
        }
        if (to != null) {
            savepath = to;
            try {
                Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(to.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException | SecurityException ex) {
                try {
                    file.delete();
                } catch (SecurityException ex2) {
                }
                throw ex;
            }
        }
        try {
            tmpzfile.close();
            if (delete) {
                tmpfile.delete();
            }
        } catch (IOException | SecurityException ex) {
        }
        tmpfile = file;
        tmpzfile = new ZipFile(file);
        for (Entry<String, jarFile> men : classes.entrySet()) {
            directlycopy.add(men.getKey());
        }
        setModified(false);
    }
    private File txtdir;

    public File getLastTextDir() {
        return txtdir;
    }

    public void exportText(File file) throws IOException {
        txtdir = file.getParentFile();
        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file), charset_utf8)) {
            for (Entry<String, jarFile> men : classes.entrySet()) {
                jarFile jf = men.getValue();
                out.putNextEntry(new ZipEntry(jf.getEntry().getName() + ".txt"));
                try {
                    jf.exportText(out);
                } catch (IOException ex) {
                }
            }
        }
    }

    public void importText(File file) throws IOException {
        txtdir = file.getParentFile();
        try (ZipFile s = new ZipFile(file, charset_utf8)) {
            Enumeration<? extends ZipEntry> en = s.entries();
            while (en.hasMoreElements()) {
                ZipEntry entry = en.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class.txt")) {
                    jarFile jf = classes.get(name.substring(0, name.length() - 4));
                    if (jf != null) {
                        try {
                            jf.importText(s.getInputStream(entry));
                        } catch (IOException ex) {
                        }
                        directlycopy.remove(jf.getEntry().getName());
                    }
                }
            }
            setModified(true);
        }
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

    public String toString() {
        String string;
        if (modified) {
            string = modifiedPrefix;
        } else {
            string = "";
        }
        if (policy.policy == FileNamePolicy.SHOW_FULL_NAME) {
            string += savepath.getAbsolutePath();
        } else if (policy.policy == FileNamePolicy.SHOW_SIMPLE_NAME) {
            string += savepath.getName();
        }
        return string;
    }

    public File getFile() {
        return savepath;
    }

    public void setDirectlyCopy(String path, boolean does) {
        if (does) {
            directlycopy.add(path);
        } else {
            directlycopy.remove(path);
        }
    }
    private HashSet<String> directlycopy;
    private HashMap<String, jarFile> classes;
    private boolean modified;
    private JComponent menu;
    private File savepath;
    private File tmpfile;
    private ZipFile tmpzfile;
    private FileNamePolicy policy;
    private static final String[] rootPath = new String[0];
    private static final String modifiedPrefix = "*";
}

class jarDir extends DefaultMutableTreeNode {

    jarDir(String str) {
        super(str);
        dir = new HashMap<>();
    }

    public boolean isLeaf() {
        return false;
    }
    protected HashMap<String, jarDir> dir;

    jarDir dir(String str) throws IOException {
        jarDir dir = (jarDir) this.dir.get(str);
        if (dir == null) {
            dir = new jarDir(str);
            this.dir.put(str, dir);
            add(dir);
        }
        return dir;
    }
}

class jarFile extends DefaultMutableTreeNode implements TableModelListener, ClassNode {

    jarFile(String str, InputStream in, long size, ZipEntry entry, jarRoot root) throws IOException {
        super(str);
        tbl = new Localization(in, size, root);
        tbl.addTableModelListener(this);
        this.entry = entry;
        this.root = root;
    }

    public void tableChanged(TableModelEvent evt) {
        root.setDirectlyCopy(entry.getName(), false);
    }

    public Localization getLocalization() {
        return tbl;
    }

    public boolean isLeaf() {
        return true;
    }

    public void insert(MutableTreeNode child, int index) {
        throw new RuntimeException("向文件中加入子节点!");
    }

    public ZipEntry getEntry() {
        return new ZipEntry(entry.getName());
    }

    public void save(OutputStream out) throws IOException {
        tbl.save(out);
    }

    public void exportText(OutputStream out) throws IOException {
        tbl.exportText(out);
    }

    public void importText(InputStream in) throws IOException {
        tbl.importText(in);
    }
    private jarRoot root;
    private Localization tbl;
    private ZipEntry entry;
}