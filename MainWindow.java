import javax.swing.*;
import java.awt.*;

public class MainWindow {

    private JFrame f;
    private JPanel p;
    private JButton startButton;
    private JButton pauseButton;
    private JButton finishButton;


    public MainWindow(){

        gui();

    }

    public void gui()
    {

        f = new JFrame("cn.sribd.si");
        f.setVisible(true);
        f.setSize(600,400);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        p = new JPanel(new GridBagLayout());
        p.setBackground(Color.lightGray);

        startButton = new JButton("▶");
        pauseButton = new JButton("▲");
        finishButton = new JButton("■");


        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10,10,10,10);

        c.gridx = 0;
        c.gridy = 1;

        p.add(startButton,c);

        c.gridx = 0;
        c.gridy = 2;

        p.add(pauseButton,c);

        c.gridx = 1;
        c.gridy = 2;
        p.add(finishButton);

        f.add(p);

    }



    public static void main(String[] args){
        new MainWindow();
    }


    private JButton recordButton;
    private JTextArea SRIBDSITextArea;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JPanel panel1;
}
