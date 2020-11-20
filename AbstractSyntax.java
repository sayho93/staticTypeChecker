// Abstract syntax for the language C++Lite,
// exactly as it appears in Appendix B.

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

class Program {
	
    // Program = Declarations decpart ; Block body
    Declarations decpart;
    Block body;
    PrintWriter pw;
    Program (Declarations d, Block b) {
        decpart = d;
        body = b;
    }
    
    void display(String path) throws IOException {
    	String Realpath = path.substring(0, path.length()-4);
        pw = new PrintWriter(new BufferedWriter(new FileWriter(Realpath + ".output", true)));
//    	pw = new PrintWriter(new FileWriter(Realpath + ".output"), true);
//    	pw = new PrintWriter(Realpath+".output");
    	pw.println("Begin parsing... programs/"+path + "\n");
    	pw.println("Program (abstract syntax):");
    	PrintDeclarations(this.decpart);
    	body.display(1, pw);
        pw.close();
		// student exercise
   	}
    void PrintDeclarations(Declarations d){
    	pw.println("\tDeclarations:");
    	pw.print("\t\tDeclaration = {");
    	for(int i = 0;i<d.size();i++){
    		pw.print("<");
    		pw.print(d.get(i).v.toString()+","+d.get(i).t.toString()+">");
    		if(i!=d.size()-1) pw.print(", ");
    	}
    	pw.println("}");
    }

}

class Declarations extends ArrayList<Declaration> {
    // Declarations = Declaration*
    // (a list of declarations d1, d2, ..., dn)

}


class Declaration {
// Declaration = Variable v; Type t
    Variable v;
    Type t;

    Declaration (Variable var, Type type) {
        v = var; t = type;
    } // declaration */

}

class Type {
    // Type = int | bool | char | float 
     static Type INT = new Type("int");
     static Type BOOL = new Type("bool");
     static Type CHAR = new Type("char");
     static Type FLOAT = new Type("float");
    // final static Type UNDEFINED = new Type("undef");
    
    private final String id;

    Type (String t) { id = t; }

    public String toString ( ) { return id; }
}

abstract class Statement {
    // Statement = Skip | Block | Assignment | Conditional | Loop
	public void display(int k, PrintWriter pw){
		pw.println("::");
	}
}

class Skip extends Statement {
}

class Block extends Statement {
    // Block = Statement*
    //         (a Vector of members)
    public ArrayList<Statement> members = new ArrayList<Statement>();
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Block:");
    	for(int i = 0;i<members.size();i++){
    		members.get(i).display(k+1, pw);
    	}
    }
}

class Assignment extends Statement {
    // Assignment = Variable target; Expression source
    Variable target;
    Expression source;

    Assignment (Variable t, Expression e) {
        target = t;
        source = e;
    }
    
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Assignment:");
    	target.display(k+1, pw);
    	source.display(k+1, pw);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "target=" + target +
                ", source=" + source +
                '}';
    }
}

class Conditional extends Statement {
// Conditional = Expression test; Statement thenbranch, elsebranch
    Expression test;
    Statement thenbranch, elsebranch;
    // elsebranch == null means "if... then"
    
    Conditional (Expression t, Statement tp) {
        test = t; thenbranch = tp; elsebranch = new Skip( );
    }
    
    Conditional (Expression t, Statement tp, Statement ep) {
        test = t; thenbranch = tp; elsebranch = ep;
    }
    
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Conditional:");
    	test.display(k+1, pw);
    	thenbranch.display(k+1, pw);
    	elsebranch.display(k+1, pw);
    }
}

class Loop extends Statement {
// Loop = Expression test; Statement body
    Expression test;
    Statement body;

    Loop (Expression t, Statement b) {
        test = t; body = b;
    }
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Loop:");
    	test.display(k+1, pw);
    	body.display(k+1, pw);
    }
    
}

abstract class Expression {
    // Expression = Variable | Value | Binary | Unary
	public void display(int k, PrintWriter pw){
	}
}

class Variable extends Expression {
    // Variable = String id
    private String id;

    Variable (String s) { id = s; }

    public String toString( ) { return id; }
    
    public boolean equals (Object obj) {
        String s = ((Variable) obj).id;
        return id.equals(s); // case-sensitive identifiers
    }
    
    public int hashCode ( ) { return id.hashCode( ); }
    
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Variable: "+ id);
    }

}

abstract class Value extends Expression {
    // Value = IntValue | BoolValue |
    //         CharValue | FloatValue
    protected Type type;
    protected boolean undef = true;

    int intValue ( ) {
        assert false : "should never reach here";
        return 0;
    }
    
    boolean boolValue ( ) {
        assert false : "should never reach here";
        return false;
    }
    
    char charValue ( ) {
        assert false : "should never reach here";
        return ' ';
    }
    
    float floatValue ( ) {
        assert false : "should never reach here";
        return 0.0f;
    }

    boolean isUndef( ) { return undef; }

    Type type ( ) { return type; }

    static Value mkValue (Type type) {
        if (type == Type.INT) return new IntValue( );
        if (type == Type.BOOL) return new BoolValue( );
        if (type == Type.CHAR) return new CharValue( );
        if (type == Type.FLOAT) return new FloatValue( );
        throw new IllegalArgumentException("Illegal type in mkValue");
    }
    
    public void display(int k, PrintWriter pw){
    }
}

class IntValue extends Value {
    private int value = 0;

    IntValue ( ) { type = Type.INT; }

    IntValue (int v) { this( ); value = v; undef = false; }

    int intValue ( ) {
        assert !undef : "reference to undefined int value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("IntValue: "+ Integer.toString(value));
    }

}

class BoolValue extends Value {
    private boolean value = false;

    BoolValue ( ) { type = Type.BOOL; }

    BoolValue (boolean v) { this( ); value = v; undef = false; }

    boolean boolValue ( ) {
        assert !undef : "reference to undefined bool value";
        return value;
    }

    int intValue ( ) {
        assert !undef : "reference to undefined bool value";
        return value ? 1 : 0;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("BoolValue: "+ Boolean.toString(value));
    }

}

class CharValue extends Value {
    private char value = ' ';

    CharValue ( ) { type = Type.CHAR; }

    CharValue (char v) { this( ); value = v; undef = false; }

    char charValue ( ) {
        assert !undef : "reference to undefined char value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("CharValue: "+ value);
    }

}

class FloatValue extends Value {
    private float value = 0;

    FloatValue ( ) { type = Type.FLOAT; }

    FloatValue (float v) { this( ); value = v; undef = false; }

    float floatValue ( ) {
        assert !undef : "reference to undefined float value";
        return value;
    }

    public String toString( ) {
        if (undef)  return "undef";
        return "" + value;
    }
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("FloatValue: "+ Float.toString(value));
    }

}

class Binary extends Expression {
// Binary = Operator op; Expression term1, term2
    Operator op;
    Expression term1, term2;

    Binary (Operator o, Expression l, Expression r) {
        op = o; term1 = l; term2 = r;
    } // binary
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Binary:");
    	op.display(k+1, pw);
    	term1.display(k+1, pw);
    	term2.display(k+1, pw);
    }

    @Override
    public String toString() {
        return "Binary{" +
                "op=" + op +
                ", term1=" + term1 +
                ", term2=" + term2 +
                '}';
    }
}

class Unary extends Expression {
    // Unary = Operator op; Expression term
    Operator op;
    Expression term;

    Unary (Operator o, Expression e) {
        op = o; term = e;
    } // unary
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Unary:");
    	op.display(k+1, pw);
    	term.display(k+1, pw);
    }

    @Override
    public String toString() {
        return "Unary{" +
                "op=" + op +
                ", term=" + term +
                '}';
    }
}

class Operator {
    // Operator = BooleanOp | RelationalOp | ArithmeticOp | UnaryOp
    // BooleanOp = && | ||
    final static String AND = "&&";
    final static String OR = "||";
    // RelationalOp = < | <= | == | != | >= | >
    final static String LT = "<";
    final static String LE = "<=";
    final static String EQ = "==";
    final static String NE = "!=";
    final static String GT = ">";
    final static String GE = ">=";
    // ArithmeticOp = + | - | * | /
    final static String PLUS = "+";
    final static String MINUS = "-";
    final static String TIMES = "*";
    final static String DIV = "/";
    // UnaryOp = !    
    final static String NOT = "!";
    final static String NEG = "-";
    // CastOp = int | float | char
    final static String INT = "int";
    final static String FLOAT = "float";
    final static String CHAR = "char";
    // Typed Operators
    // RelationalOp = < | <= | == | != | >= | >
    final static String INT_LT = "INT<";
    final static String INT_LE = "INT<=";
    final static String INT_EQ = "INT==";
    final static String INT_NE = "INT!=";
    final static String INT_GT = "INT>";
    final static String INT_GE = "INT>=";
    // ArithmeticOp = + | - | * | /
    final static String INT_PLUS = "INT+";
    final static String INT_MINUS = "INT-";
    final static String INT_TIMES = "INT*";
    final static String INT_DIV = "INT/";
    // UnaryOp = !    
    final static String INT_NEG = "-";
    // RelationalOp = < | <= | == | != | >= | >
    final static String FLOAT_LT = "FLOAT<";
    final static String FLOAT_LE = "FLOAT<=";
    final static String FLOAT_EQ = "FLOAT==";
    final static String FLOAT_NE = "FLOAT!=";
    final static String FLOAT_GT = "FLOAT>";
    final static String FLOAT_GE = "FLOAT>=";
    // ArithmeticOp = + | - | * | /
    final static String FLOAT_PLUS = "FLOAT+";
    final static String FLOAT_MINUS = "FLOAT-";
    final static String FLOAT_TIMES = "FLOAT*";
    final static String FLOAT_DIV = "FLOAT/";
    // UnaryOp = !    
    final static String FLOAT_NEG = "-";
    // RelationalOp = < | <= | == | != | >= | >
    final static String CHAR_LT = "CHAR<";
    final static String CHAR_LE = "CHAR<=";
    final static String CHAR_EQ = "CHAR==";
    final static String CHAR_NE = "CHAR!=";
    final static String CHAR_GT = "CHAR>";
    final static String CHAR_GE = "CHAR>=";
    // RelationalOp = < | <= | == | != | >= | >
    final static String BOOL_LT = "BOOL<";
    final static String BOOL_LE = "BOOL<=";
    final static String BOOL_EQ = "BOOL==";
    final static String BOOL_NE = "BOOL!=";
    final static String BOOL_GT = "BOOL>";
    final static String BOOL_GE = "BOOL>=";
    // Type specific cast
    final static String I2F = "I2F";
    final static String F2I = "F2I";
    final static String C2I = "C2I";
    final static String I2C = "I2C";
    
    String val;
    
    Operator (String s) { val = s; }
    
    public void display(int k, PrintWriter pw){
    	for(int i = 0;i<k;i++){
    		pw.print("\t");
    	}
    	pw.println("Operator: "+ val);
    }

    public String toString( ) { return val; }
    public boolean equals(Object obj) { return val.equals(obj); }
    
    boolean BooleanOp ( ) { return val.equals(AND) || val.equals(OR); }
    boolean RelationalOp ( ) {
        return val.equals(LT) || val.equals(LE) || val.equals(EQ)
            || val.equals(NE) || val.equals(GT) || val.equals(GE);
    }
    boolean ArithmeticOp ( ) {
        return val.equals(PLUS) || val.equals(MINUS)
            || val.equals(TIMES) || val.equals(DIV);
    }
    boolean NotOp ( ) { return val.equals(NOT) ; }
    boolean NegateOp ( ) { return val.equals(NEG) ; }
    boolean intOp ( ) { return val.equals(INT); }
    boolean floatOp ( ) { return val.equals(FLOAT); }
    boolean charOp ( ) { return val.equals(CHAR); }

    final static String intMap[ ] [ ] = {
        {PLUS, INT_PLUS}, {MINUS, INT_MINUS},
        {TIMES, INT_TIMES}, {DIV, INT_DIV},
        {EQ, INT_EQ}, {NE, INT_NE}, {LT, INT_LT},
        {LE, INT_LE}, {GT, INT_GT}, {GE, INT_GE},
        {NEG, INT_NEG}, {FLOAT, I2F}, {CHAR, I2C}
    };

    final static String floatMap[ ] [ ] = {
        {PLUS, FLOAT_PLUS}, {MINUS, FLOAT_MINUS},
        {TIMES, FLOAT_TIMES}, {DIV, FLOAT_DIV},
        {EQ, FLOAT_EQ}, {NE, FLOAT_NE}, {LT, FLOAT_LT},
        {LE, FLOAT_LE}, {GT, FLOAT_GT}, {GE, FLOAT_GE},
        {NEG, FLOAT_NEG}, {INT, F2I}
    };

    final static String charMap[ ] [ ] = {
        {EQ, CHAR_EQ}, {NE, CHAR_NE}, {LT, CHAR_LT},
        {LE, CHAR_LE}, {GT, CHAR_GT}, {GE, CHAR_GE},
        {INT, C2I}
    };

    final static String boolMap[ ] [ ] = {
        {EQ, BOOL_EQ}, {NE, BOOL_NE}, {LT, BOOL_LT},
        {LE, BOOL_LE}, {GT, BOOL_GT}, {GE, BOOL_GE},
    };

    final static private Operator map (String[][] tmap, String op) {
        for (int i = 0; i < tmap.length; i++)
            if (tmap[i][0].equals(op))
                return new Operator(tmap[i][1]);
        assert false : "should never reach here";
        return null;
    }

    final static public Operator intMap (String op) {
        return map (intMap, op);
    }

    final static public Operator floatMap (String op) {
        return map (floatMap, op);
    }

    final static public Operator charMap (String op) {
        return map (charMap, op);
    }

    final static public Operator boolMap (String op) {
        return map (boolMap, op);
    }

}
