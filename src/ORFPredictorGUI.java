import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ORFPredictorGUI extends JFrame implements ActionListener
{
    private static ORFPredictorGUI frame = new ORFPredictorGUI();
    private static String[] toBLAST = new String[2];
    private Font font = new Font("Arial", Font.PLAIN, 12);
    private JTextField seqField = new JTextField();
    private JTextField minLength = new JTextField();
    private JCheckBox nestedORF = new JCheckBox();
    private JButton chooseFile = new JButton();
    private JButton searchButton = new JButton();
    private JScrollPane topScrollPane;
    private JScrollPane bottomScrollPane;
    private JPanel buttonpanel = new JPanel(new FlowLayout());
    private JTextArea textAreaORF = new JTextArea();
    private ArrayList<JButton> blastlist = new ArrayList<>(50);
    private int codonHeader = 1;
    private String sequence;
    private boolean ignoreNestedORFs = false;
    private int minimalORFLength = 150;
    private static String os = System.getProperty("os.name").toLowerCase();

    private LinkedHashMap<String, ORF> ORFS = new LinkedHashMap<>();


    //dingen buiten het ontwerp
    private JPanel ORFpanel = new JPanel();
    JLabel ORFamt = new JLabel();
    private JButton blast = new JButton("BLAST");

    public static void main(String[] args)
    {
        frame.setResizable(false);
        frame.setTitle("ORF Predictor");
        frame.createGUI();
        frame.setSize(1000, 600);
    }

    public void createGUI()
    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        Container window = getContentPane();
        frame.setLayout(null);


        chooseFile.setText("Open bestand");
        chooseFile.addActionListener(this);
        chooseFile.setBounds(5, 5, 150, 25);
        chooseFile.setFont(font);
        chooseFile.setFocusable(false);
        frame.add(chooseFile);

        seqField.setText("Voer hier een sequentie in of open een bestand");
        seqField.setBounds(160, 5, 660, 25);
        seqField.setFont(font);
        frame.add(seqField);

        searchButton.setText("Vind ORF's");
        searchButton.addActionListener(this);
        searchButton.setBounds(830, 5, 150, 25);
        searchButton.setFont(font);
        searchButton.setFocusable(false);
        frame.add(searchButton);

        JLabel lengthLabel = new JLabel("Minimale ORF lengte: ");
        lengthLabel.setBounds(5, 50, 150, 25);
        lengthLabel.setFont(font);
        frame.add(lengthLabel);

        minLength.setBounds(140, 50, 50, 25);
        minLength.setText("150");
        minLength.setFont(font);
        frame.add(minLength);

        JLabel nestedLabel = new JLabel("Negeer Geneste ORFs?: ");
        nestedLabel.setBounds(220, 50, 170, 25);
        nestedLabel.setFont(font);
        frame.add(nestedLabel);

        nestedORF.setBounds(370, 50, 50, 25);
        nestedORF.setFont(font);
        frame.add(nestedORF);

        ORFpanel.setLayout(new BorderLayout());
        ORFpanel.setFont(font);
        ORFpanel.add(textAreaORF);
        textAreaORF.setText("Welkom in de ORF Predictor app.");

        bottomScrollPane = new JScrollPane(buttonpanel);
        bottomScrollPane.setBounds(5, 80, 970, 54);
        bottomScrollPane.setLayout(new ScrollPaneLayout());
        bottomScrollPane.setFont(font);
        frame.add(bottomScrollPane);

        topScrollPane = new JScrollPane(ORFpanel);
        topScrollPane.setBounds(5, 150, 970, 300);
        topScrollPane.setLayout(new ScrollPaneLayout());
        topScrollPane.setFont(font);
        frame.add(topScrollPane);

        ORFamt.setBounds(430, 50, 300, 25);
        ORFamt.setFont(font);
        frame.add(ORFamt);

        blast.setBounds(880, 528, 80, 25);
        blast.setFont(font);
        blast.addActionListener(this);
        blast.setFocusable(false);
        frame.add(blast);

        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.pack();

    }

    public void drawToPanel()
    {
        ORFamt.setText(String.format("Aantal gevonden ORF's: %s", ORFS.size()));
        for (JButton jButton : blastlist)
        {
            buttonpanel.add(jButton);
            jButton.addActionListener(this);
            buttonpanel.setFont(font);
        }

        frame.validate();
//        text.setText(sequence);
    }

    public void chooseFastaFile()
    {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Fasta  files", "fa", "fasta");
        fileChooser.setFileFilter(filter);
        File selectedFile = null;
        int result = fileChooser.showOpenDialog(fileChooser);
        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileChooser.getSelectedFile();
        }
        try
        {
            assert selectedFile != null;
            selectedFile = new File(selectedFile.toString());
        }
        catch (NullPointerException e)
        {
            seqField.setText("Geen file geselecteerd");
        }
        readFile(selectedFile);
    }

    public void readFile(File file)
    {
        StringBuilder seqTemp = new StringBuilder();
        assert file != null;
        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                if (!line.startsWith(">"))
                {
                    assert false;
                    seqTemp.append(line.toUpperCase());
                }
            }
            seqField.setText(seqTemp.toString());
        }
        catch (IOException | NullPointerException e)
        {
            seqField.setText("Geen file geselecteerd");
        }
    }

    public String getReverseComplement()
    {
        StringBuilder sequenceReverseComplement = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--)
        {
            char letter = sequence.charAt(i);
            switch (letter)
            {
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
                default:
                    sequenceReverseComplement.append(letter);
                    break;
            }
        }
        return sequenceReverseComplement.toString();
    }

    public String getCorrectSequence(int startPos, String sequence)
    {
        try
        {
            sequence = sequence.substring(startPos);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            seqField.setText("Geen file geselecteerd");
        }
        // if sequence is not multiple of 3, get new sequence that starts at startPos and delete last part
        if (sequence.length() % 3 != 0)
        {
            int end = sequence.length() - sequence.length() % 3;
            sequence = sequence.substring(0, end);
        }
        return sequence;
    }

    public void searchORF(int startPos, String sequence, String strand)
    {
        ArrayList<String> stopCodons = new ArrayList<>(Arrays.asList("TAA", "TAG", "TGA"));
        StringBuilder seq = new StringBuilder();
        int startOrfPos = 0;
        int endOrfPos = 0;

        sequence = getCorrectSequence(startPos, sequence);
        for (int i = 0; i < sequence.length(); i += 3)
        {
            String codon = sequence.substring(i, i + 3);
            if (codon.equals("ATG"))
            {
                seq.append(codon);
                for (int j = i + 3; j < sequence.length(); j += 3)
                {
                    String cod = sequence.substring(j, j + 3);
                    // ignore the same codon as in i loop
                    if (j != i)
                    {
                        seq.append(stopCodons.contains(cod) ? "*" : cod);
                    }
                    if (stopCodons.contains(cod))
                    {
                        startOrfPos = i;
                        endOrfPos = j;
                        if (ignoreNestedORFs) i = j - 3;
                        break;
                    }
                }
                if (seq.length() > minimalORFLength && seq.toString().endsWith("*") && !seq.toString().contains("N"))
                {
                    String label = ">ORF" + codonHeader;
                    codonHeader++;
                    String ignoredStopCodonSequence = seq.substring(0, seq.length() - 1);
                    ORF orf = new ORF(ignoredStopCodonSequence, strand, label, startPos, startOrfPos, endOrfPos);
                    ORFS.put(label, orf);
                }
                seq.setLength(0);
            }
        }
    }

    public boolean isDNA()
    {
        Pattern pattern = Pattern.compile(".*[^ATGCN].*");

        Matcher matcher = pattern.matcher(sequence);
        return !matcher.matches();
    }

    public void reset()
    {
        codonHeader = 1;
        ORFS.clear();
        blastlist.clear();
        buttonpanel.removeAll();
        textAreaORF.removeAll();
    }


    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getSource() == chooseFile)
        {
            chooseFastaFile();
            sequence = seqField.getText().toUpperCase();
        }
        else if (actionEvent.getSource() == searchButton)
        {
            reset();
            sequence = seqField.getText().toUpperCase();
            if (isDNA())
            {
                ignoreNestedORFs = nestedORF.isSelected();
                minimalORFLength = Integer.parseInt(minLength.getText());

                try
                {
                    String sequenceReversed = getReverseComplement();
                    for (int i = 0; i < 3; i++)
                    {
                        searchORF(i, sequence, "+");
                        searchORF(i, sequenceReversed, "-");
                    }

                    for (ORF orf : ORFS.values())
                    {
                        JButton btn = new JButton(orf.getLabel());
                        btn.setFocusable(false);
                        blastlist.add(btn);
                    }
                }
                catch (NullPointerException e)
                {
                    seqField.setText("Geen file geselecteerd");
                }
                drawToPanel();
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Geen DNA sequentie, probeer opnieuw!");
            }
        }
        else if (actionEvent.getSource() == blast)
        {
            BLASTORF blastSeq = new BLASTORF();
            blastSeq.start();
            JOptionPane.showMessageDialog(frame, String.format("%s wordt geblast, dit kan even duren", toBLAST[0]));

        }
        else
        {
            JButton btn = (JButton) actionEvent.getSource();
            ORFpanel.removeAll();
            textAreaORF.setText("");
            ORF orf = ORFS.get(btn.getText());
            toBLAST[0] = orf.getLabel();
            toBLAST[1] = orf.getTranslation();
            textAreaORF.append(String.format("Label: %s\n", orf.getLabel()));
            textAreaORF.append(String.format("Strand: %s\n", orf.getStrand()));
            textAreaORF.append(String.format("Frame: %s\n", orf.getFrame()));
            textAreaORF.append(String.format("Start positie: %s\n", orf.getStartPos()));
            textAreaORF.append(String.format("End positie: %s\n", orf.getEndPos()));
            textAreaORF.append(String.format("Eiwitsequentie: %s\n", orf.getTranslation()));
            ORFpanel.add(textAreaORF);
        }
    }

    private static void blastSeq()
    {

        String[] command;
        if (os.startsWith("windows"))
        {
            command = new String[]{"cmd.exe", "/c", "pythonFiles\\Windows\\runPython.bat"};
        }
        else
        {

            command = new String[]{"bash", "pythonFiles/Linux/runPython.sh"};
        }
        try
        {
            Process p = Runtime.getRuntime().exec(command);
            p.waitFor();
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }


    static class BLASTORF extends Thread
    {
        @Override
        public void run()
        {
            blastSeq();
        }
    }
}

