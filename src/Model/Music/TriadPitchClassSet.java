package Model.Music;

public class TriadPitchClassSet extends PitchClassSet{

	private static final long serialVersionUID = 1L;

	private int _tonic;
	private int _fifth;
	private int _third;
	private boolean _major;
	
	public TriadPitchClassSet(int n1, int n2, int n3){
		super(n1,n2,n3);
		assert 0<=n1 && n1<=11 : n1;
		assert 0<=n2 && n2<=11;
		assert 0<=n3 && n3<=11;
		
		for (int pc1 : this){
			for (int pc2 : this){
				if((pc1 + 7)%12 == pc2) {
					_tonic = pc1;
					_fifth = pc2;
					for (int pc3 : this){
						if (pc3 != pc2 && pc3 != pc1){
							_third = pc3;
							if ((_tonic+3)%12 == _third){
								_major = false;
							} else {
								_major = true;
							}
						}
					}
				}
			}
		}	
	}
	
	public TriadPitchClassSet get_L(){
		TriadPitchClassSet new_triad;
		if (_major){
			new_triad = new TriadPitchClassSet((_tonic-1+12)%12, _third, _fifth);
		} else {
			new_triad = new TriadPitchClassSet(_tonic, _third, (_fifth+1)%12);
		}
		return new_triad;
	}
	
	public TriadPitchClassSet get_R(){
		TriadPitchClassSet new_triad;
		if (_major){
			new_triad = new TriadPitchClassSet(_tonic, _third, (_fifth+2)%12);
		} else {
			new_triad = new TriadPitchClassSet((_tonic-2+12)%12, _third, _fifth);
		}
		return new_triad;
	}

	public TriadPitchClassSet get_P(){
		TriadPitchClassSet new_triad;
		if (_major){
			new_triad = new TriadPitchClassSet(_tonic, (_third-1+12)%12, _fifth);
		} else {
			new_triad = new TriadPitchClassSet(_tonic, (_third+1)%12, _fifth);
		}
		return new_triad;
	}

	
}
