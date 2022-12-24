package BytecodeTokenizer.classfileparser.nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class MethodBody {
    private String rawMethodBody;
    private Iterator<String> instructions;
    private ArrayList<String> instructionsInList;
    private boolean empty = true;

    public MethodBody(String rawMethodBody) {
//        System.out.println(rawMethodBody);
        ArrayList<String> instructionList = new ArrayList<>();
        if(rawMethodBody.trim().equals("")){
            this.empty = true;
        } else{
            this.empty = false;
        }
        this.rawMethodBody = rawMethodBody;
        rawMethodBody = rawMethodBody.trim().replace(";", " ");
        rawMethodBody = rawMethodBody.replaceAll("L\\d+:", "");
//        System.out.println(rawMethodBody);
        String[] splittedBySpace = rawMethodBody.split("\\s+");
        Instructions inst = new Instructions();
        HashSet<String> insts = inst.getOpcodes();
        for(String s : splittedBySpace){
            s = s.trim();
            if(s.startsWith("tableswitch") || s.startsWith("lookupswitch")){
                s = s.replace("{", "");
            }
            if(insts.contains(s)){
                instructionList.add(s);
            }
        }
        this.instructionsInList = instructionList;
        this.instructions = instructionList.iterator();
    }

    public String getRawMethodBody() {
        return rawMethodBody;
    }

    public Iterator<String> getInstructions() {
        return instructions;
    }

    public boolean isEmpty(){
        return empty;
    }

    public ArrayList<String> getInstructionsInList() {
        return instructionsInList;
    }
}
