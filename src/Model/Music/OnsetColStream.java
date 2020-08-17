package Model.Music;

import java.util.ArrayList;
import java.util.Collections;

public class OnsetColStream extends PitchSetStream{

	private static final long serialVersionUID = 1L;
	

	public void head_adjustment(){
		if (firstKey() != 0){
			long decalage = firstKey();
			ArrayList<Long> key_list = new ArrayList<Long>(keySet());
			Collections.sort(key_list);
			for (int n=0;n<key_list.size();n++){
				PitchSetWithDuration col = get(key_list.get(n));
				remove(key_list.get(n));
				put(key_list.get(n)-decalage,col);
			}
		}
	}
	
	public void adjustment(){
		head_adjustment();
	}

}
