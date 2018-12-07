package cn.sribd.si.threads;


import cn.sribd.si.Utils;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import static cn.sribd.si.Utils.outputBuffer;

public class Translator implements Runnable {
    @Override
    public void run() {
        String englishText, chineseText;
        for (; ; ) {
            try {
                synchronized (Utils.sentenceBuffer) {
                    while (Utils.sentenceBuffer.isEmpty()) {
                        Utils.sentenceBuffer.wait();
                    }
                    englishText = Utils.sentenceBuffer.removeFirst();
                }
                chineseText = translate(englishText);
                chineseText = cleanWords(chineseText);
                outputBuffer.append(chineseText);
                synchronized (Utils.speakBuffer) {
                    Utils.speakBuffer.add(chineseText);
                    Utils.speakBuffer.notify();
                }

                if (Utils.isStopped) {
                    break;
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private String translate(String englishText) {
        try {
            HttpResponse<String> response = Unirest.post("http://10.20.253.1:5000/translate/")
                    .header("Content-Type", "application/json")
                    .body("{\"data\": \"" + englishText + "\"}")
                    .asString();
            return response.getBody();
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String cleanWords(String chineseText) {
        if(chineseText == null) return null;
        chineseText = chineseText.replace('.','。');
        chineseText = chineseText.replaceAll("妈的","**");
        return chineseText;
    }
}
