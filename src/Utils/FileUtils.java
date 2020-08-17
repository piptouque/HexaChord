package Utils;

import java.io.File;

public abstract class FileUtils {

	
	public static boolean is_midi(String file_name){
		if (file_name.endsWith(".mid") || file_name.endsWith(".midi") || file_name.endsWith(".MID")) return true;
		return false;
	}

	public static boolean is_text(String file_name){
		if (file_name.endsWith(".txt")) return true;
		return false;
	}

	public static String get_real_name(File file){
		return get_real_name(file.toString());
	}
	
	public static String get_real_name(String name){

		int name_size = name.toCharArray().length;
		int last_slash_index = -1;
		
		for (int i=0;i<name_size;i++) if (name.charAt(i)=='/') last_slash_index=i;
		if (last_slash_index!=-1) name = name.substring(last_slash_index+1);
		
		name_size = name.toCharArray().length;
		if(name.substring(name_size-4).compareTo(".mid") == 0){
			name = name.substring(0, name_size-4);
		}

		if(name.substring(name_size-5).compareTo(".midi") == 0){
			name = name.substring(0, name_size-5);
		}

		return name;
	}


}
