package cn.sribd.si;

import cn.sribd.si.threads.Recorder;
import cn.sribd.si.threads.Synthesizer;
import cn.sribd.si.threads.Translator;
import com.iflytek.cloud.speech.SpeechUtility;

public class InterpreterCLI {


    public static void main(String[] args) {
        SpeechUtility.createUtility("appid=57f89c28");

        Thread recorder = new Thread(new Recorder());
        Thread translator = new Thread(new Translator());
        Thread synthesizer = new Thread(new Synthesizer());

        recorder.start();
        translator.start();
        synthesizer.start();

        try {
            recorder.join();
            translator.join();
            synthesizer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
