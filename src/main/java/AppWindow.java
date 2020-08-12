import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppWindow extends JFrame{
    private JPanel panel;
    private JLabel maxSizeLabel;
    private JLabel mmLabel;
    private JTextField maxSizeField;
    private JButton startButton;
    private JLabel warningLabel;
    private JLabel stockPer;
    private JLabel persLabel;
    private JTextField stockPersField;

    public AppWindow(){
        this.setTitle("Lumber Optimizer");
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setContentPane(panel);
        this.pack();
        startButton.addActionListener(new ActionListener(){
            JFileChooser chooser;
            File sourceFile;
            LumberOptimiser optimizer;

            @Override
            public void actionPerformed(ActionEvent e) {
                //check maxSizeField is digit
                String maxSize = maxSizeField.getText();
                String stockPer = stockPersField.getText();
                if (maxSize.matches("\\D") || stockPer.matches("\\D")) {
                    warningLabel.setText("Incorrect max length or stock percentage.");
                    return;
                }

                //check file is .txt
                chooser = new JFileChooser();
                chooser.setFileFilter(new FileFilter(){
                    @Override
                    public boolean accept(File f) {
                        return !f.isHidden() && f.getName().endsWith(".txt");
                    }

                    @Override
                    public String getDescription() {
                        return "txt files";
                    }
                });

                //get file
                int returnVal = chooser.showOpenDialog(AppWindow.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) sourceFile = chooser.getSelectedFile();

                //optimize work
                optimizer = new LumberOptimiser(sourceFile, Integer.parseInt(maxSize), Integer.parseInt(stockPer));
                if (!optimizer.checkMaxLength()) {
                    warningLabel.setText("There are elements with lenght > " + maxSize + "mm");
                    return;
                }

                //send res file
                chooser.setSelectedFile(new File("Пиломатериал_"+ new SimpleDateFormat("dd.MM.yy").format(new Date()) +".txt"));
                returnVal = chooser.showSaveDialog(AppWindow.this);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        Files.write(chooser.getSelectedFile().toPath(), optimizer.getOptimizeList());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }
}
