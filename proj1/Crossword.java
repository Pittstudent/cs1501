import java.lang.*;
import java.io.*;
import java.util.*;

public class Crossword{

	private static char[][] board;
	private static int boardLength = 0;
	private static int solutionCount = 0;
	private static long startTime = System.currentTimeMillis();


	public static void main(String[] args) throws IOException {

		File file = new File("dict8.txt");
		Scanner input = new Scanner(file);
		String dictType = args[0];
		DictInterface D;

		if(!file.exists()){
		    System.out.print("Dictionary File cannot found!");
	        System.exit(0);
	    }
	   
		if (dictType.equals("DLB")){
			D = new DLB();
		}else{
			D = new MyDictionary();
		}

		while(input.hasNext()){
			String str = input.nextLine();
			D.add(str);
		}
	    input.close();

		readTest();
		if(solveWord(0, 0, D)){

		}else{
			if(dictType.equals("DLB")){
				System.out.println("There are "+ solutionCount + " solutions...");
			}
			if(solutionCount == 0){
				System.out.println("... No solution ...");
				long endTime = System.currentTimeMillis();
				int t = (int)(endTime - startTime) / 1000;
				int minute = t / 60;
				int second = t % 60;
				System.out.println("It takes " + minute + " minutes " + second + " seconds to try..." );
			}
		}
	}


	public static void readTest(){
		try{
			Scanner keyboard = new Scanner(System.in);
			System.out.print("Please enter test file's name: ");
			String filename = keyboard.nextLine();
			File file = new File(filename);
			Scanner input = new Scanner(file);
			if(!file.exists()){
		        System.out.print("Test File cannot found!");
	            System.exit(0);
	        }

	        String firstLine = input.nextLine();
	        boardLength = Integer.parseInt(firstLine);
	        board = new char [boardLength][boardLength];

	        int i = 0;
	        while(input.hasNextLine()){
	        	String str = input.nextLine();
	        	char[] charArray = str.toCharArray();
	        	for(int j = 0; j < boardLength; j++){
		      		board[i][j] = charArray[j];
		    	}
		    	i++;
	        }
	        input.close();
	    
		}catch(Exception e){
			System.out.println(e);
		}
	}

		


	private static boolean solveWord(int r, int c, DictInterface D){
		boolean next;

		if(r == boardLength-1 && c == boardLength){
			return true;
		}
		if(c == boardLength){
			r = r + 1; 
			c = 0; 
		}
		
		if(isBlock(board[r][c])) {
			if(c == 0 && r == 0){
				return solveWord(r, c + 1, D);
			}else if(r == 0){
				if(!isBlock(board[r][c-1])){
					if(rowCheck(r, c, 'a', "block", D)){ 
						return solveWord(r, c + 1, D); 
					}
					return false;
				}else{
					return solveWord(r, c + 1, D);
				}
			}else if(c == 0){
				if(!isBlock(board[r-1][c])){
					if(colCheck(r, c, 'a', "block", D)){ 
						return solveWord(r, c + 1, D); 
					}
					return false;
				}else{
					return solveWord(r, c + 1, D);
				}
			}else if(r == boardLength-1 && c == boardLength-1 ){

				if(isBlock(board[r-1][c]) && isBlock(board[r][c-1])){
					return finalStep(D);
				}else if(isBlock(board[r-1][c])){
					if(colCheck(r, c, 'a', "block", D)){ 
						return finalStep(D);
					} 
					return false;
				}else if(isBlock(board[r][c-1])){
					if(rowCheck(r, c, 'a', "block", D)){ 
						return finalStep(D); 
					}
					return false; 
				}else{
					if(rowCheck(r, c, 'a', "block", D) && colCheck(r, c, 'a', "block", D)){
						return finalStep(D);
					}
					return false;
				}

			}else{		
				if(isBlock(board[r-1][c]) && isBlock(board[r][c-1])){
					return solveWord(r, c + 1, D); 
				}else if(isBlock(board[r][c-1])){
					if(colCheck(r, c, 'a', "block", D)){ 
						return solveWord(r, c + 1, D); 
					} 
					return false;
				}else if(isBlock(board[r-1][c])){
					if(rowCheck(r, c, 'a', "block", D)){ 
						return solveWord(r, c + 1, D); 
					}
					return false; 
				}else{
					if(rowCheck(r, c, 'a', "block", D) && colCheck(r, c, 'a', "block", D)){ 
						return solveWord(r, c + 1, D); 
					}
					return false; 
				}
			} 
			//ckeck previous as word 
		}else if(isPlusSign(r, c)){
			for (char alphabet = 'a'; alphabet <= 'z'; alphabet++) { 
				if(rowCheck(r, c, alphabet, "plus", D) && colCheck(r, c, alphabet, "plus", D)){ 
					board[r][c] = alphabet;
					if(r == boardLength-1 && c == boardLength-1){
						if(solutionCount == 0){
							long endTime = System.currentTimeMillis();
							int t = (int)(endTime - startTime) / 1000;
							int minute = t / 60;
							int second = t % 60;
							System.out.println("It takes " + minute + " minutes and " + second + " seconds to find the first solution..." );
						}

						if(D.getClass().getName() == "DLB"){
							if((solutionCount % 10000) == 0){
								System.out.println("The puzzle is solved...");
								printBoard();
							}
							solutionCount++;
							board[r][c] = '+';	
						}else{
							System.out.println("The puzzle is solved..."); 
							printBoard();
							return true;
						}					
					}else{
						next = solveWord(r, c + 1, D); 
						if(next){ 
							return true; 
						}
					}
				} 
			}
			board[r][c] = '+';
			return false;
		}else{
			if(r == boardLength-1 && c == boardLength-1 ){
				if(rowCheck(r, c, 'a', "word", D) && colCheck(r, c, 'a', "word", D)){
					return finalStep(D);
				}
			}else{
				if(rowCheck(r, c, 'a', "word", D) && colCheck(r, c, 'a', "word", D)){ 
					return solveWord(r, c + 1, D); 
				} 
			}
			return false;
		}
	}


	private static boolean finalStep(DictInterface D){
		if(solutionCount == 0){
			long endTime = System.currentTimeMillis();
			int t = (int)(endTime - startTime) / 1000;
			int minute = t / 60;
			int second = t % 60;
			System.out.println("It takes " + minute + " minutes and " + second + " seconds to find the first solution..." );
		}
							
		if(D.getClass().getName() == "DLB"){
			if((solutionCount % 10000) == 0){
				System.out.println("The puzzle is solved..."); 
				printBoard();
			}
			solutionCount++;
			return false;
		}else{
			System.out.println("The puzzle is solved..."); 
			printBoard();
			return true;
		}
	}


	private static boolean isPlusSign(int r, int c){
		if ('+' == board[r][c]) {
			return true;
		}
		return false;
	}


	private static boolean rowCheck(int r, int c, char letter, String state, DictInterface D){
		StringBuilder sb = new StringBuilder();
		if(c == boardLength-1){
			sb = rowSelector(r, c, letter, state);
			return isWord(sb, D);
		}
		sb = rowSelector(r, c, letter, state);

		if(!state.equals("block")){
			return isPrefix(sb, D);
		}else{
			return isWord(sb, D);
		}
	}

	private static StringBuilder rowSelector(int r, int c, char letter, String state){
		StringBuilder sb = new StringBuilder();
		if(state.equals("plus")){
			for(int i = 0; i < c; i++){
				if(!isBlock(board[r][i])){
					sb.append(board[r][i]);
				}else{
					sb.setLength(0);
				}
			}
			sb.append(letter);
			return sb;
		}else if(state.equals("word")){
			for(int i = 0; i <= c; i++){
				if(!isBlock(board[r][i])){
					sb.append(board[r][i]);
				}else{
					sb.setLength(0);
				}
			}
			return sb;
		}else{
			for(int i = 0; i < c; i++){
				if(!isBlock(board[r][i])){
					sb.append(board[r][i]);
				}else{
					sb.setLength(0);
				}
			}
			return sb;
		}
	}


	private static boolean colCheck(int r, int c, char letter, String state, DictInterface D){
		StringBuilder sb = new StringBuilder();
		if(r == boardLength-1){
			sb = colSelector(r, c, letter, state);
			return isWord(sb, D);
		}

		sb = colSelector(r, c, letter, state);
		if(!state.equals("block")){
			return isPrefix(sb, D);
		}else{
			return isWord(sb, D);
		}
		
	}

	private static StringBuilder colSelector(int r, int c, char letter, String state){
		StringBuilder sb = new StringBuilder();
		if(state.equals("plus")){
			for(int i = 0; i < r; i++){
				if(!isBlock(board[i][c])){
					sb.append(board[i][c]);
				}else{
					sb.setLength(0);
				}
			}
			sb.append(letter);
			return sb;
		}else if(state.equals("word")){
			for(int i = 0; i <= r; i++){
				if(!isBlock(board[i][c])){
					sb.append(board[i][c]);
				}else{
					sb.setLength(0);
				}
			}
			return sb;
		}else{
			for(int i = 0; i < r; i++){
				if(!isBlock(board[i][c])){
					sb.append(board[i][c]);
				}else{
					sb.setLength(0);
				}
			}
			return sb;
		}
	}

	private static boolean isBlock(char point){
		if ('-' == point) {
			return true;
		}
		return false;
	}

	private static boolean isPrefix(StringBuilder sb, DictInterface D){
		int ans = D.searchPrefix(sb);
		boolean result = false;
		switch (ans){
			case 0: result = false;
				break;
			case 1: result = true;
				break;
			case 2: result = true;
				break;
			case 3: result = true;
				break;	
		}
		return result;
	}

	private static boolean isWord(StringBuilder sb, DictInterface D){
		int ans = D.searchPrefix(sb);
		boolean result = false;
		switch (ans){
			case 0: result =  false;
				break;
			case 1: result =  false;
				break;
			case 2: result =  true;
				break;
			case 3: result =  true;
				break;
		}
		return result;
	}
	
	private static void printBoard(){
		for (int i = 0; i < boardLength; i++) {
			for (int j = 0; j < boardLength; j++) {
				System.out.print("" + board[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}



}
	