import java.util.Map;
import static java.util.Map.entry;

/**
 * ORF class that stores information about an ORF.
 *
 * @author: Armin van Eldik
 * @versionL 1.0
 */


public class ORF
{
    private String sequence;
    private String strand;
    private String label;
    private int frame;
    private int startPos;
    private int endPos;

    ORF(String sequence, String strand, String label, int frame, int startPos, int endPos)
    {
        setSequence(sequence);
        setStrand(strand);
        setLabel(label);
        setFrame(frame);
        setStartPos(startPos);
        setEndPos(endPos);
    }

    /**
     * Sets new sequence.
     *
     * @param sequence New value of sequence.
     */
    public void setSequence(String sequence)
    {
        this.sequence = sequence;
    }

    /**
     * Gets sequence.
     *
     * @return Value of sequence.
     */
    public String getSequence()
    {
        return sequence;
    }

    /**
     * Sets new strand.
     *
     * @param strand New value of strand.
     */
    public void setStrand(String strand)
    {
        this.strand = strand;
    }

    /**
     * Gets strand.
     *
     * @return Value of strand.
     */
    public String getStrand()
    {
        return strand;
    }

    /**
     * Sets new label.
     *
     * @param label New value of label.
     */
    public void setLabel(String label)
    {
        this.label = label;
    }

    /**
     * Gets label.
     *
     * @return Value of label.
     */
    public String getLabel()
    {
        return label;
    }

    /**
     * Sets new frame.
     *
     * @param frame New value of frame.
     */
    public void setFrame(int frame)
    {
        this.frame = frame;
    }

    /**
     * Gets frame.
     *
     * @return Value of frame.
     */
    public int getFrame()
    {
        return frame;
    }

    /**
     * Sets new startPos.
     *
     * @param startPos New value of startPos.
     */
    public void setStartPos(int startPos)
    {
        this.startPos = startPos;
    }

    /**
     * Gets startPos.
     *
     * @return Value of startPos.
     */
    public int getStartPos()
    {
        return startPos;
    }

    /**
     * Sets new endPos.
     *
     * @param endPos New value of endPos.
     */
    public void setEndPos(int endPos)
    {
        this.endPos = endPos;
    }

    /**
     * Gets endPos.
     *
     * @return Value of endPos.
     */
    public int getEndPos()
    {
        return endPos;
    }

    /**
     * Gets translation of a DNA sequence.
     *
     * @return Value of translated DNA sequence.
     */
    public String getTranslation()
    {
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
        try
        {
            for (int i = 0; i < this.sequence.length(); i += 3)
            {
                aminoAcid.append(codToAa.get(this.sequence.substring(i, i + 3)));
            }
        }
        catch (StringIndexOutOfBoundsException ignored)
        {
        }
        return aminoAcid.toString();
    }

    /**
     * Overrides toString method.
     *
     * @return A formatted String containing sequence, strand, label, frame. startPos and endPos.
     */
    @Override
    public String toString()
    {
        return String.format("ORF{sequence='%s', strand='%s', label='%s', frame=%d, startPos=%d, endPos=%d}", sequence, strand, label, frame, startPos, endPos);
    }

}

