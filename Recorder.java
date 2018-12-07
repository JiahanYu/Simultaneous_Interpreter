package cn.sribd.si.threads;

import cn.sribd.si.Utils;
import com.iflytek.cloud.speech.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static cn.sribd.si.Utils.inputBuffer;

public class Recorder implements Runnable {
    private SpeechRecognizer recognizer;

    private RecognizerListener recognizerListener;

    private long lastTime;

    private StringBuilder stringBuffer;


    public Recorder() {

        this.recognizerListener = new RecognizerListener() {
            @Override
            public void onBeginOfSpeech() {
            }

            @Override
            public void onEndOfSpeech() {
            }


            @Override
            public void onResult(RecognizerResult results, boolean islast) {
//                 System.out.println(islast);

                if (!results.getResultString().isEmpty()) {
                    String result = parseIatResult(results.getResultString());
                    stringBuffer.append(result);
                    inputBuffer.append(cleanWords(result));
                    //        System.out.println(result);

                    if (System.currentTimeMillis() - lastTime > 13000 || stringBuffer.length() > 5 * 30) {

//                        System.out.println(System.currentTimeMillis() - lastTime);
//                        System.out.println(stringBuffer.length());
//
//                        System.out.println(stringBuffer);

                        synchronized (Utils.sentenceBuffer) {
                            Utils.sentenceBuffer.add(stringBuffer.toString());
                            Utils.sentenceBuffer.notify();
                        }
                        stringBuffer = new StringBuilder();
                        lastTime = System.currentTimeMillis();
                    }
                }
                if (islast) {
                    if (Utils.isStopped || Utils.stopRecorder) {
                        if (stringBuffer.length() > 0) {
                            synchronized (Utils.sentenceBuffer) {
                                Utils.sentenceBuffer.add(stringBuffer.toString());
                                Utils.sentenceBuffer.notify();
                            }
                        }
                    } else {
                        recognizer.startListening(recognizerListener);
                    }
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onError(SpeechError error) {
                error.printStackTrace();
                recognizer.startListening(recognizerListener);
            }

            @Override
            public void onEvent(int eventType, int arg1, int agr2, String msg) {
            }
        };

        this.recognizer = SpeechRecognizer.createRecognizer();

        stringBuffer = new StringBuilder();


        recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        recognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
        recognizer.setParameter(SpeechConstant.VAD_EOS, "800");
    }

    private String cleanWords(String result) {
        if(result == null) return null;
        result = result.replaceAll("\\.", ". ");
        result = result.replaceAll("fuck","f**k");
        return result;
    }

    @Override
    public void run() {
        System.out.println("Start speaking ");
        lastTime = System.currentTimeMillis();
        recognizer.startListening(recognizerListener);
    }

    private String parseIatResult(String json) {
        StringBuilder ret = new StringBuilder();
        try {
            JSONObject joResult = new JSONObject(json);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return ret.toString();
    }

}
