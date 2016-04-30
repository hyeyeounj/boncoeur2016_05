package kr.ac.snu.boncoeur2016;

import android.annotation.TargetApi;
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
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import kr.ac.snu.boncoeur2016.utils.Define;

/**
 * Created by hyes on 2016. 3. 25..
 */
public class RecordingThread {
    private static final int SAMPLE_RATE = 8000;
    private static final String COMPRESSED_AUDIO_FILE_MIME_TYPE = "audio/mp4a-latm";
    private static final int COMPRESSED_AUDIO_FILE_BIT_RATE = 48000; // 48kbps
    private static final int COMPRESSED_AUDIO_SAMPLE_RATE = 8000;
    SimpleDateFormat timestamp;
    Context context;
    String position;
    private int id;
    private String filePath = null;
    private boolean mShouldContinue, mShouldSave, mNowSaving, mShouldStop;
    private AudioDataReceivedListener mListener;
    private Thread mThread;
    private FileOutputStream mFileStream = null;
    private RecordingActivity ra;

    public RecordingThread(Context context, AudioDataReceivedListener listener, RecordingActivity ra) {
        mListener = listener;
        this.context = context;
        this.ra = ra;
    }

    public boolean isAcquisitioning() {

        return mShouldContinue;
    }

    public boolean isRecording() {

        return mShouldSave || mNowSaving;
    }

    public void startSave() {

        mShouldSave = true;
    }

    public void stopSave() {

        mShouldStop = true;
    }

    public void startAcquisition(int id, String position) {
        if (mThread != null)
            return;

        this.id = id;
        this.position = position;

        mShouldContinue = true;
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                record();
            }
        });
        mThread.start();
    }

    public void stopAcquisition(int id) {

        if (mThread == null)
            return;

        if(id == Define.PAUSE){
            mShouldContinue = false;
            mThread = null;
        }else{
            Log.d("test", "ID" + id);
            Dao dao = new Dao(context);
            RecordItem record = dao.getRcordById(id);

            dao.updateData(position, filePath, record.getName(), id);
            Log.d("test", "ID!!!!!!!!!! "+ id + record.getName() + filePath);
            mShouldContinue = false;
            mThread = null;
        }
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressWarnings("deprecation")
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
//        short[] audioBuffer = new short[bufferSize / 8];
        short[] audioBuffer = new short[SAMPLE_RATE / 15];
        int drawBuffer = SAMPLE_RATE / 60;
        long curTime = 0;
//        short[] audioBuffer = new short[bufferSize * 2 ];

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

/*        BufferedOutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(filePath));
        } catch (FileNotFoundException e) {
            Log.d("test", "File not found for isRecording ", e);
        }*/

        AudioTrack at = null;
        try {
            at = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, AudioTrack.getMinBufferSize(SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT),
//                    AudioTrack.MODE_STATIC);
                    AudioTrack.MODE_STREAM);

//            Log.i("RecordingThread", "minBufferSize : " + AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT));

            at.setStereoVolume(1.0f, 1.0f);
//            if (at.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
            at.play();
//            Log.i( "RecordingThread", "latency : " + (Integer)( ((AudioManager)context.getSystemService(Context.AUDIO_SERVICE) ).getClass().getMethod("getOutputLatency", int.class) ).invoke((AudioManager)context.getSystemService(Context.AUDIO_SERVICE), AudioManager.STREAM_MUSIC));
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaFormat outputFormat;
        MediaMuxer mux = null;
        MediaCodec codec = null;
        ByteBuffer[] codecInputBuffers = null;
        ByteBuffer[] codecOutputBuffers = null;
        MediaCodec.BufferInfo outBuffInfo = null;

        long shortsRead = 0;
        double presentationTimeUs = 0;
        long totalBytesRead = 0;
        int audioTrackIdx = 0;

        while (mShouldContinue || mNowSaving) {

            if (mShouldSave) {

                try {

                    if (at != null)
                        at.setStereoVolume(0f, 0f);
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

                    filePath = new Dao(context).getRcordById(id).getRecordFile(position);
                    if (filePath != null && !filePath.equals((""))) {
                        File f = new File(filePath);
                        if (f.exists())
                            f.delete();
                    }

                    timestamp = new SimpleDateFormat("yyyyMMddHHmmss");
                    filePath = Define.RECORDED_FILEPATH + position + "_" + timestamp.format(new Date()) + "REC.";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        filePath += "mp4";
                    } else {
                        filePath += "aac";
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mux = new MediaMuxer(filePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    } else {
                        mFileStream = new FileOutputStream(new File(filePath));
                    }
                } catch (IOException ioe) {

                    ioe.printStackTrace();
                }
                totalBytesRead = 0;
                presentationTimeUs = 0;
                mShouldSave = false;
                mNowSaving = true;
            }

            int numberOfShort = 0, nRead;
            while (numberOfShort != audioBuffer.length) {
                if (numberOfShort + drawBuffer < audioBuffer.length)
                    nRead = record.read(audioBuffer, numberOfShort, drawBuffer);
                else
                    nRead = record.read(audioBuffer, numberOfShort, audioBuffer.length - numberOfShort);
                mListener.onAudioDataReceived(audioBuffer, numberOfShort, nRead);
//                Log.i("RecordingThread", "Drawing Time : " + (int) (System.currentTimeMillis() - curTime));
                curTime = System.currentTimeMillis();
                numberOfShort += nRead;
                if (mNowSaving)
                    ra.record_btn.setCurrentPercentage(50000.0 * (totalBytesRead + numberOfShort) / (SAMPLE_RATE * Define.SHORT_TIME));
            }
//            Log.i("RecordingThread", "numberOfShort : " + numberOfShort);
            shortsRead += numberOfShort;

            if (numberOfShort != 0) {

                byte bData[] = short2byte(audioBuffer, numberOfShort);
                if (at != null) {
                    at.write(audioBuffer, 0, numberOfShort);
                }
                if (mNowSaving) {

                    if (outBuffInfo != null) {
                        int processed = 0, total = bData.length, size;
                        while (processed != total) {

//                        Log.i( "RecordingThread" , "total : " + processed );
                            int ind = codec.dequeueInputBuffer(0);

//                            if (ind < 0 && ind != -1)
//                                Log.i("xxxxxx", ind + "");

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
                                Log.i("RecordingThread", "totalBytesRead : " + totalBytesRead);
                                totalBytesRead += size;
                                presentationTimeUs = 1000000L * (totalBytesRead / 2) / SAMPLE_RATE;

                                if (processed != total)
                                    ind = codec.dequeueInputBuffer(0);
                            }

                            ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                            if (ind < 0 && ind != -1)
                                Log.i("xxxxxx", ind + "");

                            if (ind == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                                outputFormat = codec.getOutputFormat();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                    audioTrackIdx = mux.addTrack(outputFormat);
                                    mux.start();
                                }
                                ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                            }

                            while (ind >= 0) {

                                try {
                                    ByteBuffer encodedData = codecOutputBuffers[ind];
                                    encodedData.position(outBuffInfo.offset);
                                    encodedData.limit(outBuffInfo.offset + outBuffInfo.size);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                        if (!((outBuffInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && outBuffInfo.size != 0))
                                            mux.writeSampleData(audioTrackIdx, codecOutputBuffers[ind], outBuffInfo);
                                    } else {

                                        int outBitsSize = outBuffInfo.size;
                                        int outPacketSize = outBitsSize + 7;    // 7 is ADTS size
                                        ByteBuffer outBuf = codecOutputBuffers[ind];

                                        outBuf.position(outBuffInfo.offset);
                                        outBuf.limit(outBuffInfo.offset + outBitsSize);
                                        try {
                                            byte[] data = new byte[outPacketSize];  //space for ADTS header included
                                            addADTStoPacket(data, outPacketSize);
                                            outBuf.get(data, 7, outBitsSize);
                                            outBuf.position(outBuffInfo.offset);
                                            mFileStream.write(data, 0, outPacketSize);  //open FileOutputStream beforehand
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        outBuf.clear();
                                    }

                                    codec.releaseOutputBuffer(ind, false);
                                    ind = codec.dequeueOutputBuffer(outBuffInfo, 0);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    if (mNowSaving && !mShouldContinue) {
                        mNowSaving = false;
                        mShouldStop = true;
                    }
                }
            }

            if (mShouldStop) {

                ra.record_btn.setCurrentPercentage(0);
                if (at != null)
                    at.setStereoVolume(1.0f, 1.0f);
                if (outBuffInfo != null) {
                    codec.stop();
                    codec.release();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mux.stop();
                        mux.release();
                    } else {

                        try {

                            mFileStream.flush();
                            mFileStream.close();
                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                }
                mNowSaving = false;
                mShouldStop = false;
            }
        }

        if (at != null)
            at.stop();
        record.stop();
        record.release();


        Log.v("test", String.format("Recording stopped. Samples read: %d", shortsRead));
    }

    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        //39=MediaCodecInfo.CodecProfileLevel.AACObjectELD;
        int freqIdx = 11;  //4 for 44.1KHz, 11 for 8000Hz
        int chanCfg = 1;  //CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}