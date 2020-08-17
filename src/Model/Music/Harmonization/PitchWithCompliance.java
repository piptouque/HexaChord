package Model.Music.Harmonization;

public class PitchWithCompliance{

	private int _pitch;
	private float _v_compliance;
	private float _h_compliance;
	private float _t_compliance;
	
	public PitchWithCompliance(int pitch, float v_compliance, float h_compliance, float t_compliance){
		_pitch = pitch;
		_v_compliance = v_compliance;
		_h_compliance = h_compliance;
		_t_compliance = t_compliance;
	}

	public int get_pitch() {
		return _pitch;
	}

	public float get_v_compliance() {
		return _v_compliance;
	}

	public float get_h_compliance() {
		return _h_compliance;
	}

	public void set_v_compliance(float v_compliance) {
		this._v_compliance = v_compliance;
	}
	
	public void set_h_compliance(float h_compliance) {
		this._h_compliance = h_compliance;
	}

	public float get_t_compliance() {
		return _t_compliance;
	}

	public void set_t_compliance(float _t_compliance) {
		this._t_compliance = _t_compliance;
	}

	public String toString(){
		String str = ""+_pitch+"(v_c="+_v_compliance+" h_v="+_h_compliance+")";
		return str;
	}

	
	
	
}
