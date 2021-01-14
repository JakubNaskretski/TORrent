package com.tracker;

import javax.sound.midi.Track;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrackerView {

    private int screenHeight, screenWidth;

    private JFrame frame;
    private JPanel mainPanel;

    private JLabel textJLabel, ipLabel, portLabel;
    JButton terminateTrackerButton, rerunTruckerButton;

    Tracker tracker;

    public TrackerView() {


        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        screenHeight = (int) screenSize.getHeight();
        screenWidth = (int) screenSize.getWidth();

//      Creating frame
        frame = new JFrame();
        addComponentsToPane();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Tracker");
        frame.setLocationRelativeTo(null);
        frame.setLocation(((screenWidth / 2) - (screenWidth / 4)), ((screenHeight / 2) - (screenHeight / 4)));


//      Display frame
        frame.pack();
        frame.setVisible(true);

    }


    private void addComponentsToPane() {
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        textJLabel = new JLabel("Tracker is working");
//        textJLabel.setFont(new Font("serif", Font.BOLD, 35));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(textJLabel, c);


        ipLabel = new JLabel("Ip: ");
//        ipLabel.setFont(new Font("serif", Font.BOLD, 35));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(ipLabel, c);

        portLabel = new JLabel("Port: ");
//        portLabel.setFont(new Font("serif", Font.BOLD, 35));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(portLabel, c);
//
//        terminateTrackerButton = new JButton("Terminate tracker");
//        terminateTrackerButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                tracker.terminate();
//                textJLabel.setText("Tracker terminated");
//            }
//        });
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.gridwidth = 1;
//        c.gridx = 0;
//        c.gridy = 3;
//        mainPanel.add(terminateTrackerButton, c);
//
//        rerunTruckerButton = new JButton("Rerun tracker");
//        rerunTruckerButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                tracker = new Tracker(10000);
//                textJLabel.setText("Tracker is working");
//            }
//        });
//        c.fill = GridBagConstraints.HORIZONTAL;
//        c.gridwidth = 1;
//        c.gridx = 1;
//        c.gridy = 3;
//        mainPanel.add(rerunTruckerButton, c);

        frame.add(mainPanel);
    }


    public JLabel getTextJLabel() {
        return textJLabel;
    }

    public JLabel getIpLabel() {
        return ipLabel;
    }

    public JLabel getPortLabel() {
        return portLabel;
    }

    public JButton getTerminateTrackerButton() {
        return terminateTrackerButton;
    }

    public JButton getRerunTruckerButton() {
        return rerunTruckerButton;
    }

}
