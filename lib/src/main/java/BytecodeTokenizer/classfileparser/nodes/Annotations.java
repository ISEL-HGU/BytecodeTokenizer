package BytecodeTokenizer.classfileparser.nodes;

public class Annotations {
    String code = "";
    public Annotations(String code){
        this.code =code;
    }

    public String getCode(){
        return this.code;
    }
}
