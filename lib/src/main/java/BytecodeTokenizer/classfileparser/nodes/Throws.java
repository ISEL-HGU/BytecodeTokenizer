package BytecodeTokenizer.classfileparser.nodes;

public class Throws {
    private String throwsStmt = "";
    public Throws(String throwsStmt) {
        this.throwsStmt = throwsStmt;
    }

    public String getThrowsStmt() {
        return throwsStmt;
    }
}
