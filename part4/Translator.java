
public class Translator {

    public void program_start() {
	System.out.println("#include <stdio.h>");
	System.out.print("int main() ");
    }

    public void program_end() {
	System.out.println("}");
    }
    
    public void block_start() {
	System.out.println("{");
    }

    public void block_end() {
	System.out.println("}");
    }

    public void declare(String var, int scope) {
	System.out.print("int ");
	
	System.out.print("x_" + scope);
	System.out.println(var + ";");
    }

    public void print() {
	System.out.print("printf(\"%d\\n\", ");
    }

    public void end_print() {
	System.out.println(");");
    }
    
    public void assignment() {
	System.out.print(" = ");
    }

    public void end_assignment() {
	System.out.println(";");
    }
    
    public void ref_id(String search_expression, String var, int table_size, int scope) {
	if (search_expression.length() == 1) {
	    System.out.print("x_1" + var);
	}
	else if (search_expression.length() > 1) {
	    int scope_level = Integer.parseInt(search_expression.substring(1,search_expression.length()));
	    System.out.print("x_" + (table_size - scope_level) + var);
	}
	else if (search_expression.length() == 0) {
	    System.out.print("x_" + scope + var);
	}
	else
	    System.err.println("Error in ref_id");
    }

    public void do_statement() {
	System.out.print("while (");
    }

    public void end_do_conditional() {
	System.out.print(" <= 0");
    }

    public void if_statement() {
	System.out.print("if (");
    }

    public void else_if_statement() {
	System.out.print("else if (");
    }

    public void else_statement() {
	System.out.print("else ");
    }
    
    public void end_control_statement() {
	System.out.print(")");
    }
    
    public void end_expr() {
	System.out.println(";");
    }

    public void l_paren() {
	System.out.print("(");
    }

    public void r_paren() {
	System.out.print(")");
    }
    
    public void number(String n) {
	System.out.print(n);
    }

    public void addop(String op) {
	System.out.print(" " + op + " ");
    }

    public void multop(String op) {
	System.out.print(" " + op + " ");
    }
}
