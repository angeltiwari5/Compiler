package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;;

public abstract class Chain extends Statement {
	TypeName typeName;
	
	public abstract TypeName gettypename();
	public abstract void settypename(TypeName t);
	public Chain(Token firstToken) {
		super(firstToken);
	}

	
}