import javax.sound.sampled.*;

public class Player {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static boolean BIG_ENDIAN = false;
	public final static int BUFFER_CHUNK_SIZE = FRAME_RATE / 100; //move these buffer things to freq or chord player class
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;
	private SourceDataLine sourceDataLine = null;
	private TrackPlayer trackPlayer = null;
	private Messages messages = Messages.getInstance();

	public static AudioFormat getAudioFormat() {
		AudioFormat af = new AudioFormat(ENCODING, SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, FRAME_SIZE, FRAME_RATE, BIG_ENDIAN);
		return af;
	}

	public Player() throws InitFailedPlayerException {
		try {
			AudioFormat af = getAudioFormat();
			DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
		    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
		    sourceDataLine.open(af, BUFFER_SIZE);
		} catch(LineUnavailableException ex) {
			throw new InitFailedPlayerException(messages.getMessage("dataLineNotAvailable"));
		}
	}

	public void play(final Track track) throws IllegalActionPlayerException {
		if(isPlaying()) {
			throw new IllegalActionPlayerException(messages.getMessage("playbackAlreadyStarted"));
		}
		trackPlayer = new TrackPlayer(sourceDataLine, track);
		Thread trackPlayerThread = new Thread(trackPlayer);
		trackPlayerThread.start();
	}

	public void stop() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException(messages.getMessage("playbackAlreadyStopped"));
		}
		trackPlayer.stop();
	}

	public double getTrackDuration() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException(messages.getMessage("playerIsNotActive"));
		}
		return trackPlayer.getTrackDuration();
	}

	public double getTrackPosition() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException(messages.getMessage("playerIsNotActive"));
		}
		return trackPlayer.getTrackPosition();
	}

	public Chord getCurrentChord() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException(messages.getMessage("playerIsNotActive"));
		}
		return trackPlayer.getCurrentChord();
	}

	public boolean isPlaying() {
		return (sourceDataLine.isActive());
	}
}