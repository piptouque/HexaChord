package Utils;

public class NotePlayer extends Thread{

	private int _pitch;
	private long _duration;
	private MidiPlayer _mp;
		
	public NotePlayer(MidiPlayer mp,int pitch, long duration){
		super();
		_mp = mp;
		_pitch = pitch;
		_duration = duration;
	}
		
	public void run(){
		_mp.playNote(0, _pitch, 64);
		try {
			Thread.sleep((long)(_duration));
		} catch (InterruptedException e) {	
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		note_off();
	}
	
	public void note_off(){
		_mp.shutNote(0, _pitch);
	}
	
}
