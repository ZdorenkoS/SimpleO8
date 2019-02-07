package project.view;

import project.Dispatcher;
import project.model.O8;
import project.utils.ConfigProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class View implements Runnable{
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JTextArea o8Done;
    private JTextArea o8Crash;
    private JProgressBar progressBar;
    private final String PAUSE_TEXT = "Остановить";
    private final String START_TEXT = "Запустить";
    private int screenWidth;
    private int screenHeight;
    private StringBuilder stringBuilderOk = new StringBuilder();
    private StringBuilder stringBuilderFail = new StringBuilder();
    private Dispatcher dispatcher;



//TODO отрисовка после создания О8 в ерп...

    public View()  {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        frame = new JFrame("Lazy O8");
        button = new JButton(PAUSE_TEXT);
        panel = new JPanel();
        frame.setContentPane(panel);
        frame.setSize(screenWidth/5, screenHeight/3*2);
        frame.setLocation(screenWidth-screenWidth/5,0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

      @Override
    public void run() {
        panel.setLayout(new GridBagLayout());
        Font panelFont = new Font("Bookman Old Style", Font.PLAIN, 14);
        panel.setFont(panelFont);
        panel.setOpaque(false);

        GridBagConstraints gbc;

        final JPanel spacer = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer, gbc);

        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer1, gbc);

        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer2, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(button, gbc);

        final JLabel label1= new JLabel("До следующей итерации осталось:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(label1, gbc);
        progressBar = new JProgressBar();
        gbc = new GridBagConstraints();
        progressBar.setStringPainted(true);
        int i = Integer.parseInt(ConfigProperties.getProperty("sleepTime"));
        progressBar.setMaximum(i);
        progressBar.setString("инициализация");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar, gbc);



        final JPanel spacer3 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(spacer3, gbc);

        final JLabel label2 = new JLabel("Созданные О8:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(label2, gbc);

        o8Done = new JTextArea();
        o8Done.setColumns(50);
        o8Done.setEditable(false);
        o8Done.setFont(panelFont);
        o8Done.setRows(100);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.8f;
        gbc.gridwidth = 2;
        JScrollPane scrollPane = new JScrollPane(o8Done);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, gbc);

        final JLabel label3 = new JLabel();
        label3.setText("О8 с ошибками:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(label3, gbc);

        o8Crash = new JTextArea();
        o8Crash.setColumns(50);
        o8Crash.setEditable(false);
        o8Crash.setFont(panelFont);
        o8Crash.setLineWrap(false);
        o8Crash.setRows(20);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1;
        gbc.weighty = 0.2f;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane2= new JScrollPane(o8Crash);
        scrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane2, gbc);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatcher.changeThreadState();
                button = (JButton) e.getSource();
                if (dispatcher.isRunning()) {
                    button.setText(PAUSE_TEXT);
                } else {
                    button.setText(START_TEXT);
                }
            }
        });
    }

    public void updateJtextAreas(ArrayList<O8> o8sOk, ArrayList<O8> o8sFail){
        for (O8 o8: o8sOk) {stringBuilderOk.append(o8.o8ForView());}
        for (O8 o8: o8sFail) {stringBuilderFail.append(o8.o8ForView());}
        stringBuilderOk.append("\n");
        o8Done.setText("");
        o8Crash.setText("");
        o8Done.setText(stringBuilderOk.toString());
        o8Crash.setText(stringBuilderFail.toString());
        o8Done.repaint();
        o8Crash.repaint();
       }

    public static class Countdown implements Runnable{
        private int x;
        private View view;
        private JProgressBar progressBar;
        public Countdown(View view) {
            x = Integer.parseInt(ConfigProperties.getProperty("sleepTime"));
            this.view = view;
            progressBar = view.progressBar;
        }

        @Override
        public void run() {
            for (int i = x; i >=0 ; i--) {
                if (view.dispatcher.isRunning()){
                progressBar.setValue(i);
                if (i == 0) progressBar.setString("идет обработка");
                else progressBar.setString(String.valueOf(i));
                progressBar.repaint();
                try{TimeUnit.SECONDS.sleep(1);} catch (InterruptedException ex){}}
                else {
                    progressBar.setValue(0);
                    progressBar.setString("-");
                }
            }
        }
    }
}


