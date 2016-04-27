package kr.ac.snu.boncoeur2016.utils;

/**
 * Created by hyes on 2016. 3. 25..
 */
public class AudioUtils {
    public static int calculateAudioLength(int samplesCount, int sampleRate, int channelCount) {
        return ((samplesCount / channelCount) * 1000) / sampleRate;
    }
}
