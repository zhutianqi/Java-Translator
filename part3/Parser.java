/* *** This file is given as part of the programming assignment. *** */

import java.util.LinkedList;

public class Parser {

    // tok is global to all these parsing methods;
    // scan just calls the scanner's scan method and saves the result in tok.
    private Token tok; // the current token
    private SymbolTable<String> table;
    
    private void scan() {
	tok = scanner.scan();
    }

    private Scan scanner;
    Parser(Scan scanner) {
	this.scanner = scanner;
	table = new SymbolTable<String>();
	scan();
	program();
	if( tok.kind != TK.EOF )
	    parse_error("junk after logical end of program");
    }

    private void program() {
	block();
    }

    private void block() {
	LinkedList<String> scope = new LinkedList<String>();
	table.push_list(scope);
	
	declaration_list();
	statement_list();
	table.pop_list();
    }

    private void declaration_list() {
	// below checks whether tok is in first set of declaration.
	// here, that's easy since there's only one token kind in the set.
	// in other places, though, there might be more.
	// so, you might want to write a general function to handle that.
	while( is(TK.DECLARE) ) {
	    declaration();
	}
    }

    private void declaration() {
	mustbe(TK.DECLARE);

	// check for redeclarations
	if (table.search_index(0, tok.string))
	    System.err.println("redeclaration of variable " + tok.string);
	else 
	    table.push(tok.string);
	
	mustbe(TK.ID);
	while( is(TK.COMMA) ) {
	    scan();
	    if (table.search_index(0, tok.string))
		System.err.println("redeclaration of variable " + tok.string);
	    else 
		table.push(tok.string);
	    mustbe(TK.ID);
	}
    }

    private void statement_list() {
	while ( is(TK.TILDE) || is(TK.PRINT) || is(TK.ID)
		|| is(TK.DO) || is(TK.IF) ) {
	    if (is(TK.PRINT))
		print();
	    else if (is(TK.TILDE) || is(TK.ID))
		assignment();
	    else if (is(TK.DO))
		do_statement();
	    else if (is(TK.IF))
		if_statement();	
	}
    }

    private void print() {
	mustbe(TK.PRINT);
	expr();
    }

    private void assignment() {
	ref_id();
	mustbe(TK.ASSIGN);
	expr();
    }

    private void ref_id() {

	Boolean found = false;
	String search_expression = "";
	
	if ( is(TK.TILDE) ) {
	    search_expression += tok.string;
	    mustbe(TK.TILDE);
	    
	    if ( is(TK.NUM) ) {
		search_expression += tok.string;
		mustbe(TK.NUM);
	    }

	    // do global search
	    if (search_expression.length() == 1) {
		if (table.search_index(table.size()-1, tok.string))
		    found = true;
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
		else if (table.search_index(scope_level, tok.string))
		    found = true;
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
		if (table.search_index(i, tok.string))
		    found = true;
	    }
	}
	
	if (!found) {
	    System.err.println(tok.string + " is an undeclared variable on line " + tok.lineNumber);
	    System.exit(1);
	}
	mustbe(TK.ID);
    }
    
    private void do_statement() {
	mustbe(TK.DO);
	guarded_command();
	mustbe(TK.ENDDO);
    }

    private void if_statement() {
	mustbe(TK.IF);
	guarded_command();
	while ( is(TK.ELSEIF) ) {
	    scan();
	    guarded_command();
	}
	if ( is(TK.ELSE) ) {
	    scan();
	    block();
	}
	mustbe(TK.ENDIF);
    }

    private void guarded_command() {
	expr();
	mustbe(TK.THEN);
	block();
    }

    private void expr() {
	term();
	while ( is(TK.PLUS) || is(TK.MINUS) ) {
	    addop();
	    term();
	}
    }

    private void term() {
	factor();
	while ( is(TK.TIMES) || is(TK.DIVIDE) ) {
	    multop();
	    factor();
	}
    }

    private void factor() {
	if ( is(TK.LPAREN) ) {
	    mustbe(TK.LPAREN);
	    expr();
	    mustbe(TK.RPAREN);
	}
	else if ( is(TK.TILDE) || is(TK.ID) )
	    ref_id();
	else if ( is(TK.NUM) ) {
	    mustbe(TK.NUM);
	}
	else {
	    System.err.println("expected expr, ref_id, or number.");
	    System.exit(1);
	}
    }

    private void addop() {
	if ( is(TK.PLUS) || is(TK.MINUS) )
	    scan();
	else {
	    System.err.println("expected '+' or '-'.");
	    System.exit(1);
	}
    }

    private void multop() {
	if ( is(TK.TIMES) || is(TK.DIVIDE) )
	    scan();
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
