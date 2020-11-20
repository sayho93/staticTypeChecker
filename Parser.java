import java.io.IOException;
import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }
  
    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
	public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        match(TokenType.LeftBrace);

        Declarations dec = declarations();
        Block bd = new Block();
        while(true){
        	if(token.type().equals(TokenType.RightBrace)) break;
        	bd.members.add(statement());
        }
        
        Program prog = new Program(dec, bd);
        
        match(TokenType.RightBrace);
        return prog;  // student exercise
    }
  
    private Declarations declarations () {
        // Declarations --> { Declaration }
    	Declarations decs = new Declarations();
    	while(true){
    		if(token.type().equals(TokenType.Int)||
    		   token.type().equals(TokenType.Bool)||
    		   token.type().equals(TokenType.Char)||
    		   token.type().equals(TokenType.Float)){
    			declaration(decs);
    		}
    		else break;
    	}
        return decs;  // student exercise
    }
  
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
    	
    	Type tp = type();
    	Variable v = new Variable(match(TokenType.Identifier));
    	Declaration dec = new Declaration(v,tp);
    	ds.add(dec);
    	while(true){
    		if(token.type().equals(TokenType.Semicolon)){
    			match(TokenType.Semicolon);
    			break;
    		}
    		match(TokenType.Comma);
    		Variable v2 = new Variable(match(TokenType.Identifier));
    		Declaration dec2 = new Declaration(v2,tp);
    		ds.add(dec2);
    	}
        // student exercise
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char 
        Type t = new Type(token.value());
    	token = lexer.next();
        // student exercise
        return t;          
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        Statement s = new Skip();
        if(token.type().equals(TokenType.Semicolon)){
        	match(TokenType.Semicolon);
        	return s;
        }
        else if(token.type().equals(TokenType.LeftBrace)){
        	s = statements();
        	//Block
        }
        else if(token.type().equals(TokenType.Identifier)){
        	s = assignment();
        }
        else if(token.type().equals(TokenType.If)){
        	s = ifStatement();
        }
        else if(token.type().equals(TokenType.While)){
        	s = whileStatement();
        }
        // student exercise
        return s;
    }
  
    private Block statements () {
        // Block --> '{' Statements '}'
        Block b = new Block();
        match(TokenType.LeftBrace);
        while(true){
        	if(token.type().equals(TokenType.RightBrace)) break;
        	b.members.add(statement());
        }
        match(TokenType.RightBrace);
        // student exercise
        return b;
    }
  
    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.Assign);
    	Expression e = expression();
    	match(TokenType.Semicolon);

    	Assignment asgn = new Assignment(v, e);
        return asgn;  // student exercise
    }
  
    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
    	Conditional cd;
    	match(TokenType.If);
    	match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	Statement s = statement();
    	if(token.type().equals(TokenType.Else)){
    		match(TokenType.Else);
    		Statement stp = statement();
    		cd = new Conditional(e,s,stp);
    	}
    	cd = new Conditional(e,s);
        return cd;  // student exercise
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
    	match(TokenType.While);
    	match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	Statement s = statement();
    	Loop lp = new Loop(e,s);
        return lp;  // student exercise
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
    	Binary br = null;
    	Expression cj = conjunction();
    	if(token.type().equals(TokenType.Or)){
    		match(TokenType.Or);
    		Expression cj2 = conjunction();
    		Operator op = new Operator(Operator.OR);
    		br = new Binary(op, cj, cj2);
    		
    		while(true){
    	    	if(token.type().equals(TokenType.Or)){
    	    		match(TokenType.Or);
    	    		Expression cj3 = conjunction();
    	    		br = new Binary(op, br, cj3);
    	    		
    	    	}
    	    	else{
    	    		return br;
    	    	}
        	}
    		
    	}
    	return cj;
    	 // student exercise
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	Binary br = null;
    	Expression eq = equality();
    	if(token.type().equals(TokenType.And)){
    		match(TokenType.And);
    		Expression eq2 = equality();
    		Operator op = new Operator(Operator.AND);
    		br = new Binary(op, eq, eq2);
    		
    		while(true){
    	    	if(token.type().equals(TokenType.And)){
    	    		match(TokenType.And);
    	    		Expression eq3 = equality();
    	    		br = new Binary(op, br, eq3);
    	    	}
    	    	else{
    	    		return br;
    	    	}
        	}
    		
    	}
    	return eq; // student exercise
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	Binary br = null;
    	Expression rel = relation();
    	if(token.type().equals(TokenType.Equals)){
    		match(TokenType.Equals);
    		Expression rel2 = relation();
    		Operator op = new Operator(Operator.EQ);
    		br = new Binary(op, rel, rel2);
    		return br;
    	}
    	return rel;
    	// student exercise
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition] 
    	Binary br = null;
    	Expression add = addition();
    	if(token.type().equals(TokenType.Less)||
    		token.type().equals(TokenType.LessEqual)||
    		token.type().equals(TokenType.Greater)||
    		token.type().equals(TokenType.GreaterEqual)||
    		token.type().equals(TokenType.NotEqual)){
    		//match(TokenType.Equals);
    		Operator op = new Operator(match(token.type()));
    		Expression add2 = addition();
    		br = new Binary(op, add, add2);
    		return br;
    	}
    	return add;
    	// student exercise
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();       
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	Value v = null;
    	if(token.type().equals(TokenType.IntLiteral)){
    		v = new IntValue(Integer.parseInt(match(TokenType.IntLiteral)));
    	}
    	else if(token.type().equals(TokenType.CharLiteral)){
    		v = new CharValue(match(TokenType.CharLiteral).charAt(0));
    	}
    	else if(token.type().equals(TokenType.FloatLiteral)){
    		v = new FloatValue(Float.parseFloat(match(TokenType.FloatLiteral)));
    	}
    	else if(token.type().equals(TokenType.True)){
    		v = new BoolValue(Boolean.parseBoolean(match(TokenType.True)));
    	}
    	else if(token.type().equals(TokenType.False)){
    		v = new BoolValue(Boolean.parseBoolean(match(TokenType.False)));
    	}
        return v;  // student exercise
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    public static void main(String args[]) throws IOException {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display(args[0]);     // display abstract syntax tree
    } //main

} // Parser
