package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import cop5556sp17.Scanner.Kind;
import java.util.Iterator;
public class Scanner {
	/**
	 * Kind enum
	 */
	

	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	
	public static enum State{
		START("START"),
		IN_INT_LIT("IN_INT_LIT"),
		AFTER_EQ("AFTER_EQ"),
		IN_IDENT("IN_IDENT"),
		AFTER_NOT("AFTER_NOT"),
		AFTER_OR("AFTER_OR"),
		AFTER_MINUS("AFTER_MINUS"),
		AFTER_LT("AFTER_LT"),
		AFTER_GT("AFTER_GT"),
		AFTER_DIV("AFTER_DIV"),
		AFTER_TIMES("TIMES"),
		WITHIN_COMMENT("WITHIN_COMMENT");
	
		State(String state){
			this.state=state;
		}
		final String state;
		//String getText(){
		//	return state;
		//}
		
	}
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}
	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
//TO Discuss with some regrading linepos function		

	

	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			
			if(kind.getText().isEmpty())
			return chars.substring(this.pos, this.pos+this.length);
			else{
				String tokenValue=kind.getText();
				return tokenValue;
			}
		}
		public boolean isKind(Kind kind)
		{	if(this.kind.compareTo(kind)==0)
			return true;
		  else
			return false;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			
			LinePos line;
			int index=Collections.binarySearch(Line_length,this.pos);
			if(index>=0)
			{
				line=new LinePos(index,0);
				
			}
			else{
				index=(index+1)*(-1)-1;
				if(index==0)
				{
					line=new LinePos(index,this.pos-Line_length.get(index));
					 
				}
				else{
				 line=new LinePos(index,this.pos-Line_length.get(index)-1);
				 }
			}
			//System.out.println("result from binary search"+index);
			return line;
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		//TO DISCUSS WITH SOMEONE
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			String number=chars.substring(this.pos, this.pos+this.length);
			return Integer.parseInt(number);
		}
		  @Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }


		
	}
//integer | boolean | image | url | file | frame | while | if | sleep | screenheight | screenwidth 
	 Scanner(String chars) {
		this.chars = chars;
		Line_length=new ArrayList<Integer>();
		Line_length.add(0);
		tokens = new ArrayList<Token>();
		 hmap = new HashMap<String, Kind>();
		 for(Kind dir:Kind.values())
			{
	    	  if (dir.text.matches("integer|boolean|image|url|file|frame|while|if|sleep|screenheight|screenwidth|gray|convolve|blur|scale|width|height|xloc|yloc|hide|show|move|true|false"))
	    		  hmap.put(dir.text, dir);
	    	  
			}
	     //System.out.print(hmap.size());
	      Iterator it = hmap.entrySet().iterator();
	     
	}

	public int skipWhiteSpace(int pos)
	{
		int newpos=pos;
		int len=chars.length();
		for(int i=pos;i<len;i++)
		{
			if(chars.charAt(i)==' '||chars.charAt(i)=='\t'||chars.charAt(i)=='\r')
			{
				newpos++;
			}
			else if(chars.charAt(i)=='\n')
		{	//System.out.print(" gfghfgf");
//				System.out.println(i);
				Line_length.add(i);
				newpos++;
			
			}
			else
				return newpos;
		}
		return newpos;
	}
	
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//boolean comment=false;
		//TODO IMPLEMENT THIS!!!!
		State state=State.START;
		int length = chars.length();
		//System.out.println(length);
		int startPos = 0;
		int ch;
		while ( pos <= length-1 )
		{
			if ( pos < length )
				ch = chars.charAt(pos);
			else
				ch = -1;
			switch(state)
			{
			case START: 
            {
            pos = skipWhiteSpace(pos);
            ch = pos < length ? chars.charAt(pos) : -1;
            startPos = pos;
            switch (ch) {
                case -1:  break;
                	
                case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1));pos++;state = State.START;} break;
                case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1));pos++;state = State.START;} break;
                case '/': {if(pos==length-1){
                	tokens.add(new Token(Kind.DIV, startPos, 1));pos++;state = State.START;
                }
                else{state = State.AFTER_DIV;
                	pos++;}
                	
                	} break;
                case '&': {tokens.add(new Token(Kind.AND, startPos, 1));pos++;state = State.START;} break;
                case '|': {
                	//ch1 = ;
                	if(pos==length-1)
                	{
                		tokens.add(new Token(Kind.OR, startPos, 1));pos++;state = State.START;
                	}
                	else{state=State.AFTER_OR;
                	pos++;}
                	 //tokens.add(new Token(Kind.OR, startPos, 1));pos++;state = State.START;
                	 } break;
                case '%': {tokens.add(new Token(Kind.MOD, startPos, 1));pos++;state = State.START;} break;
                case '!': {
                	if(pos==length-1)
                	{
                		tokens.add(new Token(Kind.NOT, startPos, 1));pos++;state = State.START;
                	}
                	else if(chars.charAt(pos+1)=='='){
                		/*pos++;
                		tokens.add(new Token(Kind.NOTEQUAL,startPos,2));
                		pos++;
                		state=State.START;*/
                		pos++; state=State.AFTER_NOT;
                		
                	}
                	else{
                	tokens.add(new Token(Kind.NOT, startPos, 1));
                	pos++;state = State.START;}
               // state=State.AFTER_NOT;pos++;	
                } break;
                case '-': {
                	if(pos==length-1)
                	{
                		tokens.add(new Token(Kind.MINUS, startPos, 1));pos++;state = State.START;
                	}
                	else if(chars.charAt(pos+1)=='>'){
                		pos++;
                		tokens.add(new Token(Kind.ARROW,startPos,2));
                		pos++;
                		state=State.START;
                		//pos++; state=State.AFTER_NOT;
                		
                	}
                	else{
                	tokens.add(new Token(Kind.MINUS, startPos, 1));
                	pos++;state = State.START;}
                	
                	
                	//tokens.add(new Token(Kind.MINUS, startPos, 1));pos++;state = State.START;
                	} break;
                case '>': {if(pos==length-1)
            	{
            		tokens.add(new Token(Kind.GT, startPos, 1));pos++;state = State.START;
            	}
            	else{state=State.AFTER_GT;
            	pos++;}} break;
                case '<': {
                	if(pos==length-1)
                	{
                		tokens.add(new Token(Kind.LT, startPos, 1));pos++;state = State.START;
                	}
                	else{state=State.AFTER_LT;
                	pos++;}
                	//tokens.add(new Token(Kind.LT, startPos, 1));pos++;state = State.START;
                	} break;
                case '=': {
                	if(pos==length-1)
                	{
                		//System.out.println("ILLEGAL CHARACTER EXCEPTION");
                		throw new IllegalCharException(
                                "illegal char " +ch+" at pos "+pos);
                		
                	}
                	
                	else{
                		state = State.AFTER_EQ;
                		pos++;
                	}
                
                }break; 
                //case '!': {state = State.AFTER_NOT;pos++;}break;
                
              
                case '0': {//System.out.println("here");
                	tokens.add(new Token(Kind.INT_LIT,startPos, 1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;}break;
                //separator ::= 	;  | ,  |  (  |  )  | { | }	
                case ';':{
                	tokens.add(new Token(Kind.SEMI,startPos,1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;
                }break;
                case ',' :{
                	if(pos==length-1)
                	{
                		tokens.add(new Token(Kind.COMMA, startPos, 1));pos++;state = State.START;
                	}
                	else{
                	tokens.add(new Token(Kind.COMMA,startPos,1));
                	pos++;
                	state=State.START;
                	//System.out.println(tokens.size());
                	}
                	//pos++;state = State.START;
                }break;
                case '(' :{
                	tokens.add(new Token(Kind.LPAREN ,startPos,1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;
                }break;
                case ')' :{
                	tokens.add(new Token(Kind.RPAREN ,startPos,1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;
                }break;
                case '{' :{
                	tokens.add(new Token(Kind.LBRACE ,startPos,1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;
                }break;
                case '}' :{
                	tokens.add(new Token(Kind.RBRACE ,startPos,1));
                	//System.out.println(tokens.size());
                	pos++;state = State.START;
                }break;
                default:
                {	
                    if (Character.isDigit(ch)) {
                    	if(pos==length-1){
//                    		try
//                			{
//                			Integer.valueOf(chars.substring(startPos, pos+1));
//                			}
//                			catch(Exception e)
//                			{	//System.out.println("Integer out of Range");
//                				throw new IllegalNumberException("Integer out of Range"+"at position"+pos);
//                				
//                			}
                			tokens.add(new Token(Kind.INT_LIT,startPos, 1));
                			pos++;
                			
                    	}
                    	else{
                    	state = State.IN_INT_LIT;
                       pos++;}
                    } //TODO handle pos==length-1 case here 
                    else if (Character.isJavaIdentifierStart(ch)) 
                    {		if(pos==length-1){
                    	tokens.add(new Token(Kind.IDENT,startPos, 1));
                    	pos++;
                    }
                    else{state = State.IN_IDENT;pos++;}
                    } 
                     else {throw new IllegalCharException(
                                "illegal char " +ch+" at pos "+pos);}
                }
            			} // switch (ch)
            } break; 
            //case for digits
			case IN_INT_LIT: 
            {
            	if(Character.isDigit(ch))
            	{
            		if(pos==length-1)
            		{
            			//System.out.println("here");
            			try
            			{
            			Integer.valueOf(chars.substring(startPos, pos+1));
            			}
            			catch(Exception nfe)
            			{	//System.out.println("Integer out of Range");
            				throw new IllegalNumberException("Digit Out of bounds"+" at pos "+pos);
            				
            			}
            			tokens.add(new Token(Kind.INT_LIT,startPos, pos-startPos+1));	
            			//System.out.println(tokens.size());
            		}
            		pos++;          		
            		
            	}
            	else
            	{	try
    				{
            			Integer.valueOf(chars.substring(startPos, pos));
    				}
    				catch(Exception nfe)
    				{	//System.out.println("Integer out of Range");
    					throw new IllegalNumberException("Digit Out of bounds"+" at pos "+pos);
    				
    				}
            		tokens.add(new Token(Kind.INT_LIT,startPos, pos-startPos));
            		state = State.START;
            	}
            	
            	
            }break;
            
			 case IN_IDENT: {
	            	if(Character.isJavaIdentifierStart(ch) | Character.isDigit(ch))
	            	{
	            		if(pos==length-1)
	            		{
	            			String str = chars.substring(startPos, pos+1);	
	            			  if(hmap.containsKey(str))
	            			  {
	            				  tokens.add(new Token(hmap.get(str),startPos, pos-startPos+1));
	            			  }
	            			
	            			  else{tokens.add(new Token(Kind.IDENT,startPos, pos-startPos+1));}	            			
	            		}
	            		pos++;          		
	            		
	            	}
	            	else
	            	{
	            		String str = chars.substring(startPos, pos);
	            		if(hmap.containsKey(str))
          			  {
          				  tokens.add(new Token(hmap.get(str),startPos, pos-startPos));
          			  }
	            		else{tokens.add(new Token(Kind.IDENT,startPos, pos-startPos));}
	            		state = State.START;
	            	}
	            	
	            	
	            } break;
	            case AFTER_EQ:{
	            	if(ch=='=')
	            	{
//	            		System.out.println("ILLEGAL EQUAL CHARACTER==");
//	            		System.out.println(tokens.size());
	            		tokens.add(new Token(Kind.EQUAL,startPos, 2));
//	            		System.out.println(tokens.size());
//	            		System.out.println(pos +" "+ startPos + " "+ chars.length());
//	            		System.out.println(tokens.get(0).kind);
	            		//System.out.println("hello"+chars.substring(startPos,pos-startPos));
	            		pos++;
	            		state=State.START;
	            	}
	            	else{
	            		//System.out.println("ILLECGAL EQUAL CHARACTER");
	            		throw new IllegalCharException(
                                "illegal char " +ch+" at pos "+pos);
	            		
	            	}
	            	
	            	
	            }break;
	            case AFTER_NOT:{
	            	if(ch=='=')
	            	{
	            		tokens.add(new Token(Kind.NOTEQUAL ,startPos, 2));
	            		pos++;
	            		state=State.START;
	            	}
	            	else{
	            		state=State.START;
	            	}
	            	
	            }break;
	            case AFTER_OR: {
	            	if(ch=='-')
	            	{  // System.out.println("code reaching here");
	            		if(pos==length-1)
                	{	//System.out.println("code reaching here1");
	            			tokens.add(new Token(Kind.OR, startPos, 1));
            		//pos++;
            		 //state = State.START;
	            		tokens.add(new Token(Kind.MINUS, startPos+1, 1));
	            		pos++;
                	}
	            	
	            		else{
	            			state=State.AFTER_MINUS;
		            		pos++;
	            		//}
	            		}
	            	}
	            	else{
	            		tokens.add(new Token(Kind.OR, startPos, 1));
	            		//pos++;
	            		state = State.START;
	            	}
	            }break;
	            case AFTER_MINUS: {
	            	if(ch=='>')
	            	{
	            		tokens.add(new Token(Kind.BARARROW ,startPos, 3));
	            		pos++;
	            		state=State.START;
	            	}
	            	else{
	            		
	            	tokens.add(new Token(Kind.OR, startPos, 1));
	            	tokens.add(new Token(Kind.MINUS, startPos+1, 1));
	            	state=State.START;}
	            	
	            }break;
	            case AFTER_LT: {
	            	if(ch=='=')
	            	{
	            		tokens.add(new Token(Kind.LE ,startPos, 2));
	            		pos++;
	            		state=State.START;
	            	}
	            	else if(ch=='-')
	            	{
	            		tokens.add(new Token(Kind.ASSIGN ,startPos, 2));
	            		pos++;
	            		state=State.START;
	            	}
	            	else{tokens.add(new Token(Kind.LT ,startPos, 1));
            		//pos++;
            		state=State.START;}
	            	
	            }break;
	            case AFTER_GT: {
	            	if(ch=='=')
	            	{
	            		tokens.add(new Token(Kind.GE ,startPos, 2));
	            		pos++;
	            		state=State.START;
	            	}
	            	
	            	else{tokens.add(new Token(Kind.GT ,startPos, 1));
            		//pos++;
            		state=State.START;}
	            	
	            }break;
	            
	            //case for comments
	            case AFTER_DIV:{
	            	//System.out.println("after div state"+pos+ch);
	            	if(ch=='*')
	            	{
	            		if(pos==length-1)
	            		{
	            			pos++;
	            		}
	            		else{
	            			//comment=true;
	            			state=State.AFTER_TIMES;
	            			pos++;
	            			//System.out.println("goes to after times"+pos);
	            		}
	            	}
	            	else{
	            		tokens.add(new Token(Kind.DIV ,startPos, 1));
	            		state=State.START;
	            	}
	            }break;
	            case AFTER_TIMES:{
	            	//System.out.println("within comment"+" "+ch+pos);
	            	if(ch=='*')
	            	{	if(pos<length-1){
	            		if(chars.charAt(pos+1)=='/')
	            		{	state=state.START;
		            		pos=pos+2;}
		            		else{pos++;
		            		//System.out.println("goes to comment"+" "+ch+" "+pos);
		            		state=state.WITHIN_COMMENT;}
	            	}
	            	else pos++;
	            	}
	            	else{//System.out.println("within comment else");
	            		state=state.WITHIN_COMMENT;
	            		//pos++;
	            		//System.out.println("after times state position"+pos);
	            	}
	            }break;
	            case WITHIN_COMMENT: {
	            	//System.out.println("within comment"+pos);
	            	//System.out.println("gfgfhgf"+ ch);
	            	if(ch=='*')
	            	{	if(pos<length-1)
	            		{
	            		if(chars.charAt(pos+1)=='/')
	            		{
	            			state=state.START;
	            			pos=pos+2;
	            		}
	            		else{pos++;}
	            	}
	            	else pos++;
	            	}
	            	else if(ch=='\n')
	            	{
	            		Line_length.add(pos);
//	            		System.out.println("hfghfgjf");
//	            		System.out.println(Line_length.size());
	            		pos++;
	            	}
	            	else{
	            	//	System.out.println("no state within comment");
	            		pos++;
	            	}
	            }break;
	            
	            default:  assert false;
			}
		}
		tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}



	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;
	final HashMap<String,Kind> hmap;
	final ArrayList<Integer> Line_length;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}
	
	 /*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */public Token peek() {
	    if (tokenNum >= tokens.size())
	        return null;
	    return tokens.get(tokenNum);
	}

	

	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		
		return t.getLinePos();
	}


}
