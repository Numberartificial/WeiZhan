package com.example.yuyin;

import java.util.Date;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class RecordThread extends Thread {
    private AudioRecord ar;
    private int bs;
    private final int SAMPLE_RATE_IN_HZ = 8000;
    private boolean isRun = false;
    private Date dt1;
    private long startTime;
    private Handler mHandler;
    public RecordThread(Handler mHandler) {
            super();
            bs = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
            ar = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, bs);
            dt1 = new Date();
            startTime = dt1.getTime();
            this.mHandler = mHandler;
    }
     
    public void run() {
            super.run();
            ar.startRecording();
            // 用于读取的
            try{
        	
            	sleep(100);
            }catch(Exception e){
            	
            }
            	
           short[] buffer = new short[bs];
            while (isRun) {
                    int r = ar.read(buffer, 0, bs);
                    int v = 0;
                    // 将 buffer 内容取出，进行平方和运算
                    for (int i = 0; i < buffer.length; i++) {
                            // 这里没有做运算的优化，为了更加清晰的展示代码
                            v += buffer[i] * buffer[i];
                    }
                     
                    Date dt2  = new Date();
                    long currentTime = dt2.getTime();
                    
                    if(currentTime - startTime > 3000)
                    {
                    	pause();
                        
                        Message msg2 = new Message();
                        msg2.what = 4;
                        mHandler.sendMessage(msg2);
                    	break;
                    }
                	
                    //value 的 值 控制 为 0 到 100 之间 0为最小 》= 100为最大！！
                    int value = (int) (Math.abs((int)(v /(float)r)/10000) >> 1);
//                    Log.d("111", "v = " + v);
                    // 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
                    // 如果想利用这个数值进行操作，建议用 sendMessage 将其抛出，在 Handler 里进行处理。
//                    Log.d("222", String.valueOf(v / (float) r));
//                     
                    double dB = 10*Math.log10(v/(double)r);
//                    Log.d("333", "dB = " + dB);
                     
                    Message msg = new Message();
                    msg.what = 3;
                    msg.arg1 = value;
                    mHandler.sendMessage(msg);
                     
            }

            ar.stop();
    }

    public void pause() {
            // 在调用本线程的 Activity 的 onPause 里调用，以便 Activity 暂停时释放麦克风
            isRun = false;
    }

    public void start() {
            // 在调用本线程的 Activity 的 onResume 里调用，以便 Activity 恢复后继续获取麦克风输入音量
            if (!isRun) {
                isRun = true;
                    super.start();
            }
    }

}