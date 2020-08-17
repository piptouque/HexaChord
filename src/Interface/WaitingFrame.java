package Interface;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Afficher une barre de progression dans un JFrame
 */
public class WaitingFrame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JProgressBar _progressbar;	
	public WaitingFrame() {
        super();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 100);
        this.setLocationRelativeTo(null);
        JPanel pane = new JPanel();
        pane.setLayout(new FlowLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel message = new JLabel("loading file");
        pane.add(message);
        c.gridy++;
        _progressbar = new JProgressBar(0, 100);
        _progressbar.setValue(0);
        _progressbar.setStringPainted(true);
        pane.add(_progressbar);
        setContentPane(pane);
        pack();
        setVisible(true);
        this.requestFocus();
    }
    
    public void update_bar(int n){
    	_progressbar.setValue(n);
    }
    
    public void dis(){
    	dispose();
    }
}