package china.qrox.ClassLocalizer;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author Qrox
 */
public class MainFrame extends javax.swing.JFrame {

    MainFrame me;

    public MainFrame() {
        me = this;

        initComponents();
        setSize(800, 600);

        setEnabled(false);
        setTitle("初始化中...");
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(size.width - 800 >> 1, size.height - 600 >> 1, 800, 600);
        setVisible(true);
		
		searchdialog = new SearchDialog(me);

        ActionListener menu_file = new ActionListener() {

            public void actionPerformed(ActionEvent evt) {
                Menu_File__Element(evt);
            }
        };
        Menu_File_Save.addActionListener(menu_file);
        Menu_File_SaveAs.addActionListener(menu_file);
        Menu_File_SaveAll.addActionListener(menu_file);
        Menu_File_Load.addActionListener(menu_file);
        Menu_File_Export.addActionListener(menu_file);
        Menu_File_Import.addActionListener(menu_file);
        Menu_File_Close.addActionListener(menu_file);
        Menu_File_CloseAll.addActionListener(menu_file);
        Menu_File_Exit.addActionListener(menu_file);
        Popup_Save.addActionListener(menu_file);
        Popup_SaveAs.addActionListener(menu_file);
        Popup_Close.addActionListener(menu_file);
        Popup_Export.addActionListener(menu_file);
        Popup_Import.addActionListener(menu_file);

        addWindowListener(new WindowListener() {

            public void windowClosing(WindowEvent e) {
                closeAll(true);
            }

            //<editor-fold defaultstate="collapsed" desc="unused">
            public void windowOpened(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
            //</editor-fold>
        });

        jfc = new JFileChooser();
        jfc.setAcceptAllFileFilterUsed(false);
        load = new FileChooserPolicy("Java运行时(*.class;*.jar)", CLASS_AND_JAR, true, false);
        save_class = new FileChooserPolicy("类文件(*.class)", CLASS, false, true);
        save_jar = new FileChooserPolicy("Java包(*.jar)", JAR, false, true);
        text_class = new FileChooserPolicy("文本文档(.txt)", TXT_CLASS, false, true);
        text_jar = new FileChooserPolicy("文本包(.zip)", TXT_JAR, false, true);
        //add dir

        fileTree.addTreeSelectionListener(new TreeSelectionListener() {

            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = fileTree.getSelectionPath();
                if (path == null) {
                    return;
                }
                Object o = path.getLastPathComponent();
                if (o instanceof ClassNode && ((ClassNode) o).getLocalization() != currentModel()) {
                    showTable(((ClassNode) o).getLocalization());
                } else {
                    showTable(null);
                }
                if (o instanceof FileNode || o instanceof ClassNode) {
                    Menu_File_Close.setEnabled(true);
                } else {
                    Menu_File_Close.setEnabled(false);
                }
            }
        });
        fileTree.addMouseListener(new MouseListener() {

            //<editor-fold defaultstate="collapsed" desc="unused">
            public void mouseClicked(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
            //</editor-fold>

            public void mousePressed(MouseEvent e) {
                popup(e);
            }

            public void mouseReleased(MouseEvent e) {
                popup(e);
            }

            public void popup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    synchronized (fileTree.getModel()) {
                        TreePath clicked = fileTree.getPathForLocation(e.getX(), e.getY());
                        if (clicked == null) {
                            return;
                        }
                        fileTree.setSelectionPath(clicked);
                        Object selected = clicked.getLastPathComponent();
                        if (selected instanceof FileNode || selected instanceof ClassNode) {
                            Popup_Close.setEnabled(true);
                        } else {
                            Popup_Close.setEnabled(false);
                        }
                        Point loc = fileTree.getPopupLocation(e);
                        if (loc == null) {
                            loc = e.getPoint();
                        }
                        popup.show(fileTree, loc.x, loc.y);
                    }
                }
            }
        });
        policy = new FileNamePolicy(FileNamePolicy.SHOW_SIMPLE_NAME);

        //register file drop event
        DropTargetListener dtl = new DropTargetListener() {

            //<editor-fold defaultstate="collapsed" desc="unused">
            public void dragEnter(DropTargetDragEvent dtde) {
            }

            public void dragOver(DropTargetDragEvent dtde) {
            }

            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            public void dragExit(DropTargetEvent dte) {
            }
            //</editor-fold>

            public void drop(DropTargetDropEvent dtde) {
                if (dropsupported) {
                    try {
                        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                            load((File[]) ((List) (dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor))).toArray());
						}
                    } catch (UnsupportedFlavorException ex) {
                        JOptionPane.showMessageDialog(null, "当前环境不支持文件拖放!");
                        dropsupported = false;
                    } catch (IOException ex) {
                    }
                }
            }
            private boolean dropsupported = true;
        };
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, dtl);

        about = new About(this);
        loader = new FileLoader(fileTree, policy, this);
        loader.start();
        table = new JTable();

        setEnabled(true);
        setTitle("类文件汉化工具  作者 Qrox");
    }
	
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popup = new JPopupMenu();
        Popup_Save = new JMenuItem();
        Popup_SaveAs = new JMenuItem();
        Popup_Close = new JMenuItem();
		Separator Popup_Sep1 = new Separator();
        Popup_Export = new JMenuItem();
        Popup_Import = new JMenuItem();
        rightPane = new JScrollPane();
        treeScroll = new JScrollPane();
        root = new DefaultMutableTreeNode();
        fileTree = new JTree(root);
		JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPane);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, rightPane);
        searchScroll = new JScrollPane();
        searchList = new JTable();
		JMenuBar Menu = new JMenuBar();
		JMenu Menu_File = new JMenu();
        Menu_File_Save = new JMenuItem();
        Menu_File_SaveAs = new JMenuItem();
        Menu_File_SaveAll = new JMenuItem();
        Menu_File_Load = new JMenuItem();
        Menu_File_Sep0 = new Separator();
        Menu_File_Export = new JMenuItem();
        Menu_File_Import = new JMenuItem();
		Separator Menu_File_Sep1 = new Separator();
        Menu_File_Close = new JMenuItem();
        Menu_File_CloseAll = new JMenuItem();
		Separator Menu_File_Sep2 = new Separator();
        Menu_File_Exit = new JMenuItem();
		JMenu Menu_Search = new JMenu();
        Menu_Search_Search = new JMenuItem();
		JMenu Menu_View = new JMenu();
        Menu_View_ShowFullName = new JCheckBoxMenuItem();
        Menu_About = new JMenu();

        Popup_Save.setText("保存");
        popup.add(Popup_Save);

        Popup_SaveAs.setText("另存为...");
        popup.add(Popup_SaveAs);

        Popup_Close.setText("关闭");
        popup.add(Popup_Close);
        popup.add(Popup_Sep1);

        Popup_Export.setText("导出文本");
        popup.add(Popup_Export);

        Popup_Import.setText("导入文本");
        popup.add(Popup_Import);

        fileTree.setRootVisible(false);
        model = (DefaultTreeModel) fileTree.getModel();
        treeScroll.setViewportView(fileTree);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        split1.setDividerLocation(400);
        split1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        split1.setPreferredSize(new Dimension(800, 600));

        split.setDividerLocation(235);
        split1.setTopComponent(split);

        searchList.setModel(new DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        searchList.setColumnSelectionAllowed(true);
        searchList.getTableHeader().setReorderingAllowed(false);
        searchList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                searchListAction(evt);
            }
        });
        searchScroll.setViewportView(searchList);
        searchList.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        split1.setRightComponent(searchScroll);

        Menu_File.setText("文件");

        Menu_File_Save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        Menu_File_Save.setText("保存");
        Menu_File.add(Menu_File_Save);

        Menu_File_SaveAs.setText("另存为...");
        Menu_File.add(Menu_File_SaveAs);

        Menu_File_SaveAll.setText("全部保存");
        Menu_File.add(Menu_File_SaveAll);

        Menu_File_Load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        Menu_File_Load.setText("载入...");
        Menu_File.add(Menu_File_Load);
        Menu_File.add(Menu_File_Sep0);

        Menu_File_Export.setText("导出文本");
        Menu_File.add(Menu_File_Export);

        Menu_File_Import.setText("导入文本");
        Menu_File.add(Menu_File_Import);
        Menu_File.add(Menu_File_Sep1);

        Menu_File_Close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        Menu_File_Close.setText("关闭");
        Menu_File.add(Menu_File_Close);

        Menu_File_CloseAll.setText("全部关闭");
        Menu_File.add(Menu_File_CloseAll);
        Menu_File.add(Menu_File_Sep2);

        Menu_File_Exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));
        Menu_File_Exit.setText("退出");
        Menu_File.add(Menu_File_Exit);

        Menu.add(Menu_File);

        Menu_Search.setText("查找");

        Menu_Search_Search.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        Menu_Search_Search.setText("查找");
        Menu_Search_Search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Menu_Search_Search(evt);
            }
        });
        Menu_Search.add(Menu_Search_Search);

        Menu.add(Menu_Search);

        Menu_View.setText("视图");

        Menu_View_ShowFullName.setText("显示文件全名");
        Menu_View_ShowFullName.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                Menu_View_ShowFullName(evt);
            }
        });
        Menu_View.add(Menu_View_ShowFullName);

        Menu.add(Menu_View);

        Menu_About.setText("关于");
        Menu_About.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                Menu_About__Element(evt);
            }
        });
        Menu.add(Menu_About);

        setJMenuBar(Menu);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(split1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addComponent(split1, GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void load(File[] files) {
        if (loader.load(files)) setTitle("载入中...");
    }

    private boolean save(FileNode s, boolean forceOpenSaveDialog) throws IOException {
        if (s == null) {
            return true;
        }
        File to;
        if (forceOpenSaveDialog) {
            FileChooserPolicy policy = null;
            if (s instanceof classFile) {
                policy = save_class;
            } else if (s instanceof jarRoot) {
                policy = save_jar;
            }

            if (policy.show(this, jfc) == JFileChooser.APPROVE_OPTION) {
                to = jfc.getSelectedFile();
                if (policy == save_class) {
                    if (!to.getName().endsWith(".class")) {
                        to = new File(to.getAbsolutePath() + ".class");
                    }
                } else if (policy == save_jar) {
                    if (!to.getName().endsWith(".jar")) {
                        to = new File(to.getAbsoluteFile() + ".jar");
                    }
                }
            } else {
                return false;
            }
        } else if (s.isModified()) {
            to = s.getFile();
        } else {
            return true;
        }
        s.save(to);
        return true;
    }

    public void ExportOrImport(FileNode tosave, boolean export) throws IOException {
        FileChooserPolicy policy = tosave instanceof classFile ? text_class : text_jar;
        jfc.setCurrentDirectory(tosave.getLastTextDir());
        if (policy.show(this, jfc, export) == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            String suffix = policy == text_class ? ".txt" : ".zip";
            if (!file.getName().endsWith(suffix)) {
                file = new File(file.getAbsolutePath() + suffix);
            }
            if (export) {
                tosave.exportText(file);
            } else {
                tosave.importText(file);
            }
            JOptionPane.showMessageDialog(this, export ? "成功导出" : "成功导入");
        }
    }

    private void close0(FileNode s) {
        if (s == null) {
            return;
        }
        synchronized (fileTree.getModel()) {
            Enumeration<TreePath> en = fileTree.getExpandedDescendants(new TreePath(new Object[]{root}));
            int row = fileTree.getSelectionRows()[0];
            root.remove(s);
            Localization lastModel = currentModel();
            if (lastModel != null && lastModel.getRoot() == s) {
                showTable(null);
            }
            model.reload(root);
            if (en != null) {
                while (en.hasMoreElements()) {
                    fileTree.expandPath(en.nextElement());
                }
            }
            if (row >= root.getChildCount()) {
                row = root.getChildCount() - 1;
            }
            if (row >= 0) {
                fileTree.setSelectionPath(new TreePath(new Object[]{root, root.getChildAt(row)}));
            }
            s.onClose();
        }
    }

    private void close(FileNode s) {
        if (s == null) {
            return;
        }
        if (s.isModified()) {
            switch (JOptionPane.showConfirmDialog(this, "该文件已被修改, 你要保存它么?", "信息", JOptionPane.YES_NO_CANCEL_OPTION)) {
                case JOptionPane.YES_OPTION:
                    try {
                        if (save(s, true)) {
                            break;
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
                    }
                    return;
                case JOptionPane.NO_OPTION:
                    break;
                case JOptionPane.CANCEL_OPTION:
                case JOptionPane.CLOSED_OPTION:
                    return;
            }
        }
        close0(s);
    }

    private void closeAll(boolean exit) {
        String err = "";
        int lines = 1;
        boolean exc = false, first = true, allsaved = true;
        synchronized (fileTree.getModel()) {
            int count = root.getChildCount();
            for (int i = count - 1; i >= 0; i--) {
                FileNode selected = (FileNode) root.getChildAt(i);
                if (first && selected.isModified()) {
                    switch (JOptionPane.showConfirmDialog(this, "一些文件被修改了. 你想保存它们么? ", "信息", JOptionPane.YES_NO_CANCEL_OPTION)) {
                        case JOptionPane.YES_OPTION:
                            first = false;
                            break;
                        case JOptionPane.NO_OPTION:
                            int childcount = root.getChildCount();
                            for (int ii = 0; ii < childcount; ii++) {
                                ((FileNode) root.getChildAt(i)).onClose();
                            }
                            if (exit) {
                                System.exit(0);
                            } else {
                                showTable(null);
                                root.removeAllChildren();
                                model.reload(root);
                                showTable(null);
                            }
                        case JOptionPane.CANCEL_OPTION:
                        case JOptionPane.CLOSED_OPTION:
                            return;
                    }
                }
                try {
                    if (save(selected, false)) {
                        if (!exit) {
                            close0(selected);
                        } else {
                            selected.onClose();
                        }
                    } else {
                        allsaved = false;
                    }
                } catch (IOException ex) {
                    allsaved = false;
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
            if (allsaved) {
                if (exit) {
                    System.exit(0);
                }
            } else if (exc) {
                err = err.substring(0, err.length() - 1);//remove the last return
                JOptionPane.showMessageDialog(this, "错误:  " + (lines == 2 ? "" : "\n") + err);
            } else {
                JOptionPane.showMessageDialog(this, "一些文件未保存. 程序将不会退出");
            }
        }
    }

    private void showTable(DefaultTableModel model) {
        if (model != null) {
            table.setModel(model);
            rightPane.getViewport().setView(table);
			if (jumpTo >= 0) {
				reselect(jumpTo);
				jumpTo = -1;
			}
        } else {
			jumpTo = -1;
            table.setModel(empty);
            rightPane.getViewport().setView(null);
        }
    }
	
	private void reselect(int row) {
		table.setRowSelectionInterval(row, row);
		table.scrollRectToVisible(table.getCellRect(row, 0, true));
	}

    private Localization currentModel() {
        Component comp = rightPane.getViewport().getView();
        if (comp == null || !(comp instanceof JTable)) {
            return null;
        } else {
            return (Localization) ((JTable) comp).getModel();
        }
    }

    private void Menu_File__Element(java.awt.event.ActionEvent evt) {
        Object src = evt.getSource();
        if (src == Menu_File_Load) {
            if (load.show(this, jfc) == JFileChooser.APPROVE_OPTION) {
                load(jfc.getSelectedFiles());
            }
        } else if (src == Menu_File_Save || src == Menu_File_SaveAs || src == Popup_Save || src == Popup_SaveAs) {
            TreePath path = fileTree.getSelectionPath();
            if (path == null) {
                return;
            }
            FileNode selected = (FileNode) path.getPathComponent(1);
            try {
                save(selected, src == Menu_File_SaveAs || src == Popup_SaveAs);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
            }
        } else if (src == Menu_File_SaveAll) {
            String err = "";
            int lines = 1;
            boolean exc = false;
            int count = root.getChildCount();
            for (int i = 0; i < count; i++) {
                try {
                    save((FileNode) root.getChildAt(i), false);
                } catch (IOException ex) {
                    exc = true;
                    if (lines == 9) {//at most 10 lines
                        err += "...更多\n";
                        lines++;
                    } else if (lines < 9) {
                        err += "  " + ex.getMessage() + ";\n";
                        lines++;
                    }
                }
            }
            if (exc) {
                err = err.substring(0, err.length() - 1);
                JOptionPane.showMessageDialog(this, "错误: " + (lines == 2 ? "" : "\n") + err);
            }
        } else if (src == Menu_File_Close || src == Popup_Close) {
            TreePath path = fileTree.getSelectionPath();
            if (path == null) {
                return;
            }
            Object selected = path.getLastPathComponent();
            if (selected instanceof FileNode) {
                close((FileNode) selected);
            } else if (selected instanceof ClassNode) {
                Localization model = ((ClassNode) selected).getLocalization();
                if (currentModel() == model) {
                    showTable(null);
                }
            }
        } else if (src == Menu_File_Exit || src == Menu_File_CloseAll) {
            closeAll(src == Menu_File_Exit);
        } else if (src == Menu_File_Export || src == Popup_Export || src == Menu_File_Import || src == Popup_Import) {
            try {
                TreePath path = fileTree.getSelectionPath();
                if (path == null) {
                    return;
                }
                ExportOrImport((FileNode) path.getPathComponent(1), src == Menu_File_Export || src == Popup_Export);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "错误: " + ex.getMessage());
            }
        }
    }

    private void Menu_View_ShowFullName(ChangeEvent evt) {//GEN-FIRST:event_Menu_View_ShowFullName
        boolean state = Menu_View_ShowFullName.getState();
        int policy = state ? FileNamePolicy.SHOW_FULL_NAME : FileNamePolicy.SHOW_SIMPLE_NAME;
        if (this.policy.policy != policy) {
            this.policy.policy = policy;
            fileTree.updateUI();
        }
    }//GEN-LAST:event_Menu_View_ShowFullName
    private final About about;

    private void Menu_About__Element(MouseEvent evt) {//GEN-FIRST:event_Menu_About__Element
        Object source = evt.getSource();
        if (source == Menu_About) {
            about.setVisible(true);
        }
    }//GEN-LAST:event_Menu_About__Element

    private void Menu_Search_Search(ActionEvent evt) {//GEN-FIRST:event_Menu_Search_Search
        Object source = evt.getSource();
		if (source == Menu_Search_Search) {
			searchdialog.setVisible(true);
		}
    }//GEN-LAST:event_Menu_Search_Search

    private void searchListAction(MouseEvent evt) {//GEN-FIRST:event_searchListAction
        if (evt.getClickCount() == 2) {
            int row = searchList.rowAtPoint(evt.getPoint());
            if (row >= 0) {
				searchResult res = (searchResult) searchList.getModel().getValueAt(row, 0);
				if (fileTree.isPathSelected(res.path)) {
					reselect(res.row);
				} else {
					jumpTo = res.row;
					fileTree.setSelectionPath(res.path);
					fileTree.scrollRectToVisible(fileTree.getRowBounds(fileTree.getRowForPath(res.path)));
				}
			}
        }
    }//GEN-LAST:event_searchListAction
	
	private void search(SearchPolicy policy, TreeNode node, TreePath path, List<searchResult> tp) {
		if (node instanceof ClassNode) {
			((ClassNode) node).getLocalization().search(policy, path, tp);
		}
		for (Enumeration e = node.children(); e.hasMoreElements();) {
			TreeNode n = (TreeNode) e.nextElement();
			TreePath child = path.pathByAddingChild(n);
			search(policy, n, child, tp);
		}
    }
	
	public List<searchResult> search(SearchPolicy policy, JTree tree) {
		List<searchResult> tp = new ArrayList<>();
		synchronized (tree.getModel()) {
			TreeNode node = (TreeNode) tree.getModel().getRoot();
			search(policy, node, new TreePath(node), tp);
		}
		return tp;
	}
	
	public void search(SearchPolicy policy) {
		List<searchResult> lst = search(policy, fileTree);
		if (!lst.isEmpty()) {
			TableModel tm = new SearchTableModel(searchColumn, lst.size());
			for (int i = 0; i < lst.size(); i++) {
				searchResult res = lst.get(i);
				tm.setValueAt(res, i, 0);
				tm.setValueAt(res.text, i, 1);
			}
			searchList.setModel(tm);
		} else {
			JOptionPane.showMessageDialog(me, "未找到匹配!");
		}
	}
	
	private String[] searchColumn = {"位置", "文本"};
	
    public static void main(String args[]) {
        //Set the Nimbus look and feel
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            JOptionPane.showMessageDialog(null, "无法设置Nimbus外观. 使用默认外观替代");
        }
        //</editor-fold>

        new MainFrame().setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JMenu Menu_About;
    private JMenuItem Menu_File_Close;
    private JMenuItem Menu_File_CloseAll;
    private JMenuItem Menu_File_Exit;
    private JMenuItem Menu_File_Export;
    private JMenuItem Menu_File_Import;
    private JMenuItem Menu_File_Load;
    private JMenuItem Menu_File_Save;
    private JMenuItem Menu_File_SaveAll;
    private JMenuItem Menu_File_SaveAs;
    private Separator Menu_File_Sep0;
    private JMenuItem Menu_Search_Search;
    private JCheckBoxMenuItem Menu_View_ShowFullName;
    private JMenuItem Popup_Close;
    private JMenuItem Popup_Export;
    private JMenuItem Popup_Import;
    private JMenuItem Popup_Save;
    private JMenuItem Popup_SaveAs;
    private JTree fileTree;
    private JPopupMenu popup;
    private JScrollPane rightPane;
    private JTable searchList;
    private JScrollPane searchScroll;
    private JScrollPane treeScroll;
    // End of variables declaration//GEN-END:variables
    private JFileChooser jfc;
    private DefaultMutableTreeNode root;
    private DefaultTreeModel model;
    private FileNamePolicy policy;
    private FileChooserPolicy load, save_class, save_jar, text_class, text_jar;
    private FileLoader loader;
    private JTable table;
	private SearchDialog searchdialog;
	private int jumpTo = -1;
    private static final String[] CLASS_AND_JAR = {"class", "jar"},
            CLASS = {"class"},
            JAR = {"jar"},
            TXT_CLASS = {"txt"},
            TXT_JAR = {"zip"};
    private static final DefaultTableModel empty = new DefaultTableModel();
}
