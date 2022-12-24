package BytecodeTokenizer.classfileparser.nodes;

public class LocalVarSize {
    int localVarSize = -1;

    public LocalVarSize(String localVarSize) {
        this.localVarSize = Integer.parseInt(localVarSize.trim());
    }

    public int getLocalVarSize() {
        return localVarSize;
    }
}
