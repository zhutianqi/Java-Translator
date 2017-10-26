/* *** This file is given as part of the programming assignment. *** */

import java.util.LinkedList;

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private Boolean from_do_or_if_statement; // determine if in do or if statement
    private Boolean for_initialization; // determine if in for loop initialization
    private Boolean from_for; // determine if in for loop
    private String for_control_var; // keep track of what was the for loop control var
    private SymbolTable<String> table; // symbol table for storing variables
    private Translator translator; // instance of class that handles the translation
    
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	table = new SymbolTable<String>();
	translator = new Translator();
	scan();	
	from_do_or_if_statement = false;
	for_initialization = false;
	from_for = false;
	for_control_var = "";
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    /** begin the program */
    private void program() {
	translator.program_start();
	block();
    }

    /** new block of code, new lexical scope */
    private void block() {
	LinkedList<String> scope = new LinkedList<String>();
	table.push_list(scope);

	translator.block_start();
	declaration_list();
	statement_list();
	table.pop_list();
	translator.block_end();
    }

    /** run through declarations */
    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    /** determine if declaration follows grammar rules, if so translate */
    private void declaration() {
	mustbe(TK.DECLARE);

	// check for redeclarations
	if (table.search_index(0, tok.string))
	    System.err.println("redeclaration of variable " + tok.string);
	else  {
	    table.push(tok.string);
	    translator.declare(tok.string, table.size());
	}
	
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    if (table.search_index(0, tok.string))
		System.err.println("redeclaration of variable " + tok.string);
	    else {
		table.push(tok.string);
		translator.declare(tok.string, table.size());
	    }
	    mustbe(TK.ID);
	}
    }

    /** branching function for all diffent types of statements */
    private void statement_list() {
	while ( is(TK.TILDE) || is(TK.PRINT) || is(TK.ID)
		|| is(TK.DO) || is(TK.FOR) || is(TK.IF) ) {
	    if (is(TK.PRINT))
		print();
	    else if (is(TK.TILDE) || is(TK.ID))
		assignment();
	    else if (is(TK.DO))
		do_statement();
	    else if (is(TK.FOR))
		for_loop();
	    else if (is(TK.IF))
		if_statement();	
	}
    }

    /** check if print statement is grammatically correct, if so translate */
    private void print() {
	mustbe(TK.PRINT);
	translator.print();
	expr();
	translator.end_print();
    }

    /** determine if assignment is grammatically correct, if so translate it */
    private void assignment() {
	ref_id();
	translator.assignment();
	mustbe(TK.ASSIGN);
	expr();
	if (!from_for)
	    translator.end_assignment();
    }

    /**  determine if variable exists and is scoped properly. if so, translate it */
    private void ref_id() {

	Boolean found = false;
	String search_expression = "";
	String id_string;
	
	if ( is(TK.TILDE) ) {
	    search_expression += tok.string;
	    mustbe(TK.TILDE);
	    
	    if ( is(TK.NUM) ) {
		search_expression += tok.string;
		mustbe(TK.NUM);
	    }

	    // do global search
	    if (search_expression.length() == 1) {
		if (table.search_index(table.size()-1, tok.string)) {
		    found = true;
		    id_string = translator.ref_id(search_expression, tok.string, table.size(), 0);
		    if (for_initialization) {
			translator.assignment();
			translator.variable(id_string);
			for_control_var = id_string;
		    }
		}
		else {
		    System.err.println("no such variable " + search_expression + tok.string +
				       " on line " + tok.lineNumber);
		    System.exit(1);
		}
	    }
	    // do specific scope search
	    else if (search_expression.length() > 1) {
		int scope_level = Integer.parseInt(search_expression.substring(1,search_expression.length()));
		if (scope_level > table.size()-1) {
		    System.err.println("no such variable " + search_expression + tok.string +
				       " on line " + tok.lineNumber);
		    System.exit(1);
		}
		else if (table.search_index(scope_level, tok.string)) {
		    found = true;
		    id_string = translator.ref_id(search_expression, tok.string, table.size(), 0);
		    if (for_initialization) {
			translator.assignment();
			translator.variable(id_string);
			for_control_var = id_string;
		    }
		}
	    }
	    if (!found) {
		System.err.println("no such variable " + search_expression + tok.string +
				   " on line " + tok.lineNumber);
		System.exit(1);
	    }
	}

	// variable could be in any block
	if (search_expression == "") {
	    for (int i = 0; i < table.size(); i++) {
		if (table.search_index(i, tok.string)) {
		    found = true;
		    id_string = translator.ref_id(search_expression, tok.string, table.size(), table.size() - i);
		    if (for_initialization) {
			translator.assignment();
			translator.variable(id_string);
			for_control_var = id_string;
		    }
		}
	    }
	}
	
	if (!found) {
	    System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
	    System.exit(1);
	}
	mustbe(TK.ID);
    }

    /** in do statement, determine if grammatically correct. if so, translate. */
    private void do_statement() {
	translator.do_statement();
	mustbe(TK.DO);
	from_do_or_if_statement = true;
	guarded_command();
	mustbe(TK.ENDDO);
    }

    /** in for loop, if grammatically correct, translate */
    private void for_loop() {
	for_control_var = "";
	mustbe(TK.FOR);
	mustbe(TK.DO);
	translator.for_loop();
	for_initialization = true;
	from_for = true;
	expr();
	translator.semi_colon();
	for_initialization = false;
	translator.for_control_statement(for_control_var);
	mustbe(TK.THEN);
	assignment();
	translator.end_control_statement();
	mustbe(TK.THEN);
	from_for = false;
	for_control_var = "";
	block();
	mustbe(TK.ENDDO);
    }

    /** in if statement, if grammatically correct, translate */
    private void if_statement() {
	translator.if_statement();
	mustbe(TK.IF);
	from_do_or_if_statement = true;
	guarded_command();
	while ( is(TK.ELSEIF) ) {
	    scan();
	    translator.else_if_statement();
	    from_do_or_if_statement = true;
	    guarded_command();
	}
	if ( is(TK.ELSE) ) {
	    translator.else_statement();
	    scan();
	    block();
	}
	mustbe(TK.ENDIF);
    }

    /** determine if guarded command is syntactically correct if so translate it */
    private void guarded_command() {
	expr();
	if (from_do_or_if_statement) {
	    translator.end_do_conditional();
	}
	from_do_or_if_statement = false;
	translator.end_control_statement();
	mustbe(TK.THEN);
	block();
    }

    /** term with potential addition or subtraction */
    private void expr() {
	term();
	while ( is(TK.PLUS) || is(TK.MINUS) ) {
	    addop();
	    term();
	}
    }

    /** factor with potential multiplication or division */
    private void term() {
	factor();
	while ( is(TK.TIMES) || is(TK.DIVIDE) ) {
	    multop();
	    factor();
	}
    }

    /** number, id, or another expr with parentheses */
    private void factor() {
	if ( is(TK.LPAREN) ) {
	    translator.l_paren();
	    mustbe(TK.LPAREN);
	    expr();
	    translator.r_paren();
	    mustbe(TK.RPAREN);
	}
	else if ( is(TK.TILDE) || is(TK.ID) )
	    ref_id();
	else if ( is(TK.NUM) ) {
	    translator.number(tok.string);
	    mustbe(TK.NUM);
	}
	else {
	    System.err.println("expected expr, ref_id, or number.");
	    System.exit(1);
	}
    }

    /** addition or subtraction */
    private void addop() {
	if ( is(TK.PLUS) || is(TK.MINUS) ) {
	    translator.addop(tok.string);
	    scan();
	}
	else {
	    System.err.println("expected '+' or '-'.");
	    System.exit(1);
	}
    }

    /** multiplication or division */
    private void multop() {
	if ( is(TK.TIMES) || is(TK.DIVIDE) ) {
	    translator.multop(tok.string);
	    scan();
	}
	else {
	    System.err.println("expected '*' or '/'.");
	    System.exit(1);
	}
    }	    
    
    // is current token what we want?
    private boolean is(TK tk) {
        return tk == tok.kind;
    }

    // ensure current token is tk and skip over it.
    private void mustbe(TK tk) {
	if( tok.kind != tk ) {
	    System.err.println( "mustbe: want " + tk + ", got " +
				    tok);
	    parse_error( "missing token (mustbe)" );
	}
	scan();
    }

    private void parse_error(String msg) {
	System.err.println( "can't parse: line "
			    + tok.lineNumber + " " + msg );
	System.exit(1);
    }
}
