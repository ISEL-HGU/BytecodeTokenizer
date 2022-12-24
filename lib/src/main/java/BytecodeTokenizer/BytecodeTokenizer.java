package BytecodeTokenizer;

import BytecodeTokenizer.classfileparser.ClassFileParser;
import BytecodeTokenizer.linkedclass.LinkedClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class BytecodeTokenizer {
    ArrayList<String> list = new ArrayList<>();

    public BytecodeTokenizer(String fileListPath){
        readFileList(fileListPath);
        sort();
        for(String s : list){
            System.out.println(s);
        }
        System.out.println("Number of files: " + list.size());
    }

    private void readFileList(String fileListPath){
        BufferedReader r = null;
        try {
            String l;
            r = new BufferedReader(new FileReader(fileListPath));
            while ((l = r.readLine()) != null) {
                if(l.equals("")) continue;
                list.add(l);
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
    }

    private void sort(){
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                String tmpA = getClassName(a);
                String tmpB = getClassName(b);
                StringBuilder sb = new StringBuilder();
                int len = Math.min(tmpA.length(), tmpB.length());

                // file name based on the alphabetical order
                for(int i = 0; i < len; i ++){
                    if((int) tmpA.charAt(i) < (int) tmpB.charAt(i)){
                        return -1;
                    } else if ((int) tmpA.charAt(i) > tmpB.charAt(i)) {
                        return 1;
                    }
                }

                if(tmpA.length() != tmpB.length()){
                    return tmpA.length() - tmpB.length();
                }

                //if two file names are the same, the file contains "$" precede.
                if(a.contains("$") && !b.contains("$")){
                    return -1;
                } else if(!a.contains("$") && b.contains("$")){
                    return 1;
                }

                // if two file contain "$", compare based on the number
                else{
                    double aNum = Double.parseDouble(getNumPart(a));
                    double bNum = Double.parseDouble(getNumPart(b));
                    return Double.compare(aNum, bNum);
                }
            }
        });
    }

    private String getClassName(String p){
        StringBuilder sb = new StringBuilder();

        if(p.contains("$")){
            for(String s:p.split("\\$")){
                s = s.replace(".txt","");
                if(!s.matches("\\d+")){
                    sb.append(s).append("$");
                }
            }
            p = sb.toString();
        } else {
            p = p.replace(".txt","");
        }

        if(p.endsWith("$")){
            p = p.substring(0, p.length()-1);
        }
        return p;
    }

    private String getNumPart(String n){
        StringBuilder sb = new StringBuilder();
        for(String s:n.split("\\$")){
            s = s.replace(".txt", "");
            if(s.matches("\\d+")){
                sb.append(s).append("$");
            }
        }
        n = sb.toString();
        if(n.endsWith("$")){
            n = n.substring(0, n.length()-1).replace("$", ".");
        }

        if(n.split("\\.").length > 2){
            StringBuilder newNum = new StringBuilder();
            int dotCnt = 0;
            for(char c : n.toCharArray()){
                if(c != '.') {
                    newNum.append(c);
                }
                else if(dotCnt == 0){
                    dotCnt++;
                    newNum.append(c);
                }
                else if(dotCnt > 0){
                    newNum.append("000");
                }
            }
            n = newNum.toString();
        }
        if(n.equals("")){
            n = "0";
        }
        return n;
    }

    public void linkTokenize(String outputPath){
        Iterator<String> fileList = list.iterator();
        list = null;
        Iterator<LinkedClass> lcIterator = link(fileList);
        ClassFileParser p = new ClassFileParser();
        int numEmpty = 0;
        while(lcIterator.hasNext()){
            LinkedClass lc = lcIterator.next();
            p.parseLinkedClassFile(lc);
            numEmpty += p.OPtokenize(outputPath);
        }
        System.out.println("number of empty methods: "+ numEmpty);
    }
    private Iterator<LinkedClass> link(Iterator<String> fileList){
        ArrayList<LinkedClass> lcList = new ArrayList<>();
        LinkedClass lc = new LinkedClass();
        while(fileList.hasNext()){
            String fileName = fileList.next();
            String className = getClassName(fileName);
            String numPart = getNumPart(fileName);

            if(numPart.equals("0")){
                lc.setClassName(className);
                lc.setClassFileName(fileName);
                lcList.add(lc);
                lc = new LinkedClass();
            } else{
                lc.addLinkedClassName(fileName);
            }
        }
        return lcList.iterator();
    }

    public void tokenize(String outputPath){
        int numEmpty = 0;
        Iterator<String> fileList = list.iterator();
        ClassFileParser p = new ClassFileParser();
        while(fileList.hasNext()){
            String f = fileList.next();
            p.parseClassFile(f);
            numEmpty += p.OPtokenize(outputPath);
        }
        System.out.println("number of empty methods: "+ numEmpty);
    }
}
