import javafx.stage.FileChooser;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ORFPredictorGUI extends JFrame implements ActionListener {
    private static ORFPredictorGUI frame = new ORFPredictorGUI();
    private static JTextField seqField = new JTextField();
    private static JButton chooseFile = new JButton();
    private static JButton searchButton = new JButton();
    private static ArrayList<ORF> ORFs = new ArrayList<>();
    private static JButton[] blast;
    private static String sequence;
    private static String[] toBLAST;

    public static void main(String[] args) {
        ORFs.add(new ORF("ATATACAGAGCAGCAGAGGCACGAGCGACAGGCAGCAGAGACGCAAGATTCGCTGGCTCTCGGCGCGAGTGATAGTAGAGGCGTCTCGTATATGCGCTC", "+-", "ORF+i", 1, 2));
        frame.setResizable(true);

        frame.setTitle("ORF Predictor");
        frame.createGUI();
        frame.setSize(1000,600);
    }

    private void createGUI() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        frame.setLayout(new GridBagLayout());
        JMenuBar menuBar = new JMenuBar();
        JMenu m1 = new JMenu("files");
        menuBar.add(m1);
        JMenuItem m11 = new JMenuItem("open");
        m1.add(m11);
        frame.add(BorderLayout.NORTH,menuBar);
        GridBagConstraints constraints = new GridBagConstraints();

        chooseFile.setText("Open bestand");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridx = 0;
        constraints.gridy = 0;
        chooseFile.addActionListener(this);
        frame.add(chooseFile, constraints);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 1;
        constraints.gridy = 0;
        seqField.setText("voer hier een sequentie in of open een bestand");
        frame.add(seqField, constraints);

        searchButton.setText("vind ORF's");
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.5;
        constraints.gridx = 2;
        constraints.gridy = 0;
        frame.add(searchButton,constraints);

        frame.setVisible(true);
        frame.pack();

    }

    private static void chooseFastaFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fasta  files", "fa", "fasta");
        fileChooser.setFileFilter(filter);
        File selectedFile = null;

        int result = fileChooser.showOpenDialog(fileChooser);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }
        try {
            assert selectedFile != null;
            selectedFile = new File(selectedFile.toString());
        } catch (NullPointerException e) {
            seqField.setText("geen file geselecteerd");
        }
        readFile(selectedFile);
    }

    private static void readFile(File file) {
        StringBuilder seqTemp = new StringBuilder();
        assert file != null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    assert false;
                    seqTemp.append(line.toUpperCase());
                }
            }
            seqField.setText(seqTemp.toString());
        } catch (IOException | NullPointerException e) {
           seqField.setText("geen file geselecteerd");
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == chooseFile) {
            chooseFastaFile();
            sequence = seqField.getText();
        }

    }


    static class BLASTORF extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}

