package com.client.view;

import com.SeederModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ClientView {

    private int screenHeight, screenWidth;

    private JFrame frame;
    private JPanel mainPanel, topInformationPanel, rightFilesPanel, leftSeedersPanel, buttonsPanel, progressBarPanel,
    hostInformationJPanel;
    private JScrollPane leftSeedersScrollPanel, rightFilesScrollPanel;

    private Dimension mainFrameDimension,seederJPanelSize, fileJPanelSize;
    private int mainFrameWidth, mainFrameHeight;

    private JLabel appNumberLabel, yourAppNumberLabel, yourIpLabel, ipLabel, yourPortLabel, portLabel;
    private JButton sendFileButton, downloadFileButton;

    private ArrayList<JPanel> seedersJPanelList, filesList;
    private ArrayList<SeederModel> seeders;


    public ClientView() {

//      Getting screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.screenHeight = (int) screenSize.getHeight();
        this.screenWidth = (int) screenSize.getWidth();

//      Create JPanels
        this.seedersJPanelList = new ArrayList<JPanel>();
        this.filesList = new ArrayList<JPanel>();

//      Create frame
        this.frame = new JFrame();
        mainFrameWidth = screenWidth / 5;
        mainFrameHeight = screenHeight / 3;

        this.mainFrameDimension = new Dimension(mainFrameWidth, mainFrameHeight);
        frame.setSize(mainFrameDimension);
        frame.setLocation(((screenWidth / 2) - (screenWidth / 4)), ((screenHeight / 2) - (screenHeight / 4)));
        frame.setLocationRelativeTo(null);

//      Create size of seedersJPanels
//        this.tasksBlockDimensions = new Dimension(frame.getWidth() / 2, (frame.getHeight() / 10)*4);
//        this.blockDimensions = new Dimension((int) (tasksBlockDimensions.getWidth()*0.8), ((int) (frame.getHeight() / 10)));

//      Adds components to the panel
        addComponentsToThePanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("TORrent");

//      Display Frame
        frame.pack();
        frame.setVisible(true);
    }


//TODO: are all gridbagsconstrains needed ?
    private void addComponentsToThePanel() {

//      Creating main panel
        mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


//      Creating host and port information panel
        topInformationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints topC = new GridBagConstraints();

//      Add top panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(topInformationPanel, c);


//      Creating files panel
        rightFilesPanel = new JPanel(new GridBagLayout());
        rightFilesPanel.setLayout(new BoxLayout(rightFilesPanel, BoxLayout.Y_AXIS));
        rightFilesPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/3));
        rightFilesScrollPanel = new JScrollPane(rightFilesPanel);
//      Set scrolling unit
//        rightFilesScrollPanel.getVerticalScrollBar().setUnitIncrement((int) blockDimensions.getHeight());
        rightFilesScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

//      Add right panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 1;
        mainPanel.add(rightFilesScrollPanel, c);


//      Creating seeders panel
        leftSeedersPanel = new JPanel(new GridBagLayout());
        leftSeedersPanel.setLayout(new BoxLayout(leftSeedersPanel, BoxLayout.Y_AXIS));
        leftSeedersPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/3));
        leftSeedersScrollPanel = new JScrollPane(leftSeedersPanel);
//      Set scrolling unit
//        leftSeedersScrollPanel.getVerticalScrollBar().setUnitIncrement((int) blockDimensions.getHeight());
        leftSeedersScrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

//      Add left panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(leftSeedersScrollPanel, c);


//      Creating buttons panel
        buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsC = new GridBagConstraints();

//      Add buttons panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(buttonsPanel, c);


//      Creating progress bar panel
        progressBarPanel = new JPanel(new GridBagLayout());
        progressBarPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));

//      Add progress bar panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(progressBarPanel, c);


//      Setting information labels for information panel
        this.yourAppNumberLabel = new JLabel("app no-");
//        yourAppNumberLabel.setFont(new Font("serif", Font.BOLD, yourAppNumberLabel.getFont().getSize()));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 0;
        topC.gridy = 0;
        topInformationPanel.add(yourAppNumberLabel, topC);

//      Setting information labels for information panel
        this.appNumberLabel = new JLabel("");
//        yourIpLabel.setFont(new Font("serif", Font.BOLD, 25));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 1;
        topC.gridy = 0;
        topInformationPanel.add(appNumberLabel, topC);

//      Setting information labels for information panel
        this.yourIpLabel = new JLabel("  ip-");
//        yourIpLabel.setFont(new Font("serif", Font.BOLD, yourIpLabel.getFont().getSize()));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 2;
        topC.gridy = 0;
        topInformationPanel.add(yourIpLabel, topC);

        this.ipLabel = new JLabel("000.000.000.000");
//        ipLabel.setFont(new Font("serif", Font.BOLD, 25));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 3;
        topC.gridy = 0;
        topInformationPanel.add(ipLabel, topC);


        this.yourPortLabel = new JLabel("  port-");
//        yourPortLabel.setFont(new Font("serif", Font.BOLD, yourPortLabel.getFont().getSize()));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 4;
        topC.gridy = 0;
        topInformationPanel.add(yourPortLabel, topC);


        this.portLabel = new JLabel("44445");
//        yourPortLabel.setFont(new Font("serif", Font.BOLD, 25));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 5;
        topC.gridy = 0;
        topInformationPanel.add(portLabel, topC);


//      Setting information buttons for buttons panel
        this.sendFileButton = new JButton("Send file");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
        sendFileButton.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
//        c.insets = new Insets(5, 15, 10, 15);
        buttonsC.gridwidth = 2;
        buttonsC.gridx = 2;
        buttonsC.gridy = 0;
        buttonsPanel.add(sendFileButton, buttonsC);

        this.downloadFileButton = new JButton("Download file");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
        downloadFileButton.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
//        c.insets = new Insets(5, 15, 10, 15);
        buttonsC.gridwidth = 2;
        buttonsC.gridx = 0;
        buttonsC.gridy = 0;
        buttonsPanel.add(downloadFileButton, buttonsC);


//      Adds main panel to the frame
        frame.add(mainPanel);
    }

    public void repaintFrame(){
        frame.revalidate();
        frame.repaint();
    }

//
//    // Repaints container with ToDOTasks
//    public void revaluateToDoList(){
//        tasksPanel.removeAll();
//        for (JPanel taskJPanel : tasksToDoJPanelsList) {
//            taskJPanel.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createRaisedBevelBorder(), "",
//                    TitledBorder.CENTER,
//                    TitledBorder.TOP));
//            tasksPanel.add(taskJPanel);
//            taskJPanel.setPreferredSize(blockDimensions);
//        }
//
//        frame.revalidate();
//        frame.repaint();
//
//    }
//
//    // Repaints container with DoneTasks
//    public void revaluateDoneList(){
//        doneTasksPanel.removeAll();
//        for (JPanel doneTaskInList : tasksDoneJPanelsList) {
//            doneTaskInList.setBorder(BorderFactory.createTitledBorder(
//                    BorderFactory.createRaisedBevelBorder(), "",
//                    TitledBorder.CENTER,
//                    TitledBorder.TOP));
//            doneTasksPanel.add(doneTaskInList);
//            doneTaskInList.setPreferredSize(blockDimensions);
//        }
//
//        frame.revalidate();
//        frame.repaint();

//  Adds information about seeders to the list from which later will be displayed seeders list
    public void createSeedersJPanelsArray() {

//      sorts seeders models list
        Collections.sort(seeders, SeederModel.AppNoComparator);
        for (SeederModel seeder : seeders) {
            seedersJPanelList.add(createSeederJPanel(seeder));
        }
    }

//  Creates separate JPanel for given seeder model
    public JPanel createSeederJPanel(SeederModel seeder) {

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        JPanel tmpPanel = new JPanel();
        tmpPanel.setLayout(gridBagLayout);

        JTextField tmpJTextField = new JTextField(seeder.getSeederAppNumber()+" - "+seeder.getSeederPort());
        tmpJTextField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//      Sets info about notes in view or removes task
        tmpJTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("Kliknięto prawym");
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Kliknięto lewym");
                }
            }
        });

        tmpJTextField.setBorder(null);
        tmpJTextField.setEditable(false);
        c.fill = GridBagConstraints.HORIZONTAL;
//        c.weightx = 0.8;
//        c.gridwidth = 1;
//        c.gridx = 1;
//        c.gridy = 0;
        tmpPanel.add(tmpJTextField, c);

//        tmpPanel.setPreferredSize(new Dimension((int)((mainFrameWidth/3)*0.99),mainFrameHeight/20));

        return tmpPanel;
    }


//  Adds seeders JPanels to the left ScrollPanel
    public void addSeedersJPanelListToLeftPanel() {
//      Removes all previous seeders
        leftSeedersPanel.removeAll();

        for (JPanel seederJPanel : seedersJPanelList) {

//          Changing look of the JPanel
            seederJPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createRaisedBevelBorder(), "",
                    TitledBorder.CENTER,
                    TitledBorder.TOP));

            seederJPanel.setMaximumSize(new Dimension((int)((mainFrameWidth/2)),mainFrameHeight/15));

            leftSeedersPanel.add(seederJPanel);
        }

        repaintFrame();
    }

    //Get information how many hosts are there and put this data to array
// Create required amount of JPanels for each seeder
// Each JPanel should be clickable
//Add JPanel to the left panel
//Repaint


//  Adds
    public void addSeedersInformationToTheLeftJPanel() {

    }


    public JFrame getFrame() {
        return frame;
    }

    public JPanel getRightFilesPanel() {
        return rightFilesPanel;
    }

    public JPanel getLeftSeedersPanel() {
        return leftSeedersPanel;
    }

    public JLabel getIpLabel() {
        return ipLabel;
    }

    public JLabel getPortLabel() {
        return portLabel;
    }

    public JButton getSendFileButton() {
        return sendFileButton;
    }

    public JButton getDownloadFileButton() {
        return downloadFileButton;
    }

    public ArrayList<JPanel> getSeedersJPanelList() {
        return seedersJPanelList;
    }

    public ArrayList<JPanel> getFilesList() {
        return filesList;
    }

    public void setSeeders(ArrayList<SeederModel> seeders) {
        this.seeders = seeders;
    }

    public JLabel getAppNumberLabel() {
        return appNumberLabel;
    }
}
