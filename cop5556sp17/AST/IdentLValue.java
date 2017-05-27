package cop5556sp17.AST;

import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.Scanner.Token;

public class IdentLValue extends ASTNode {
	Dec dec;
	TypeName typeName;
	public void setDec(Dec dec){
		this.dec=dec;
	}
	public Dec getDec(){
		return this.dec;
	}
	public TypeName gettypename() {
		// TODO Auto-generated method stub
		return this.typeName;
	}

	
	public void settypename(TypeName type) {
		// TODO Auto-generated method stub
		this.typeName=type;
	}
	public IdentLValue(Token firstToken) {
		super(firstToken);
		
	}
	
	@Override
	public String toString() {
		return "IdentLValue [firstToken=" + firstToken + "]";
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentLValue(this,arg);
	}

	public String getText() {
		return firstToken.getText();
	}

}
