package com.client.view;

import com.SeederModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class ClientView {

    private int screenHeight, screenWidth;

    private JFrame frame;
    private JPanel mainPanel, topInformationPanel, rightFilesPanel, leftSeedersPanel, buttonsPanel, progressBarPanel,
    hostInformationJPanel, checkSumBar;
    private JScrollPane leftSeedersScrollPanel, rightFilesScrollPanel;

    private Dimension mainFrameDimension,seederJPanelSize, fileJPanelSize;
    private int mainFrameWidth, mainFrameHeight;

    private JLabel appNumberLabel;
    private JLabel yourAppNumberLabel;
    private JLabel yourIpLabel;
    private JLabel ipLabel;
    private JLabel yourPortLabel;
    private JLabel portLabel;
    private JLabel downloadProgressLabel;
    private JTextArea checkSumArea;
    private JButton reloadHostsList, reloadFiles, sendFileButton, downloadFileButton;

    private ArrayList<JPanel> seedersJPanelList, filesList;
    private ArrayList<SeederModel> seeders;

    private ArrayList<Integer> currentlyChosenSeeders = new ArrayList<>();
    private ArrayList<Integer> previouslyChosenSeeders = new ArrayList<>();
    private int previouslyChosenFileNo, currentlyChosenFileNo;
    private String currentlyChoosenFileName;
    private String currentlyChosenFileCheckSum, previouslyChosenFileCheckSum;

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
        mainFrameWidth = (int)(screenWidth / 2.5);
        mainFrameHeight = screenHeight / 2;

        this.mainFrameDimension = new Dimension(mainFrameWidth, mainFrameHeight);
        frame.setSize(mainFrameDimension);
        frame.setResizable(true);
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
        rightFilesScrollPanel = new JScrollPane(rightFilesPanel);
        rightFilesScrollPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/3));
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
        leftSeedersScrollPanel = new JScrollPane(leftSeedersPanel);
        leftSeedersScrollPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/3));


//      Add left panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(leftSeedersScrollPanel, c);


        //      Creating progress bar panel
        checkSumBar = new JPanel(new GridBagLayout());
        GridBagConstraints sumC = new GridBagConstraints();
        checkSumBar.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));

//      Add progress bar panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(checkSumBar, c);

//      Creating buttons panel
        buttonsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints buttonsC = new GridBagConstraints();

//      Add buttons panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(buttonsPanel, c);

//      Creating progress bar panel
        progressBarPanel = new JPanel(new GridBagLayout());
        progressBarPanel.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
        GridBagConstraints progressC = new GridBagConstraints();

//      Add progress bar panel to the main
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 5;
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

        this.ipLabel = new JLabel("");
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


        this.portLabel = new JLabel("");
//        yourPortLabel.setFont(new Font("serif", Font.BOLD, 25));
        topC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        topC.gridwidth = 1;
        topC.gridx = 5;
        topC.gridy = 0;
        topInformationPanel.add(portLabel, topC);


        this.checkSumArea = new JTextArea();
        checkSumArea.setEditable(false);
        checkSumArea.setMaximumSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
        checkSumArea.setBackground(null);
//        yourPortLabel.setFont(new Font("serif", Font.BOLD, 25));
        sumC.fill = GridBagConstraints.HORIZONTAL;
//        c.insets = new Insets(5, 15, 10, 15);
        sumC.gridwidth = 4;
        sumC.gridx = 0;
        sumC.gridy = 0;
        checkSumBar.add(checkSumArea, sumC);


//      Setting information buttons for buttons panel
        this.reloadHostsList = new JButton("Reload hosts and files");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
        reloadHostsList.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
//        c.insets = new Insets(5, 15, 10, 15);
        buttonsC.gridwidth = 4;
        buttonsC.gridx = 0;
        buttonsC.gridy = 0;
        buttonsPanel.add(reloadHostsList, buttonsC);

////      Setting information buttons for buttons panel
//        this.reloadFiles = new JButton("Reload files");
//        buttonsC.fill = GridBagConstraints.HORIZONTAL;
//        reloadFiles.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
////        c.insets = new Insets(5, 15, 10, 15);
//        buttonsC.gridwidth = 2;
//        buttonsC.gridx = 2;
//        buttonsC.gridy = 0;
//        buttonsPanel.add(reloadFiles, buttonsC);

        this.downloadFileButton = new JButton("Download file");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
        downloadFileButton.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
//        c.insets = new Insets(5, 15, 10, 15);
        buttonsC.gridwidth = 2;
        buttonsC.gridx = 0;
        buttonsC.gridy = 1;
        buttonsPanel.add(downloadFileButton, buttonsC);

//      Setting information buttons for buttons panel
        this.sendFileButton = new JButton("Send file");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
        sendFileButton.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
//        c.insets = new Insets(5, 15, 10, 15);
        buttonsC.gridwidth = 2;
        buttonsC.gridx = 2;
        buttonsC.gridy = 1;
        buttonsPanel.add(sendFileButton, buttonsC);

//      Setting information buttons for buttons panel
        this.downloadProgressLabel = new JLabel("% of file download");
        buttonsC.fill = GridBagConstraints.HORIZONTAL;
//        sendFileButton.setPreferredSize(new Dimension(mainFrameWidth/3,mainFrameHeight/12));
        progressC.gridwidth = 4;
        progressC.gridx = 0;
        progressC.gridy = 0;
        progressBarPanel.add(downloadProgressLabel, progressC);




//      Adds main panel to the frame
        frame.add(mainPanel);
    }

    public void repaintFrame(){
        frame.revalidate();
        frame.repaint();
    }


//  Adds information about seeders to the list from which later will be displayed seeders list
    public void createSeedersJPanelsArray() {
        seedersJPanelList.clear();
//      sorts seeders models list
        Collections.sort(seeders, SeederModel.AppNoComparator);
        for (int i = 0; i < seeders.size(); i++) {
            seedersJPanelList.add(createSeederJPanel(seeders.get(i), i));
        }
    }

//  Creates separate JPanel for given seeder model
    public JPanel createSeederJPanel(SeederModel seeder, int seederNumberInList) {

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

//                  If clicked seeder not in list add it
                    if (!currentlyChosenSeeders.contains(Integer.valueOf(seederNumberInList))) {
                        currentlyChosenSeeders.add(Integer.valueOf(seederNumberInList));

//                  If seeder already in list, remove it after 2nd click
                    } else if (currentlyChosenSeeders.contains(Integer.valueOf(seederNumberInList))) {
//                        previouslyChosenSeeders.add(currentlyChosenSeeders.get)
//
//                        Integer tmp = currentlyChosenSeeders.get(seederNumberInList);

                        previouslyChosenSeeders.add((currentlyChosenSeeders.get(currentlyChosenSeeders.indexOf(Integer.valueOf(seederNumberInList)))));
                        currentlyChosenSeeders.remove((currentlyChosenSeeders.get(currentlyChosenSeeders.indexOf(Integer.valueOf(seederNumberInList)))));

                    }

//                  TODO: FIX ConcurenctModicifactionException
                    clickedJPanelVisualSeeders();

//                  Clears JPanel files list
                    filesList.clear();

//                  Counter used to get numeration of files for border change
                    int fileNumber = 0;

////                  Creates JPanel array containing all files for clicked App
//                    for (String fileName : seeder.getFilesMap().keySet()) {
////                      For each file, create JPanel and add it to files JPanel list
//                        filesList.add(createFilePanel(seeder, fileName, fileNumber));
//                        fileNumber++;
//                    }

//                  Creates JPanel array containing all files from currently clicked seeders
                    for (Integer seederNumber : currentlyChosenSeeders) {
                        for (String fileName : seeders.get(seederNumber).getFilesMap().keySet()) {
//                      For each file, create JPanel and add it to files JPanel list
                            filesList.add(createFilePanel(seeders.get(seederNumber).getSeederAppNumber(), fileName, fileNumber, seeders.get(seederNumber).getFilesMap().get(fileName)));
                            fileNumber++;
                        }
                    }

                    rightFilesPanel.removeAll();

                    for (JPanel fileJPanel : filesList) {

                        //          Changing look of the JPanel
                        fileJPanel.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createRaisedBevelBorder(), "",
                                TitledBorder.CENTER,
                                TitledBorder.TOP));

                        fileJPanel.setMaximumSize(new Dimension(((mainFrameWidth/2)),mainFrameHeight/15));

//                      Adds JPanel with file to the right JPanel
                        rightFilesPanel.add(fileJPanel);
                    }

                    repaintFrame();

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


    //  Creates separate JPanel for given seeders files
    public JPanel createFilePanel(Integer seederNumber, String fileName, int fileNumber, String checkSum) {

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        JPanel tmpPanel = new JPanel();
        tmpPanel.setLayout(gridBagLayout);

        JTextField tmpJTextField = new JTextField(fileName + " - on app " + seederNumber);
        tmpJTextField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

//      Sets info about notes in view or removes task
        tmpJTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    System.out.println("Kliknięto prawym przyciskiem na plik");
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    previouslyChosenFileNo = currentlyChosenFileNo;
                    currentlyChosenFileNo = fileNumber;
                    currentlyChoosenFileName = fileName;

                    currentlyChosenFileCheckSum = currentlyChosenFileCheckSum;
                    currentlyChosenFileCheckSum = checkSum;

                    clickedJPanelVisualFiles();
                    displayFileControlSum(checkSum);
                    System.out.println("Klinięto lewym przyciskiem na plik");

                    repaintFrame();
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


//  Changes borders visual according to the currently clicked JPanel
    public void clickedJPanelVisualSeeders() {
//      If there was previously changed JPanel
        if (!previouslyChosenSeeders.isEmpty()){


            for (Iterator<Integer> it = previouslyChosenSeeders.iterator(); it.hasNext();) {
                Integer next = it.next();
                seedersJPanelList.get(next).setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createRaisedBevelBorder(), "",
                        TitledBorder.CENTER,
                        TitledBorder.TOP));
                it.remove();

////          Return normal border
//            for (Integer previouslyChosenSeeder : previouslyChosenSeeders) {
//                seedersJPanelList.get(previouslyChosenSeeder).setBorder(BorderFactory.createTitledBorder(
//                        BorderFactory.createRaisedBevelBorder(), "",
//                        TitledBorder.CENTER,
//                        TitledBorder.TOP));
//                previouslyChosenSeeders.remove(previouslyChosenSeeder);
            }

        }
//      Change border in new JPanel


        for (Integer currentlyChosenSeeder : currentlyChosenSeeders) {
            seedersJPanelList.get(currentlyChosenSeeder).setBorder(BorderFactory.createLoweredBevelBorder());
        }
    }


    public void displayFileControlSum(String checkSum) {
        System.out.println(checkSum);
        checkSumArea.setText(checkSum);
    }


//  Changes borders visual according to the currently clicked JPanel
    public void clickedJPanelVisualFiles() {
//      If there was previously changed JPanel
        if (previouslyChosenFileNo >= 0){
//          Return normal border
            filesList.get(previouslyChosenFileNo).setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createRaisedBevelBorder(), "",
                    TitledBorder.CENTER,
                    TitledBorder.TOP));
        }
//      Change border in new JPanel
        filesList.get(currentlyChosenFileNo).setBorder(BorderFactory.createLoweredBevelBorder());
        repaintFrame();
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

            seederJPanel.setMaximumSize(new Dimension(((mainFrameWidth/2)),mainFrameHeight/15));

            leftSeedersPanel.add(seederJPanel);
        }

        repaintFrame();
    }


//  Adds seeders JPanels to the left ScrollPanel
    public void addFilesJPanelListToRightPanel() {
//      Removes all previous seeders
        rightFilesPanel.removeAll();

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


    public void changeDownloadFileLabel(String text) {
        downloadProgressLabel.setText(text);
        progressBarPanel.repaint();
        repaintFrame();
    }

    public void createPopUpWindow(String text) {
        JOptionPane.showMessageDialog(frame,
                text);
    }

//  Method to pass seeders from whom files should be downloaded
    public ArrayList<SeederModel> getListOfSeedersForDownload(){

//        Creates tmp array
        ArrayList<SeederModel> tmp = new ArrayList<SeederModel>();

//      For each currently clicked seeder makes copy in new tmp array
        for (Integer element : currentlyChosenSeeders) {
            tmp.add(seeders.get(element));
        }

        return tmp;

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

    public JButton getReloadHostsList() {
        return reloadHostsList;
    }

    public JButton getReloadFiles() {
        return reloadFiles;
    }

    public ArrayList<Integer> getCurrentlyChosenSeeders() {
        return currentlyChosenSeeders;
    }

    public int getCurrentlyChosenFileNo() {
        return currentlyChosenFileNo;
    }

    public String getCurrentlyChoosenFileName() {
        return currentlyChoosenFileName;
    }

    public ArrayList<SeederModel> getSeeders() {
        return seeders;
    }

    public JTextArea getCheckSumArea() {
        return checkSumArea;
    }

    public JPanel getCheckSumBar() {
        return checkSumBar;
    }

    public JLabel getDownloadProgressLabel() {
        return downloadProgressLabel;
    }

    public String getCurrentlyChosenFileCheckSum() {
        return currentlyChosenFileCheckSum;
    }
}
