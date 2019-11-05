import java.util.*;

public class DLB implements DictInterface{
	
	private DLBnode root;

	private class DLBnode{
		private char val;
		private DLBnode child;
		private DLBnode rightSib;
	}

	public DLB(){
		root = new DLBnode();
		root.child = null;
		root.rightSib = null;
	}
	
	
	public boolean add(String s){
		char[] charArr = s.toCharArray();
		DLBnode pointer = root;

		for (int i = 0; i < charArr.length; i++){
			if(pointer.child != null){
				pointer = pointer.child;
				while(pointer.val != charArr[i]){				
					if (pointer.rightSib != null){
						pointer = pointer.rightSib;
					}else{
						DLBnode newNode = new DLBnode();
						newNode.val = charArr[i];
						pointer.rightSib = newNode;
						pointer = pointer.rightSib;
					}
				}
			}else{
				DLBnode newNode = new DLBnode();
				newNode.val = charArr[i];
				pointer.child = newNode;
				pointer = pointer.child;
			}

			if (i == charArr.length-1){
				if(pointer.child == null){
					DLBnode endNode = new DLBnode();
					endNode.val = '$';
					pointer.child = endNode;
					pointer = pointer.child;
				}else{
					pointer = pointer.child; 
					while(pointer != null){
						pointer = pointer.rightSib;
					}
					DLBnode endNode = new DLBnode();
					endNode.val = '$';
					pointer = endNode;
				}
				
				return true;
			}			
		}
		return false;
	}

	public int searchPrefix(StringBuilder s){
		boolean prefix = false;
		boolean word = false;
		DLBnode pointer = new DLBnode();
		pointer = root.child;
    	
    	for(int i = 0; i < s.length(); i++){
    		char letter = s.charAt(i);
    		if(i == s.length()-1){
	    		if(pointer != null){
		    		while(pointer.val != letter){
		    			if (pointer.rightSib != null){	
		    				pointer = pointer.rightSib;
		    			}else{
		    				return 0;
		    			}			
					}
				    pointer = pointer.child;
				    
					if(pointer != null){
						//System.out.println(" last: " +pointer.val);
						if(pointer.val == '$'){
							if(pointer.rightSib != null){	
								prefix = true;		
							}
							word = true;
						}else{
							while(pointer.val != '$'){
								prefix = true;
								if (pointer.rightSib != null){	
				    				pointer = pointer.rightSib;
				    			}else{
				    				return 1;
				    			}			
							}
							word = true;
						}	
					}else{
						return 1;
					}
	    		}
	    	}else{
		    	if(pointer != null){
			    	while(pointer.val != letter){
			    		if (pointer.rightSib != null){	
			    			pointer = pointer.rightSib;
			    		}else{
			    			return 0;
			    		}			
					}
					pointer = pointer.child;		
		    	}
		    }
	    }
		
		if (prefix && word) return 3;
		else if (word) return 2;
		else if (prefix) return 1;
		else return 0;
	}
	 
	public int searchPrefix(StringBuilder s, int start, int end){
		return 0;
	}

	}