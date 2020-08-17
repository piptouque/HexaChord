package Utils;

import Model.Music.PitchSetWithDuration;

public class CollectionPlayer extends Thread{
	
	private PitchSetWithDuration _mc;
	private MidiPlayer _mp;
		
	public CollectionPlayer(MidiPlayer mp,PitchSetWithDuration mc){
		super();
		_mc = new PitchSetWithDuration(mc.get_duration());
		for (int n : mc) {
			if (n<12) _mc.add(60+n);
			else _mc.add(n);
		}
		_mp = mp;
	}
		
	public void run(){
		for(Integer i : _mc) _mp.playNote(0, i, 64);
		//long sleep_duration = (long)(1000*(_mp.get_tick_length())*_mc.get_duration());
		//System.out.println("sleep duration : "+sleep_duration+" milliseconds "+_mc.get_duration()+" "+_mp.get_tick_length());
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {	
		// TODO Auto-generated catch block
			e.printStackTrace();
		}
		note_off();
	}
	
	public void note_off(){
		for (Integer i : _mc){
			_mp.shutNote(0, i);
		}
	}
	
}
