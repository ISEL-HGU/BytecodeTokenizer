package BytecodeTokenizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TokenWriter {
    private String outputPath = "";

    public TokenWriter(String outputPath){
        if(outputPath.endsWith("/")){
            this.outputPath = outputPath.substring(0, outputPath.length()-1);
        } else {
            this.outputPath = outputPath;
        }
        createOutputPath(this.outputPath);
    }

    public void createOutputPath(String outputPath){
        File f = new File(outputPath);
        if(!f.exists()){
            f.mkdirs();
        }
    }

    public void writeOPCodes(int cnt, String methodSignature, Iterator<String> ts){
        BufferedWriter bw = null;
        String tmpOutputPath = outputPath + "_OPCodes.txt";

        try {
            File file = new File(tmpOutputPath);
            createFile(file);
            FileWriter fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
            bw.write("$$METHOD#"+ cnt + "\n"+ "$$METHOD_SIGNATURE:" + methodSignature + "\n");
            while(ts.hasNext()) {
                String token = ts.next();
                if(token != null)
                    bw.write(token + " ");
            }
            bw.write("\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try{
                if(bw!=null)
                    bw.close();
            }catch(Exception ex) {
                System.out.println("Error in closing the BufferedWriter"+ex);
            }
        }
    }

    public void createFile(File file){
        try{
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e){
            e.printStackTrace();
            System.err.println("ERROR: output file is not created!");
            System.exit(-1);
        }
    }
}
