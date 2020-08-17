package Path;

import java.util.ArrayList;


public class TranslationInTonnetz extends ArrayList<Integer> implements TransformationInTonnetz{

	private static final long serialVersionUID = 1L;

	public TranslationInTonnetz(ArrayList<Integer> coefs){
		super();
	}

	public TranslationInTonnetz(int x, int y, int z){
		super();
		add(x);
		add(y);
		add(z);
	}
	
}
