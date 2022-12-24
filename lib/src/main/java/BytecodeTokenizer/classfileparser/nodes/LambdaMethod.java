package BytecodeTokenizer.classfileparser.nodes;

import java.util.ArrayList;

public class LambdaMethod {
    String methodName;
    ArrayList<String> instructionsList;

    public LambdaMethod(String methodName, LinkedMethodBody m) {
        this.methodName = methodName;
        this.instructionsList = m.getInstructionsInList();
    }

    public void linkLambda(ArrayList<LambdaMethod> lambdaMethods){
        ArrayList<String> tmpInstructions = new ArrayList<>();
            for(String inst : this.instructionsList){
                if(inst.contains("lambda$")){
                    for(LambdaMethod lm : lambdaMethods){
                        if(inst.equals(lm.getMethodName())){
                            tmpInstructions.addAll(lm.getInstructionsList());
                        }
                    }
                } else{
                    tmpInstructions.add(inst);
                }
            }
            this.instructionsList = tmpInstructions;
    }

    public ArrayList<String> getInstructionsList() {
        return instructionsList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setInstructionsList(ArrayList<String> instructionsList) {
        this.instructionsList = instructionsList;
    }
}
