package BytecodeTokenizer.classfileparser.nodes;

public class StackSize {
    private int stackSize = -1;

    public StackSize(String stackSize) {
        this.stackSize = Integer.parseInt(stackSize.trim());
    }

    public int getStackSize() {
        return stackSize;
    }
}
