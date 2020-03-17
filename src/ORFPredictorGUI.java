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
    private static JScrollPane panel = new JScrollPane();
    private static JButton[] blast = new JButton[40];
    private static String sequence;
    private static String[] toBLAST;

    public static void main(String[] args) {
        ORFs.add(new ORF("ATATACAGAGCAGCAGAGGCACGAGCGACAGGCAGCAGAGACGCAAGATTCGCTGGCTCTCGGCGCGAGTGATAGTAGAGGCGTCTCGTATATGCGCTC", "+-", "ORF+i", 1, 2));
        frame.setResizable(false);

        frame.setTitle("ORF Predictor");
        frame.createGUI();
        frame.setSize(1000, 600);
    }

    private void createGUI() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container window = getContentPane();
        frame.setLayout(null);

        chooseFile.setText("Open bestand");
        chooseFile.addActionListener(this);
        chooseFile.setBounds(5,5,150,25);
        frame.add(chooseFile);

        seqField.setText("voer hier een sequentie in of open een bestand");
        seqField.setBounds(160,5,660,25);
        frame.add(seqField);

        searchButton.setText("vind ORF's");
        searchButton.addActionListener(this);
        searchButton.setBounds(830,5,150,25);
        frame.add(searchButton);

        panel.setBounds(5,50,970,500);
        panel.setLayout(new ScrollPaneLayout());
        for (int i=0;i<blast.length;i++){
            blast[i]=new JButton();
            blast[i].setText(Integer.toString(i));
            panel.add(blast[i]);
        }
        frame.add(panel);
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
        } else if (actionEvent.getSource() == searchButton) {
            System.out.println("searching");
        }

    }


    static class BLASTORF extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }
}

