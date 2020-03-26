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
import java.util.Arrays;

public class ORFPredictorGUI extends JFrame implements ActionListener {
    private static ORFPredictorGUI frame = new ORFPredictorGUI();
    private static String[] toBLAST = new String[2];
    private JTextField seqField = new JTextField();
    private JTextField minLength = new JTextField();
    private JCheckBox nestedORF = new JCheckBox();
    private JButton chooseFile = new JButton();
    private JButton searchButton = new JButton();
    private ArrayList<ORF> ORFs = new ArrayList<>();
    private JScrollPane topScrollPane;
    private JScrollPane bottomScrollPane;
    private JPanel buttonpanel = new JPanel(new FlowLayout());
    private ArrayList<JButton> blastlist = new ArrayList<>(50);
//    private JButton[] blastlist = new JButton[400];
    private String sequence;
    private boolean ignoreNestedORFs = false;
    private int minimalORFLength = 150;

    JLabel text = new JLabel("Welkom in de ORF Predictor app.");

    //dingen buiten het ontwerp
    private JPanel ORFpanel = new JPanel();
    //blokje om ORF in te voeren en te blasten
    private JTextField blastField = new JTextField();

    private JButton blast = new JButton();

    public static void main(String[] args) {
        frame.setResizable(false);
        frame.setTitle("ORF Predictor");
        frame.createGUI();
        frame.setSize(1000, 600);
    }

    public void createGUI() {

        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        Container window = getContentPane();
        frame.setLayout(null);


        chooseFile.setText("Open bestand");
        chooseFile.addActionListener(this);
        chooseFile.setBounds(5, 5, 150, 25);
        frame.add(chooseFile);

        seqField.setText("voer hier een sequentie in of open een bestand");
        seqField.setBounds(160, 5, 660, 25);
        frame.add(seqField);

        searchButton.setText("vind ORF's");
        searchButton.addActionListener(this);
        searchButton.setBounds(830, 5, 150, 25);
        frame.add(searchButton);

        JLabel lengthLabel = new JLabel("Minimale ORF lengte: ");
        lengthLabel.setBounds(5, 50, 150, 25);
        frame.add(lengthLabel);

        minLength.setBounds(130, 50, 50, 25);
        minLength.setText("150");
        frame.add(minLength);

        JLabel nestedLabel = new JLabel("Negeer Geneste ORFs?: ");
        nestedLabel.setBounds(200, 50, 150, 25);
        frame.add(nestedLabel);

        nestedORF.setBounds(340, 50, 50, 25);
        frame.add(nestedORF);

        ORFpanel.setLayout(new BorderLayout());
        ORFpanel.add(text, BorderLayout.NORTH);

        topScrollPane = new JScrollPane(ORFpanel);
        topScrollPane.setBounds(5, 80, 970, 350);
        topScrollPane.setLayout(new ScrollPaneLayout());
        frame.add(topScrollPane);




        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();

    }

    public void drawToPanel() {
        for (JButton jButton : blastlist) {
            buttonpanel.add(jButton);
        }
        bottomScrollPane = new JScrollPane(buttonpanel);
        bottomScrollPane.setBounds(5,450,970,54);
        bottomScrollPane.setLayout( new ScrollPaneLayout());
        frame.add(bottomScrollPane);
        text.setText(sequence);
    }

    public void chooseFastaFile() {
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

    public void readFile(File file) {
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

    public String getReverseComplement() {
        StringBuilder sequenceReverseComplement = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            char letter = sequence.charAt(i);
            switch (letter) {
                case 'A':
                    sequenceReverseComplement.append("T");
                    break;
                case 'T':
                    sequenceReverseComplement.append("A");
                    break;
                case 'G':
                    sequenceReverseComplement.append("C");
                    break;
                case 'C':
                    sequenceReverseComplement.append("G");
                    break;
            }
        }
        return sequenceReverseComplement.toString();
    }

    public String getCorrectSequence(int startPos, String sequence) {
        try {
            sequence = sequence.substring(startPos);
        } catch (StringIndexOutOfBoundsException e) {
            seqField.setText("geen file geselecteerd");
        }
        // if sequence is not multiple of 3, get new sequence that starts at startPos and delete last part
        if (sequence.length() % 3 != 0) {
            int end = sequence.length() - sequence.length() % 3;
            sequence = sequence.substring(0, end);
        }
        return sequence;
    }

    public void searchORF(int startPos, String sequence, String strand) {
        ArrayList<String> stopCodons = new ArrayList<>(Arrays.asList("TAA", "TAG", "TGA"));
        StringBuilder seq = new StringBuilder();
        int startOrfPos = 0;
        int endOrfPos = 0;
        int codonHeader = 1;

        sequence = getCorrectSequence(startPos, sequence);
        for (int i = 0; i < sequence.length(); i += 3) {
            String codon = sequence.substring(i, i + 3);
            if (codon.equals("ATG")) {
                seq.append(codon);
                for (int j = i + 3; j < sequence.length(); j += 3) {
                    String cod = sequence.substring(j, j + 3);
                    // ignore the same codon as in i loop
                    if (j != i) {
                        seq.append(stopCodons.contains(cod) ? "*" : cod);
                    }
                    if (stopCodons.contains(cod)) {
                        startOrfPos = i;
                        endOrfPos = j;
                        if (ignoreNestedORFs) i = j - 3;
                        break;
                    }
                }
                String label = ">ORF" + codonHeader;
                codonHeader++;
                String ignoredStopCodonSequence = seq.substring(0, seq.length() - 1);

                if (seq.length() > minimalORFLength && seq.toString().endsWith("*")) {
                    ORF orf = new ORF(ignoredStopCodonSequence, strand, label, startPos, startOrfPos, endOrfPos);
                    ORFs.add(orf);
                }
                seq.setLength(0);
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == chooseFile) {
            chooseFastaFile();
            sequence = seqField.getText();
        } else if (actionEvent.getSource() == searchButton) {
            ORFs.clear();
            ignoreNestedORFs = nestedORF.isSelected();
            minimalORFLength = Integer.parseInt(minLength.getText());
            try {
                System.out.println("searching");
                String sequenceReversed = getReverseComplement();
                for (int i = 0; i < 3; i++) {
                    searchORF(i, sequence, "+");
                    searchORF(i, sequenceReversed, "-");
                }

                System.out.println(ORFs.size());
                for (int i=0;i<ORFs.size();i++) {
                    blastlist.add(new JButton(String.valueOf(i)));
                }
            } catch (NullPointerException e) {
                seqField.setText("geen file geselecteerd");
            }
            System.out.println(blastlist.size());
            drawToPanel();
        }
    }


    static class BLASTORF extends Thread {
        @Override
        public void run() {
            System.out.println("BLASTing: " + Arrays.toString(ORFPredictorGUI.toBLAST));
        }
    }
}

