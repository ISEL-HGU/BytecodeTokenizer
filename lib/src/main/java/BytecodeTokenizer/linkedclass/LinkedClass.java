package BytecodeTokenizer.linkedclass;

import java.util.ArrayList;

public class LinkedClass {
    String classFileName;
    String className;
    ArrayList<String> lc = new ArrayList<>();

    public boolean isEmpty(){
        if(className == null || className.equals("")){
            return true;
        } else return false;
    }

    public void addLinkedClassName(String className){
        lc.add(className);
    }

    public void setClassFileName(String classFileName) {
        this.classFileName = classFileName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void clear(){
        className = null;
        lc = new ArrayList<>();
    }

    public String getClassName() {
        return className;
    }

    public String getClassFileName() {
        return classFileName;
    }

    public ArrayList<String> getLc() {
        return lc;
    }
}
