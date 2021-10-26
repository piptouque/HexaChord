package Model.Music;

public class Note {

	private int _pitch;

	public Note(int pitch) {
		_pitch = pitch;
	}

	public int get_pitch() {
		return _pitch;
	}

	public static String get_name(int pitch) {
		return get_name(pitch, 'E');
	}

	public static String get_name(int pitch, char language) {

		if (language == 'E') {
			switch (pitch % 12) {
			case 0:
				return "C";
			case 1:
				return "C#";
			case 2:
				return "D";
			case 3:
				return "Eb";
			case 4:
				return "E";
			case 5:
				return "F";
			case 6:
				return "F#";
			case 7:
				return "G";
			case 8:
				return "Ab";
			case 9:
				return "A";
			case 10:
				return "Bb";
			case 11:
				return "B";
			default:
				return "??";
			}
		} else {
			switch (pitch % 12) {
			case 0:
				return "Do";
			case 1:
				return "Do#";
			case 2:
				return "R";
			case 3:
				return "Mib";
			case 4:
				return "Mi";
			case 5:
				return "Fa";
			case 6:
				return "Fa#";
			case 7:
				return "Sol";
			case 8:
				return "Lab";
			case 9:
				return "La";
			case 10:
				return "Sib";
			case 11:
				return "Si";
			default:
				return "??";
			}

		}
	}

	public static int get_pitch_class(String note_name) {
		int pc;
		switch (note_name.charAt(0)) {
		case 'N':
			System.err.println("note_name not found");
			return -1;
		case 'C':
			pc = 0;
			break;
		case 'D':
			pc = 2;
			break;
		case 'E':
			pc = 4;
			break;
		case 'F':
			pc = 5;
			break;
		case 'G':
			pc = 7;
			break;
		case 'A':
			pc = 9;
			break;
		case 'B':
			pc = 11;
			break;
		default:
			pc = 0;
			System.err.println("Root pitch class not found");
			System.exit(0);
			break;
		}
		if (note_name.length() > 1) {
			if (note_name.charAt(1) == 'b')
				pc = (pc - 1 + 12) % 12;
			if (note_name.charAt(1) == 's')
				pc = (pc + 1) % 12;
		}
		return pc;
	}

	public static int pitch_class_symmetry(int pitch_class, float pitch_class_center) {
		assert (pitch_class_center % 1 == 0 || pitch_class_center % 1 == (float) 0.5)
				: "pitch class center not conform : " + pitch_class_center;
		float interval = pitch_class_center - pitch_class;
		return (int) (24 + pitch_class + 2 * interval) % 12;
	}

	public static int get_closer_pitch_having_pitch_class(int original_pitch, int pitch_class) {
		int interval = Interval.smaller_distance_interval(pitch_class - original_pitch % 12, 12);

		return original_pitch + interval;
	}

}
