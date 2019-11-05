import java.util.*;

public class PHPArray<V> implements Iterable<V>{
    private static final int INIT_CAPACITY = 4;

    private int N;           // number of key-value Nodes in the symbol table
    private int M;           // size of linear probing table
    private Node<V> root; 
    private Node<V> end;
    private Node<V>[] table;
    private Node<V> node = root;
    private int eachCounter = 0;

    public static class Pair<V> {
        public final String key;
        public final V value;

        public Pair(String key, V value) {
            this.key = key;
            this.value = value;
        }
    }
        

    private static class Node<V> {
    	private Node<V> previous;
    	private Node<V> next;
        private String key;                         
        private V val; 

        private Node() {}                  
    }


    // create an empty hash table - use 16 as default size
    public PHPArray() {
        this(INIT_CAPACITY);
        table = (Node<V>[]) new Node[16];
    }


    // create linear proving hash table of given capacity
    public PHPArray(int capacity) {
        M = capacity;
        table = (Node<V>[]) new Node[M];
    }

    // return the number of key-value Nodes in the symbol table
    public int size() {
        return N;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // does a key-value Node with the given key exist in the symbol table?
    public boolean contains(String key) {
        return get(key) != null;
    }

    // hash function for keys - returns value between 0 and M-1
    private int hash(String key) {
        return (key.hashCode() & 0x7fffffff) % M;
    }


    // resize the hash table to the given capacity by re-hashing all of the keys
    private void resize(int capacity) {
        System.out.println("Number of keys: " + N + " -- resizing array from "+ M +" to " + capacity);
        PHPArray<V> tempArr = new PHPArray<V>(capacity);
        Node<V> tempNode = root;
        while (tempNode != null) {
            tempArr.put(tempNode.key, tempNode.val);
            tempNode = tempNode.next;
        }
        table = tempArr.table;
        M = tempArr.M;
        root = tempArr.root;
        end = tempArr.end;
    }


    public void put(String key, V val) {
        if (val == null) unset(key);

        // double table size if 50% full
        if (N >= M/2) {
        	resize(2*M);
        }
        //System.out.println("1 adding key " + key + " with value " + val);
        int i; 
        for (i = hash(key); table[i] != null; i = (i + 1) % M) {
            String s = table[i].key;
            if (s.equals(key)) {
                table[i].val = val; 
                setNodeVal(key, val);
                return; 
            }
        }
        

        if(root == null){
        	root = put(root, key, val);
        	end = root;
        }else{
        	end.next = put(end, key, val);
            end = end.next;
            Node<V> temp = end.previous;
        }

        table[i] = end;
        N++;
    }

    private void setNodeVal(String key, V val) {
        Node<V> tempNode = root;
        while (tempNode != null) {
            if(key.equals(tempNode.key)){
                tempNode.val = val;
                return;
            }
            tempNode = tempNode.next;
        }
    }

    public void put(int k, V val) {
        String key = String.valueOf(k);
        if (val == null) unset(key);

        // double table size if 50% full
        if (N >= M/2) {
            resize(2*M);
        }

        int i = hash(key); 
        while (table[i] != null) {
            Node<V> temp = table[i];
            String s = temp.key;
            if (s.equals(key)) { 
                temp.val = val; 
                setNodeVal(key, val);
                return; 
            }
            i = (i + 1) % M;
        }

        if(root == null){
            root = put(root, key, val);
            end = root;
        }else{
            end.next = put(end, key, val);
            end = end.next;
            Node<V> temp = end.previous;
        }

        table[i] = end;
        N++;
    }

    private Node<V> put(Node<V> head, String s, V val) {
        Node<V> temp = new Node<V>();
        temp.previous = head;
        temp.key = s;
        temp.val = val;
        return temp;
    }


    public V get(String key) {
        if (key == null) throw new NullPointerException();

        Node<V> temp = get(root, key);
        if (temp == null){
        	return null;
        }
        return temp.val;
    }

    public V get(int key) {
        String k = String.valueOf(key);

        Node<V> temp = get(root, k);
        if (temp == null){
            return null;
        }
        
        return temp.val;
    }
 
    private Node<V> get(Node<V> n, String k) {
        if (n == null){
        	return null;
        }

        if(k.equals(n.key)){
        	return n;
        }else{
        	return get(n.next, k);
        }
    }


	public Pair<V> each(){
        ArrayList<String> keyArr = keys();
        ArrayList<V> vArr = valueArr();
        int numVal = size();
        if(eachCounter < numVal){
            Pair<V> p = new Pair(keyArr.get(eachCounter), vArr.get(eachCounter));
            eachCounter++;
            return p;
        }
        return null;
    }


	//The reset() method will re-initialize the iteration such that each()
	// will again go through the (key, value) Nodes of the PHPArray
    public void reset(){
    	eachCounter = 0;
    }

    private ArrayList<V> valueArr(){
        ArrayList<V> array = new ArrayList<>();
        Node<V> temp = root;
        while (temp != null) {
            array.add(temp.val);
            temp = temp.next;
        }
        return array;
    }


    public ArrayList<String> keys(){
    	ArrayList<String> array = new ArrayList<String>();
    	Node<V> temp = root;
        while (temp != null) {
            array.add(String.valueOf(temp.key));
            temp = temp.next;
        }
	    return array;
    }


	public ArrayList<Integer> values(){
        ArrayList<Integer> array = new ArrayList<>();
    	Node<V> temp = root;
        while (temp != null) {
            array.add((int)(temp.val));
            temp = temp.next;
        }
	    return array;
    }
 

	public void showTable(){
    	for (int i = 0; i < M; i++) {
    		System.out.print(i + ": ");
            if (table[i] != null) {
                System.out.println("Key: " + table[i].key + ", Value: " + table[i].val);
            }else{
            	System.out.println("null ");
            }
        }
    }

   
    public int length(){
    	return N;
    }


    public void unset(String key){
        unsetArr(key);
    }

    public void unset(int key){
        String k = String.valueOf(key);
        unsetArr(k);
    }


    private void unsetArr(String key){
        	if (!contains(key)) return;

            // find position i of key
            int i = hash(key);
            while (!key.equals(table[i].key)) {
                i = (i + 1) % M;
            }

            if(key.equals(root.key)){
                root = root.next;
                table[i] = null;

            }else if(key.equals(end.key)){
                end = end.previous;
                table[i] = null;
            }else{  
                Node<V> tempNode = root;
                while (tempNode != null) {
                    if(key.equals(tempNode.key)){
                        tempNode.previous.next = tempNode.next;
                        tempNode.next.previous = tempNode.previous;
                    }
                    tempNode = tempNode.next;
                }
                table[i] = null;
            }

            // rehash all keys in same cluster
            i = (i + 1) % M;
            while (table[i] != null) {
                Node<V> pt = table[i];
                System.out.println("\t key rehashs during the delete: " + pt.key );
                table[i] = null;
                N--;  
                putNode(pt);
                i = (i + 1) % M;
            }

            N--;        
            // halves size of array if it's 12.5% full or less
            if (N > 0 && N <= M/8) resize(M/2);
    }

    private void putNode(Node<V> pt) {
        int i;
        String key = pt.key;
        for (i = hash(key); table[i] != null; i = (i + 1) % M) {
            
        }

        table[i] = pt;
        N++;
    }
    

    public void sort(){
        int size = N;  
        try{
            Comparable vl = (Comparable) root.val;
        }catch(Exception e){
            System.out.println("Data is not Comparable. Exiting sort() now...");
            return;
        }
        root = mergeSort(root, size);
        renewKey();
    }

    private Node<V> mergeSort(Node<V> list, int size){
        if (size > 1){
            int mid = size/2;       // cut list in half
            Node<V> leftList = list;   // Start left half at beginning
            Node<V> rightList = null;
            Node<V> temp = list;
            for (int i = 0; i < mid-1; i++) // Traverse to starting point of 
                temp = temp.next;           // right half.  This is some extra
                                            // overhead not needed for the array.
            rightList = temp.next;      // Assign and disconnect right half
            temp.next = null;
            
            // make recursive calls on sublists
            leftList = mergeSort(leftList, mid);
            rightList = mergeSort(rightList, size - mid);
            
            // Now we merge the lists back together. Basically this code
            // is moving nodes out of the subprogram return lists into the single
            // sorted list.  
            temp = null;
            Comparable leftFront = null;
            Comparable rightFront = null;
            boolean done = false;
            // if either list is null we do not have to merge anything
            if (leftList == null || rightList == null){
                done = true;
            }else{    // Both lists have at least one item.  Get front of each one.
                    leftFront = (Comparable)leftList.val;
                    rightFront = (Comparable)rightList.val;
            }
            Node<V> curr = null;
            list = null;    // This will be the list we return
            while (!done){
                if (leftList == null || rightList == null){
                    done = true;
                }else{
                    // Should next item come from left or right list?
                    int cmp = leftFront.compareTo(rightFront);
                    if (cmp <= 0){
                        curr = leftList;
                        leftList = leftList.next;
                        curr.next = null;
                        if (leftList != null)
                            leftFront = (Comparable) leftList.val;
                    }else{
                        curr = rightList;
                        rightList = rightList.next;
                        curr.next = null;
                        if (rightList != null)
                            rightFront = (Comparable) rightList.val;
                    }
                    
                    if (list == null){   // special case for first node
                        list = curr;
                        temp = list;
                    }else{
                        temp.next = curr;   // general case -- connect the curr
                        curr.previous = temp;
                        temp = curr;        // node to the list
                    }
                }
            }
            
            if (leftList != null){       // After loop one of the sides still has
                                       // at least one node in it.  Append those
                if (list == null){       // nodes to the end of the list.
                    list = leftList;
                }else{
                    temp.next = leftList;
                    leftList.previous = temp;
                }
            }
            else if (rightList != null){
            
                if (list == null){
                    list = rightList;
                }else{
                    temp.next = rightList;
                    rightList.previous = temp;
                }
            }   
           // System.out.println("return " + list.val + " in counter " + counter);
            return list;
        }else{
           // System.out.println("return " + list.val + " in counter " + counter);
            return list;
        }
    }
        
    private void renewKey() {
        Node<V> tempNode = root;
        int counter = 0;
        while (tempNode != null) {
            tempNode.key = String.valueOf(counter);
            counter++;
            tempNode = tempNode.next;
         }
    }
    

    public void asort(){
        int size = N;  
        root = mergeSort(root, size);
    }


    public PHPArray<String> array_flip(){
        PHPArray<String> tempArr = new PHPArray<String>();
        Node<V> tempNode = root;

        if(!tempNode.val.getClass().equals(String.class)){
            throw new ClassCastException( "Cannot convert class java.lang.Integer to String ");
        }

        while (tempNode != null) {
            tempArr.put((String)tempNode.val, tempNode.key);
            tempNode = tempNode.next;
        } 
        return tempArr;
    }



    public Iterator<V> iterator(){
        return new MyArrayIterator();
    }

    private class MyArrayIterator implements Iterator<V>{
        private Node<V> currentNode;
        
        public MyArrayIterator(){
            currentNode = root;
        }
        
        public boolean hasNext(){
            if(currentNode != null){
                return true;
            }
            return false;
        }
        
        public V next(){
            if (hasNext()){
                //System.out.println("current Node is " + currentNode.key);
                V item = currentNode.val;
                currentNode = currentNode.next; 
                return item;
            }else{
                throw new NoSuchElementException("Iterating past end of list");
            }
        }
        
        public void remove(){
            throw new UnsupportedOperationException("remove() not implemented");
        }
    }
    

}