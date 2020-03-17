import java.util.Map;
import static java.util.Map.entry;

public class ORF {
    private String strand;
    private String sequence;
    private String label;
    private int startPos;
    private int endPos;

    ORF(String sequence, String strand, String label, int startPos, int endPos) {
        setSequence(sequence);
        setStrand(strand);
        setLabel(label);
        setStartPos(startPos);
        setEndPos(endPos);
    }

    public String getTranslation() {
        StringBuilder aminoAcid = new StringBuilder();
        Map<String, String> codToAa = Map.ofEntries(
                entry("ATA", "I"), entry("ATC", "I"), entry("ATT", "I"), entry("ATG", "M"),
                entry("ACA", "T"), entry("ACC", "T"), entry("ACG", "T"), entry("ACT", "T"),
                entry("AAC", "N"), entry("AAT", "N"), entry("AAA", "K"), entry("AAG", "K"),
                entry("AGC", "S"), entry("AGT", "S"), entry("AGA", "R"), entry("AGG", "R"),
                entry("CTA", "L"), entry("CTC", "L"), entry("CTG", "L"), entry("CTT", "L"),
                entry("CCA", "P"), entry("CCC", "P"), entry("CCG", "P"), entry("CCT", "P"),
                entry("CAC", "H"), entry("CAT", "H"), entry("CAA", "Q"), entry("CAG", "Q"),
                entry("CGA", "R"), entry("CGC", "R"), entry("CGG", "R"), entry("CGT", "R"),
                entry("GTA", "V"), entry("GTC", "V"), entry("GTG", "V"), entry("GTT", "V"),
                entry("GCA", "A"), entry("GCC", "A"), entry("GCG", "A"), entry("GCT", "A"),
                entry("GAC", "D"), entry("GAT", "D"), entry("GAA", "E"), entry("GAG", "E"),
                entry("GGA", "G"), entry("GGC", "G"), entry("GGG", "G"), entry("GGT", "G"),
                entry("TCA", "S"), entry("TCC", "S"), entry("TCG", "S"), entry("TCT", "S"),
                entry("TTC", "F"), entry("TTT", "F"), entry("TTA", "L"), entry("TTG", "L"),
                entry("TAC", "Y"), entry("TAT", "Y"), entry("TAA", "_"), entry("TAG", "_"),
                entry("TGC", "C"), entry("TGT", "C"), entry("TGA", "_"), entry("TGG", "W")
        );
        try {
            for (int i = 0; i < this.sequence.length(); i += 3) {
                aminoAcid.append(codToAa.get(this.sequence.substring(i, i + 3)));
            }
        } catch (StringIndexOutOfBoundsException ignored) {
        }
        return aminoAcid.toString();
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getStrand() {
        return strand;
    }

    public void setStrand(String strand) {
        this.strand = strand;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getStartPos() {
        return startPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

}

