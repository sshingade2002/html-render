import java.util.Scanner;

/**

 * HTMLRender

 * This program renders HTML code into a JFrame window.

 * It requires your HTMLUtilities class and

 * the SimpleHtmlRenderer and HtmlPrinter classes.

 *

 * The tags supported:

 * <html>, </html> - start/end of the HTML file

 * <body>, </body> - start/end of the HTML code

 * <p>, </p> - Start/end of a paragraph.

 * Causes a newline before and a blank line after. Lines are restricted

 * to 80 characters maximum.

 * <hr> - Creates a horizontal rule on the following line.

 * <br> - newline (break)

 * <b>, </b> - Start/end of bold font print

 * <i>, </i> - Start/end of italic font print

 * <q>, </q> - Start/end of quotations

 * <hx>, </hx> - Start/end of heading of size x = 1, 2, 3, 4, 5, 6

 * Optional tags supported:

 * <pre>, </pre> - Preformatted text, optional

 *

 * @author

 * @version

 */

public class HTMLRender {

	// the array holding all the tokens of the HTML file

	private String [] tokens;

	private final int TOKENS_SIZE = 100000; // size of array


	// SimpleHtmlRenderer fields

	private SimpleHtmlRenderer render;

	private HtmlPrinter browser;

	private HTMLUtilites util;

	private String [] tag;
	
	private int charcount;
	
	private final int MAX_LINE_COUNT = 80;

	public HTMLRender() {

		// Initialize token array

		tokens = new String[TOKENS_SIZE];

		// Initialize Simple Browser

		render = new SimpleHtmlRenderer();

		browser = render.getHtmlPrinter();

		util = new HTMLUtilites();
		
		tag  = new String [100000];
		
		charcount = 0;

	}

	public static void main(String[] args) {

		HTMLRender hf = new HTMLRender();

		hf.run(args);

	}

	public void run(String[]args) 
	{

		Scanner input = null;

		String fileName = "";
		if (args.length > 0)

			fileName = args[0];

		else 
		{
			System.out.println("Usage: java HTMLTester <htmlFileName>");	
			System.exit(0);
		}
		
		input = OpenFile.openToRead(fileName);
		int tagcount = -1;
		while (input.hasNext()) 
		{
			String line = input.nextLine();
			tokens = util.tokenizeHTMLString(line);

			int count = 0;

			while (count < tokens.length)
			{ 
				//checks if the token is a tag
				if(checkIftag(tokens[count]))
				{
					//if the tag is a break and then print the break
					if (tokens[count].equalsIgnoreCase("<br>")) 
					{
						//reset the charcount because a new line has started
						charcount = 0;
						browser.printBreak();
					}
					
					//if tag is horizontal rule then prints the horizontal rule
					if (tokens[count].equalsIgnoreCase("<hr>")) 
						browser.printHorizontalRule();
					
					//if tag is a opening quote then print an open quote
					if(tokens[count].equalsIgnoreCase("<q>"))
						browser.print("\"");
					
					//if tag is a closing quote, then print closing quote
					if(tokens[count].equalsIgnoreCase("</q>"))
						browser.print("\"");
					
					//checks if the tag is a closing is present
					if(tokens[count].charAt(1) == '/' && !(tokens[count].equalsIgnoreCase("</q>")))
					{	
						//this switch case checks if the tag is any of the header closing tags and prints a new line a
						//resets character count
						switch(tokens[count].toLowerCase())
						{
							case "</h1>":
							case "</h2>":
							case "</h3>":
							case "</h4>":
							case "</h5>":
							case "</h6>":
								charcount = 0;
								browser.print(" ");
								break;
						}
						//if the tag is a closing paragraph tag then the program prints a new line
						//also resets the character count to 0
						if(tokens[count].equalsIgnoreCase("</p>"))
						{
							browser.println();
							charcount = 0;
						}
						//pops the tag out of the tag array because it is closed and no longer a viable "rule"
						tag[tagcount] = null;
						tagcount--;
					}

					else
					{
						//prints new line before printing what is in the paragraph tag
						if(tokens[count].equalsIgnoreCase("<p>"))
							browser.println();
						
						//the tag array saves the last open tag (like a stack, whatever is on top)
						tagcount +=1;
						tag[tagcount] = tokens[count].toLowerCase();
					
					}
				}
				else
				{
					//looks in the array just in case the tag array at the count is null
					if(tag[tagcount] != null)
					{
						//this switch case checks according to the opening tag (the tag on top of the stack)
						//depending on the open tag the tokens print
						switch(tag[tagcount])
						{
							//paragraph tag
							case "<p>" :
								charcount +=tokens[count].length();
								//checks if the character count as exceeded the maximum characters per line
								if(checkIfNewLine(charcount))
								{
									//prints new line and add counts the characters again
									browser.println();
									charcount += tokens[count].length();
								}
								
								//prints the token
								browser.print(tokens[count]);
								//if the next token is not punctuation and also not a tag then a space is printed
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.print(" ");	
									//adds more to character count to account for the space
									charcount++;
								}
								
								//checks if the next token is a tag (previously checks for punctuation)
								//if the next token is a tag, then a space is printed
								if (count+1 < tokens.length && checkIftag(tokens[count+1])) {
									browser.print(" ");
									charcount++;
								}
								
								// if end of line then print space
								if (count+1 >= tokens.length && charcount < 80) {
									browser.print(" ");
									charcount++;
								}
							break;
							//bold tag
							case "<b>" :
								charcount +=tokens[count].length();
								//checks if the character count as exceeded the maximum characters per line
								if(checkIfNewLine(charcount))
								{
									//prints new line and add counts the characters again
									browser.println();
									charcount += tokens[count].length();
								}
								//prints the token
								browser.printBold(tokens[count]);
								
								//if the next token is not punctuation and also not a tag then a space is printed
								if(!checkIfNextPunctuation(count) && count+1<tokens.length /*&& !checkIftag(tokens[count+1])*/)
								{
									browser.printBold(" ");
									charcount++;
								}
								
								//checks if the next token is a tag (previously checks for punctuation)
								//if the next token is a tag, then a space is printed
								if (count+1 < tokens.length && !checkIfNextPunctuation(count) && checkIftag(tokens[count+1]) && !checkIfNextPunctuation(count)) {
									browser.print(" ");
									charcount++;
								}
								
								// if eol then print space
								if (count+1 >= tokens.length && charcount < 80) {
									browser.printBold(" ");
									charcount++;
								}
							break;

							case "<i>" :
								charcount += tokens[count].length();
								if(checkIfNewLine(charcount))
								{
									browser.println();
									charcount += tokens[count].length();
								}
								
								browser.printItalic(tokens[count]);
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printItalic(" ");
									charcount++;
								}
								
								// if eol then print space
								if (count+1 >= tokens.length && charcount < 80) {
									browser.printItalic(" ");
									charcount++;
								}
								
							break;

							case "<h1>" :
								charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/40);
								if(checkIfNewLine(charcount))
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/40);
								}
								
								browser.printHeading1(tokens[count]);
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading1(" ");
									charcount++;
								}
							break;

							case "<h2>" :
								charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/53);
								if(checkIfNewLine(charcount))
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/53);
								}
								
								browser.printHeading2(tokens[count]);
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading2(" ");
									charcount++;
								}
								
							break;

							case "<h3>" :
								charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/68);
								if(checkIfNewLine(charcount))
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/68);
								}
								
								browser.printHeading3(tokens[count]);
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading3(" ");
									charcount++;
								}
								
								// if eol then print space
								if (count+1 == tokens.length && charcount < 80) {
									browser.printHeading3(" ");
									charcount++;
								}
								
							break;

							case "<h4>" :
								charcount = charcount +tokens[count].length()*(int)(MAX_LINE_COUNT/80);
								if (checkIfNewLine(charcount)) 
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/80);
								}
								
								browser.printHeading4(tokens[count] + " ");
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading4(" ");
									charcount++;
								}
							break;

							case "<h5>" :
								charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/96);
								if (checkIfNewLine(charcount)) 
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/96);
								}
								
								browser.printHeading5(tokens[count] + " ");
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading5(" ");
									charcount++;
								}
							break;

							case "<h6>" :
								charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/119);
								if (checkIfNewLine(charcount)) 
								{
									browser.println();
									charcount = charcount + tokens[count].length()*(int)(MAX_LINE_COUNT/119);
								}
								
								browser.printHeading6(tokens[count] + " ");
								if(!checkIfNextPunctuation(count) && count+1<tokens.length && !checkIftag(tokens[count+1]))
								{
									browser.printHeading6(" ");
									charcount++;
								}
							break;
								
							default :
								charcount += tokens[count].length();
								if(checkIfNewLine(charcount))
								{
									browser.println();
									charcount += tokens[count].length();
								}
								
								browser.print(tokens[count]);
								
								// reading next word in the token and check for any punctuation
								if( count+1 < tokens.length && !checkIfNextPunctuation(count) && !checkIftag(tokens[count+1]))
								{
									browser.print(" ");
									charcount++;
								}
								
								if (count+1 < tokens.length && checkIftag(tokens[count+1])) {
									browser.print(" ");
									charcount++;
								}
								
								// if eol then print space
								if (count+1 >= tokens.length) {
									browser.print(" ");
									charcount++;
								}
						}
						
					}

				}

				count++;

			}
			
		}
			input.close();
		}

		private boolean checkIfNextPunctuation(int counter)
		{
			if(counter+1 < tokens.length && util.isPunctuation(tokens[counter+1].charAt(0)))
				return true;
			return false;
		}
		private boolean checkIfNewLine(int cCount)
		{
			if(cCount > MAX_LINE_COUNT)
			{
				charcount = 0;
				return true;
			}
			return false;
		}
		
		private boolean checkIftag(String s)
		{
			
			if(s.indexOf('<') == 0 && s.indexOf('>') == s.length()-1) {
				return true;
			}
			else
				return false;

		}
		
		
}