// StaticTypeCheck.java

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;

// Static type checking for Clite is defined by the functions 
// V and the auxiliary functions typing and typeOf.  These
// functions use the classes in the Abstract Syntax of Clite.


public class StaticTypeCheck {

    public static TypeMap typing (Declarations d) {
        TypeMap map = new TypeMap();
        for (Declaration di : d)
            map.put (di.v, di.t);
        return map;
    }

    public static void check(boolean test, String msg) {
        if (test)  return;
        System.err.println(msg);
        System.exit(1);
    }

    public static void V (Declarations d) {
        for (int i=0; i<d.size() - 1; i++)
            for (int j=i+1; j<d.size(); j++) {
                Declaration di = d.get(i);
                Declaration dj = d.get(j);
                check( ! (di.v.equals(dj.v)),
                       "duplicate declaration: " + dj.v);
            }
    } 

    public static void V (Program p) {
        V (p.decpart);
        V (p.body, typing (p.decpart));
    } 

    public static Type typeOf (Expression e, TypeMap tm) {
        if (e instanceof Value) return ((Value)e).type;
        if (e instanceof Variable) {
            Variable v = (Variable)e;
            check (tm.containsKey(v), "undefined variable: " + v);
            return (Type) tm.get(v);
        }
        if (e instanceof Binary) {
            Binary b = (Binary)e;
            if (b.op.ArithmeticOp( ))
                if (typeOf(b.term1,tm).toString().equals(Type.FLOAT.toString()))
                    return (Type.FLOAT);
                else return (Type.INT);
            if (b.op.RelationalOp( ) || b.op.BooleanOp( )) 
                return (Type.BOOL);
        }
        if (e instanceof Unary) {
            Unary u = (Unary)e;
            if (u.op.NotOp( ))        return (Type.BOOL);
            else if (u.op.NegateOp( )) return typeOf(u.term,tm);
            else if (u.op.intOp( ))    return (Type.INT);
            else if (u.op.floatOp( )) return (Type.FLOAT);
            else if (u.op.charOp( ))  return (Type.CHAR);
        }
        throw new IllegalArgumentException("should never reach here");
    } 

    public static void V (Expression e, TypeMap tm) {
        if (e instanceof Value){
            System.out.println("Variable");
            return;
        }

        if (e instanceof Variable) {
            System.out.println("Variable");
            Variable v = (Variable)e;
            check( tm.containsKey(v)
                   , "undeclared variable: " + v);
            return;
        }
        if (e instanceof Binary) {
            Binary b = (Binary) e;
            Type typ1 = typeOf(b.term1, tm);
            Type typ2 = typeOf(b.term2, tm);
            V (b.term1, tm);
            V (b.term2, tm);
            final boolean equals = typ1.toString().equals(typ2.toString());
            if (b.op.ArithmeticOp( )){
                System.out.println("AO");
                check( equals && (typ1.toString().equals(Type.INT.toString()) || typ1.toString().equals(Type.FLOAT.toString())), "type error for " + b.op);
            }

            else if (b.op.RelationalOp( )){
                System.out.println("RO");
                check(equals, "type error for " + b.op);
            }
            else if (b.op.BooleanOp( ))
                check( typ1.toString().equals(Type.BOOL.toString()) && typ2.toString().equals(Type.BOOL.toString()), b.op + ": non-bool operand");
            else
                throw new IllegalArgumentException("should never reach here");
            return;
        }

        if(e instanceof Unary){
            System.out.println("Unary");
            Unary unary = (Unary)e;
            Type type = typeOf(unary.term, tm);
            V(unary.term, tm);
            return;
            // student exercise
        }

        throw new IllegalArgumentException("should never reach here");
    }

    public static void V (Statement s, TypeMap tm) {
        if ( s == null )
            throw new IllegalArgumentException( "AST error: null statement");
        if (s instanceof Skip) return;
        if (s instanceof Assignment) {
            System.out.println("Assignment");
            Assignment a = (Assignment)s;
            check( tm.containsKey(a.target), " undefined target in assignment: " + a.target);
            V(a.source, tm);
            Type ttype = (Type)tm.get(a.target);
            Type srctype = typeOf(a.source, tm);
            if (ttype != srctype) {
                if (ttype.toString().equals(Type.FLOAT.toString()))
                    check( srctype.toString().equals(Type.INT.toString()), "mixed mode assignment to " + a.target);
                else if (ttype.toString().equals(Type.INT.toString()))
                    check( srctype.toString().equals(Type.INT.toString()), "mixed mode assignment to " + a.target);
                else
                    check( false, "mixed mode assignment to " + a.target);
            }
            return;
        }
        if(s instanceof Block){
            System.out.println("Block");
            Block b = (Block)s;
            for (Statement item: b.members) {
                if(item instanceof Conditional){
                    System.out.println("Conditional");
                    return;
                }
                if(item instanceof Loop){
                    System.out.println("Loop");
                    return;
                }

                Assignment a = (Assignment)item;
                System.out.println(a.target);
                if(a.source instanceof Binary){
                    System.out.println(((Binary) a.source).toString());
                    if(((Binary) a.source).term2 instanceof Unary)
                        System.out.println(((Unary) ((Binary) a.source).term2).toString());
                }
                else System.out.println(a.source);

                V(a.source, tm);
            }
            return;
            // student exercise
        }

        throw new IllegalArgumentException("should never reach here");
    }

    public static void main(String... args) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        try{
            prog.display(args[0]);
        }catch (Exception e){
            e.printStackTrace();
        }

        TypeMap map = typing(prog.decpart);
        try{
            map.display(args[0]);   // student exercise
        }catch (Exception e){
            e.printStackTrace();
        }
        V(prog);
    } //main

} // class StaticTypeCheck

