package cop5556sp17;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

import static cop5556sp17.AST.Type.TypeName.FRAME;
import static cop5556sp17.AST.Type.TypeName.IMAGE;
import static cop5556sp17.AST.Type.TypeName.URL;
import static cop5556sp17.Scanner.Kind.*;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
		this.slot=1;
		this.slot1=1;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	Integer count=0;
	int slot;
	int slot1 ;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params){
			dec.visit(this, mv);
			 count++;
		}
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();

		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, startRun, endRun, 1);
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method
		
		
		cw.visitEnd();//end of class
		
		//generate classfile and return it
		return cw.toByteArray();
	}



	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().gettypename());
		if (assignStatement.getVar().getDec().gettypename().isType(IMAGE))
			mv.visitMethodInsn(INVOKESTATIC,  "cop5556sp17/PLPRuntimeImageOps", "copyImage", PLPRuntimeImageOps.copyImageSig, false);
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		arg = "isLeft";
		Chain chain = binaryChain.getE0();
		ChainElem chainelem = binaryChain.getE1();
		chain.visit(this, arg);
		
		if(chain.gettypename().equals(TypeName.FILE)) {
			mv.visitMethodInsn(INVOKESTATIC,  "cop5556sp17/PLPRuntimeImageIO", "readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
		}
		else if (chain.gettypename().equals(TypeName.URL)) {
			mv.visitMethodInsn(INVOKESTATIC,  "cop5556sp17/PLPRuntimeImageIO", "readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
		} 
		arg = "false";
		chainelem.visit(this, arg);

		if (binaryChain.getArrow().isKind(BARARROW) && chainelem.getFirstToken().isKind(Kind.OP_GRAY))
			//chain.visit(this, arg);
		{
			mv.visitInsn(DUP);
			mv.visitVarInsn(ASTORE, slot1);
		
		}
		return null;

		
	}

@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this
		binaryExpression.getE0().visit(this, arg);
		binaryExpression.getE1().visit(this, arg);
		Label startLabel = new Label();
		Label endLabel = new Label();
		Kind op=binaryExpression.getOp().kind;
        switch (op) {
        case PLUS:
        	if (binaryExpression.getE0().gettypename().equals(TypeName.IMAGE)&&binaryExpression.getE1().gettypename().equals(TypeName.IMAGE)) {
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "add",PLPRuntimeImageOps.addSig,false);
			} else {
				mv.visitInsn(IADD);
			}
            break;
        case MINUS:
        	if (binaryExpression.getE0().gettypename().equals(TypeName.IMAGE)&&binaryExpression.getE1().gettypename().equals(TypeName.IMAGE)) {
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "sub",PLPRuntimeImageOps.subSig,false);
			} else {
				mv.visitInsn(ISUB);
			}
            break;
        case AND:
            mv.visitInsn(IAND);
            break;
        case OR:
            mv.visitInsn(IOR);
            break;
        case TIMES:
        	if (binaryExpression.getE0().gettypename().equals(TypeName.INTEGER)&&binaryExpression.getE1().gettypename().equals(TypeName.IMAGE)) {
					mv.visitInsn(SWAP);
					mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul",PLPRuntimeImageOps.mulSig,false);
				
			}
        	else if(binaryExpression.getE0().gettypename().equals(TypeName.IMAGE)&&binaryExpression.getE1().gettypename().equals(TypeName.INTEGER)){
        		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul",PLPRuntimeImageOps.mulSig,false);
        	}
        	else {
				mv.visitInsn(IMUL);
			}
			break;
        case DIV:
       if(binaryExpression.getE0().gettypename().equals(TypeName.IMAGE)&&binaryExpression.getE1().gettypename().equals(TypeName.INTEGER)){
        		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "div",PLPRuntimeImageOps.divSig,false);
        	}
        	else {
				mv.visitInsn(IDIV);
			}
        	
        	
			break;
        case MOD:
		
    	 if(binaryExpression.getE0().gettypename().equals(TypeName.IMAGE)&&binaryExpression.getE1().gettypename().equals(TypeName.INTEGER)){
    		mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mod",PLPRuntimeImageOps.modSig,false);
    	}
    	else {
			mv.visitInsn(IREM);
		}
        	
        	
        	break;
        case LT:
        	mv.visitJumpInsn(IF_ICMPLT, startLabel);
        	mv.visitInsn(ICONST_0);
        	break;
		case LE:
			mv.visitJumpInsn(IF_ICMPLE, startLabel);
			mv.visitInsn(ICONST_0);
			break;
        case GT:
            mv.visitJumpInsn(IF_ICMPGT, startLabel);
            mv.visitInsn(ICONST_0);
            break;
        case GE:
            mv.visitJumpInsn(IF_ICMPGE, startLabel);
            mv.visitInsn(ICONST_0);
            break;
        case EQUAL:
        	if((binaryExpression.getE0().gettypename().equals(TypeName.INTEGER)&&binaryExpression.getE1().gettypename().equals(TypeName.INTEGER))||(binaryExpression.getE0().gettypename().equals(TypeName.BOOLEAN)&&binaryExpression.getE1().gettypename().equals(TypeName.BOOLEAN)))
            {mv.visitJumpInsn(IF_ICMPEQ, startLabel);
            mv.visitInsn(ICONST_0);}
        	else if (binaryExpression.getE0().gettypename().equals(binaryExpression.getE1().gettypename())){
        		mv.visitJumpInsn(IF_ACMPEQ, startLabel);
                mv.visitInsn(ICONST_0);
        	}
            break;
        case NOTEQUAL:
        	if((binaryExpression.getE0().gettypename().equals(TypeName.INTEGER)&&binaryExpression.getE1().gettypename().equals(TypeName.INTEGER))||(binaryExpression.getE0().gettypename().equals(TypeName.BOOLEAN)&&binaryExpression.getE1().gettypename().equals(TypeName.BOOLEAN)))
            {mv.visitJumpInsn(IF_ICMPNE, startLabel);
            mv.visitInsn(ICONST_0);}
        	else if (binaryExpression.getE0().gettypename().equals(binaryExpression.getE1().gettypename())){
        		mv.visitJumpInsn(IF_ACMPNE, startLabel);
                mv.visitInsn(ICONST_0);
        	}
            break;
        default: break;
        
        }
		mv.visitJumpInsn(GOTO, endLabel);
		mv.visitLabel(startLabel);
		mv.visitInsn(ICONST_1);
		mv.visitLabel(endLabel);
		return null;	
    
 }

@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		
		Label startBlock = new Label();
		mv.visitLabel(startBlock);
		List<Dec> declist=block.getDecs();
		List<Statement> statlist=block.getStatements();
		for(Dec d: declist){
			d.visit(this,arg);
			
			
		}
		
		for(Statement stat:statlist){
			
			stat.visit(this, arg);
			if(stat instanceof BinaryChain)
				mv.visitInsn(POP);
		}
		Label endBlock = new Label();
		mv.visitLabel(endBlock);
		for(Dec d: declist){
			mv.visitLocalVariable(d.getIdent().getText(), d.gettypename().getJVMTypeDesc(), null,startBlock, endBlock, d.getSlot());
			
		}
		
		return null;
	}
	
	

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		//assert false : "not yet implemented";
		if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENHEIGHT)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenHeight", PLPRuntimeFrame.getScreenHeightSig, false);
		}
		else if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENWIDTH)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName, "getScreenWidth", PLPRuntimeFrame.getScreenWidthSig, false);
		}
		return null;
		
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this
		declaration.setSlot(slot);
		
		if(declaration.gettypename().equals(TypeName.IMAGE))
		{
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slot);				
		}
		else if(declaration.gettypename().equals(TypeName.FRAME))
		{
			
			mv.visitInsn(ACONST_NULL);
			mv.visitVarInsn(ASTORE, slot);
		}
		slot++;
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		//mv.visitInsn(DUP);
		
		if(filterOpChain.getFirstToken().isKind(Kind.OP_BLUR)){
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp",PLPRuntimeFilterOps.opSig, false);
		}
		else if(filterOpChain.getFirstToken().isKind(Kind.OP_CONVOLVE)){
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig, false);
		}
		else if(filterOpChain.getFirstToken().isKind(Kind.OP_GRAY)){
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		}
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		//getx?
		frameOpChain.getArg().visit(this, arg);
		switch(frameOpChain.getFirstToken().kind)
		{
		case KW_SHOW:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc, false);
			break;
		case KW_MOVE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc, false);
			break;
		case KW_HIDE:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc, false);
			break;
		case KW_XLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc, false);
			break;
		case KW_YLOC:
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getYValDesc, false);
			break;
		default: break;
		}
		
		return null;
	}


	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		String str=(String)arg;
		if (str.equals("isLeft")) {
			//System.out.println("CodeHere");
			switch(identChain.getDec().gettypename()) {
			case FILE:
			case URL:
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(), identChain.gettypename().getJVMTypeDesc());
				break;	
			case INTEGER:
					if (identChain.getDec() instanceof ParamDec) {
						mv.visitVarInsn(ALOAD, 0);
						mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(), identChain.gettypename().getJVMTypeDesc());
					}
					else
						mv.visitVarInsn(ILOAD, identChain.getDec().getSlot());
					break;
				
			case IMAGE:
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					slot1=identChain.getDec().getSlot();
					break;
			case FRAME:
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					break;
			}
		}
		
		else {
			switch(identChain.getDec().gettypename()) {
				case FILE:
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className, identChain.getFirstToken().getText(), identChain.gettypename().getJVMTypeDesc());
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "write", PLPRuntimeImageIO.writeImageDesc, false);
				break;
				case INTEGER:
					if (identChain.getDec() instanceof ParamDec) {
						mv.visitInsn(DUP);
						mv.visitVarInsn(ALOAD, 0);
						mv.visitInsn(SWAP);
						mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(), identChain.gettypename().getJVMTypeDesc());
					}
					else {
						mv.visitInsn(DUP);
						mv.visitVarInsn(ISTORE, identChain.getDec().getSlot());
					}
					break;
				case IMAGE:
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
					break;
				
				case FRAME:
					mv.visitVarInsn(ALOAD, identChain.getDec().getSlot());
					mv.visitMethodInsn(INVOKESTATIC,  "cop5556sp17/PLPRuntimeFrame", "createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
					mv.visitInsn(DUP);
					mv.visitVarInsn(ASTORE, identChain.getDec().getSlot());
					break;
			}
		}
		
	return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		if(identExpression.getDec() instanceof ParamDec)
		{		
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className, identExpression.getFirstToken().getText(), identExpression.gettypename().getJVMTypeDesc());
		}
		else {
			if (identExpression.gettypename().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitVarInsn(ILOAD, identExpression.getDec().getSlot());
			else
				mv.visitVarInsn(ALOAD, identExpression.getDec().getSlot());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		if(identX.getDec() instanceof ParamDec)
		{	mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(SWAP);
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(), identX.getDec().gettypename().getJVMTypeDesc());
			
		}
		else {
			if (identX.getDec().gettypename().isType(TypeName.INTEGER, TypeName.BOOLEAN))
				mv.visitVarInsn(ISTORE, identX.getDec().getSlot());
			else
				mv.visitVarInsn(ASTORE, identX.getDec().getSlot());
		}
		return null;

	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label After=new Label();
		mv.visitJumpInsn(IFEQ, After);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(After);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		//assert false : "not yet implemented";
		imageOpChain.getArg().visit(this, arg);
		if(imageOpChain.getFirstToken().isKind(Kind.OP_WIDTH)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", "()I", false);
		}
		else if(imageOpChain.getFirstToken().isKind(Kind.OP_HEIGHT)){
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", "()I", false);
		}
		else if(imageOpChain.getFirstToken().isKind(Kind.KW_SCALE)){
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", PLPRuntimeImageOps.scaleSig, false);
		}
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(intLitExpression.value);
        return null;
	}

 @Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		
		FieldVisitor fv = cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(), paramDec.gettypename().getJVMTypeDesc(), null, null);
		fv.visitEnd();
	
		mv.visitVarInsn(ALOAD, 0);
		if(paramDec.gettypename().equals(TypeName.FILE)) {
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
		}

		mv.visitVarInsn(ALOAD, 1);
		mv.visitInsn(count+3);
		switch(paramDec.gettypename()){
		case INTEGER:
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			break;
		case BOOLEAN:
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			break;
		case FILE:
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
		   break;
		case URL:
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className, "getURL", PLPRuntimeImageIO.getURLSig, false);
			break;
		default:
			break;
		}

		mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), paramDec.gettypename().getJVMTypeDesc());
		return null;


	}
	
	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		//assert false : "not yet implemented";
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		//assert false : "not yet implemented";
		List<Expression> list=tuple.getExprList();
		for(Expression expr: list){
		  expr.visit(this,arg);
			}
				
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		Label Guard=new Label();
	    mv.visitJumpInsn(GOTO, Guard);
	    Label Body=new Label();
		mv.visitLabel(Body);
		whileStatement.getB().visit(this, null);
		mv.visitLabel(Guard);
		whileStatement.getE().visit(this, null);
		mv.visitJumpInsn(IFNE, Body);
		return null;
	}

}
