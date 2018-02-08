

/**
 *	Utilities for handling HTML
 *
 *	@author Supriya Shingade
 *	@since October 17, 2017
 */
public class HTMLUtilites {
	
	/**
	 * The main utility in this class. 
	 * It takes a string as input, then tokenizes the string 
	 * and passes back an array of string tokens.
	 * Precondition: the string is not null
	 * @param str 			the input string to tokenize
	 * @return 				the array of string tokens
	*/
	
	// NONE = not nested in a block, COMMENT = inside a comment block
	// PREFORMAT = inside a pre-format block
	private enum TokenState { NONE, COMMENT, PREFORMAT };
	// the current tokenizer state
	private static TokenState state; 
	
	public HTMLUtilites()
	{
		state = TokenState.NONE;
		
	}
	public String[] tokenizeHTMLString(String str) {
		String[] result = new String[ 100000 ];	// make the size of the array large!
		int resultIndex = -1;
	
		int strIndex = 0;
		
		//string to hold the token
		String token = "";
		while(strIndex<str.length())
		{
			char c = str.charAt(strIndex);
			//check if the state is comment because if it is comment then it shouldn't be tokenized
			if(state == TokenState.COMMENT)
			{
				//this check is inside because we only need to check the end if the comment has started
				if(checkIfCommentEnd(c, str)) 
				{
					//sets state to none again and pushes forward the pointer in the string
					String endCommentTag = "-->";
					state = TokenState.NONE;
					strIndex = str.indexOf("-->",str.indexOf(c))+endCommentTag.length();
					
				}
				//if it is a comment but there was no end comment tag the rest of the line gets skipped
				else
					strIndex = str.length();
			}
			//if the state is not comment then tokenize
			else
			{
				//if the c is a letter then tokenize
				if(Character.isLetter(c))
				{
					//make sure to check the word does not run out or the line also checks if the word continues
					while(strIndex < str.length() && Character.isLetter(c) && !Character.isWhitespace(c)&& !isPunctuation(c))
					{
						token+=c;
						strIndex++;
						if(strIndex<str.length())
							c = str.charAt(strIndex);
									
					}
					//tokenizes and adds to the array
					resultIndex++;
					result[resultIndex] = token;
					token = "";
		
				}
				//if c is a digit or a negative digit then it tokenizes
				else if (c == '-' && checkIfNeg(c,str,strIndex)||Character.isDigit(c))
				{
					//accepts exponents and '-'
					while(strIndex < str.length() && (c == '.' || c == 'e'||c == '-')||Character.isDigit(c))
					{
						token+=c;
						strIndex++;
						if(strIndex<str.length())
							c = str.charAt(strIndex);
										
					}
					//tokenizes and adds to array
					resultIndex++;
					result[resultIndex] = token;
					token = "";
				}
				// if the character is not a number or a letter then checks if c is a tag	
				if(c == '<')
				{
					//checks if the rest of the tag isn't for a comment
					if(!checkIfComment(str, c, strIndex))
					{
						//loops through and tokenizes till end of tag
						while(!(c == '>') )
						{
							token+=str.charAt(strIndex);
							strIndex++;
							if(strIndex < str.length())
								c = str.charAt(strIndex);
																
						}
						//tokenizes and adds to array
						//adds the last character to token because in the loop it wasn't added
						token+=c;
						resultIndex++;
						result[resultIndex] = token;
						token = "";
					}
					//if it is a comment then its state is set to comment
					else
					{
						state = TokenState.COMMENT;
					}
				}
				
				//if its not a tag then the checks if c is puncuation
				else if(isPunctuation(c))
				{		
					//has to check again if it is not a comment because puncuation can occur after comment tag
					if(state == TokenState.NONE)
					{
						//separately tokenizes ellipses
						if(str.indexOf("...", strIndex) == strIndex)
						{
							token = "...";
							strIndex+=2;
							c = str.charAt(strIndex);
						}
						//normally tokenizes puncuation
						else
							token+=c;
						
						resultIndex++;
						result[resultIndex] = token;
						token = "";
							
					}

				}	
				//increments pointer to set to next character
				strIndex++;
	
			}
		}
			
		
		
		//in case last token goes to the end of the str
		if (token.length()>0)
		{
			resultIndex++;
			result[resultIndex] = token;
		}
		
		result = sizeArray(result, resultIndex+1);
		
		return result;
	}
	/**
	 * checks if the tag is a start of a comment
	 * @param s 			the input string to tokenize
	 * @param c				character that is currently being checked
	 * @param index			where the pointer is at in the string
	 * @return 				boolean value of if it is a comment or not
	*/
	public boolean checkIfComment(String s, char c, int index)
	{
		if(s.indexOf("<!--", s.indexOf(c)) > -1)
			return true;
		
		
		return false;
	}
	/**
	 * checks if the tag is a start of a comment
	 * @param s 			the input string to tokenize
	 * @param c				character that is currently being checked
	 * @return 				boolean value of if it is at end of comment or not
	*/
	public boolean checkIfCommentEnd(char c, String s)
	{
		if(s.indexOf("-->")>-1 )
		{
			//System.out.print("true comment entd");
			
			return true;
		}
		return false;
	}
	/**
	 * checks if it is a negative number (if number is present after the '-'
	 * @param s 			the input string to tokenize
	 * @param c				character that is currently being checked
	 * @param index			where the pointer is at in the string
	 * @return 				boolean value of if it is a negative number or not
	*/
	public boolean checkIfNeg(char c, String s, int index)
	{
		if(c == '-' && Character.isDigit(s.charAt(index+1)))
		{
			//System.out.println("true buddy");
			return true;
		}
		
		//System.out.print("false");
		return false;
	}
	
	
	
	/**
	 *	Takes a large string array as input and outputs a 
	 * copy String array that is exactly the size of the number of valid elements
	 * @param arr 			the input String array
	 * @param num 			the number of valid elements in array
	 * @return				a copy of the String array with exactly
	 * 						the number of valid elemtes
	 */
	 public String[] sizeArray(String[] arr, int num)
	 {
		 String [] result = new String[num];
		 for(int a = 0; a<num; a++)
			result [a] = arr[a];
		return result;
	}
	 
	/**
	 *	<Comments go here>
	 */
	public boolean isPunctuation(char c) {
		if(!(Character.isLetterOrDigit(c))&&c<='~'&&c>='!')
			return true;
		return false;
	}
	
	/**
	 *	Print the tokens in the array
	 *	Precondition: All elements in the array are valid String objects. (no nulls)
	 *	@param tokens	an array of String tokens
	 */
	public void printTokens(String[] tokens) {
		if (tokens == null) return;
		for (int a = 0; a < tokens.length; a++) {
			if (a % 5 == 0) System.out.print("\n  ");
			System.out.print("[token " + a + "]: " + tokens[a] + " ");
		}
		System.out.println();
	}

}