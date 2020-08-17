package Path;

import java.util.ArrayList;

public class TonnetzTranslationPath extends ArrayList<TranslationInTonnetz>{

	private static final long serialVersionUID = 1L;
	
	private String _name;
	
	public TonnetzTranslationPath(String name){
		super();
		_name = name;
	}

	public void add(int x, int y, int z){
		add(new TranslationInTonnetz(x,y,z));
	}
	
	public void add_up(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(1,0,0));
		}
	}

	public void add_up_right(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(1,1,0));
		}
	}

	public void add_down_right(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(0,1,0));
		}
	}

	public void add_down(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(-1,0,0));
		}
	}

	public void add_down_left(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(0,0,1));
		}
	}

	public void add_up_left(int n) {
		for (int i=0;i<n;i++){
			add(new TranslationInTonnetz(1,0,1));
		}
	}
	
	public String get_name() {
		return _name;
	}

}
