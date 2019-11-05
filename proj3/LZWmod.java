/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/
import java.lang.*;

public class LZWmod {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width begin with 9
    private static boolean reset = false; 

    public static void compress() {
        StringBuilder sb = new StringBuilder();
        TST<Integer> st = new TST<Integer>();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < R; i++){
            temp.append((char) i);
            st.put(temp, i);
            temp.setLength(0);
        }
        int code = R+1;  // R is codeword for EOF
        BinaryStdOut.writeBit(reset);

       char input = BinaryStdIn.readChar();
        sb.append(input);

        while(!BinaryStdIn.isEmpty()){

            if(code == L){//when dictionary is full
                if(W < 16){
                    W++;
                    L *= 2;
                }
                
                if(code == 65536){
                    if(reset){ //reset mode
                        W = 9;
                        L = 512;

                        st = new TST<Integer>();
                        temp.setLength(0);
                        for(int i = 0; i < R; i++){
                            temp.append((char) i);
                            st.put(temp, i);
                            temp.setLength(0);
                        }
                        code = R+1; 
                    }
                }  
            }

            while(st.contains(sb) && !BinaryStdIn.isEmpty()){ 
                char k = BinaryStdIn.readChar();
                sb.append(k);   
            }
            
            if(code < L){  
                st.put(sb, code++);
            }
            
            char last = sb.charAt(sb.length()-1);
            sb.setLength(sb.length() - 1);
            BinaryStdOut.write(st.get(sb), W);
            sb.setLength(0);
            sb.append(last);  
              
        }
        if(sb != null){
            BinaryStdOut.write(st.get(sb), W);
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[65536];//2^16
        int i; // next available codeword value
        boolean r = BinaryStdIn.readBoolean();

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        

        int codeword = BinaryStdIn.readInt(W);

        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
    
        while(true){
            BinaryStdOut.write(val);
            if (i == 65536){
                if(r){
                    W = 9;
                    L = 512;
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                }
            }

            codeword = BinaryStdIn.readInt(W);
            
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L){
                st[i++] = val + s.charAt(0);
            }
            if (i == L-1){
                if (W < 16){
                    W++;
                    L *= 2;
                }
            }
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        int count = args.length;
        if (args[0].equals("-")) {
            if(count == 2){
                if(args[1].charAt(0) == 'r'){
                    reset = true; 
                }
            }
            compress();
        }
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
