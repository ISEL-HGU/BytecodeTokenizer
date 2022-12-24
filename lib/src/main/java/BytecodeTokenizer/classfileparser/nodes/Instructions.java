package BytecodeTokenizer.classfileparser.nodes;

import java.util.Arrays;
import java.util.HashSet;

public class Instructions {
    HashSet<String> opcodes;

    public Instructions() {
        opcodes = new HashSet<>(Arrays.asList(
                "invokespecial",
                // no param
                "aaload",  "aastore",  "aconst_null",  "aload_0",  "aload_1",  "aload_2",  "aload_3",  "aload_w",
                "areturn",  "arraylength",  "astore_0",  "astore_1",  "astore_2",  "astore_3",  "astore_w",  "athrow",
                "baload",  "bastore",  "caload",  "castore",  "d2f",  "d2i",  "d2l",  "dadd",  "daload",  "dastore",
                "dcmpg",  "dcmpl",  "dconst_0",  "dconst_1",  "ddiv",  "dead", "dload_0",  "dload_1", "dload_2",
                "dload_3",  "dload_w",  "dmul", "dneg", "drem", "dreturn", "dstore_0",  "dstore_1",  "dstore_2",
                "dstore_3",  "dstore_w", "dsub",  "dup",  "dup2",  "dup2_x1",  "dup2_x2",  "dup_x1",  "dup_x2",
                "f2d", "f2i",  "f2l",  "fadd", "faload", "fastore", "fcmpg", "fcmpl", "fconst_0", "fconst_1",
                "fconst_2", "fdiv",  "fload_0", "fload_1", "fload_2", "fload_3", "fload_w", "fmul", "fneg", "frem",
                "freturn",  "fstore_0",  "fstore_1", "fstore_2", "fstore_3", "fstore_w", "fsub", "i2b", "i2c", "i2d",
                "i2f", "i2l", "i2s", "iadd", "iaload", "iand", "iastore", "iconst_0", "iconst_1", "iconst_2",
                "iconst_3", "iconst_4", "iconst_5", "iconst_m1", "idiv", "iinc_w", "iload_0", "iload_1", "iload_2",
                "iload_3", "iload_w", "imul", "ineg", "int2byte", "int2char", "int2short", "ior", "irem", "ireturn",
                "ishl", "ishr", "istore_0", "istore_1", "istore_2", "istore_3", "istore_w", "isub", "iushr", "ixor",
                "l2d",  "l2f", "l2i", "label", "ladd", "laload", "land", "lastore", "lcmp", "lconst_0", "lconst_1",
                "ldiv", "lload_0", "lload_1", "lload_2", "lload_3", "lload_w", "lmul", "lneg", "lor", "lrem", "lreturn",
                "lshl", "lshr", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "lstore_w", "lsub", "lushr", "lxor",
                "monitorenter", "monitorexit", "nonpriv", "nop", "pop", "pop2", "priv", "ret", "return", "ret_w",
                "saload",  "sastore",  "swap", "wide",
                // one param: local variable
                "aload", "astore", "fload", "fstore", "iload", "istore", "lload", "lstore", "dload", "dstore", "ver",
                "endvar",
                // one param: number
                "sipush", "bipush", "bytecode",
                // one param: switch table, NOTE: tableswitch and lookupswitch precede { without any space
                "tableswitch", "lookupswitch",
                // one param: type
                "newarray",
                // one param: label
                "jsr", "goto", "ifeq", "ifge", "ifgt", "ifle", "iflt", "ifne", "if_icmpeq", "if_icmpne", "if_icmpge",
                "if_icmpgt", "if_icmple", "if_icmplt", "if_acmpeq", "if_acmpne", "ifnull", "ifnonnull", "try", "endtry",
                "jsr_w", "goto_w",
                // one param: constant cell
                "ldc_w", "ldc2_w", "ldc",
                // one param: constant cell (class)
                "new", "anewarray", "instanceof", "checkcast",
                // one param: constant cell (field)
                "putstatic", "getstatic", "putfield", "getfield",
                // one param: constant cell (method)
                "invokevirtual", "invokenonvirtual", "invokestatic",
                // one param: constant cell (InvokeDynamic)
                "invokedynamic",
                // two params: number, constant cell (class)
                "multianewarray",
                // two params: number, constant cell (method)
                "invokeinterface",
                // two params: local variable, number
                "iinc"
        ));
    }

    public HashSet<String> getOpcodes() {
        return opcodes;
    }
}
