package Utils;

import javax.sound.midi.MidiChannel;

/**
 * Stores MidiChannel information.
 */

public class ChannelData {

	    /**
		 * @uml.property  name="channel"
		 * @uml.associationEnd  multiplicity="(1 1)"
		 */
	    public MidiChannel channel;
	    /**
		 * @uml.property  name="solo"
		 */
	    public boolean solo;
		/**
		 * @uml.property  name="mono"
		 */
		public boolean mono;
		/**
		 * @uml.property  name="mute"
		 */
		public boolean mute;
		/**
		 * @uml.property  name="sustain"
		 */
		public boolean sustain;
	    /**
		 * @uml.property  name="velocity"
		 */
	    public int velocity;
		/**
		 * @uml.property  name="pressure"
		 */
		public int pressure;
		/**
		 * @uml.property  name="bend"
		 */
		public int bend;
		/**
		 * @uml.property  name="reverb"
		 */
		public int reverb;
	    /**
		 * @uml.property  name="row"
		 */
	    public int row;
		/**
		 * @uml.property  name="col"
		 */
		public int col;
		/**
		 * @uml.property  name="num"
		 */
		public int num;

	    public ChannelData(MidiChannel channel, int num) {
	        this.channel = channel;
	        this.num = num;
	        velocity = pressure = bend = reverb = 64;
	    }
}
