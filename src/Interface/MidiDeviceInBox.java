package Interface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import Main.HexaChord;
import Model.Music.Parameters;
import Utils.MidiExtern;

public class MidiDeviceInBox extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private static MidiDeviceInBox singleton = null;

	
	public static MidiDeviceInBox getInstance(){
		if (singleton == null){
			singleton = new MidiDeviceInBox();
		}
		return singleton;
	}

	public MidiDeviceInBox(){
		super("select MIDI IN device");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(400,400));
		setResizable(false);
		setLocation(500,50);
		setFocusable(true);
		
		JList<String> jlist = new JList<String>(MidiExtern.get_devices_string_list());
		JScrollPane scrollPane1 = new JScrollPane(jlist);
		add(scrollPane1, BorderLayout.CENTER);
		
	    MouseListener mouseListener = new MouseAdapter() {
	        public void mouseClicked(MouseEvent mouseEvent) {
	          JList<String> theList = (JList<String>) mouseEvent.getSource();
	          if (mouseEvent.getClickCount() == 2) {
	            int index = theList.locationToIndex(mouseEvent.getPoint());
	            if (index >= 0) {
	            	MidiExtern.set_ext_KB_device(index);
	            	MidiExtern.set_display_transmitter();
	            	//MidiExtern.connect_KB_display_transmitter(Parameters.getInstance().get_note_receiver());
	            	Parameters.getInstance().set_display_receiver_from_KB(new DisplayReceiver(HexaChord.getInstance(),true));
	            	MidiExtern.connect_KB_display_transmitter(Parameters.getInstance().get_display_receiver_from_KB());
	            }
	            setVisible(false);
	          }
	        }
	      };
	      jlist.addMouseListener(mouseListener);

		setSize(350, 300);
        setVisible(true);

	}
	
	
}
