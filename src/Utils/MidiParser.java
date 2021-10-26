package Utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import Interface.WaitingFrame;
import Main.HexaChord;
import Model.Music.PitchSetStream;
import Model.Music.PitchSetWithDuration;
import Model.Music.OnsetColStream;

public class MidiParser {

	// static ProgressDialog _progress_frame;
	private static Sequence _midi_seq;
	private static ArrayList<Integer> _track_list;
	private static boolean _sustain;
	private static PitchSetStream _stream;
	private static WaitingFrame _waiting_frame;

	public static OnsetColStream onset_stream_generator(Sequence midi_seq) {
		OnsetColStream onset_stream = new OnsetColStream();
		for (int track_number = 0; track_number < midi_seq.getTracks().length; track_number++) {
			Track track = midi_seq.getTracks()[track_number];
			for (int event_number = 0; event_number < track.size(); event_number++) {
				MidiEvent event = track.get(event_number);
				if (event.getMessage() instanceof ShortMessage) {
					ShortMessage short_message = (ShortMessage) event.getMessage();
					if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
						if (onset_stream.containsKey(event.getTick())) {
							onset_stream.get(event.getTick()).add(short_message.getData1());
						} else {
							PitchSetWithDuration collection = new PitchSetWithDuration();
							collection.add(short_message.getData1());
							onset_stream.put(event.getTick(), collection);
						}
					}
				}
			}
		}

		return onset_stream;
	}

	// A utiliser sans interface graphique : pas de bar de progression, fonction
	// bloquante

	public static PitchSetStream stream_generator(Sequence midi_seq, ArrayList<Integer> track_list, boolean sustain) {
		// System.out.println("Voici le nombre de Tracks :
		// "+midi_seq.getTracks().length);
		// System.out.println("voici les tracks : "+track_list);
		ArrayList<TreeMap<Long, Boolean>> sustain_table = build_sustain_table(midi_seq, sustain);

		ArrayList<PitchSetStream> track_stream_list = new ArrayList<PitchSetStream>();

		for (int t = 0; t < midi_seq.getTracks().length; t++) {
			if (track_list.contains(t)) {
				// System.out.println("Track "+t+" size : "+midi_seq.getTracks()[t].size());
				PitchSetStream track_stream = track_to_colStream(midi_seq.getTracks()[t], sustain_table);
				if (!track_stream.isEmpty()) {
					track_stream_list.add(track_stream);
				}
			}
			// System.out.println("prog :
			// "+(100*(float)t/(float)midi_seq.getTracks().length)/(float)2);
		}
		PitchSetStream stream = PitchSetStream.mix_colStream_list(track_stream_list);
		// System.out.println("prog : "+75);
		// System.out.println("PPQ = "+Sequence.PPQ+" Resolution =
		// "+midi_seq.getResolution());
		boolean bool = false;
		while (!bool) {
			if (!stream.try_defragmentation())
				bool = true;
		}

		return stream;

	}

	// identique stream_generator mais :
	// - gnre le stream dans un thread spar grce un SwingWorker. La fonction n'est
	// donc pas bloquante
	// - affiche une bar de progression
	// - ne retourne rien (le stream est renvoy hexachord dans la mthode done()

	public static void stream_generator_thread(Sequence midi_seq, ArrayList<Integer> track_list, boolean sustain,
			int round_tick) {

		_midi_seq = midi_seq;
		_sustain = sustain;
		_track_list = track_list;

		_waiting_frame = new WaitingFrame();
		// On cre le SwingWorker
		SwingWorker<PitchSetStream, Integer> sw = new SwingWorker<PitchSetStream, Integer>() {
			protected PitchSetStream doInBackground() throws Exception {
				// System.out.println("Voici le nombre de Tracks :
				// "+midi_seq.getTracks().length);
				// System.out.println("voici les tracks : "+track_list);
				ArrayList<TreeMap<Long, Boolean>> sustain_table = build_sustain_table(_midi_seq, _sustain);

				ArrayList<PitchSetStream> track_stream_list = new ArrayList<PitchSetStream>();

				for (int t = 0; t < _midi_seq.getTracks().length; t++) {
					if (_track_list.contains(t)) {
						System.out.println("--Track " + t + " size : " + midi_seq.getTracks()[t].size());
						PitchSetStream track_stream = track_to_colStream(_midi_seq.getTracks()[t], sustain_table);
						if (!track_stream.isEmpty()) {
							track_stream_list.add(track_stream);
						}
					}
					setProgress((int) ((100 * (float) t / (float) _midi_seq.getTracks().length) / (float) 2));
				}
				_stream = PitchSetStream.mix_colStream_list(track_stream_list);
				setProgress(75);
				// System.out.println("PPQ = "+Sequence.PPQ+" Resolution =
				// "+midi_seq.getResolution());
				boolean bool = false;
				while (!bool) {
					if (!_stream.try_defragmentation())
						bool = true;
				}
				return _stream;
			}

			public void done() {
				if (SwingUtilities.isEventDispatchThread())
					try {
						HexaChord.getInstance().stream_init(get(), round_tick); // get() fournit le stream renvoy par
																				// doInBackground
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				_waiting_frame.dis();
			}
		};
		sw.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("progress".equals(event.getPropertyName())) {
					_waiting_frame.update_bar((Integer) event.getNewValue());
				}
			}
		});
		sw.execute(); // On lance le SwingWorker

	}

	public static void stream_generator_thread(Sequence midi_seq, boolean sustain, int round_tick) {

		ArrayList<Integer> track_list = new ArrayList<Integer>();
		for (int t = 0; t < midi_seq.getTracks().length; t++) {
			track_list.add(t);
		}
		stream_generator_thread(midi_seq, track_list, sustain, round_tick);
	}

	public static PitchSetStream stream_generator(Sequence midi_seq, boolean sustain) {

		ArrayList<Integer> track_list = new ArrayList<Integer>();
		for (int t = 0; t < midi_seq.getTracks().length; t++) {
			track_list.add(t);
		}
		return stream_generator(midi_seq, track_list, sustain);
	}

	public static PitchSetStream stream_generator_without_track(Sequence midi_seq, int track_to_ignore,
			boolean sustain) {
		ArrayList<Integer> track_list = new ArrayList<Integer>();
		for (int t = 0; t < midi_seq.getTracks().length; t++) {
			if (t != track_to_ignore)
				track_list.add(t);
		}
		return stream_generator(midi_seq, track_list, sustain);
	}

	public static PitchSetStream stream_generator_with_one_track(Sequence midi_seq, int track_to_parse,
			boolean sustain) {
		ArrayList<Integer> track_list = new ArrayList<Integer>();
		for (int t = 0; t < midi_seq.getTracks().length; t++) {
			if (t == track_to_parse)
				track_list.add(t);
		}
		return stream_generator(midi_seq, track_list, sustain);
	}

	public static PitchSetStream track_to_colStream(Track track, ArrayList<TreeMap<Long, Boolean>> sustain_table) {

		HashSet<Long> track_ticks = new HashSet<Long>();
		HashSet<Integer> track_channels = new HashSet<Integer>(); // Ensembles des cannaux sollicits par le fichier Midi
		ArrayList<ArrayList<MidiEvent>> channels = new ArrayList<ArrayList<MidiEvent>>(); // Les 16 listes d'vnement
																							// associes aux 16 canaux
		for (int i = 0; i < 16; i++) {
			channels.add(new ArrayList<MidiEvent>());
		}

		// System.out.println("Track size : "+t.size());
		for (int n = 0; n < track.size(); n++) {
			track_ticks.add(track.get(n).getTick());
			if (track.get(n).getMessage() instanceof ShortMessage) {
				// System.out.println(t.get(n).getTick()+" : MidiEvent :
				// "+MessageInfo.toString(t.get(n).getMessage()));
				ShortMessage short_message = (ShortMessage) track.get(n).getMessage();
				track_channels.add(short_message.getChannel()); // on met jour l'ensemble des cannaux sollicits
				channels.get(short_message.getChannel()).add(track.get(n)); // on rajoute l'vnement dans la liste associ
																			// au canal sollicit
			}
		}
		ArrayList<Long> ticks_list = new ArrayList<Long>(track_ticks);
		Collections.sort(ticks_list);
		// System.out.println("Les track_ticks : "+ticks_list);
		Long last_tick = ticks_list.get(ticks_list.size() - 1);
		ArrayList<Integer> channels_list = new ArrayList<Integer>(track_channels); // Liste des canaux sollicits par la
																					// piste
		Collections.sort(channels_list);
		// System.out.println("Les track_channels : "+channels_list);
		// System.out.println("Les channels : "+channels);

		ArrayList<PitchSetStream> channels_stream_list = new ArrayList<PitchSetStream>();
		for (int ch = 0; ch < channels.size(); ch++) {
			if (channels.get(ch).size() > 0 && ch != 9) { // On ne considre pas les pitchs inclus dans la channel ddie
															// aux percussions
				// System.out.println("Channel "+ch);
				if (last_tick < sustain_table.get(ch).lastKey())
					last_tick = sustain_table.get(ch).lastKey(); // le dernier tick peut tre un sustain
				PitchSetStream channel_stream = midi_messages_to_colStream(channels.get(ch), last_tick,
						sustain_table.get(ch));
				// ColStream channel_stream = midi_messages_to_colStream(channels.get(ch),
				// last_tick, sustain_list);
				if (!channel_stream.isEmpty()) {
					channels_stream_list.add(channel_stream);
				}
			}
		}

		PitchSetStream col_stream = PitchSetStream.mix_colStream_list(channels_stream_list);
		return col_stream;

	}

	public static PitchSetStream track_to_colStream(Track track) {
		return track_to_colStream(track, build_sustain_table(null, false));
	}

	public static PitchSetStream midi_messages_to_colStream(ArrayList<MidiEvent> midi_event_list, long last_tick,
			TreeMap<Long, Boolean> sustain_tree) {
		// for (int n=0;n<list.size();n++){
		// System.out.println(list.get(n).getTick()+"
		// "+MessageInfo.toString(list.get(n).getMessage()));
		// }

		HashSet<Long> channel_ticks_set = new HashSet<Long>();
		long higher = 0;
		for (MidiEvent e : midi_event_list) {
			channel_ticks_set.add(e.getTick());
			if (e.getTick() > higher)
				higher = e.getTick();
		}

		// On injecte aussi tous les ticks de sustain OFF
		boolean bool = true;
		for (Long l : sustain_tree.keySet()) {
			if (bool) {
				if (!sustain_tree.get(l)) {
					channel_ticks_set.add(l);
					if (l > higher)
						bool = false; // on s'arrte lorsqu'il n'y a plus de NOTE ON et NOTE OFF
				}
			}
		}
		HashSet<Integer> sustained_pitches = new HashSet<Integer>();

		ArrayList<Long> channel_all_event_ticks = new ArrayList<Long>(channel_ticks_set);
		Collections.sort(channel_all_event_ticks);
		// System.out.println("Voici la liste des Ticks : "+channel_ticks);
		PitchSetStream col_stream = new PitchSetStream();
		PitchSetWithDuration previous_col = null;
		for (int n = 0; n < channel_all_event_ticks.size(); n++) {
			if (n == 0) { // Premier Event de la squence
				if (channel_all_event_ticks.size() > 1) {
					PitchSetWithDuration collection = new PitchSetWithDuration(
							channel_all_event_ticks.get(1) - channel_all_event_ticks.get(0));
					for (MidiEvent e : midi_event_list) {
						if (e.getTick() == channel_all_event_ticks.get(n)) {
							ShortMessage short_message = (ShortMessage) e.getMessage();
							if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
								collection.add(short_message.getData1());
							}
						}
					}
					col_stream.put(channel_all_event_ticks.get(n), collection);
					previous_col = collection;
				}
				// Autre events que le premier ou le dernier
			} else {
				if (channel_all_event_ticks.get(n) != last_tick) {

					PitchSetWithDuration collection2 = new PitchSetWithDuration(0);
					collection2 = collection2.union(previous_col);
					if (n == channel_all_event_ticks.size() - 1) {
						collection2.set_duration(last_tick - channel_all_event_ticks.get(n));
					} else {
						collection2.set_duration(channel_all_event_ticks.get(n + 1) - channel_all_event_ticks.get(n));
					}

					// 1) Sustain OFF Event ?
					if (sustain_tree.containsKey(channel_all_event_ticks.get(n))) {
						if (!sustain_tree.get(channel_all_event_ticks.get(n))) { // Si cet instant correspond un
																					// relachement de pdale
							for (int pitch : sustained_pitches) {
								collection2 = collection2.remove_pitch(pitch);
							}
							sustained_pitches.clear();
						}
					}

					// 2) NOTE OFF ?
					for (MidiEvent e : midi_event_list) {
						if (e.getTick() == channel_all_event_ticks.get(n)) {
							ShortMessage short_message = (ShortMessage) e.getMessage();
							if (short_message.getCommand() == ShortMessage.NOTE_OFF
									|| (short_message.getCommand() == ShortMessage.NOTE_ON
											&& short_message.getData2() == 0)) {
								if (sustain_tree.headMap(e.getTick(), true).lastEntry().getValue()) {
									sustained_pitches.add(short_message.getData1());
								} else {
									collection2 = collection2.remove_pitch(short_message.getData1());
								}

							}
						}
					}
					// 3) NOTE ON ? Il est ncssaire de faire 2 boucles for la suite sinon les
					// NOTE_OFF annulent immdiatement les NOTES ON
					for (MidiEvent e : midi_event_list) {
						if (e.getTick() == channel_all_event_ticks.get(n)) {
							ShortMessage short_message = (ShortMessage) e.getMessage();
							if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
								collection2.add(short_message.getData1());
							}
						}
					}
					col_stream.put(channel_all_event_ticks.get(n), collection2);
					previous_col = collection2;
				}

			}
		}
		// System.out.println("Voci la col stream de la Channel de taille
		// "+col_stream.size()+" : "+col_stream);
		return col_stream;
	}

	private static ArrayList<TreeMap<Long, Boolean>> build_sustain_table(Sequence midi_seq, boolean sustain) {

		ArrayList<TreeMap<Long, Boolean>> table = new ArrayList<TreeMap<Long, Boolean>>(); // Chaque canal est reprsent
																							// par un TreeMap
		for (int n = 0; n < 16; n++) {
			table.add(new TreeMap<Long, Boolean>());
			table.get(n).put((long) 0, false);
		}
		if (!sustain) { // si on ne prend pas en compte le sustain, la table indique false dans le
						// premier et unique Entry de chacun des canaux
			return table;
		}

		for (int i = 0; i < midi_seq.getTracks().length; i++) {
			Track t = midi_seq.getTracks()[i];
			for (int n = 0; n < t.size(); n++) {
				MidiMessage message = t.get(n).getMessage();
				if (message instanceof ShortMessage
						&& ((ShortMessage) message).getCommand() == ShortMessage.CONTROL_CHANGE
						&& ((ShortMessage) message).getData1() == 64) {
					// System.out.println(t.get(n).getTick()+" : MidiEvent :
					// "+MessageInfo.toString(t.get(n).getMessage()));
					table.get(((ShortMessage) message).getChannel()).put(t.get(n).getTick(),
							((ShortMessage) message).getData2() > 63 ? true : false);
				}
			}
		}
		return table;
	}

	public static Sequence getSequenceFromFile(File file) {
		Sequence seq = null;
		// System.out.println("file : "+file);
		try {
			seq = MidiSystem.getSequence(file);
		} catch (InvalidMidiDataException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error : MIDI file " + file + " doesn't exists");
			System.exit(1);
			e.printStackTrace();
		}
		return seq;

	}

	// returned values : pitch if NOTE_ON message, -1 otherwise.
	public static List<Integer> get_pitch_event_list_from_track(Track track) {
		List<Integer> pitch_list = new ArrayList<Integer>(track.size());
		int pitch;
		for (int i = 0; i < track.size(); i++) {
			MidiEvent midi_event = track.get(i);
			pitch = -1;
			if (midi_event.getMessage() instanceof ShortMessage) {
				ShortMessage short_message = (ShortMessage) midi_event.getMessage();
				if (short_message.getCommand() == ShortMessage.NOTE_ON && short_message.getData2() != 0) {
					pitch = short_message.getData1();
				}
			}
			pitch_list.add(pitch);
		}
		return pitch_list;
	}

	// returned values : pitch if NOTE_ON message, -1 otherwise.
	public static List<Integer> get_NOTE_ON_pitch_list_from_track(Track track) {
		List<Integer> pitch_event_sequence = get_pitch_event_list_from_track(track);
		List<Integer> NOTE_ON_pitch_sequence = new ArrayList<Integer>();
		for (int p : pitch_event_sequence)
			if (p != -1)
				NOTE_ON_pitch_sequence.add(p);
		return NOTE_ON_pitch_sequence;
	}

	public static int get_time_sign_numerator(Sequence sequence) {
		int numerator = 0;
		for (int i = 0; i < sequence.getTracks().length; i++) {
			Track track = sequence.getTracks()[i];
			for (int j = 0; j < track.size(); j++) {
				MidiMessage midi_message = track.get(j).getMessage();
				if (midi_message instanceof MetaMessage) {
					MetaMessage meta_message = (MetaMessage) midi_message;
					if (meta_message.getType() == 0x58) {
						byte[] data = meta_message.getData();
						numerator = data[0];
						// System.out.println("meta message type : "+meta_message.getType()+" data :
						// "+Table.toString(data));
					}
				}
			}
		}
		return numerator;
	}

	public static MidiMessage get_onset_message(Track track, Long tick) {

		for (int i = 0; i < track.size(); i++) {
			if (track.get(i).getTick() == tick && track.get(i).getMessage() instanceof ShortMessage
					&& ((ShortMessage) track.get(i).getMessage()).getCommand() == ShortMessage.NOTE_ON) {
				return track.get(i).getMessage();
			}
		}

		return null;
	}

}
