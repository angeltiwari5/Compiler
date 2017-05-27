package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public class IdentChain extends ChainElem {
	Dec dec;
	public IdentChain(Token firstToken) {
		super(firstToken);
	}


	@Override
	public String toString() {
		return "IdentChain [firstToken=" + firstToken + "]";
	}


	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitIdentChain(this, arg);
	}

	public Dec getDec() {
		// TODO Auto-generated method stub
		return this.dec;
	}
	public void setDec(Dec d) {
		// TODO Auto-generated method stub
		this.dec=d;
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
