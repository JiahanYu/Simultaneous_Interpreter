package cn.sribd.si;

import java.util.LinkedList;

public class Utils {
    public static final LinkedList<String> sentenceBuffer = new LinkedList<>();
    public static final LinkedList<String> speakBuffer = new LinkedList<>();
    public static volatile boolean isStopped = false;
    public static volatile boolean stopRecorder = false;
    public static volatile StringBuilder inputBuffer = new StringBuilder();
    public static volatile StringBuilder outputBuffer = new StringBuilder();
}
