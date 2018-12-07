//package cn.sribd.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizerListener;

import javax.swing.*;

public class TestSpeech {

    private SpeechRecognizer mIat;
    private SynthesizerListener mSynListener;
    private RecognizerListener recognizerListener;
    private SpeechSynthesizer mTts;

    private StringBuffer text;

    private void initSound() {
        // text = new StringBuffer();
        SpeechUtility.createUtility("appid=57f89c28");

        // Speech Synthesizer
        this.mSynListener = new SynthesizerListener() {

            public void onSpeakBegin() {
                text = new StringBuffer();
            }

            public void onBufferProgress(int progress, int beginPos, int endPos, String info) {
            }

            public void onSpeakPaused() {
            }

            public void onSpeakResumed() {
            }

            public void onSpeakProgress(int progress, int beginPos, int endPos) {
            }

            public void onCompleted(SpeechError error) {
                mIat.startListening(recognizerListener);
            }
        };

        // sound recognizer
        this.recognizerListener = new RecognizerListener() {
            @Override
            public void onBeginOfSpeech() {
            }

            @Override
            public void onEndOfSpeech() {
            }

            /**
             * Get recognizer results and display
             */
            @Override
            public void onResult(RecognizerResult results, boolean islast) {
                // receive the results and processing
                try {
                    text.append(parseIatResult(results.getResultString()));
                    if (islast) {
                        System.out.println(text.toString());
                        if (text.toString().indexOf("再见") < 0) {
                            mTts.startSpeaking("您刚才说的是：" + text.toString() + "。请继续聊天，说再见退出本系统。", mSynListener);
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onError(SpeechError error) {
            }

            @Override
            public void onEvent(int eventType, int arg1, int agr2, String msg) {
            }
        };

        this.mTts = SpeechSynthesizer.createSynthesizer();
        this.mIat = SpeechRecognizer.createRecognizer();
    }

    public void testSound() {
        String mText = "欢迎来到香港中文大学，请开始聊天。请说再见退出本系统.";
        mTts.startSpeaking(mText, mSynListener);
    }

    public static void main(String[] args) {
        TestSpeech test = new TestSpeech();
        test.initSound();
        test.testSound();
    }

    public String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return ret.toString();
    }

}
