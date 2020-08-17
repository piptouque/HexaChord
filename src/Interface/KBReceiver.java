package Interface;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import Model.Music.PitchClassSet;
import Model.Music.PitchSetStream;
import Model.Music.PitchSetWithDuration;
import Model.Music.PosPitchSetStream;


public class KBReceiver implements Receiver{

	PitchClassSet _current_pcs = new PitchClassSet();
	public PosPitchSetStream _pos_pcs_stream;
	long _current_key=1;
	
	public KBReceiver(){
		PitchSetStream stream = new PitchSetStream();
		stream.put((long)0, new PitchSetWithDuration(new PitchClassSet(),1));
		_pos_pcs_stream = new PosPitchSetStream(stream);
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(MidiMessage message, long arg1) {
		// TODO Auto-generated method stub
		
		if ( message instanceof ShortMessage ) {
			ShortMessage short_message = (ShortMessage) message;
//			if (short_message.getCommand() == ShortMessage.CONTROL_CHANGE && short_message.getData1() == 123 && short_message.getData2() == 0) {
//
//			}
//			if (!_playing && short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2()!=0 ) {
//				_playing = true;
//			}

			if (short_message.getChannel()+1 != 10) {
				if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
					_current_pcs.add(short_message.getData1()%12);
//					_current_mc.add(short_message.getData1());
//					System.out.println("sequncer tick pos : "+_seq.getTickPosition());
				}
				if (short_message.getCommand() == ShortMessage.NOTE_OFF || short_message.getData2() == 0) {
					_current_pcs.remove(short_message.getData1()%12);
//					_current_mc.remove(short_message.getData1());
//					System.out.println("sequencer tick pos : "+_seq.getTickPosition());
				}
			}
		}
		_current_key++;
		_pos_pcs_stream.put(_current_key, new PitchSetWithDuration(_current_pcs,1));
		System.out.println("pcs : "+_current_pcs);
		System.out.println("stream : "+_pos_pcs_stream);
	}

}
