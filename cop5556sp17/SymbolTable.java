package cop5556sp17;



import java.util.Stack;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.*;

import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Type.TypeName;


public class SymbolTable {
	
	
	//TODO  add fields
	Stack<Integer> scope_stack;
	int current_scope, next_scope;
    HashMap<String, ArrayList<TableEntry>> table ;
	
	
	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		current_scope = next_scope++;
		scope_stack.push(current_scope);
		
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
	scope_stack.pop(); 
		
		current_scope=scope_stack.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		if(table.containsKey(ident))
		{
			ArrayList<TableEntry> scope_list= table.get(ident);
			
			for (int i=0;i<scope_list.size();i++) {
			   TableEntry entry=scope_list.get(i);
			   int scope=entry.getscope();
					if(current_scope==scope)
					{
					return false;
					}
				}
			TableEntry entry=new TableEntry(current_scope,dec);
			scope_list.add(0,entry);
			table.put(ident, scope_list);
			
		}
		else{
			ArrayList<TableEntry> new_scope= new ArrayList<TableEntry>();
			TableEntry entry=new TableEntry(current_scope,dec);
			new_scope.add(entry);
			table.put(ident,new_scope);
		}
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		Dec dec=null;
		if(table.containsKey(ident))
		{
			ArrayList<TableEntry> scope_list= table.get(ident);
			
			for (int i=0;i<scope_list.size();i++) {
			   TableEntry entry=scope_list.get(i);
			   int scope=entry.getscope();
			   Iterator<Integer> itr=scope_stack.iterator();
			   while(itr.hasNext())
			   {
				   if(itr.next()==scope)
				   {
					   dec=entry.getDeclaration();
						return dec;
				   }
			   }
					
				
			    // now work with key and value...
			}
			
		}
		return dec;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
	
	scope_stack = new Stack<Integer>();
	scope_stack.push(0);
	table = new HashMap<String, ArrayList<TableEntry>>();
	current_scope = 0;
	next_scope = 1;
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		StringBuilder sb=new StringBuilder();
		for(String str : table.keySet()){
			
			sb.append(str);
			ArrayList<TableEntry> entrylist=table.get(str);
			for (int i=0;i<entrylist.size();i++)
			{	TableEntry entry=entrylist.get(i);
				sb.append(entry.getscope());
				sb.append(entry.getDeclaration().firstToken);
			}
			
		}
		return sb.toString();
	}
	class TableEntry{
		int scope;
		Dec dec;
		public TableEntry(int scope, Dec dec){
			this.scope=scope;
			this.dec=dec;
			
		}
		public int getscope() {
			
			return this.scope;
		}

		
		public void settypename(int scope) {
			
			this.scope=scope;
		}
		public Dec getDeclaration() {
			
			return this.dec;
		}

	
		public void setDeclaration(Dec dec) {
			
			this.dec=dec;
		}
		
	}
	


}
