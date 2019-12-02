package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	String temp, prev = "";
    	while (st.hasMoreTokens()) {
    		temp = st.nextToken(); 
    		//if the token is just a number, we don't have to worry about it and continue
    		if(!Character.isDigit(temp.charAt(0))) {
    			if(!isDelims(temp)){
    				//check if the last token was supposed to be an array
    				if(temp.equals("[")){
    					Array arr = new Array(prev);
    					//last element wasn't an a variable, remove it to maintain consistency
    					vars.remove(vars.size()-1);
    					
    					//check for duplicates before adding
    					if(!arrays.contains(arr)) arrays.add(arr); 
    				} else {
    					Variable var = new Variable(temp);
    					//check for duplicates before adding
    					if(!vars.contains(var)) vars.add(var);
    				}
    			}
    		}
            prev = temp; 
        }
    	/*
    System.out.println("Array size: " + arrays.size());
    System.out.println("Variable size: " + vars.size());
    */
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	
    	StringTokenizer st = new StringTokenizer(expr, delims, true);
    	Stack<Float> values = new Stack <Float>();
    	Stack<String> opts = new Stack <String>();
    	
    	String temp,next,varName,arrName,insideExpr;
    	boolean isVar; 
    	int end,val,bracket;
    	float arrVal,first,second,varVal;
    	String openBrack = "[";
    	String closeBrack = "]";
    	String operators = "+-*/";
    	String spaceOrTab = " \t";
    	
    	while(st.hasMoreTokens()) {
    		temp = st.nextToken(); 
    		//!temp.equals(" ") && !temp.equals("\t")
    		if(!spaceOrTab.contains(temp)) {
    			//System.out.println(temp);
    			if(Character.isDigit(temp.charAt(0))){
    				values.push(Float.parseFloat(temp));
    			}
    			
    			else if(operators.contains(temp)) {
        			while( !opts.isEmpty()  &&  pemdas(temp, opts.peek()) ) {
        				//have to pop out of order, as the first item popped is the second operand, and the next item is the first operand
        				second = values.pop();
        				first = values.pop();
        				values.push(solveMath(opts.pop(),first,second));

        			}
        			opts.push(temp);
        		}
    			//checked for all other operations, now time to evaluate all parenthesis and inner expressions first
    			else if(temp.equals("(")) {
    				opts.push(temp);
    			}
    			//first push everything into the the stack, then once the entire term is finished, we can evaluate
    			else if(temp.equals(")")){
    				while(!opts.peek().equals("(")) {
    					//Same logic as previous math execution, have to pop second and then first due to LIFO principle of stacks
    					second = values.pop();
        				first = values.pop();
        				float newNum = solveMath(opts.pop(), first, second);
        				values.push(newNum);
        			}
        			opts.pop();
    			}
    			
    			
    			//all other cases have been checked, at this point the token must be some kind of letter i.e. a variable or array
    			else {
					  isVar = true;
					  for(int i = 0; i < vars.size(); i++) {
						  varName = vars.get(i).name;
						  varVal = (float)vars.get(i).value;
						  if(temp.equals(varName)){ 
							  values.push(varVal);
							  break; 
						  } 
					  } 
					  isVar = false;
					  
					  //if it's not an variable, the boolean variable will be false and we know it must be a array, who's value can be easily found from the array list
					  if(!isVar) { 
						  for(int i = 0; i < arrays.size(); i++) {
							  arrName = arrays.get(i).name;
							  //compare the token to all the known arrays, if we hit a match we start counting brackets
							  if(temp.equals(arrName)) { 
								  isVar = false; 
								  bracket = 0;  
								  //the insideExpr is the expression inside the array used for the recursive evaluation
								  insideExpr = ""; 
								  next = st.nextToken(); 
								  
								  if(next.equals(openBrack))  bracket++;
								  else if(next.equals(closeBrack)) bracket--; 
								  
								  insideExpr += next;
								  
								  while(bracket!= 0) { 
									  next = st.nextToken(); 
									  if(next.equals(openBrack)) bracket++;
									  else if(next.equals(closeBrack)) bracket--; 
									  //add on the token to the statement to be evaluated
									  insideExpr += next;
									  }
								  
								  //can't include last index, will get out of bounds error
								  end = insideExpr.length()-1;
								  //exclude the opening bracket so that the recursive loop isn't triggered
								  insideExpr = insideExpr.substring(1, end); 
								  //call upon recursion to evaluate the statement inside the array
								  val = (int)evaluate(insideExpr,vars,arrays);
								  //any statement inside the array is going to be an index, so we then retrieve the value at the index and push it into the values stack
								  arrVal = (float)arrays.get(i).values[val];
								  values.push(arrVal); 
								  break; 
							  }
							  
						  } 
					  }
					 
					  
					  
					  
    			}
    		}
    	}
    	
//left over numbers are evaluated and math is done
    	while(!opts.isEmpty()) {
    		second = values.pop();
			first = values.pop();
			values.push(solveMath(opts.pop(),first,second));
    		//values.push(solveMath(opts.pop(),values.pop(),values.pop()));	
    	}
    	
    	// following line just a placeholder for compilation
    	//the last number pushed is the final result of all operations (in PEMDAS order)
    	return values.pop();
    }
    //determines which of two operations should go first (i.e. checks does operator 2 have precedence over operator 1)
    private static boolean pemdas(String opt1, String opt2) {
      	if ("()[]".contains(opt2)) {
            return false; 
    	}
        if ( ("*/".contains(opt1)) && ("+-".contains(opt2)) ){
            return false; 
        }
            return true;
        
    }
    //the first number is the second number popped out of the stack (it came before, so it is further in the stack)
    //simple math to solve the operations once we've isolated them
    private static float solveMath(String operator, float operand1, float operand2) {
    	switch(operator.charAt(0)) {
    	case '+':
    		return operand1 + operand2;
    	case '-':
    		return operand1 - operand2;
    	case '*':
    		return operand1 * operand2;
    	case '/':
    		return operand1 / operand2;
    	}
    	return 0;
    }
    
    private static boolean isDelims(String token) {
    	boolean presence = false;
    	String modifiedDelims = " \t*+-/()]";
    	if(modifiedDelims.contains(token)) presence = true;
    	
    	return presence;
    }
}
