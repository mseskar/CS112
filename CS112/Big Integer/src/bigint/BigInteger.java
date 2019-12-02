package bigint;

/**
 * This class encapsulates a BigInteger, i.e. a positive or negative integer with 
 * any number of digits, which overcomes the computer storage length limitation of 
 * an integer.
 * 
 */
public class BigInteger {

	/**
	 * True if this is a negative integer
	 */
	boolean negative;
	
	/**
	 * Number of digits in this integer
	 */
	int numDigits;
	
	/**
	 * Reference to the first node of this integer's linked list representation
	 * NOTE: The linked list stores the Least Significant Digit in the FIRST node.
	 * For instance, the integer 235 would be stored as:
	 *    5 --> 3  --> 2
	 *    
	 * Insignificant digits are not stored. So the integer 00235 will be stored as:
	 *    5 --> 3 --> 2  (No zeros after the last 2)        
	 */
	DigitNode front;
	
	/**
	 * Initializes this integer to a positive number with zero digits, in other
	 * words this is the 0 (zero) valued integer.
	 */
	public BigInteger() {
		negative = false;
		numDigits = 0;
		front = null;
	}
	
	/**
	 * Parses an input integer string into a corresponding BigInteger instance.
	 * A correctly formatted integer would have an optional sign as the first 
	 * character (no sign means positive), and at least one digit character
	 * (including zero). 
	 * Examples of correct format, with corresponding values
	 *      Format     Value
	 *       +0            0
	 *       -0            0
	 *       +123        123
	 *       1023       1023
	 *       0012         12  
	 *       0             0
	 *       -123       -123
	 *       -001         -1
	 *       +000          0
	 *       
	 * Leading and trailing spaces are ignored. So "  +123  " will still parse 
	 * correctly, as +123, after ignoring leading and trailing spaces in the input
	 * string.
	 * 
	 * Spaces between digits are not ignored. So "12  345" will not parse as
	 * an integer - the input is incorrectly formatted.
	 * 
	 * An integer with value 0 will correspond to a null (empty) list - see the BigInteger
	 * constructor
	 * 
	 * @param integer Integer string that is to be parsed
	 * @return BigInteger instance that stores the input integer.
	 * @throws IllegalArgumentException If input is incorrectly formatted
	 */
	public static BigInteger parse(String integer) 
	throws IllegalArgumentException {
		integer = integer.trim();	//remove all empty spaces added on accident
		BigInteger bigInt = new BigInteger();	//create the new BigInteger object
		if((integer.length() == 1 && (!Character.isDigit(integer.charAt(0)))) || integer.isEmpty()) throw new java.lang.IllegalArgumentException("Incorrect Format");
		//check the positive and negative signs and spaces in the number, throw an error if the syntax isn't correct, otherwise continue and check for negative or positive 
		if(integer.charAt(0) == '-') {
			bigInt.negative = true; 
			integer = integer.substring(1); //cut off the sign, just remember it 
		}
		else if(integer.charAt(0) == '+') integer = integer.substring(1);
		//at this point the integer has been completely processed and is ready to be broken up into the linked list
		//by now all spaces, positive and negative signs are removed, so if there are any non-digits it's an error
		for (int i = 0; i < integer.length(); i++) {
            //If we find a non-digit character we return false.
            if (!Character.isDigit(integer.charAt(i))) throw new java.lang.IllegalArgumentException("Incorrect Format");
            else if(integer.charAt(i) == '0' && bigInt.front == null) continue; //if the first digit is a zero, we can just remove it and continue checking
            else {
            	//if the item is a number, and the 
            	bigInt.front = new DigitNode(Character.getNumericValue(integer.charAt(i)), bigInt.front);
            	bigInt.numDigits++; 
            }
		}
		//System.out.println("Num Dig: " + bigInt.numDigits + "\nNeg: " + bigInt.negative);
		return bigInt; 
		
	}

	/**
	 * Adds the first and second big integers, and returns the result in a NEW BigInteger object. 
	 * DOES NOT MODIFY the input big integers.
	 * a
	 * NOTE that either or both of the input big integers could be negative.
	 * (Which means this method can effectively subtract as well.)
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return Result big integer
	 */
	public static BigInteger add(BigInteger first, BigInteger second) {
		int addition;
		int carry = 0; //counter value to dictate if the list is empty or not 
		BigInteger result = new BigInteger();
		int count = 0;
		BigInteger above = first;
		BigInteger below = second;
		
		//if the two are the same sign, we can do regular addition and then apply the sign after
		if(first.negative == second.negative) {
			DigitNode ptr1 = first.front;
			DigitNode ptr2 = second.front;
			DigitNode tail = null;
			DigitNode current = new DigitNode(0, null);
			result.negative = first.negative; //set the sum to the same sign as the two integers since they are the same
			
			while (ptr1 != null || ptr2 != null) {

				addition = carry; //either zero or one depending on previous values
				if (ptr1 != null) {
					addition += ptr1.digit;
					ptr1 = ptr1.next;

				}
				if (ptr2 != null) {
					addition += ptr2.digit;
					ptr2 = ptr2.next;
				}

				if (addition > 9) carry = 1;
				else carry = 0;

				addition = addition % 10; //remove any carry's

				tail = new DigitNode(addition, null);//new node
				current.next = tail;//slap the node onto the end of the linked list
				current = tail;//shift our current pointer to the new tail
				result.numDigits++; //keep track of length
				
				if (count == 0) {
					result.front = current;
					count++;
				}

				if (carry == 1 && ptr1 == null && ptr2 == null) {
					tail = new DigitNode(1, null); //add a new digit to the tail.
					current.next = tail;
					result.numDigits++;
				}

			}
			
		}
		
		/*
		 * BIG INTEGERS ARE OF DIFFERENT SIGNS
		 * 
		 * 
		 */
		else {
			if (first.numDigits < second.numDigits) {
				//swap the first and second digits if the first one is smaller than the second (purely based on number of digits)
				above = second;
				below = first;
			}
			//if they are the same length, we need to go through and check each individual digit to determine which is larger
			else if(first.numDigits == second.numDigits) {
				String a = first.toString();
				String b = second.toString();
				if(first.negative) a = a.substring(1);
				if(second.negative) b = b.substring(1);
				boolean same = true;
				for(int i = 0; i < a.length(); i++) {
					//if the second BigInt is larger than the first, swap the two
					if(Character.getNumericValue(a.charAt(i)) < Character.getNumericValue(b.charAt(i))) {
						above = second;
						below = first;
						same = false;
						break; //exit the for loop, no need to go further
					}
					else if(Character.getNumericValue(a.charAt(i)) > Character.getNumericValue(b.charAt(i))) {
						//set the sum to the same sign as the two integers since they are the same
						above = first;
						below = second; 
						same = false;
						break; //first is bigger than second, so we can proceed as usual
					}
					
				}
				
				if(same) return result; //if the two BigIntegers are exactly the same, just opposite signs, return 0	
			}
			
			//first is bigger than second (or they've been swapped accordingly so we can continue as we would normally subtracting)
			DigitNode top = above.front;
			DigitNode bot = below.front;
			int difference = 0;
			int take = 0;
			int topInt,botInt;
			DigitNode tail = null;
			DigitNode current = new DigitNode(0,null);
			
			while(top != null) {
				topInt  = top.digit;
				
				if(bot == null) {
					botInt = 0; 
				}
				else {
					botInt = bot.digit;
					bot = bot.next; 
				}
				if(topInt - botInt - take < 0) {
					difference = topInt - botInt - take + 10;
					take = 1;
				}
				else {
					difference = topInt-botInt-take;
					take = 0;
				}
							
				top = top.next;
				

				//insert the new node into the list
				tail = new DigitNode(difference,null);
				current.next = tail;
				current = tail;
				result.numDigits++;

				if (count == 0) {
					result.front = current;
					count++;
				}
				
			}
			
			
		}
		//System.out.println(result.toString());
		String parseAgain = result.toString();
		result = parse(parseAgain);
		result.negative = above.negative;
		//System.out.println(result.toString());
		//System.out.println("Num Dig: " + result.numDigits + "\nNeg: " + result.negative);
		return result;
	}
	
	/**
	 * Returns the BigInteger obtained by multiplying the first big integer
	 * with the second big integer
	 * 
	 * This method DOES NOT MODIFY either of the input big integers
	 * 
	 * @param first First big integer
	 * @param second Second big integer
	 * @return A new BigInteger which is the product of the first and second big integers
	 */
	public static BigInteger multiply(BigInteger first, BigInteger second) {
		BigInteger top = new BigInteger();
		BigInteger bot = new BigInteger();
		if(first.numDigits < second.numDigits) {
			 top = second;
			 bot = first;
		}
		else {
			top = first;
			bot = second;

		}
		/* IMPLEMENT THIS METHOD */
		BigInteger product = new BigInteger();
		if(top.front == null || bot.front == null) return product;
		DigitNode ptr1 = top.front;
		DigitNode ptr2 = bot.front;

		int prod = (ptr1.digit * ptr2.digit) % 10;
		int carry = (ptr1.digit * ptr2.digit) / 10;
		product.front = new DigitNode(prod, null);
		ptr1 = ptr1.next;

		DigitNode current = product.front;
		DigitNode tail = product.front;

		if (top.negative != bot.negative)
			product.negative = true;
		else
			product.negative = false;

		while (ptr2 != null) {
			while (ptr1 != null) {

				prod = ptr1.digit * ptr2.digit + carry;
				carry = prod / 10;
				prod = prod % 10;

				if (current.next == null) {
					current.next = new DigitNode(prod, null);
				} 
				else {
					carry += (current.next.digit + prod) / 10;
					current.next.digit = (current.next.digit + prod) % 10;
				}
				current = current.next;
				ptr1 = ptr1.next;
			}
			if (carry != 0) {
				current.next = new DigitNode(carry, null);
				carry = 0;
			}
			ptr1 = top.front;
			ptr2 = ptr2.next;
			current = tail;
			tail = tail.next;
			product.numDigits++;

		}

		// following line is a placeholder for compilation
		String parseAgain = product.toString();
		product = parse(parseAgain);
		//System.out.println("Num Dig: " + product.numDigits + "\nNeg: " + product.negative);
		return product;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (front == null) {
			return "0";
		}
		String retval = front.digit + "";
		for (DigitNode curr = front.next; curr != null; curr = curr.next) {
				retval = curr.digit + retval;
		}
		
		if (negative) {
			retval = '-' + retval;
		}
		return retval;
	}
}
