package BytecodeTokenizer.classfileparser;

import BytecodeTokenizer.TokenWriter;
import BytecodeTokenizer.classfileparser.nodes.*;
import BytecodeTokenizer.linkedclass.LinkedClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class ClassFileParser {
    Iterator<MethodDeclaration> methodIterator;
    String fileName = "";
    int cnt = 0;

    public int OPtokenize(String outputPath){
        int numEmptyMethod = 0;
        TokenWriter tw = new TokenWriter(outputPath);

        //methodListIterator is null
        if(methodIterator == null){
            return 0;
        }

        while(methodIterator.hasNext()){
            MethodDeclaration md = methodIterator.next();
            String methodSignature = fileName + ":" + md.getSignature();

            if(md.getMethodBody() != null){
                if(!md.getMethodBody().isEmpty() || !md.getMethodBody().getRawMethodBody().matches("\\s+") ){
                    cnt++;
                    tw.writeOPCodes(cnt, methodSignature, md.getMethodBody().getInstructions());
                }
                else {
                    numEmptyMethod ++;
                }
            } else {
                if(!md.getLinkedMethodBody().isEmpty() || !md.getLinkedMethodBody().getRawMethodBody().matches("\\s+") ){
                    cnt++;
                    tw.writeOPCodes(cnt, methodSignature, md.getLinkedMethodBody().getInstructionsInList().iterator());
                }
                else {
                    numEmptyMethod ++;
                }
            }
            System.out.println(cnt);
        }
        return numEmptyMethod;
    }

    /* Method declaration is a sequence of:
    ANNOTATIONS METHOD_ACCESS Method EXTERNAL_NAME:CONSTANT_NAME
    [THROWS]
    STACK_SIZE [LOCAL_VAR_SIZE]
    {
    INSTRUCTION_STATEMENT...
    }
     */
    private enum State {ANNOTATIONS, METHOD_ACCESS, THROWS, STACK_SIZE, METHOD_BODY, DEFAULT}

    public void parseLinkedClassFile(LinkedClass lc){
        this.methodIterator = null;
        this.fileName = "";
        this.fileName = generateFileName(lc.getClassFileName());
        this.methodIterator = parse(lc);
    }

    private Iterator<MethodDeclaration> parse(LinkedClass lc){
        ArrayList<MethodDeclaration> methods = new ArrayList<>();
        ArrayList<LambdaMethod> lambdaMethods = new ArrayList<>();
        int lambdaUsed = 0;
        BufferedReader r = null;
        State s = State.ANNOTATIONS;
        try {
            String l;
            r = new BufferedReader(new FileReader(lc.getClassFileName()));
            MethodDeclaration m = new MethodDeclaration();
            StringBuilder mb = new StringBuilder();
            int blockFlag = -1;
            int lambdaFlag = 0;
            while ((l = r.readLine()) != null) {
                if(l.equals("")) continue;
                l = l.trim();
                if(s == State.ANNOTATIONS && l.startsWith("@")){
                    m.setAnnotations(new Annotations(l));
                    s = State.METHOD_ACCESS;
                }
                else if (s == State.DEFAULT){
                    for(char c : l.toCharArray()){
                        if(c == '}'){
                            blockFlag--;
                        }
                        if(blockFlag == 0){
                            blockFlag = -1;
                            s = State.ANNOTATIONS;
                        }
                    }
                }
                else if ((s == State.ANNOTATIONS || s == State.METHOD_ACCESS) && !l.startsWith("@")){
                    if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*;")){
                        s = State.ANNOTATIONS;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*")){
                        if(l.contains("{")){
                            blockFlag = 0;
                        }
                        for(char c : l.toCharArray()){
                            if(c == '{'){
                                blockFlag++;
                            }
                        }
                        s = State.DEFAULT;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*;")){
                        s = State.ANNOTATIONS;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*")){
                        if(l.contains("lambda$")){
                            lambdaFlag = 1;
                        }
                        String[] methodAccessAndNames = l.trim().split("Method",2);
                        m.setMethodAccess(new MethodAccess(methodAccessAndNames[0].trim()));
                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
                        m.setConstantName(new ConstantName(externalAndConstant[1].trim()));
                        s= State.THROWS;
                    }
                    else if(l.matches("Method.*")){
                        if(l.contains("lambda$")){
                            lambdaFlag = 1;
                        }
                        String[] methodAccessAndNames = l.trim().split("Method ",2);
                        m.setMethodAccess(new MethodAccess(methodAccessAndNames[0].trim()));
                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
                        m.setConstantName(new ConstantName(externalAndConstant[1].trim()));
                        s= State.THROWS;
                    }
                }
                else if(s == State.THROWS && l.startsWith("throws")){
                    if(l.matches(".*;")){
                        s =State.ANNOTATIONS;
                    }
                    else{
                        m.setThrowsStmt(new Throws(l));
                        s = State.STACK_SIZE;
                    }
                }
                else if(s == State.THROWS || s == State.STACK_SIZE){
                    String[] stackSizeAndLocalVarSize = l.split("\\s+");
                    // case of only stack size exists
                    if(stackSizeAndLocalVarSize.length == 2){
                        m.setStackSize(new StackSize(stackSizeAndLocalVarSize[1]));
                    }
                    // case of stack size and local variable size exist
                    else {
                        m.setStackSize(new StackSize(stackSizeAndLocalVarSize[1]));
                        m.setLocalVarSize(new LocalVarSize(stackSizeAndLocalVarSize[3]));
                    }
                    s = State.METHOD_BODY;
                }
                else if(s == State.METHOD_BODY){
                    if(l.equals("{")){
                        blockFlag = 0;
                    }
                    for(char c : l.toCharArray()){
                        if(c == '{'){
//                            mb.append(c);
                            blockFlag++;
                        }
                        else if(c == '}'){
//                            mb.append(c);
                            blockFlag--;
                        } else {
                            mb.append(c);
                        }
                    }
                    mb.append("\n");
                    if(blockFlag == 0){
                        // if method name contains lambda,
                        // store their names and instructions in additional places
                        // --> in instruction List, store lambda expression
                        if(lambdaFlag == 1){
                            lambdaUsed = 1;
                            lambdaMethods.add(new LambdaMethod(m.getExternalName().getMethodName(), new LinkedMethodBody(mb.toString(), lc.getLc())));
                            lambdaFlag = 0;
                        }
                        m.setLinkedMethodBody(new LinkedMethodBody(mb.toString(), lc.getLc()));

                        methods.add(m);
                        m = new MethodDeclaration();
                        mb.setLength(0);
                        blockFlag = -1;
                        s = State.ANNOTATIONS;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // resolve lambda
        // --> traverse instruction lists, and when there is lambda expression,
        //     resolve the lambda as stored lambda expressions.
        if(lambdaUsed == 1){
            // need to link lambda methods before linking
            lambdaMethods.iterator().forEachRemaining(l -> {
                l.linkLambda(lambdaMethods);
            });
            linkLambda(methods, lambdaMethods);
            methods = removeLambda(methods);
        }
        getLinkedInstructions(methods, lc);
        return methods.iterator();
    }

    private void getLinkedInstructions(ArrayList<MethodDeclaration> methods, LinkedClass lc){
        // get instructions of linked classes
        HashSet<String> inst = new Instructions().getOpcodes();
        ArrayList<String> tmpInstructionList = new ArrayList<>();
        for(MethodDeclaration m : methods){
            for(String opcode : m.getLinkedMethodBody().getInstructionsInList()){
                if(!inst.contains(opcode)){
                    for(String linkedPath : lc.getLc()){
                        if(linkedPath.contains(opcode)){
                            lc.setClassFileName(linkedPath);
                            Iterator<MethodDeclaration> linkedMethods = parse(lc);
                            ArrayList<String> methodSignatures = new ArrayList<>();
                            ArrayList<String> methodNames = new ArrayList<>();
                            // add instructions in tmpInstructionList
                            while(linkedMethods.hasNext()){
                                MethodDeclaration linkedMethod = linkedMethods.next();
                                // pass <init>
                                if(linkedMethod.getExternalName().getMethodName().contains("<init>")){
                                    continue;
                                } else {
                                    // if No method names
                                    // add all instructions in the method
                                    if(!methodNames.contains(linkedMethod.getExternalName().getMethodName())){
                                        tmpInstructionList.addAll(linkedMethod.getLinkedMethodBody().getInstructionsInList());
                                        methodNames.add(linkedMethod.getExternalName().getMethodName());
                                    }
                                    // if name is matched, but the parameter types are not Object,
                                    // then add all instructions in the method
                                    else if(!methodSignatures.contains(linkedMethod.getSignature())
                                            && !linkedMethod.getConstantName().getParameterSimpleTypes().contains("Object")){
                                        tmpInstructionList.addAll(linkedMethod.getLinkedMethodBody().getInstructionsInList());
                                        methodSignatures.add(linkedMethod.getSignature());
                                    }
                                }
                            }

                        }
                    }
                } else{
                    tmpInstructionList.add(opcode);
                }
            }
            m.getLinkedMethodBody().setInstructionsInList(tmpInstructionList);
            tmpInstructionList = new ArrayList<>();
        }
    }

    private void linkLambda(ArrayList<MethodDeclaration> methods, ArrayList<LambdaMethod> lambdaMethods){
        ArrayList<String> tmpInstructions = new ArrayList<>();
        for(MethodDeclaration m : methods){
            for(String inst : m.getLinkedMethodBody().getInstructionsInList()){
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

            m.getLinkedMethodBody().setInstructionsInList(tmpInstructions);
            tmpInstructions = new ArrayList<>();
        }
    }

    private ArrayList<MethodDeclaration> removeLambda(ArrayList<MethodDeclaration> methods){
        ArrayList<MethodDeclaration> tmpMethods = new ArrayList<>();
        for(MethodDeclaration md : methods){
            if(!md.getExternalName().getMethodName().contains("lambda$")){
                tmpMethods.add(md);
            }
        }
        return tmpMethods;
    }

    public void parseClassFile(String path){
        this.methodIterator = null;
        this.fileName = "";
        this.fileName = generateFileName(path);
        this.methodIterator = parse(path);
    }

    private Iterator<MethodDeclaration> parse(String path){
        ArrayList<MethodDeclaration> methods = new ArrayList<>();
        BufferedReader r = null;
        State s = State.ANNOTATIONS;
        try {
            String l;
            r = new BufferedReader(new FileReader(path));
            MethodDeclaration m = new MethodDeclaration();
            StringBuilder mb = new StringBuilder();
            int blockFlag = -1;
            while ((l = r.readLine()) != null) {
                if(l.equals("")) continue;
                l = l.trim();
                if(s == State.ANNOTATIONS && l.startsWith("@")){
                    m.setAnnotations(new Annotations(l));
                    s = State.METHOD_ACCESS;
                }
                else if (s == State.DEFAULT){
                    for(char c : l.toCharArray()){
                        if(c == '}'){
                            blockFlag--;
                        }
                        if(blockFlag == 0){
                            blockFlag = -1;
                            s = State.ANNOTATIONS;
                        }
                    }
                }
                else if ((s == State.ANNOTATIONS || s == State.METHOD_ACCESS) && !l.startsWith("@")){
                    if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*;")){
                        s = State.ANNOTATIONS;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*\\sdefault\\s.*")){
                        if(l.contains("{")){
                            blockFlag = 0;
                        }
                        for(char c : l.toCharArray()){
                            if(c == '{'){
                                blockFlag++;
                            }
                        }
                        s = State.DEFAULT;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*;")){
                        s = State.ANNOTATIONS;
                    }
                    else if(l.matches("[a-zA-Z ]+.Method.*")){
                        String[] methodAccessAndNames = l.trim().split("Method",2);
                        m.setMethodAccess(new MethodAccess(methodAccessAndNames[0].trim()));
                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
                        m.setConstantName(new ConstantName(externalAndConstant[1].trim()));
                        s= State.THROWS;
                    }
                    else if(l.matches("Method.*")){
                        String[] methodAccessAndNames = l.trim().split("Method ",2);
                        m.setMethodAccess(new MethodAccess(methodAccessAndNames[0].trim()));
                        String[] externalAndConstant = methodAccessAndNames[1].split(":");
                        m.setExternalName(new ExternalName(externalAndConstant[0].trim()));
                        m.setConstantName(new ConstantName(externalAndConstant[1].trim()));
                        s= State.THROWS;
                    }
                }
                else if(s == State.THROWS && l.startsWith("throws")){
                    if(l.matches(".*;")){
                        s =State.ANNOTATIONS;
                    }
                    else{
                        m.setThrowsStmt(new Throws(l));
                        s = State.STACK_SIZE;
                    }
                }
                else if(s == State.THROWS || s == State.STACK_SIZE){
                    String[] stackSizeAndLocalVarSize = l.split("\\s+");
                    // case of only stack size exists
                    if(stackSizeAndLocalVarSize.length == 2){
                        m.setStackSize(new StackSize(stackSizeAndLocalVarSize[1]));
                    }
                    // case of stack size and local variable size exist
                    else {
                        m.setStackSize(new StackSize(stackSizeAndLocalVarSize[1]));
                        m.setLocalVarSize(new LocalVarSize(stackSizeAndLocalVarSize[3]));
                    }
                    s = State.METHOD_BODY;
                }
                else if(s == State.METHOD_BODY){
                    if(l.equals("{")){
                        blockFlag = 0;
                    }
                    for(char c : l.toCharArray()){
                        if(c == '{'){
//                            mb.append(c);
                            blockFlag++;
                        }
                        else if(c == '}'){
//                            mb.append(c);
                            blockFlag--;
                        } else {
                            mb.append(c);
                        }
                    }
                    if(blockFlag == 0){
                        m.setMethodBody(new MethodBody(mb.toString()));
                        methods.add(m);
                        m = new MethodDeclaration();
                        mb.setLength(0);
                        blockFlag = -1;
                        s = State.ANNOTATIONS;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (r != null)
                    r.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return methods.iterator();
    }

    private String generateFileName(String path){
        String fileName = "";
        String[] p = path.split("/");
        int pLength = p.length;
//        if(pLength > 3 && !p[pLength - 4].equals("Java")){
//            fileName = p[pLength - 4] + "/" + p[pLength - 3] + "/" + p[pLength-2] + "/" + p[pLength-1];
//        }
//        else if (pLength > 2 && !p[pLength - 3].equals("Java")){
//            fileName = p[pLength - 3] + "/" + p[pLength-2] + "/" + p[pLength-1];
//        }
        if (pLength > 1 && !p[pLength - 2].equals("Java")){
            fileName = p[pLength-2] + "/" + p[pLength-1];
        }
        else {
            fileName = p[pLength-1];
        }
        return fileName;
    }

    private String getMethodSignature(String path){
        String methodSignature = "";

        return methodSignature;
    }
}
