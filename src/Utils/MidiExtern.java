package Utils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

//import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Receiver;

import Model.Music.Parameters;

public abstract class MidiExtern {
	
	private static MidiDevice _ext_KB_device=null;
	private static Transmitter _ext_KB_display_transmitter = null;
	private static Transmitter _ext_KB_record_transmitter = null;

	public static void print_extern_midi_info(){
		for (int i = 0;i<MidiSystem.getMidiDeviceInfo().length;i++){
			System.out.println("device "+i+": "+MidiSystem.getMidiDeviceInfo()[i].getName());
//			System.out.println("Midi device available on the system "+i);
//			System.out.println(MidiSystem.getMidiDeviceInfo()[i].getName()+ " - "+MidiSystem.getMidiDeviceInfo()[i].getDescription()+ " - "+MidiSystem.getMidiDeviceInfo()[i].getVendor());
//			try {
//				MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]).open();
//				System.out.println("receivers : "+MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]).getReceivers());
//				System.out.println("transmitters : "+MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]).getTransmitters());
//				MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[i]).close();;
//
//			} catch (MidiUnavailableException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			System.out.println("-----");
		}

	}
	
	public static int get_device_index(String device_name){
		for (int i = 0;i<MidiSystem.getMidiDeviceInfo().length;i++){
			if (MidiSystem.getMidiDeviceInfo()[i].getName().equals(device_name)) {
				System.out.println("device fund on index "+i);
				return i;
			}
		}
		System.err.println("device not found");
		return 0;
	}
	
	public static String[] get_devices_string_list(){
		MidiDevice.Info[] midi_devices_infos = MidiSystem.getMidiDeviceInfo();
		String[] string_list = new String[midi_devices_infos.length];
		for (int i=0;i<midi_devices_infos.length;i++){
			string_list[i]=midi_devices_infos[i].getName();
		}
		return string_list;
	}
	
	public static void set_ext_KB_device(int device_index){
		try {
			_ext_KB_device = MidiSystem.getMidiDevice(MidiSystem.getMidiDeviceInfo()[device_index]);
			_ext_KB_device.open();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void set_display_transmitter(){
		_ext_KB_display_transmitter = get_new_transmitter();
	}
	
	public static void connect_KB_display_transmitter(javax.sound.midi.Receiver receiver){
		_ext_KB_display_transmitter.setReceiver(receiver);
		//_ext_KB_record_transmitter.setReceiver(receiver);
	}
	
	public static void close_display_transmitter(){
		_ext_KB_display_transmitter.close();
	}
	
	public static void set_record_transmitter(){
		_ext_KB_record_transmitter = get_new_transmitter();
	}
	
	public static void connect_record_transmitter(javax.sound.midi.Receiver receiver){
		_ext_KB_record_transmitter.setReceiver(receiver);
	}
	
	public static void close_record_transmitter(){
		_ext_KB_record_transmitter.close();
	}

	public static Transmitter get_new_transmitter(){
		Transmitter new_transmitter = null;
		try {
			new_transmitter = _ext_KB_device.getTransmitter();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new_transmitter;
	}

}
