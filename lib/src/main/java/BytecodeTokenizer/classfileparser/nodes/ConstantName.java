package BytecodeTokenizer.classfileparser.nodes;

import java.util.ArrayList;

public class ConstantName {
    private ArrayList<String> parameterFullTypes = new ArrayList<>();
    private ArrayList<String> parameterSimpleTypes= new ArrayList<>();

    public ConstantName(String constantName) {
        constantName = constantName.replace("\"", "");
        StringBuilder parameters = getParameters(constantName);
        String params = parameters.toString();
        if(!params.equals("")){
            for(String s: params.split(";")){
                if(!s.equals("")){
                    parameterFullTypes.add(s);
                    String[] tmpFullType = s.split("/");
                    parameterSimpleTypes.add(tmpFullType[tmpFullType.length - 1]);
                }
            }
        }
    }

    private StringBuilder getParameters(String constantName){
        StringBuilder parameters = new StringBuilder();
        int pFlag = 0;
        for(char c : constantName.toCharArray()){
            if(pFlag == 0 && c == '('){
                pFlag = 1;
            } else if(pFlag == 1 && c == ')'){
                pFlag = 0;
            } else if(pFlag == 1){
                parameters.append(c);
            }
        }
        return parameters;
    }

    public ArrayList<String> getParameterFullTypes() {
        return parameterFullTypes;
    }

    public ArrayList<String> getParameterSimpleTypes() {
        return parameterSimpleTypes;
    }
}
