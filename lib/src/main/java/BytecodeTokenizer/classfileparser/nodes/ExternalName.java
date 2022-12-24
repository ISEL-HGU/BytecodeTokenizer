package BytecodeTokenizer.classfileparser.nodes;

public class ExternalName {
    private String methodName = "";
    public ExternalName(String externalName) {
        this.methodName = externalName;
    }
    public String getMethodName(){
        return this.methodName;
    }
}
