package BytecodeTokenizer.classfileparser.nodes;

public class MethodAccess {
    String[] modifiers;
    public MethodAccess(String modifier){
        if(modifier.equals("")){
            this.modifiers = new String[]{};
        }
        this.modifiers = modifier.split(" ");
    }

    public String[] getModifiers(){
        return modifiers;
    }
}
