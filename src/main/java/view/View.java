package view;

import controller.Controller;
import controller.Task;
import model.O8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class View implements Runnable{
    private Task task;
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JLabel labelOk;
    private JLabel labelFail;
    private JTextArea o8Done;
    private JTextArea o8Crash;
    private JProgressBar progressBar1;
    private final String PAUSE_TEXT = "Остановить";
    private final String START_TEXT = "Запустить";
    private int screenWidth;
    private int screenHeight;
    private Controller controller;

    public View(Task task) throws HeadlessException {
        this.task = task;
        controller = task.getController();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
        frame = new JFrame("Lazy O8");
        button = new JButton(PAUSE_TEXT);
        panel = new JPanel();
        frame.setContentPane(panel);
        frame.setSize(screenWidth/4, screenHeight/3*2);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    @Override
    public void run() {
        panel.setLayout(new GridBagLayout());
        Font panelFont = new Font("Bookman Old Style", Font.PLAIN, 18);
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
        panel.add(spacer1, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(button, gbc);

        labelOk = new JLabel("До следующей итерации осталось:");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(labelOk, gbc);

        progressBar1 = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar1, gbc);

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
        o8Done.setText(o8ToString(controller.getO8s()));
        o8Done.setRows(100);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0.8f;
        gbc.gridwidth = 2;
        panel.add(o8Done, gbc);

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
        o8Done.setText(o8ToString(controller.getO8sFail()));
        o8Crash.setLineWrap(false);
        o8Crash.setRows(20);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 1;
        gbc.weighty = 0.2f;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(o8Crash, gbc);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                task.changeThreadState();
                button = (JButton) e.getSource();
                if (task.isRunning()) {
                    button.setText(PAUSE_TEXT);
                } else {
                    button.setText(START_TEXT);
                }
            }
        });
    }

    private String o8ToString(ArrayList<O8> list){
        StringBuilder stringBuilder = new StringBuilder();
        for (O8 o8: list) {
            stringBuilder.append(o8.getSupplier()+ "  " + o8.getSumm()+ "\n");
        }
        return stringBuilder.toString();
    }
}

