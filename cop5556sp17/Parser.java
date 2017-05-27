package cop5556sp17;

import cop5556sp17.Scanner.Kind;

import static cop5556sp17.Scanner.Kind.*;

import java.util.ArrayList;

import cop5556sp17.Scanner.Token;

import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.Chain;
import cop5556sp17.AST.ChainElem;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.WhileStatement;


public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	//
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	/*KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME*/
//	@SuppressWarnings("serial")	
//	public static class UnimplementedFeatureException extends RuntimeException {
//		public UnimplementedFeatureException() {
//			super();
//		}
//	}
	
	Scanner scanner;
	Token t;
	ArrayList<Kind> statementSet;
	ArrayList<Kind> decSet;
	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
		statementSet=new ArrayList<Kind>();
		decSet=new ArrayList<Kind>();
		decSet.add(KW_INTEGER);
		decSet.add(KW_BOOLEAN);
		decSet.add(KW_IMAGE);
		decSet.add(KW_FRAME);
		statementSet.add(OP_SLEEP);
		statementSet.add(KW_WHILE);
		statementSet.add(KW_IF);
		statementSet.add(IDENT);
		statementSet.add(OP_BLUR);
		statementSet.add(OP_GRAY);
		statementSet.add(OP_CONVOLVE);
		statementSet.add(KW_SHOW);
		statementSet.add(KW_HIDE);
		statementSet.add(KW_MOVE);
		statementSet.add(KW_XLOC);
		statementSet.add(KW_YLOC);
		statementSet.add(OP_WIDTH);
		statementSet.add(OP_HEIGHT);
		statementSet.add(KW_SCALE);

   }

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		Program programNode=null;
		programNode=program();
		matchEOF();
		return programNode;
	}

    Expression expression() throws SyntaxException {
		//TODO
		//System.out.println("expression method");
    	Expression e0=null;
		Expression e1=null;
		Token tok= null;
		Token first_token=t;
    	try{
			
			
			e0=term();
		while(t.isKind(LT)||t.isKind(LE)||t.isKind(GT)||t.isKind(GE)||t.isKind(LT)||t.isKind(EQUAL)||t.isKind(NOTEQUAL))
		{	
			tok=t;
			consume();
			e1= term();
			e0=new BinaryExpression(first_token, e0, tok, e1);
		}
		
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in expression found "+t.kind+t.pos);
		}

    	return e0;
	}

	Expression term() throws SyntaxException {
		//TODO
		//System.out.println("in term");
		Expression e0=null;
		Expression e1=null;
		Token tok=null;
		Token first_token =t;
		try{
			
			e0 = elem();
		while(t.isKind(PLUS)||t.isKind(MINUS)||t.isKind(OR))
		{	
			tok=t;
			consume();
			e1 = elem();
			e0=new BinaryExpression(first_token, e0, tok, e1);
		}
		
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in term found "+t.kind+t.pos);
		}
		return e0;
	}

	Expression elem() throws SyntaxException {
		//TODO
		//System.out.println("in elem");
		Expression e0=null;
		Expression e1=null;
		Token tok=null;
		Token first_token=t;
		
		try{
			e0= factor();
			  while(t.isKind(TIMES)||t.isKind(DIV)||t.isKind(AND)||t.isKind(MOD))
				{
					tok=t;
				  	consume();
					e1=factor();
					e0=new BinaryExpression(first_token, e0, tok, e1);
				}
			  return e0;
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in elem found "+t.kind+t.pos);
		}

		
	}

	Expression factor() throws SyntaxException {
		Expression e=null;
		Kind kind = t.kind;
		//System.out.println(kind);
		switch (kind) {
		case IDENT: {
			e=new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {
			e=new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE://System.out.println("true");
		case KW_FALSE: {
			e=new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {
			e=new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			//System.out.println("line 120");
			consume();
			e=expression();
			match(RPAREN);
		}
			break;
		default:
			
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor"+t.kind+t.pos);
		}
		return e;
	}

	Block block() throws SyntaxException {
		//TODO
		//System.out.println("in block");
		ArrayList<Dec> declist=new ArrayList<>();
		
		ArrayList<Statement> statementlist=new ArrayList<>();
		Token first_token=null;
		first_token=t;
		Dec dec=null;
		Statement stat=null;
		try{if(t.isKind(LBRACE))
		{	consume();
			while(decSet.contains(t.kind)||statementSet.contains(t.kind))
			{
				if(decSet.contains(t.kind))
				{
					dec=dec();
					declist.add(dec);
				}
				else{
					stat=statement();
					statementlist.add(stat);
				}
			}
			match(RBRACE);
		}
		else throw new SyntaxException("Syntax error in block found "+t.kind+t.pos);
		}
		
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in block found "+t.kind+t.pos);
		}
		return new Block(first_token, declist, statementlist);

	}

	Program program() throws SyntaxException {
		//TODO
		ArrayList<ParamDec> paramlist=new ArrayList<>();
		ParamDec param=null;
		Block block1=null;
		Token first_token=null;
		first_token=t;
		Kind kind=t.kind;
		try{
			if(t.isKind(IDENT)){
			if(scanner.peek().isKind(LBRACE))
			{	consume();
			//consume();
				block1=block();
				//matchEOF();
			}
			else if(scanner.peek().isKind(KW_URL)||scanner.peek().isKind(KW_FILE)||scanner.peek().isKind(KW_INTEGER)||scanner.peek().isKind(KW_BOOLEAN))
			{	
				
				consume();
				//consume();
				param=paramDec();
				paramlist.add(param);
				while(t.isKind(COMMA))
				{
					consume();
					param=paramDec();
					paramlist.add(param);
				}
				block1=block();
				//matchEOF();
			}
			else{
				throw new SyntaxException("Syntax error in program found "+t.kind+t.pos);
			}
			}
			else{
				throw new SyntaxException("Syntax error in program found "+t.kind+t.pos);
			}
		}
		catch(SyntaxException e)
		{throw new SyntaxException("Syntax error in program found "+t.kind+t.pos);}
		return new Program(first_token, paramlist,block1);
	}
	/*program ::=  IDENT block
	program ::=  IDENT param_dec ( , param_dec )*   block
	paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
	block ::= { ( dec | statement) * }
	dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT*/
	ParamDec paramDec() throws SyntaxException {
		//TODO
		Token first_token=t;
		Token t1=null;
		Kind kind=t.kind;
		//System.out.println("in paradec");
		try{
			switch(kind)
			{
			case KW_URL:
			case KW_FILE:
			case KW_INTEGER:
			case KW_BOOLEAN:
			{	//System.out.println("kw_boolean in paramdec");
				consume();
				t1=t;
				match(IDENT);
			}break;
			default:
				throw new SyntaxException("Syntax error in paramdec found "+t.kind+t.pos);
			}
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in paramdec found "+t.kind+t.pos);
		}
		return new ParamDec(first_token, t1);

	}
	/*program ::=  IDENT block
program ::=  IDENT param_dec ( , param_dec )*   block
paramDec ::= ( KW_URL | KW_FILE | KW_INTEGER | KW_BOOLEAN)   IDENT
block ::= { ( dec | statement) * }
dec ::= (  KW_INTEGER | KW_BOOLEAN | KW_IMAGE | KW_FRAME)    IDENT*/


	Dec dec() throws SyntaxException {
		//TODO
		Token first_tok=t;
		Token t1=null;
		Kind kind=t.kind;
		//System.out.println(kind);
		//System.out.println("in dec");
		try{
			switch(kind)
			{
			case KW_INTEGER:
			case KW_BOOLEAN:
			case KW_IMAGE:
			case KW_FRAME:
			{
				consume();
				t1=t;
				match(IDENT);
			}break;
			default:
				throw new SyntaxException("Syntax error in dec found "+t.kind+t.pos);
			}
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in dec found "+t.kind+t.pos);
		}
		return new Dec(first_tok, t1);

	}
	
	


	Statement statement() throws SyntaxException {
		//TODO
		Token first_token=t;
		Expression express=null;
		Statement stat=null;
		Block block1=null;
		Chain chain=null;
		Kind kind = t.kind;
		//System.out.println(kind);
		try{switch(kind)
		{
		case OP_SLEEP:{
			consume();
			express=expression();
			stat =new SleepStatement(first_token, express);
			match(SEMI);
			
		}break;
		case KW_WHILE:{
			//System.out.println("inwhile");
			consume();
			//System.out.println("after consume");
			if(t.isKind(LPAREN))
			{//System.out.println("after consume in paren");
				consume();
				express=expression();
				//System.out.println("after expression in while");
				match(RPAREN);
				//System.out.println("after rparen");
				block1=block();
				
			}
			else throw new SyntaxException("Syntax error in statement found "+t.kind+t.pos);
			stat=new WhileStatement(first_token, express, block1);
		}break;
		case KW_IF:{
			consume();
			if(t.isKind(LPAREN))
			{	consume();
				express=expression();
				match(RPAREN);
				block1=block();
			}
			else throw new SyntaxException("Syntax error in statement found "+t.kind+t.pos);
			stat=new IfStatement(first_token, express, block1);
		}break;
		case IDENT:{
			
			if(scanner.peek().isKind(ASSIGN))
			{
				consume();
				consume();
				express=expression();
				match(SEMI);
				IdentLValue val=new IdentLValue(first_token);
				stat=new AssignmentStatement(first_token, val, express);
			}
			else if(scanner.peek().isKind(ARROW)||scanner.peek().isKind(BARARROW)){ stat=chain();
			match(SEMI);
			}
			else throw new SyntaxException("Syntax error in statement found "+t.kind+t.pos);
		}break;
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:{
			stat=chain();
			match(SEMI);
		}break;
		default:
			
			throw new SyntaxException("Syntax error in statement found "+t.kind+t.pos);
		}
		}
		
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in statement found "+t.kind+t.pos);
		}
		return stat;
		
	}
	//chain ::=  chainElem arrowOp chainElem ( arrowOp  chainElem)*
	/*whileStatement ::= KW_WHILE ( expression ) block
			ifStatement ::= KW_IF ( expression ) block
			arrowOp ∷= ARROW   |   BARARROW*/

	Chain chain() throws SyntaxException {
		//TODO
		Token arrow=null;
		Chain chain1=null;
		Chain chain2=null;
		Token first_token=t;
		
		try{
			chain1=chainElem();
			if(t.isKind(ARROW)||t.isKind(BARARROW))
		{//System.out.pri;ntln("in arrow of chain");
				arrow=t;
			consume();
			chain2=chainElem();
			chain1=new BinaryChain(first_token, chain1, arrow, (ChainElem) chain2 );
			//System.out.println("in arrow of chain");
			while(t.isKind(ARROW)||t.isKind(BARARROW)){
				//System.out.println("here");
				arrow=t;
				consume();
				chain2=chainElem();
				chain1=new BinaryChain(first_token, chain1, arrow, (ChainElem) chain2 );
			}
		}
			else throw new SyntaxException("Syntax error in chain found "+t.kind+t.pos);
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in chain found "+t.kind+t.pos);
		}
		return chain1;
	}
	

	ChainElem chainElem() throws SyntaxException {
		//TODO
		Token first_token=null;
		ChainElem chainelem=null;
		Tuple arg=null;
		
		Kind kind = t.kind;
		//System.out.println(kind);
		try{
		switch (kind){
		case IDENT:{
			chainelem=new IdentChain(t);
			consume();
		}
		break;
		case OP_BLUR:
		case OP_GRAY:
		case OP_CONVOLVE:{
			first_token=t;
			consume();
			arg=arg();
			chainelem=new FilterOpChain(first_token, arg);
		}break;
		case KW_SHOW:
		case KW_HIDE:
		case KW_MOVE:
		case KW_XLOC:
		case KW_YLOC:
		{	
			first_token=t;
			consume();
			arg=arg();
			chainelem=new FrameOpChain(first_token, arg);
		}break;
		case OP_WIDTH:
		case OP_HEIGHT:
		case KW_SCALE:{
			first_token=t;
			consume();
			arg=arg();
			chainelem=new ImageOpChain(first_token, arg);
		}break;
		default:
			throw new SyntaxException("Syntax error in chainele found "+t.kind+t.pos);
		}
		}
		catch(SyntaxException e)
		{
			throw new SyntaxException("Syntax error in chainele found "+t.kind+t.pos);
		}
		return chainelem;
		
	}

	Tuple arg() throws SyntaxException {
		//TODO
		//System.out.println(t.kind);
		//System.out.println("in argument");
		ArrayList<Expression> alist=new ArrayList<>();
		
		Token first_token=t;
		Expression e1=null;
		try{
		
			if(t.isKind(LPAREN))
		{	//System.out.println("laparen of arg");
			consume();
			e1=expression();
			alist.add(e1);
			while(t.isKind(COMMA))
			{	//System.out.println("in comma");
				consume();
				e1=expression();
				alist.add(e1);
			}
			match(RPAREN);
			//matchEOF();
		}
//need to clarify about return .
			
			

		}
		catch(SyntaxException e)
		{//System.out.println("syntax error from arg");
			throw new SyntaxException("Syntax error in arg found "+t.kind+t.pos);
		}
		return new Tuple(first_token, alist);
		
	}

	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			//System.out.println("here"+t.getText());
			return consume();
		}
		//System.out.println("after expression in while");
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
		//Do we need to add missing kind
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		return null; //replace this statement
	}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}
