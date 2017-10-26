
public class Translator {

    /** start of C program */
    public void program_start() {
	System.out.println("#include <stdio.h>");
	System.out.print("int main() ");
    }

    /** end program */
    public void program_end() {
	System.out.println("}");
    }

    /** start a new block of code */
    public void block_start() {
	System.out.println("{");
    }

    /** end of current block of code */
    public void block_end() {
	System.out.println("}");
    }

    /** declare new variable according to scope */
    public void declare(String var, int scope) {
	System.out.print("int ");
	
	System.out.print("x_" + scope);
	System.out.println(var + ";");
    }

    /** begin C print statement */
    public void print() {
	System.out.print("printf(\"%d\\n\", ");
    }

    /** end C printf statement */
    public void end_print() {
	System.out.println(");");
    }

    /** C assignment expression */
    public void assignment() {
	System.out.print(" = ");
    }

    /** end C assignment statement */
    public void end_assignment() {
	System.out.println(";");
    }

    /** print out variable */
    public void variable(String var) {
	System.out.print(var);
    }

    /** prepend E variables according to scope */
    public String ref_id(String search_expression, String var, int table_size, int scope) {
	if (search_expression.length() == 1) {
	    System.out.print("x_1" + var);
	    return "x_1" + var;
	}
	else if (search_expression.length() > 1) {
	    int scope_level = Integer.parseInt(search_expression.substring(1,search_expression.length()));
	    System.out.print("x_" + (table_size - scope_level) + var);
	    return "x_" + (table_size - scope_level) + var;
	}
	else if (search_expression.length() == 0) {
	    System.out.print("x_" + scope + var);
	    return "x_" + scope + var;
	}
	else {
	    System.err.println("Error in ref_id");
	    return "";
	}
    }

    /** translate do statement to while loop */
    public void do_statement() {
	System.out.print("while (");
    }

    /** begin for loop expression */
    public void for_loop() {
	System.out.print("for (");
    }

    /** terminating character for C expressions */
    public void semi_colon() {
	System.out.print("; ");
    }

    /** controls how long the for loop iterates */
    public void for_control_statement(String var) {
	System.out.print(" " + var);
	end_do_conditional();
	semi_colon();
    }

    /** default conditional statement from E */
    public void end_do_conditional() {
	System.out.print(" <= 0");
    }

    /** print out if statement */
    public void if_statement() {
	System.out.print("if (");
    }

    /** print out else if statement */
    public void else_if_statement() {
	System.out.print("else if (");
    }

    /** print out else statement */ 
    public void else_statement() {
	System.out.print("else ");
    }

    /** end of control statement for while or for loops */
    public void end_control_statement() {
	System.out.print(")");
    }

    /** end current C expression */
    public void end_expr() {
	System.out.println(";");
    }

    /** print out left parentheses */
    public void l_paren() {
	System.out.print("(");
    }

    /** print out right parentheses */
    public void r_paren() {
	System.out.print(")");
    }

    /** print out number */
    public void number(String n) {
	System.out.print(n);
    }

    /** print out addition or subtraction */
    public void addop(String op) {
	System.out.print(" " + op + " ");
    }

    /** print out multiplication or division */
    public void multop(String op) {
	System.out.print(" " + op + " ");
    }
}
