
import java.util.LinkedList;
    
public class SymbolTable<T> {

    private LinkedList<LinkedList<T>> list;

    public SymbolTable() {
	list = new LinkedList<LinkedList<T>>();
    }

    public void insert(int i, T data) {
	list.get(i).addFirst(data);
    }
    
    public void pop() {
	list.get(0).remove(0);
    }

    public void pop_list() {
	list.remove(0);
    }

    public void push(T data) {
	list.get(0).addFirst(data);
    }

    public void push_list(LinkedList<T> new_list) {
	list.addFirst(new_list);
    }

    public Boolean search_index(int i, T data) {
	if (list.get(i).contains(data))
	    return true;
	return false;
    }

    public int size() {
	return list.size();
    }
    
    public static void main(String[] args) {
	
	SymbolTable<String> sl = new SymbolTable<String>();
	
	for (int i = 0; i < 3; i++) {
	    LinkedList<String> l = new LinkedList<String>();
	    l.addFirst("Catch-22");
	    sl.push_list(l);
	}

	if (sl.search_index(1, "Catch-22"))
	    System.out.println("Catch-22 found");
    }
    
}
