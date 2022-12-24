package BytecodeTokenizer.classfileparser.nodes;

import java.util.ArrayList;

public class MethodDeclaration {
    Annotations annotations;
    ConstantName constantName;
    ExternalName externalName;
    LocalVarSize localVarSize;
    MethodAccess methodAccess;
    MethodBody methodBody;
    LinkedMethodBody linkedMethodBody;
    StackSize stackSize;
    Throws throwsStmt;

    public String getSignature() {
        StringBuilder methodSignature = new StringBuilder();
        methodSignature.append(this.externalName.getMethodName()).append("(");
        ArrayList<String> paramTypes = this.constantName.getParameterSimpleTypes();
        int numParam = paramTypes.size();
        int cnt = 0;
        for(String t : this.constantName.getParameterSimpleTypes()){
            cnt++;
            if(cnt == numParam){
                methodSignature.append(t);
            } else {
                methodSignature.append(t).append(", ");
            }
        }
        methodSignature.append(")");
        return methodSignature.toString();
    }

    public LinkedMethodBody getLinkedMethodBody() {
        return linkedMethodBody;
    }

    public void setLinkedMethodBody(LinkedMethodBody linkedMethodBody) {
        this.linkedMethodBody = linkedMethodBody;
    }

    public Annotations getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Annotations annotations) {
        this.annotations = annotations;
    }

    public ConstantName getConstantName() {
        return constantName;
    }

    public void setConstantName(ConstantName constantName) {
        this.constantName = constantName;
    }

    public ExternalName getExternalName() {
        return externalName;
    }

    public void setExternalName(ExternalName externalName) {
        this.externalName = externalName;
    }

    public LocalVarSize getLocalVarSize() {
        return localVarSize;
    }

    public void setLocalVarSize(LocalVarSize localVarSize) {
        this.localVarSize = localVarSize;
    }

    public MethodAccess getMethodAccess() {
        return methodAccess;
    }

    public void setMethodAccess(MethodAccess methodAccess) {
        this.methodAccess = methodAccess;
    }

    public MethodBody getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(MethodBody methodBody) {
        this.methodBody = methodBody;
    }

    public StackSize getStackSize() {
        return stackSize;
    }

    public void setStackSize(StackSize stackSize) {
        this.stackSize = stackSize;
    }

    public Throws getThrowsStmt() {
        return throwsStmt;
    }

    public void setThrowsStmt(Throws throwsStmt) {
        this.throwsStmt = throwsStmt;
    }
}
