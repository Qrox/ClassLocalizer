package china.qrox.ClassLocalizer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;

public class About extends JDialog implements WindowFocusListener, MouseListener {

    public About(java.awt.Frame parent) {
        super(parent, false);
        //setSize(400, 114);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setUndecorated(true);
        setBackground(new Color(0xF0F0F0));
        setResizable(false);
        setAlwaysOnTop(true);
		Container pnl = getContentPane();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
		pnl.add(new JLabel("ClassLocalizer　版本 0.0.1　作者 Qrox"));
		pnl.add(new JLabel("感谢倒霉の忍者、Zesty、大刘等提出的意见建议"));
		pnl.add(new JLabel("你可以自由传播、修改本程序，但是不能将本程序用于任何商业用途"));
		pnl.add(new JLabel("任何转载或修改请注明原作者 Qrox"));
		pack();
        addWindowFocusListener(this);
        addMouseListener(this);
    }

    public void mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            setVisible(false);
        }
    }

    public void windowGainedFocus(WindowEvent e) {
    }

    public void windowLostFocus(WindowEvent e) {
        setVisible(false);
    }

    public void setVisible(boolean b) {
		if (b) {
			Point lefttop = getParent().getLocationOnScreen();
			Dimension size = getParent().getSize();
			setLocation(lefttop.x + ((size.width - getWidth()) >> 1), lefttop.y + ((size.height - getHeight()) >> 1));
		}
		super.setVisible(b);
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
