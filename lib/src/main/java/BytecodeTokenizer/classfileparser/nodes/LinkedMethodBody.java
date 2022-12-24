package BytecodeTokenizer.classfileparser.nodes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class LinkedMethodBody {
    private String rawMethodBody;
    private ArrayList<String> instructionsInList;
    private boolean empty = true;
    private enum LambdaState {HANDLE, METHOD, NONE}
    private enum InstState {INVOKE, METHOD, NONE}
//    private enum ParseState {ANNOTATIONS, METHOD_ACCESS, THROWS, STACK_SIZE, METHOD_BODY, DEFAULT}

    public LinkedMethodBody(String rawMethodBody, ArrayList<String> linkedClasses) {
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
        InstState state = InstState.NONE;
        LambdaState lState = LambdaState.NONE;
        for(String s : splittedBySpace){
            s = s.trim();
            // get tableswitch and lookupswitch opcodes by separating opcodes and "{"
            if(s.startsWith("tableswitch") || s.startsWith("lookupswitch")){
                s = s.replace("{", "");
            }
            // get instructions
            if(insts.contains(s)){
                instructionList.add(s);
            }
            // get lambda call
            // (ex) MethodHandle
            // REF_invokeStatic:Method
            // io/reactivex/rxjava3/internal/operators/flowable/FlowableObserveOnTest.lambda$syncFusedCancelAfterPoll$0:"(Lio/reactivex/rxjava3/subscribers/TestSubscriber;Ljava/lang/Integer;)Ljava/lang/Integer;",
            lState = addLambdaExpression(lState, s, instructionList);
            state = getLinkedInstructions(state, s, linkedClasses, instructionList);
        }
        this.instructionsInList = instructionList;
    }

    private LambdaState addLambdaExpression(LambdaState ls, String s, ArrayList<String> instructionList){
        if(s.equals("MethodHandle")){
            return LambdaState.HANDLE;
        }
        if(ls == LambdaState.HANDLE && s.contains("Method")){
            return LambdaState.METHOD;
        }
        String[] lambdaOne = s.split("\\.");
        if(lambdaOne.length > 1 ){
           String[] lambdaTwo = lambdaOne[1].split(":");
           if(lambdaTwo.length > 1){
               String lambdaExpression = lambdaTwo[0];
               if(lambdaExpression.contains("lambda$")){
                   instructionList.add(lambdaExpression);
               }
           }
        }
        return LambdaState.NONE;
    }

    private InstState getLinkedInstructions(InstState state, String s,
                                            ArrayList<String> linkedClasses, ArrayList<String> instructionList){
        if(s.equals("invokespecial")){
            return InstState.INVOKE;
        }
        if(state == InstState.INVOKE && s.equals("Method")){
            return InstState.METHOD;
        } else if(state  == InstState.METHOD && s.contains("$")){
            s = s.split("\\.", 2)[0];
            for(String path : linkedClasses){
                if(path.contains(s)){
                    instructionList.add(s);
//                    instructionList =  parseLinkedClasses(path, linkedClasses, instructionList);
                    return InstState.NONE;
                }
            }
            return InstState.NONE;
        } else {
            return InstState.NONE;
        }
    }

//    private ArrayList<String> parseLinkedClasses(String linkedPath, ArrayList<String> linkedClasses, ArrayList<String> instructionList){
//        ArrayList<MethodDeclaration> methods = new ArrayList<>();
//        ArrayList<LambdaMethod> lambdaMethods = new ArrayList<>();
//        int lambdaUsed = 0;
//        BufferedReader r = null;
//        ParseState s = ParseState.ANNOTATIONS;
//        int initFlag = 0;
//        try {
//            String l;
//            r = new BufferedReader(new FileReader(linkedPath));
//            MethodDeclaration m = new MethodDeclaration();
//            int blockFlag = -1;
//            int lambdaFlag = 0;
//            StringBuilder mb = new StringBuilder();
//            while ((l = r.readLine()) != null) {
//                if(l.equals("")) continue;
//                l = l.trim();
//                if(s == ParseState.ANNOTATIONS && l.startsWith("@")){
//                    s = ParseState.METHOD_ACCESS;
//                }
//                else if (s == ParseState.DEFAULT){
//                    for(char c : l.toCharArray()){
//                        if(c == '}'){
//                            blockFlag--;
//                        }
//                        if(blockFlag == 0){
//                            blockFlag = -1;
//                            s = ParseState.ANNOTATIONS;
//                        }
//                    }
//                }
//                else if ((s == ParseState.ANNOTATIONS || s == ParseState.METHOD_ACCESS) && !l.startsWith("@")){
//                    // ignore default methods
//                    if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*;")){
//                        s = ParseState.ANNOTATIONS;
//                    }
//                    else if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*")){
//                        if(l.contains("{")){
//                            blockFlag = 0;
//                        }
//                        for(char c : l.toCharArray()){
//                            if(c == '{'){
//                                blockFlag++;
//                            }
//                        }
//                        s = ParseState.DEFAULT;
//                    }
//                    // ignore method that has empty body
//                    else if(l.matches("[a-zA-Z ]+.Method.*;")){
//                        s = ParseState.ANNOTATIONS;
//                    }
//                    // method with modifiers
//                    else if(l.matches("[a-zA-Z ]+.Method.*")){
//                        String[] methodAccessAndNames = l.trim().split("Method",2);
//                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
//                        if(externalAndConstant[0].trim().contains("<init>")){
//                            initFlag = 1;
//                        }
//                        if(l.contains("lambda$")){
//                            lambdaFlag = 1;
//                        }
//                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
//                        s= ParseState.THROWS;
//                    }
//                    // method without modifiers
//                    else if(l.matches("Method.*")){
//                        String[] methodAccessAndNames = l.trim().split("Method ",2);
//                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
//                        if(externalAndConstant[0].trim().contains("<init>")){
//                            initFlag = 1;
//                        }
//                        if(l.contains("lambda$")){
//                            lambdaFlag = 1;
//                        }
//                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
//                        m.setConstantName(new ConstantName(externalAndConstant[1].trim()));
//                        s= ParseState.THROWS;
//                    }
//                }
//                else if(s == ParseState.THROWS && l.startsWith("throws")){
//                    if(l.matches(".*;")){
//                        s = ParseState.ANNOTATIONS;
//                    }
//                    else{
//                        s = ParseState.STACK_SIZE;
//                    }
//                }
//                else if(s == ParseState.THROWS || s == ParseState.STACK_SIZE){
//                    s = ParseState.METHOD_BODY;
//                }
//                else if(s == ParseState.METHOD_BODY){
//                    if(l.equals("{")){
//                        blockFlag = 0;
//                    }
//                    for(char c : l.toCharArray()){
//                        if(c == '{'){
////                            mb.append(c);
//                            blockFlag++;
//                        }
//                        else if(c == '}'){
////                            mb.append(c);
//                            blockFlag--;
//                        } else {
//                            mb.append(c);
//                        }
//                    }
//                    mb.append("\n");
//                    if(blockFlag == 0){
//                        m.setLinkedMethodBody(new LinkedMethodBody(mb.toString(),linkedClasses));
//                        if(lambdaFlag == 1){
//                            lambdaUsed = 1;
//                            if(m.getExternalName() != null){
//                                lambdaMethods.add(new LambdaMethod(m.getExternalName().getMethodName(), new LinkedMethodBody(mb.toString(), linkedClasses)));
//                            }
//                            lambdaFlag = 0;
//                        }
//                        if(initFlag == 0){
//                            blockFlag = -1;
//                            mb.setLength(0);
//                            s = ParseState.ANNOTATIONS;
//                        } else if(initFlag == 1){
//                            initFlag = 2;
//                            blockFlag = -1;
//                            mb.setLength(0);
//                            s = ParseState.ANNOTATIONS;
//                        } else {
//                            String tmpRawMethodBody = mb.toString();
//                            tmpRawMethodBody = tmpRawMethodBody.trim().replace(";", " ");
//                            tmpRawMethodBody = tmpRawMethodBody.replaceAll("L\\d+:", "");
//                            String[] splittedBySpace = tmpRawMethodBody.split("\\s+");
//                            Instructions inst = new Instructions();
//                            HashSet<String> insts = inst.getOpcodes();
//                            InstState state = InstState.NONE;
//                            LambdaState lState = LambdaState.NONE;
//                            for(String str : splittedBySpace){
//                                str = str.trim();
//                                if(str.startsWith("tableswitch") || str.startsWith("lookupswitch")){
//                                    str = str.replace("{", "");
//                                }
//                                if(insts.contains(str)){
//                                    instructionList.add(str);
//                                }
//                                lState = addLambdaExpression(lState, str, instructionList);
//                                state = getLinkedInstructions(state, str, linkedClasses, instructionList);
//                            }
//                            initFlag = 0;
//                            mb.setLength(0);
//                            blockFlag = -1;
//                            s = ParseState.ANNOTATIONS;
//                        }
//                        methods.add(m);
//                        m = new MethodDeclaration();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (r != null)
//                    r.close();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        if(lambdaUsed == 1){
//            this.lambdaLinkedInstructions = linkLambda(instructionList, lambdaMethods);
//        }
//        return instructionList;
//    }

//    private ArrayList<String> linkLambda(ArrayList<String> instructionsList, ArrayList<LambdaMethod> lambdaMethods){
//        ArrayList<String> tmpInstructions = new ArrayList<>();
//
//        for(String inst : instructionsList){
//            if(inst.contains("lambda$")){
//                for(LambdaMethod lm : lambdaMethods){
//                    if(lm.getMethodName() == null) continue;
//
//                    if(inst.equals(lm.getMethodName())){
//                        while(lm.getInstructions().hasNext()){
//                            tmpInstructions.add(lm.getInstructions().next());
//                        }
//                    }
//                }
//            } else{
//                tmpInstructions.add(inst);
//            }
//        }
//        return tmpInstructions;
//    }

    public String getRawMethodBody() {
        return rawMethodBody;
    }

    public ArrayList<String> getInstructionsInList() {
        return instructionsInList;
    }

    public void setInstructionsInList(ArrayList<String> instructionsInList) {
        this.instructionsInList = instructionsInList;
    }

    public boolean isEmpty(){
        return empty;
    }
}
