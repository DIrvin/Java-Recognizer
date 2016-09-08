import java.io.*;
//--------------------------------------------
// Homework Assignment 1
// Professor Gordon 
// CSC 135 10:30 – 11:45am Tue/Thurs
// Programmed By Derek Irvin
// to run on Athena (linux) -
//    save as:  Recognizer.java
//    compile:  javac Recognizer.java
//    execute:  java Recognizer
//	  It will however give two Warnings
//
// EBNF Grammar is -
//      program ::= P {declare} B {statemt} E $
//      declare ::= ident {, ident} : V ;
//      statemt ::= assnmt | ifstmt | loop | read | output
//      assnmt  ::= ident ~ exprsn ;
//      ifstmt  ::= I comprsn @ {statemt} [% {statemt}] &
//      loop    ::= W comprsn L {statemt} T
//      read    ::= R ident {, ident } ;
//      output  ::= O ident {, ident } ;
//      comprsn ::= ( oprnd opratr oprnd )
//      exprsn  ::= factor {+ factor}
//      factor  ::= oprnd {* oprnd}
//      oprnd   ::= integer | ident | ( exprsn )
//      opratr  ::= < | = | > | !
//      ident   ::= letter {char}
//      char    ::= letter | digit
//      integer ::= digit {digit}
//      letter  ::= _ | X | Y | Z
//      digit   ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
// 		Tokens: P B E ; , : V ~ I @ % & W L T R O ( ) + *
//				< = > ! _ X Y Z 0 1 2 3 4 5 6 7 8 9 $ 
//--------------------------------------------

//-------------------------------------------------------
// Test Notes
// Test Number 1: PBE$ - Legal
// Test Number 2: PXX:V;BE$ - Legal
// Test Number 3: PXXXXX,X:V;BE$ - Legal
// Test Number 4: P_X_X38,Z3X_12:V;BZZ_~2312*YZ__*((23));E$ - Legal
// Test Number 5: PX:V;BY~1+2;OY;E$ - Legal
// Test Number 6: P_YZ:V;BI(01234=(1))@&E$ - Legal
// Test Number 7: P_X_X38,Z3X_12:V;BZZ_~2312*YZ__*((23));E$ - Legal
// Test Number 8: P_,_,_,_,_,_,_:V;BI(00<00)@&E$ - Legal
// Test Number 9: PBW((666+6<0)LTE$ - Error at Position 10. Testing for an error. Worked out. 
// Test Number 10: PBR__;O__;E$ - Legal
//---------------------------------------------------------

public class Recognizer
{
  static String inputString;	// User Input String
  static int index = 0;		// Position
  static int errorflag = 0;	// Error Flag Variable to check for logical //error in grammar

  private char token()
  { return(inputString.charAt(index)); }

  private void advancePtr()
  { if (index < (inputString.length()-1)) index++; }

  private void match(char T)
  { if (T == token()) advancePtr(); else error(); }

  private void error()
  {
    System.out.println("error at position: " + index);
    errorflag = 1;
    System.exit(0);	// Was Going Forever. Was Not Fun. At All. 
   // advancePtr();
  }

//----------------------

//checks for program syntax
// Program ::= P {declare} B {Statemt} E $

  private void program()
  {
	// P
	 match('P');
	 // {Declare} B
  	while((token() != 'B'))	
  	declare();
  	match('B');
// {Statemt} E $
  	while((token() != 'E'))
  	statemt(); 
  	match('E');
  }
  
// Delcare::= Ident { , Ident} : V ;
  private void declare()
  {
	// Ident
  	ident();

	// { , Ident}
	while ((token() == ',')){	
		match(',');
		ident();
	}

	// : V ;
	match(':');
	match('V');
	match(';');
	
  }
  
  //checks for each type of statemt using the first token of each of the   //branches
// Statemt ::= assnmt | ifstmt | loop | read | output
  private void statemt()
  {
// First Token of the ifstmt is I
  if ((token() == 'I'))
 		ifstmt(); 
// First Token of loop is W
  else if((token() == 'W'))
  		 loop();
// First Token of Read is R
  else if((token() == 'R'))
		read();
// First token of Output is O
  else if((token() == 'O'))
	output();
// Assnmt doesn’t have a first token but goes to ident being the last option
  else assnmt();

  }
  // assnmt ::= ident ~ exprsn ;
  private void assnmt()
  {
  	ident();
  	match('~');
  	exprsn();
  	match(';');
  }
  
// ifstmt ::= I Comprsn @ {statmt} [ % {statemt}] &
  private void ifstmt()
  {
	match('I');
	comprsn();
	match('@');
	while((token() != '&')
			&& token() != '%'){
		statemt();
	}
	if((token() == '%')){
	match('%');
	}						
	while((token() != '&')){
		statemt();
		}
	match('&');	
	
  }

  // Loop ::= W Comprsn L {statmt} T
  private void loop()
  {
	// W Comprsn L
	match('W');
	comprsn();
	match('L');
	// one or more {statmt} until T
	while(token() != 'T')	
		statemt();
	match('T');
	
  }
  
  //checks for output syntax
// read ::= R Ident { , ident} ;
  private void read()
  {
	// R Ident
	match('R');
	ident();
	// Loop one or more after each ,
	while ((token() == ',')){	//checks for , ident
		match(',');
		ident();
	}
	// ;
	match(';');
		  
  }

  //checks for output syntax
// output ::= O ident {, ident} ;
  private void output()
  {
	// O Ident
	match('O');
	ident();
	// one more more {ident} until ;
	while ((token() == ',')){ //checks for: , ident
		match(',');
		ident();
	}
	match(';');
		  
  }
	
	//checks for comprsn syntax: ( oprnd oprtr oprnd )
// Comprsn ::= ( oprnd opratr oprnd )
  private void comprsn()
  {
	// Straight forward grammar for grammar here
    match('(');
    oprnd();
    opratr();
    oprnd();
    match(')');
  }
  
  // exprsn ::= factor { + factor }
  private void exprsn()
  {	
	// Factor
	  factor();
	// Add another factor with + 
	  while((token() == '+'))
	  {
	  match('+');
	  factor();
	  }
  }
  
  //checks for 1 or more oprnds separated by a *
// factor ::= oprnd { * oprnd }
  private void factor()
  {
	// Oprnd();
	  oprnd();
	// while * is matched call oprnd
	  while((token() == '*'))
	  {
	  match('*');
	  oprnd();
	  }
  }
  
  
  // oprnd ::= integer | ident | (exprsn)
  private void oprnd()
// Since Ident starts with a letter value we check for this first
  { if ((token() == '_')
     || (token() == 'X')
     || (token() == 'Y')
     || (token() == 'Z')) ident(); 
// If the value 
  	 else if((token() == '(')){
	 	match('(');
  		 exprsn();
  		 match(')');
  	 }
  	 else integer(); }
  
  
// operatr ::= < | = | > | !
  private void opratr()
  { if ((token() == '<')
     || (token() == '>')
     || (token() == '=')
     || (token() == '!')) match(token()); else error(); }
	  
	  
  //ident ::= letter | digit

  private void ident()
  { 
	  letter();								
	  while ((token() == '_')
        || (token() == 'X')
        || (token() == 'Y')
        || (token() == 'Z')
		  || (token() == '0')
		  || (token() == '1')
		  || (token() == '2')
		  || (token() == '3')
		  || (token() == '4')
		  || (token() == '5')
		  || (token() == '6')
		  || (token() == '7')
		  || (token() == '8')
		  || (token() == '9'))
		  cha();
  }
  
// char := letter | digit // if it isn't one of the letters, it is a digit. makes things easy
  private void cha()
  { if ((token() == '_')
     || (token() == 'X')
     || (token() == 'Y')
     || (token() == 'Z')) letter(); else digit(); }
  
// Integer ::= Digit {Digit} // 1 or more Digits as long as user input is a digit 
  private void integer()
  { do{
	  digit();
  }
	  while ((token() == '0')
        || (token() == '1')
        || (token() == '2')
        || (token() == '3')
        || (token() == '4')
        || (token() == '5')
        || (token() == '6')
        || (token() == '7')
        || (token() == '8')
        || (token() == '9'));
  }
   
// Digit:: = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
  private void digit()
  { if ((token() == '0') 
		  || (token() == '1')
		  || (token() == '2')
		  || (token() == '3')
		  || (token() == '4')
		  || (token() == '5')
		  || (token() == '6')
		  || (token() == '7')
		  || (token() == '8')
		  || (token() == '9')) match(token()); else error(); }

// Letter ::= _ | X | Y | Z
  private void letter()
  { if ((token() == '_') 
		|| (token() == 'X') 
		|| (token() == 'Y')
		|| (token() == 'Z')) match(token()); else error(); }

//----------------------
  private void start()
  {
    program();	// Start with program until we reach the end. 
    match('$');

    if (errorflag == 0)
      System.out.println("Input is legal." + "\n");
    else
      System.out.println("errors found." + "\n");
      System.exit(0);
  }
//----------------------
  public static void main (String[] args) throws IOException
  {
    Recognizer rec = new Recognizer();	// Recursive Recognizer initialization

    BufferedReader input = new BufferedReader	// Read the User Input
      (new InputStreamReader(System.in));

    System.out.print("\n" + "enter an expression: ");	// Ask for the user expression
    inputString = input.readLine();

    rec.start();	// Initialize recognizer
  }
}

