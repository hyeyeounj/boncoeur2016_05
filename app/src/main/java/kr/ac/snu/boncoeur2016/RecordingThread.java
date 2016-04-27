package kr.ac.snu.boncoeur2016;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Exchanger;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 25..
 */
public class RecordingThread {
    private static final int SAMPLE_RATE = 8000;
    private static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    private static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 48000; // 128kbps
    private static final int COMPRESSED_AUDIO_SAMPLE_RATE = 8000;
    SimpleDateFormat timestamp;
    Context context;
    private String fileName = null;
    private boolean mShouldContinue;
    private AudioDataReceivedListener mListener;
    private Thread mThread;
    public RecordingThread(Context context, AudioDataReceivedListener listener) {
        mListener = listener;
        this.context = context;
    }

    public boolean recording() {
        return mThread != null;
    }

    public void startRecording() {
        if (mThread != null)
            return;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopRecording() {
        if (mThread == null)
            return;

        mShouldContinue = false;
        mThread = null;

        //fileName = "aaa"+timestamp.format(new Date()).toString() + "REC.mp4";

    }

    private byte[] short2byte(short[] sData, int len) {
        byte[] bytes = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
//            sData[i] = 0;
        }
        return bytes;

    }

    private void record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

        // buffer size in bytes
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2;
        }

//        byte audioData[] = new byte[bufferSize];
        short[] audioBuffer = new short[bufferSize * 1 / 4];

        AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize);

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("test", "Audio Record can't initialize!");
            return;
        }
        record.startRecording();

        timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
        String filePath = Define.RECORDED_FILEPATH + "_" + timestamp.format(new Date()).toString() + "REC.mp4";
        //사용할 수 없는 파일 형식 ;; 확인

/*        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            Log.d("test", "File not found for recording ", e);
        }*/

        AudioTrack at = null;
        try {
            at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize * 4,
//                    AudioTrack.MODE_STATIC);
                    AudioTrack.MODE_STREAM);
            at.setStereoVolume(1.0f, 1.0f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaMuxer mux = null;
        MediaFormat outputFormat = null;
        MediaCodec codec = null;
        ByteBuffer[] codecInputBuffers = null;
        ByteBuffer[] codecOutputBuffers = null;
        MediaCodec.BufferInfo outBuffInfo = null;
        try {

            mux = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            outputFormat = MediaFormat.createAudioFormat(COMPRESSED_AUDIO_FILE_MIME_TYPE,
                    COMPRESSED_AUDIO_SAMPLE_RATE, 1);
            outputFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            outputFormat.setInteger(MediaFormat.KEY_BIT_RATE, COMPRESSED_AUDIO_FILE_BIT_RATE);
            outputFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

            codec = MediaCodec.createEncoderByType(COMPRESSED_AUDIO_FILE_MIME_TYPE);
            codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            codec.start();

            codecInputBuffers = codec.getInputBuffers(); // Note: Array of buffers
            codecOutputBuffers = codec.getOutputBuffers();

            outBuffInfo = new MediaCodec.BufferInfo();
        } catch (IOException ioe) {

            ioe.printStackTrace();
        }

        long shortsRead = 0;
        double presentationTimeUs = 0;
        long totalBytesRead = 0;
        int audioTrackIdx = 0;

        while (mShouldContinue) {
//            int read = 0;
//            while( audioBuffer.length - read != 0 ) {
//            int numberOfShort = record.read(audioBuffer, read, audioBuffer.length - read) ;
            int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
            mListener.onAudioDataReceived(Arrays.copyOfRange(audioBuffer, 0, numberOfShort));
            shortsRead += numberOfShort;
//                read += numberOfShort;
//            }
            // Notify waveform
            if (numberOfShort != 0) {

                byte bData[] = short2byte(audioBuffer, (int) numberOfShort);
                if (at != null) {
                    at.write(Arrays.copyOfRange(audioBuffer, 0, numberOfShort), 0, numberOfShort);
                    if (at.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
                        at.play();
                }
                if (outBuffInfo != null) {
                    int processed = 0, total = bData.length, size = 0;
                    while (processed != total) {

//                        Log.i( "RecordingThread" , "total : " + processed );
                        int ind = codec.dequeueInputBuffer(0);

                        if (ind < 0 && ind != -1)
                            Log.i("xxxxxx", ind + "");

                        while (ind >= 0 && processed != total) {

                            ByteBuffer dstBuf = codecInputBuffers[ind];
                            dstBuf.clear();
                            if (dstBuf.limit() > total - processed) {
                                dstBuf.put(bData, processed, total - processed);
                                size = total - processed;
                            } else {

                                dstBuf.put(bData, processed, dstBuf.limit());
                                size = dstBuf.limit();
                            }
                            processed += size;
                            codec.queueInputBuffer(ind, 0, size, (long) presentationTimeUs, 0);
                            totalBytesRead += size;
                            presentationTimeUs = 1000000l * (totalBytesRead / 2) / SAMPLE_RATE;

                            if (processed != total)
                                ind = codec.dequeueInputBuffer(0);
                        }

                        ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                        if (ind < 0 && ind != -1)
                            Log.i("xxxxxx", ind + "");

                        if (ind == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                            outputFormat = codec.getOutputFormat();
                            audioTrackIdx = mux.addTrack(outputFormat);
                            mux.start();
                            ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                        }

                        while (ind >= 0) {

                            try {
                                ByteBuffer encodedData = codecOutputBuffers[ind];
                                encodedData.position(outBuffInfo.offset);
                                encodedData.limit(outBuffInfo.offset + outBuffInfo.size);

                                if ((outBuffInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && outBuffInfo.size != 0) {
                                    codec.releaseOutputBuffer(ind, false);
                                } else {
                                    mux.writeSampleData(audioTrackIdx, codecOutputBuffers[ind], outBuffInfo);
                                    codec.releaseOutputBuffer(ind, false);
                                }

                                ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
/*                try {
                    os.write(bData, 0, audioBuffer.length * 2);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                catch( Exception e ){
                    e.printStackTrace();
                }*/
            }
        }


/*        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        if (outBuffInfo != null) {
            codec.stop();
            codec.release();
            mux.stop();
            mux.release();
        }
        if (at != null)
            at.stop();
        record.stop();
        record.release();


        Log.v("test", String.format("Recording stopped. Samples read: %d", shortsRead));
    }


}
