import java.util.ArrayList;
import java.util.Arrays;

public class ORFPredictorGUI
{
    private static ArrayList<String> stopCodons = new ArrayList<>(Arrays.asList("TAA", "TAG", "TGA"));
    private static ArrayList<ORF> ORFs = new ArrayList<>();
    private static int codonHeader = 1;



    public static void main(String[] args)
    {
        String sequence = "ATGATGTAA";
        String sequenceReversed = new StringBuilder(sequence).reverse().toString();
        for (int i = 0; i < 3; i++)
        {
            findORF(i, sequence, "+");
            findORF(i, sequenceReversed, "-");
        }
        ORFs.forEach((orf -> System.out.println(orf.getStrand())));

    }

    private static void findORF(int startPos, String sequence, String strand)
    {
        StringBuilder seq = new StringBuilder();
        int endPos = 0;
        // if sequence is not multiple of 3, get new sequence that starts at startPos and delete last part
        String seqToLookIn = sequence.substring(startPos);
        if (seqToLookIn.length() % 3 != 0)
        {
            int end = seqToLookIn.length() - seqToLookIn.length() % 3;
            seqToLookIn = seqToLookIn.substring(0, end);
        }

        for (int i = 0; i < seqToLookIn.length(); i += 3)
        {
            String codon = seqToLookIn.substring(i, i + 3);
            if (codon.equals("ATG"))
            {
                seq.append(codon);
                for (int j = i; j < seqToLookIn.length(); j += 3)
                {
                    String cod = seqToLookIn.substring(j, j + 3);
                    // ignore the same codon as in i loop
                    if (j != i)
                    {
                        seq.append(stopCodons.contains(cod) ? "*" : cod);
                    }
                    if ((cod.equals("ATG") && seq.toString().contains("*")) || stopCodons.contains(cod)) {
                        endPos = j;
                        break;
                    }
                }
                String label = ">ORF"+ codonHeader;
                codonHeader++;
                String ignoredStopCodonSequence = seq.substring(0, seq.length() - 1);
                ORF orf = new ORF(ignoredStopCodonSequence, strand, label, i, endPos);
                // ignore sequence that are either only ATG, or do not have a stop codon
                if (seq.length() > 3 && seq.toString().endsWith("*")) ORFs.add(orf);
                seq.setLength(0);
            }
        }
    }
}
