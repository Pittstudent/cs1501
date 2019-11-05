
import java.lang.Math;
import java.io.*;
import java.util.logging.Logger;

public class myLZW {
    private static final int R = 256;        // number of input chars
    private static int L = (int)Math.pow(2,9);       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    public static Logger LOGGER = Logger.getLogger("InfoLogging");


    public static void compress(char cr) {
        int count=0; 
        int comp=0;
        int uncomp=0;
        double rate=0;
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF
        
        int c=0;
        if(cr=='r'){
            c=1;
        }
        else if(cr=='m'){
            c=2;
        }
        else if(cr=='n'){
            c=3;
        }
        else{
            LOGGER.info("invalid compress method! should be 'r', 'm', or 'n'\n");
        }
        BinaryStdOut.write(c, 9);
        LOGGER.info("c="+c+"char"+cr);
        while (input.length() > count) {
            String s = st.longestPrefixOf(input,count);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int n=st.get(s);
            int t = s.length();
            uncomp+=t*8;
            comp+=W;
            if (count+t+1 < input.length() && code < L){   // Add s to symbol table.
                st.put(input.substring(count, count+t + 1), code++);
            }
            if(code==L&&16>W){
                W++;
                L=(int)Math.pow(2,W);
                if(c==2){
                    double newrate=(double)comp/(double)uncomp;
                    if(newrate/rate>1.1){
                        rate=newrate;
                        st=new TST<Integer>();
                        for (int i = 0; i < R; i++){
                            st.put("" + (char) i, i);
                        }
                        W=9;
                        L=(int)Math.pow(2,9);
                        code = R+1; 
                    }
                    else{
                        rate=newrate;
                    }
                }      
            }
            if(code==L&&W==16){
                if(c==1){
                    st=new TST<Integer>();
                    for (int i = 0; i < R; i++){
                        st.put("" + (char) i, i);
                    }
                    W=9;
                    L=(int)Math.pow(2,9);
                    code = R+1; 
                    LOGGER.info("Resized!\n\n");
                }
                          
            }
            
            
            count+=t;            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    

    public static void expand() {
        String[] st = new String[(int)Math.pow(2,16)];
        int i; // next available codeword value
        int uncomp=0;
        int comp=0;
        double rate=0;

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        i=R+1;

        
        int c=BinaryStdIn.readInt(9);
        int codeword = BinaryStdIn.readInt(W);
        comp+=W;
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
        

        while (true) {
            //LOGGER.info("+1");
            BinaryStdOut.write(val);
            uncomp+=val.length()*8;
            codeword = BinaryStdIn.readInt(W);
            comp+=W;
            if (codeword == R) break;
            String s = st[codeword];
            //LOGGER.info("codeword="+codeword+"  i="+i+"  W="+W);
            if(codeword>i){
                LOGGER.info("Waaaaaaaaaaagh! Error!!!!!!!!!!happened at codeword"+codeword+"  i="+i+"  W="+W);
                LOGGER.info("Quit");
                System.exit(0);
            }
            if (i == codeword) {
                s = val + val.charAt(0);   // special case hack
            }
            if (i < L){
                st[i++] = val + s.charAt(0);
            }
            if(i==L-1&&16>W){
                
                if(c==2){
                    double newrate=((double)comp-W)/(double)uncomp;
                    if(newrate/rate>1.1){
                        st=new String[(int)Math.pow(2,16)];
                        for (i = 0; i < R; i++)
                            st[i] = "" + (char) i;
                        st[i++] = ""; 
                        i=R+1;                       // (unused) lookahead for EOF
                        L=(int)Math.pow(2,9);
                        W=9;
                    }
                    rate=newrate;
                }
                else{
                    W++;
                    L=(int)Math.pow(2,W);
                }
            }
            if(i==L-1&&W==16){
                if(c==1){
                    st=new String[(int)Math.pow(2,16)];
                    for (i = 0; i < R; i++)
                        st[i] = "" + (char) i;
                    st[i++] = "";
                    i=R+1;                        // (unused) lookahead for EOF
                    L=(int)Math.pow(2,9);
                    LOGGER.info("JIng is stupid"+codeword+" "+i);
                    W=9;
                }             
            }
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        LOGGER.info("Logging an INFO-level message");
        if(args[0].equals("-")&&args[1].length()==1){
            char c=args[1].charAt(0);
            String[] newargs=new String[args.length-1];
            newargs[0]=args[0];
            for(int i=1;i<newargs.length;i++){
                newargs[i]=args[i+1];
            }
            args=newargs;
            compress(c);
        }
        else if (args[0].equals("+")){
            expand();
        }
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}