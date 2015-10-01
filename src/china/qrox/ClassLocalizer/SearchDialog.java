package china.qrox.ClassLocalizer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class SearchDialog extends javax.swing.JDialog implements KeyListener {

    public SearchDialog(MainFrame parent) {
        super(parent, false);
        initComponents();
        this.parent = parent;
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(size.width - getWidth() >> 1, size.height - getHeight() >> 1, getWidth(), getHeight());
		searchText.addKeyListener(this);
	}
	
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			startSearch(null);
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.ButtonGroup btnGrp = new javax.swing.ButtonGroup();
        wildcard = new javax.swing.JRadioButton();
        javax.swing.JButton startSearch = new javax.swing.JButton();
        javax.swing.JLabel lable1 = new javax.swing.JLabel();
        searchText = new javax.swing.JTextField();
        caseSensitive = new javax.swing.JCheckBox();
        regEx = new javax.swing.JRadioButton();
        fullText = new javax.swing.JRadioButton();
        normal = new javax.swing.JRadioButton();

        btnGrp.add(wildcard);
        wildcard.setText("使用通配符(?,*)");

        startSearch.setText("开始查找");
        startSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startSearch(evt);
            }
        });

        lable1.setText("查找文本");

        caseSensitive.setText("区分大小写");

        btnGrp.add(regEx);
        regEx.setText("使用正则表达式");

        btnGrp.add(fullText);
        fullText.setText("全文匹配");

        btnGrp.add(normal);
        normal.setSelected(true);
        normal.setText("普通搜索");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lable1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchText))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(caseSensitive)
                    .addComponent(normal))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(regEx)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fullText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                        .addComponent(startSearch)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lable1)
                    .addComponent(searchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(normal)
                    .addComponent(regEx))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(caseSensitive)
                    .addComponent(fullText)
                    .addComponent(startSearch)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startSearch(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startSearch
        if (!searchText.getText().isEmpty()) {
			setVisible(false);
			parent.search(new SearchPolicy(searchText.getText(), !caseSensitive.isSelected(), getSearchType()));
		}
    }//GEN-LAST:event_startSearch

    public int getSearchType() {
        if (normal.isSelected()) {
            return SearchPolicy.NORMAL;
        } else if (wildcard.isSelected()) {
            return SearchPolicy.WILDCARD;
        } else if (regEx.isSelected()) {
            return SearchPolicy.REGEX;
        } else if (fullText.isSelected()) {
            return SearchPolicy.FULLTEXT;
        }
        return SearchPolicy.NORMAL;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox caseSensitive;
    private javax.swing.JRadioButton fullText;
    private javax.swing.JRadioButton normal;
    private javax.swing.JRadioButton regEx;
    private javax.swing.JTextField searchText;
    private javax.swing.JRadioButton wildcard;
    // End of variables declaration//GEN-END:variables
    private MainFrame parent;
}
