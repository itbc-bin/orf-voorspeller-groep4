import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ORFPredictorGUI
{
    private static ArrayList<String> stopCodons = new ArrayList<>(Arrays.asList("TAA", "TAG", "TGA"));
    private static ArrayList<ORF> ORFs = new ArrayList<>();
    private static int codonHeader = 1;
    private static boolean ignoreNestedORFs = false;
    private static int minimalORFLength = 150;


    public static void main(String[] args)
    {
        File file = new File("/home/yarisvthiel/Documents/HAN/Course_7_Bio-informatica/Bio-informatica/Week4/SD.fa");
        String sequence = readFile(file);
        String sequenceReversed = getReverseComplement(sequence);
        for (int i = 0; i < 3; i++)
        {
            findORF(i, sequence, "+");
            findORF(i, sequenceReversed.toString(), "-");
        }
        System.out.println(ORFs.size());
//        ORFs.forEach(orf -> System.out.println(orf.getTranslation()));
    }

    public static String readFile(File file)
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
        }
        catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
        }
        return seqTemp.toString();
    }

    public static String getReverseComplement(String sequence)
    {
        StringBuilder sequenceReverseComplement = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--){
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
            }
        }
        return sequenceReverseComplement.toString();
    }

    public static String getCorrectSequence(int startPos, String sequence)
    {
        sequence = sequence.substring(startPos);
        // if sequence is not multiple of 3, get new sequence that starts at startPos and delete last part
        if (sequence.length() % 3 != 0)
        {
            int end = sequence.length() - sequence.length() % 3;
            sequence = sequence.substring(0, end);
        }
        return sequence;
    }

    public static void findORF(int startPos, String sequence, String strand)
    {
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
                String label = ">ORF" + codonHeader;
                codonHeader++;
                String ignoredStopCodonSequence = seq.substring(0, seq.length() - 1);

                if (seq.length() > minimalORFLength && seq.toString().endsWith("*"))
                {
                    ORF orf = new ORF(ignoredStopCodonSequence, strand, label, startPos, startOrfPos, endOrfPos);
                    ORFs.add(orf);
                }
                seq.setLength(0);
            }
        }
    }
}
