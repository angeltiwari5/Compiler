package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class ConstantExpression extends Expression {

	public ConstantExpression(Token firstToken) {
		super(firstToken);
	}
	

	@Override
	public String toString() {
		return "ConstantExpression [firstToken=" + firstToken + "]";
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitConstantExpression(this,arg);
		
	}


	@Override
	public TypeName gettypename() {
		// TODO Auto-generated method stub
		return this.typeName;
	}


	@Override
	public void settypename(TypeName type) {
		// TODO Auto-generated method stub
		this.typeName=type;
		
	}

}
