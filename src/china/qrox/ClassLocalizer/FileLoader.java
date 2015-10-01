package china.qrox.ClassLocalizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Enumeration;
import java.util.zip.ZipException;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class FileLoader extends Thread {

    private Deque<File> dq;
    private JTree tree;
    private FileNamePolicy policy;
    private MainFrame parent;

    public FileLoader(JTree tree, FileNamePolicy policy, MainFrame parent) {
        dq = new ArrayDeque<>();
        this.tree = tree;
        this.policy = policy;
        this.parent = parent;
    }

    public boolean load(File[] f) {
        if (f.length == 0) {
            return false;
        }
        synchronized (this) {
            for (File file : f) {
                dq.addLast(file);
            }
            notify();
        }
		return true;
    }

    public void run() {
        while (true) {
            while (true) {
                synchronized (this) {
                    if (!dq.isEmpty()) {
                        break;
                    }
                    try {
                        wait(0);
                    } catch (InterruptedException ex) {
                    }
                }
            }

            String err = "";
            boolean exc = false;
            int lines = 1;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            Enumeration<TreePath> en = tree.getExpandedDescendants(new TreePath(new Object[]{root}));
            while (true) {
                try {
                    File file;
                    synchronized (this) {
                        if (dq.isEmpty()) {
                            break;
                        }
                        file = dq.removeFirst();
                    }
                    _load(file, model, root);
                } catch (IOException ex) {
                    exc = true;
                    if (lines == 9) {//at most 10 lines
                        err += "...更多\n";
                        lines++;
                    } else if (lines < 9) {
                        err += "  " + ex.getMessage() + "\n";
                        lines++;
                    }
                }
            }
            synchronized (model) {
                model.reload(root);
                if (en != null) {
                    while (en.hasMoreElements()) {
                        tree.expandPath(en.nextElement());
                    }
                }
                if (root.getChildCount() > 0) {
                    tree.setSelectionPath(new TreePath(new Object[]{root, root.getChildAt(root.getChildCount() - 1)}));
                }
            }
            if (exc) {
                err = err.substring(0, err.length() - 1);
                JOptionPane.showMessageDialog(parent, "错误: " + (lines == 2 ? "" : "\n") + err);
            }
            parent.setTitle("类文件汉化工具  作者 Qrox");
        }
    }

    private void _load(File file, DefaultTreeModel model, DefaultMutableTreeNode root) throws IOException {
        synchronized (model) {//don't allow other modification to the tree
            if (file != null) {
                if (!file.exists()) {
                    throw new IOException("[" + file.getAbsolutePath() + "] 不存在!");
                }
                if (!file.isFile()) {
                    throw new IOException("[" + file.getAbsolutePath() + "] 不是文件!");
                }
                String name = file.getName();
                FileNode node;
                if (name.endsWith(".jar")) {
                    try {
                        node = new jarRoot(file, tree, policy);
                    } catch (ZipException ex) {
                        throw new IOException("Jar文件 [" + file.getAbsolutePath() + "] 损坏!");
                    }
                } else if (name.endsWith(".class")) {
                    node = new classFile(file, tree, policy);
                } else {
                    throw new IOException("文件 [" + file.getAbsolutePath() + "] 不是Java运行时文件!");
                }
                root.add(node);
            }
        }
    }
}
