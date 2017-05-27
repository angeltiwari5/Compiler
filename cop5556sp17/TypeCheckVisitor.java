package cop5556sp17;

import cop5556sp17.AST.ASTNode;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;
import java.util.ArrayList;
import java.util.*;
import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.LinePos;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Chain c=binaryChain.getE0();
		Token op=binaryChain.getArrow();
		ChainElem chainelem=binaryChain.getE1();
		TypeName val1 = (TypeName) binaryChain.getE0().visit(this, null);
		TypeName val2 = (TypeName) binaryChain.getE1().visit(this, null);
		Token token2= chainelem.getFirstToken();
		if(val1.equals(TypeName.URL) && val2.equals(TypeName.IMAGE))
		{
		if(op.isKind(ARROW))
			{
				binaryChain.settypename(TypeName.IMAGE);
				return binaryChain.gettypename();
			}
				
			
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else if(val1.equals(TypeName.FILE))
		{
			if(op.isKind(ARROW))
			{
				if(val2.equals(TypeName.IMAGE))
				{
					binaryChain.settypename(TypeName.IMAGE);
					return binaryChain.gettypename();
				}
				else{
					throw new TypeCheckException("TypeCheckException occured");
				}
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else if(val1.equals(TypeName.FRAME))
		{
			if(op.isKind(ARROW))
			{
				if(chainelem instanceof FrameOpChain &&(token2.isKind(KW_XLOC)||token2.isKind(KW_YLOC)))
				{
					binaryChain.settypename(TypeName.INTEGER);
					return binaryChain.gettypename();
				}
				else if(chainelem instanceof FrameOpChain &&(token2.isKind(KW_HIDE)||token2.isKind(KW_SHOW)||token2.isKind(KW_MOVE))){
					binaryChain.settypename(TypeName.FRAME);
					return binaryChain.gettypename();
				}
				else{
					throw new TypeCheckException("TypeCheckException occured");
				}
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		else if(val1.equals(TypeName.IMAGE))
		{
			if(op.isKind(ARROW))
			{
				if(chainelem instanceof ImageOpChain &&(token2.isKind(OP_WIDTH)||token2.isKind(OP_HEIGHT)))
				{
					binaryChain.settypename(TypeName.INTEGER);
					return binaryChain.gettypename();
				}
				else if(chainelem.visit(this, null).equals(TypeName.FRAME)){
					binaryChain.settypename(TypeName.FRAME);
					return binaryChain.gettypename();
				}
				
				else if(val2.equals(TypeName.FILE)){
					binaryChain.settypename(TypeName.NONE);
					return binaryChain.gettypename();
				}
				else if(chainelem instanceof FilterOpChain &&(token2.isKind(OP_GRAY)||token2.isKind(OP_BLUR)||token2.isKind(OP_CONVOLVE))){
					binaryChain.settypename(TypeName.IMAGE);
					return binaryChain.gettypename();
				}
				else if(chainelem instanceof ImageOpChain && token2.isKind(KW_SCALE)) {
					binaryChain.settypename(TypeName.IMAGE);
					return binaryChain.gettypename();
				}
				else if(chainelem instanceof IdentChain && chainelem.gettypename().equals(TypeName.IMAGE)){
					binaryChain.settypename(TypeName.IMAGE);
					return binaryChain.gettypename();
				}
				else{
					throw new TypeCheckException("TypeCheckException occured");
				}
			}
			else if(op.isKind(BARARROW))
			{
				if(chainelem instanceof FilterOpChain &&(token2.isKind(OP_GRAY)||token2.isKind(OP_BLUR)||token2.isKind(OP_CONVOLVE))){
					binaryChain.settypename(TypeName.IMAGE);
					return binaryChain.gettypename();
				}
				else{
					throw new TypeCheckException("TypeCheckException occured");
				}
				
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		else if(val1.equals(TypeName.INTEGER)&&op.isKind(ARROW)){
		
			if(chainelem instanceof IdentChain && chainelem.gettypename().equals(TypeName.INTEGER)){
				binaryChain.settypename(TypeName.INTEGER);
				return binaryChain.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		else{
			throw new TypeCheckException("TypeCheckException Occured");
		}

		

	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		Expression e0=binaryExpression.getE0();
		Expression e1=binaryExpression.getE1();
		Token op=binaryExpression.getOp();

		
		
		if(e0.visit(this, null).equals(TypeName.INTEGER)&&e1.visit(this, null).equals(TypeName.INTEGER)){
			if(op.isKind(PLUS)||op.isKind(MINUS)||op.isKind(TIMES)||op.isKind(DIV)||op.isKind(MOD)){
				binaryExpression.settypename(TypeName.INTEGER);
				return binaryExpression.gettypename();
			}
			
			else if(op.isKind(LT)||op.isKind(GT)||op.isKind(LE)||op.isKind(GE))
			{
				binaryExpression.settypename(TypeName.BOOLEAN);
				return binaryExpression.gettypename();
			}
			else if(op.isKind(EQUAL)||op.isKind(NOTEQUAL))
			{
				binaryExpression.settypename(TypeName.BOOLEAN);
				return binaryExpression.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else if(e0.visit(this, null).equals(TypeName.IMAGE)&&e1.visit(this, null).equals(TypeName.IMAGE)){
			if(op.isKind(PLUS)||op.isKind(MINUS)){
				binaryExpression.settypename(TypeName.IMAGE);
				return binaryExpression.gettypename();
			}
			else if(op.isKind(EQUAL)||op.isKind(NOTEQUAL))
			{
				binaryExpression.settypename(TypeName.BOOLEAN);
				return binaryExpression.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		else if(e0.visit(this, null).equals(TypeName.INTEGER)&&e1.visit(this, null).equals(TypeName.IMAGE))
		{
			if(op.isKind(TIMES))
			{
				binaryExpression.settypename(TypeName.IMAGE);
				return binaryExpression.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else if(e0.visit(this, null).equals(TypeName.IMAGE)&&e1.visit(this, null).equals(TypeName.INTEGER))
		{
			if(op.isKind(TIMES)||op.isKind(MOD)||op.isKind(DIV))
			{
				binaryExpression.settypename(TypeName.IMAGE);
				return binaryExpression.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else if(e0.visit(this, null).equals(TypeName.BOOLEAN)&&e1.visit(this, null).equals(TypeName.BOOLEAN))
		{
			 if(op.isKind(LT)||op.isKind(GT)||op.isKind(LE)||op.isKind(GE))
			{
				binaryExpression.settypename(TypeName.BOOLEAN);
				return binaryExpression.gettypename();
			}
			 else if(op.isKind(EQUAL)||op.isKind(NOTEQUAL))
				{
					binaryExpression.settypename(TypeName.BOOLEAN);
					return binaryExpression.gettypename();
				}
			 else if(op.isKind(AND)||op.isKind(OR))
			 {
				 binaryExpression.settypename(TypeName.BOOLEAN);
					return binaryExpression.gettypename();
			 }
			 else{
					throw new TypeCheckException("TypeCheckException occured");
				}
		}
		
		else if(e0.visit(this, null).equals(e1.visit(this, null))){
			if(op.isKind(EQUAL)||op.isKind(NOTEQUAL))
			{
				binaryExpression.settypename(TypeName.BOOLEAN);
				return binaryExpression.gettypename();
			}
			else{
				throw new TypeCheckException("TypeCheckException occured");
			}
		}
		
		else{
			throw new TypeCheckException("TypeCheckException occured");
		}
		
		
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		List<Dec> declist=block.getDecs();
		List<Statement> statlist=block.getStatements();
		for(Dec d:declist)
		{
			visitDec(d,null);
		}
		for(Statement st:statlist)
		{
			st.visit(this, null);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.settypename(TypeName.BOOLEAN);
		return booleanLitExpression.gettypename();
		
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple t= filterOpChain.getArg();
		List<Expression> list=t.getExprList();
		if(list.isEmpty())
		{	t.visit(this, null);
			filterOpChain.settypename(TypeName.IMAGE);
			return filterOpChain.gettypename();
		}
		else 
			throw new TypeCheckException("TypeCheckException occured");
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple t=frameOpChain.getArg();
		List<Expression> list=t.getExprList();
		Token first=frameOpChain.getFirstToken();
		if(first.isKind(KW_SHOW)||first.isKind(KW_HIDE))
		{
			if(list.isEmpty())
			{
				t.visit(this, null);
				frameOpChain.settypename(TypeName.NONE);
				return frameOpChain.gettypename();
				
			}
			else 
				throw new TypeCheckException("TypeCheckException occured");
		}
		else if (first.isKind(KW_XLOC)||first.isKind(KW_YLOC) ){
			if(list.isEmpty())
			{	
				t.visit(this, null);
				frameOpChain.settypename(TypeName.INTEGER);
				return frameOpChain.gettypename();
				
			}
			else 
				throw new TypeCheckException("TypeCheckException occured");
}	
		
        else if(first.isKind(KW_MOVE)){
   
        	if(list.size()==2)
			{
        		t.visit(this, null);
				frameOpChain.settypename(TypeName.NONE);
				return frameOpChain.gettypename();
				
			}
        	else 
				throw new TypeCheckException("TypeCheckException occured bug in the parser");
        	
}
        else 
			throw new TypeCheckException("TypeCheckException occured bug in the parser");
		
		
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		
		// TODO Auto-generated method stub
		Token first=identChain.getFirstToken();
		Dec d=symtab.lookup(first.getText());
		if(d!=null)
		{
			
	//	System.out.println("HERE CODE");
		 identChain.settypename(d.gettypename());
		 identChain.setDec(d);
		 
		// System.out.println(identChain.getDec().getFirstToken().getText());
//			d.settypename(Type.getTypeName(d.getFirstToken()));
//			identChain.setDec(d);
//			identChain.settypename(Type.getTypeName(d.getFirstToken()));
		 return identChain.gettypename();
		}
		else 
			throw new TypeCheckException("TypecheckException occured");
		
		
	}


	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first=identExpression.getFirstToken();
		Dec d=symtab.lookup(first.getText());
		if(d!=null)
		{
			
		 identExpression.settypename(d.gettypename());
		
		 identExpression.setDec(d);
		 return identExpression.gettypename();
		}
		else 
			throw new TypeCheckException("TypecheckException occured");
		
		
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(ifStatement.getE().visit(this,null).equals(TypeName.BOOLEAN))
		{
			visitBlock(ifStatement.getB(),null);
		}
		else throw new TypeCheckException("TypeCheckException Occured");
		return null;
		
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.settypename(TypeName.INTEGER);
		return intLitExpression.gettypename();
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e=sleepStatement.getE();
		if(!(e.visit(this, null).equals(TypeName.INTEGER)))
			throw new TypeCheckException("TypeCheckException Occured");
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(whileStatement.getE().visit(this,null).equals(TypeName.BOOLEAN))
		{
			visitBlock(whileStatement.getB(),null);
		}
		else throw new TypeCheckException("TypeCheckException Occured");
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		
		if(!symtab.insert(declaration.getIdent().getText(), declaration))
		{
			throw new TypeCheckException("Cannot be inserted in same scope in declration.Inserting at"+declaration.getIdent().getText());
		}
		else{
			symtab.insert(declaration.getIdent().getText(), declaration);
			declaration.settypename(Type.getTypeName(declaration.getFirstToken()));
		}
		return declaration.gettypename();
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<ParamDec> paramlist=program.getParams();
		for(ParamDec pd : paramlist){
			visitParamDec(pd,null);
		}
		visitBlock(program.getB(),null);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		IdentLValue val=assignStatement.getVar();
		
		

		if(!(val.visit(this, null).equals(assignStatement.getE().visit(this,null))))
			throw new TypeCheckException("TypeCheckException occured");
			
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token first=identX.getFirstToken();
		Dec d=symtab.lookup(first.getText());
		if(d!=null)
		{
			
		 identX.setDec(d);
		 
		 return d.gettypename();
		
		}
		else 
			throw new TypeCheckException("no dec obtained");
		
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		if(!(symtab.insert(paramDec.getIdent().getText(),paramDec)))
		{
			throw new TypeCheckException("Cannot be inserted in same scope in paramdec.Inserting at"+paramDec.getIdent().getText());
		}
		
		else{
			symtab.insert(paramDec.getIdent().getText(),paramDec);
			paramDec.settypename(Type.getTypeName(paramDec.getFirstToken()));
		}
		return paramDec.gettypename();
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.settypename(TypeName.INTEGER);
		return constantExpression.gettypename();
		
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Tuple t=imageOpChain.getArg();
		List<Expression> list=t.getExprList();
		Token first=imageOpChain.getFirstToken();
		
		if(first.isKind(OP_WIDTH)||first.isKind(OP_HEIGHT))
		{
			if(list.size()==0)
			{	t.visit(this, null);
				imageOpChain.settypename(TypeName.INTEGER);
				return imageOpChain.gettypename();
				
			}
			else 
				throw new TypeCheckException("TypeCheckException occured");
		}
	
		
        else if(first.isKind(KW_SCALE)){
   
        	if(list.size()==1)
        		
			{	t.visit(this, null);
			
				imageOpChain.settypename(TypeName.IMAGE);
				return imageOpChain.gettypename();
				
			}
        	else 
				throw new TypeCheckException("TypeCheckException occured bug in the parser");
        	
}
        else 
			throw new TypeCheckException("TypeCheckException occured bug in the parser");
		
		
		
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<Expression> exprList = tuple.getExprList();
		
		for(Expression expr : exprList) {
			if(!(expr.visit(this,null).equals(TypeName.INTEGER)))
					{
				throw new TypeCheckException("TypeCheckingExceptionOccured");
					}
			
}
		
		return null;
}


}
