import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class AppWindow extends JFrame {
    private JPanel panel;
    private JLabel maxSizeLabel;
    private JLabel mmLabel;
    private JTextField maxSizeField;
    private JButton startButton;
    private JLabel warningLabel;
    private JLabel stockPer;
    private JLabel persLabel;
    private JTextField stockPersField;
    private JLabel limberFilesLabel;
    private JLabel lumberFilesNamesLable;
    private JButton addFilesButton;

    public AppWindow() {
        this.setTitle("Lumber Optimizer");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setContentPane(panel);
        this.pack();
        startButton.setName("start");
        addFilesButton.setName("lumber");

        MultiActionListener multiAction = new MultiActionListener();
        addFilesButton.addActionListener(multiAction);
        startButton.addActionListener(multiAction);
    }

    class MultiActionListener implements ActionListener{
        JFileChooser chooser;
        File[] lumberFiles;
        LumberOptimiser optimizer;

        @Override
        public void actionPerformed(ActionEvent e) {
            // -> start button action
            String buttonName = ((JButton) e.getSource()).getName();
            if (buttonName.equals(startButton.getName())) startButtonAction();
            // -> add file actions
            else addFileAction();
        }

        private void startButtonAction(){
            //check maxSizeField is digit
            String maxSize = maxSizeField.getText();
            String stockPerText = stockPersField.getText();
            if (maxSize.matches("\\D") || stockPerText.matches("\\D")) {
                warningLabel.setText("Incorrect max length or stock percentage.");
                return;
            }

            //check lumber files !null
            if (lumberFiles == null) {
                warningLabel.setText("Lumber file isn't added yet.");
                return;
            }

            //optimize work
            optimizer = new LumberOptimiser(lumberFiles, Integer.parseInt(maxSize), Integer.parseInt(stockPerText));
            if (!optimizer.checkMaxLength()) {
                warningLabel.setText("There are elements with lenght > " + maxSize + "mm");
                return;
            }

            //send res file
            chooser.setSelectedFile(new File("Пиломатериал_" + new SimpleDateFormat("dd.MM.yy").format(new Date()) + ".txt"));
            int returnVal = chooser.showSaveDialog(AppWindow.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    Files.write(chooser.getSelectedFile().toPath(), optimizer.getOptimizeList());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        private void addFileAction(){
            //check file is .txt
            chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return !f.isHidden() && f.getName().endsWith(".txt");
                }

                @Override
                public String getDescription() {
                    return "txt files";
                }
            });

            //get lumber/lather file
            int returnVal = chooser.showOpenDialog(AppWindow.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                lumberFiles = chooser.getSelectedFiles();
                String textForFilesLabel = String.format("<html>%s</html>", Arrays.stream(lumberFiles).map(file -> file.getName()+"<br>").collect(Collectors.joining()));
                lumberFilesNamesLable.setText(textForFilesLabel);
            }
        }
    }

}
