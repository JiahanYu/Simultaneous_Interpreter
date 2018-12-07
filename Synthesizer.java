package cn.sribd.si.threads;

import cn.sribd.si.Utils;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;

public class Synthesizer implements Runnable {

    private SynthesizerListener mSynthesizerListener;
    private SpeechSynthesizer speechSynthesizer;
    private volatile boolean speakCompleted;

    public Synthesizer() {
        this.mSynthesizerListener = new SynthesizerListener() {
            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {
            }

            @Override
            public void onSpeakBegin() {
            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {
            }

            @Override
            public void onSpeakPaused() {
            }

            @Override
            public void onSpeakResumed() {
            }

            @Override
            public void onCompleted(SpeechError speechError) {
                speakCompleted = true;
            }
        };

        this.speechSynthesizer = SpeechSynthesizer.createSynthesizer();

        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "40");//设置语速
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100

        speakCompleted = true;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                String chineseText;
                if (!speakCompleted) {
                    Thread.sleep(10);
                    continue;
                }
                synchronized (Utils.speakBuffer) {
                    while (Utils.speakBuffer.isEmpty()) {
                        Utils.speakBuffer.wait();
                    }
                    chineseText = Utils.speakBuffer.removeFirst();
                }

                if (chineseText != null) {
//                    System.out.println(chineseText);
                    speakCompleted = false;
                    speechSynthesizer.startSpeaking(chineseText, mSynthesizerListener);
                }

                if (Utils.isStopped) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

}
