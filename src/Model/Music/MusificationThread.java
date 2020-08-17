package Model.Music;

public class MusificationThread extends Thread{

	private static final long NOTE_DURATION = 100;
	private static final long LAST_NOTE_DURATION = 5000;
	
	private MusificationMovingCell _cell;
	private Parameters _p;
	private boolean _end;
	
	
	public MusificationThread(MusificationMovingCell cell, Parameters p){
		super();
		_cell = cell;
		_p=p;
		_end = false;
	}
	
	public void run(){
		
		//_cell.update(0);
		_cell.play(NOTE_DURATION);
		try {
			Thread.sleep(NOTE_DURATION);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		while (i < _cell.get_path().size()-1 && !_end){
			_cell.update(i);
			_cell.play(NOTE_DURATION);
			try {
				Thread.sleep(NOTE_DURATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
			
		}
		
		if (!_end){
			_cell.update(_cell.get_path().size()-1);
			_cell.play(LAST_NOTE_DURATION);
			try {
				Thread.sleep(LAST_NOTE_DURATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//_cell.init();
		_p.set_musification(false);
	}

	public void end() {
		this._end = true;
	}
		
}
