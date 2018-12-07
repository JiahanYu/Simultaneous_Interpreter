package cn.sribd.si;


import cn.sribd.si.threads.Recorder;
import cn.sribd.si.threads.Synthesizer;
import cn.sribd.si.threads.Translator;
import com.iflytek.cloud.speech.SpeechUtility;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class SimultaneousInterpreterGUI extends JPanel {

    private Thread recorder;
    private Thread translator;
    private Thread synthesizer;
    private JTextArea inputText;
    private JTextArea outputText;

    private SimultaneousInterpreterGUI() {
        super(new BorderLayout());
//        JTabbedPane tabbedPane = new JTabbedPane();
        setPreferredSize(new Dimension(1200, 400));
        JButton startButton;
        JButton pauseButton;
        JButton finishButton;

        startButton = new JButton("►");
        pauseButton = new JButton("❙❙");
        finishButton = new JButton("◼");


        JPanel labelAndComponent = new JPanel();
        JPanel buttonColumn = new JPanel();
        //Use default FlowLayout.
        buttonColumn.setLayout(new BoxLayout(buttonColumn, BoxLayout.Y_AXIS));
        buttonColumn.add(startButton);
        buttonColumn.add(pauseButton);
        buttonColumn.add(finishButton);
        labelAndComponent.add(buttonColumn);

        labelAndComponent.add(createLabelAndComponent(false));
        labelAndComponent.add(createLabelAndComponent(true));
//        tabbedPane.addTab("Welcome", labelAndComponent);


        add(labelAndComponent, BorderLayout.CENTER);

        pauseButton.setEnabled(false);
        finishButton.setEnabled(false);

        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.isStopped = false;
                Utils.stopRecorder = false;
                recorder = new Thread(new Recorder());
                if (translator == null) {
                    translator = new Thread(new Translator());
                    translator.start();
                }
                if (synthesizer == null) {
                    synthesizer = new Thread(new Synthesizer());
                    synthesizer.start();
                }
                recorder.start();
                pauseButton.setEnabled(true);
                finishButton.setEnabled(true);
                startButton.setEnabled(false);

                super.mouseClicked(e);
            }
        });


        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.stopRecorder = true;
                try {
                    recorder.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                recorder = null;
                pauseButton.setEnabled(false);
                finishButton.setEnabled(true);
                startButton.setEnabled(true);

                super.mouseClicked(e);
            }
        });


        finishButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Utils.isStopped = true;
                try {
                    if (recorder != null) {
                        recorder.join();
                    }
                    translator.interrupt();
                    synthesizer.interrupt();
                    translator.join();
                    synthesizer.join();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                recorder = null;
                translator = null;
                synthesizer = null;
                pauseButton.setEnabled(false);
                finishButton.setEnabled(false);
                startButton.setEnabled(true);

                super.mouseClicked(e);
            }
        });

    }

    private JPanel createLabelAndComponent(boolean doItRight) {
        JPanel pane = new JPanel();

        JComponent component = new JPanel();
        Dimension size = new Dimension(500, 300);
        component.setMaximumSize(size);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        TitledBorder border = new TitledBorder(
                new LineBorder(Color.black));
        component.setBorder(border);

        JTextArea text = new JTextArea(12, 28);

        text.setBackground(Color.white);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setAutoscrolls(true);
        text.setFont(text.getFont().deriveFont(Font.BOLD).deriveFont(18f));

        JScrollPane jScrollPane = new JScrollPane(text);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        component.add(jScrollPane);

        String title;
        if (doItRight) {
            title = "Chinese";
            outputText = text;
        } else {
            title = "English";
            inputText = text;
        }

        pane.setBorder(BorderFactory.createTitledBorder(title));
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
        pane.add(component);
        return pane;
    }


    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Simultaneous Interpreting");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SimultaneousInterpreterGUI newContentPane = new SimultaneousInterpreterGUI();
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        ActionListener taskPerformer = evt -> {
            if (Utils.inputBuffer.length() > newContentPane.inputText.getText().length()) {
                newContentPane.inputText.setText(Utils.inputBuffer.toString());
            }
            if (Utils.outputBuffer.length() > newContentPane.outputText.getText().length()) {
                newContentPane.outputText.setText(Utils.outputBuffer.toString());
            }
        };
        new Timer(100, taskPerformer).start();
    }

    public static void main(String[] args) {
        SpeechUtility.createUtility("appid=5a694959");

        javax.swing.SwingUtilities.invokeLater(SimultaneousInterpreterGUI::createAndShowGUI);

    }


}
