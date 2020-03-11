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
        return "top";
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

