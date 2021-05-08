/*
 * Authors: Michael Valentino-Manno (Lab section 5), Benjamin Seal (Lab section 4), Blake Mitchell (Lab section 4)
 * Date: 2/6/18
 * Overview: This program takes an input message from a text file "input.txt". Using 
 * a huffman algorithm, encodes the message, and decodes the message to a text file "output.txt"
 */

/*
 * We used and modified TreeApp.java to use in this lab
 * Rovert Lafore. 2002. Data Structures and Algorithms in Java (2 ed.). Sams, Indianapolis, IN, USA
 */


import java.io.*; //For scanners

class Node { //Class to create node objects

    public int freq;           // Integer holds frequency of the node's char
    public char ch;        // Char holds the caracter belonging to the node
    public Node leftChild;      // this Node's left child
    public Node rightChild;     // this Node's right child

    public boolean isNodeALeaf() { //Method that checks if the node is a leaf
        if (leftChild == null && rightChild == null) { //its a leaf if its children are null
            return true;
        } else {
            return false;
        }
    }

    public int getCharNode() { //method to return the node's char
        return ch;
    }

    public int getFreqNode() { //method to return the node's freq
        return freq;
    }

} // end Class Node
////////////////////////////////////////////////////////////////

class Tree { //Class allowing us to create trees

    public String[] codes = new String[256]; //Array to hold the codes of each character, lenth 256 will hold all chars entered
    public Node root;    // first Node of Tree

    public Tree() {                    // constructor
        root = null;                   // no nodes in tree yet
    }

    public int getFreq() { //returns the roots frequency, which for this lab, will be the sum of its children's freqs
        return root.freq;
    }

    public String findCode(Node n, char c, String code) { //recursive method to find a char's code

        if (n.isNodeALeaf() == true && n.getCharNode() == c) { //if node is a leaf
            codes[c] = code;         //save code to the char's ascii value index in the codes array
            return code;            

        } else {
            if (n.leftChild != null) { //if there is a path left
                findCode(n.leftChild, c, code + "0"); //recursive call with next node, append a 0 to the string
            }
            if (n.rightChild != null) { //if there is a parth right
                findCode(n.rightChild, c, code + "1"); //recursive call with next node, append a 1 to the sring
            }

        }
        return code;
    }  //end find()

    public void insert(int id, char dd) { //insert a new node into the tree
        Node newNode = new Node();    // make new Node
        newNode.freq = id;           // insert data
        newNode.ch = dd;
        newNode.leftChild = null;
        newNode.rightChild = null;
        if (root == null) {            // no node in root
            root = newNode;
        } else {                        // root occupied
            Node current = root;      // start at root  
            Node parent;
            while (true) {            // exits internally			
                parent = current;
                if (id < current.freq) {              // go left?
                    current = current.leftChild;
                    if (current == null) {             // if the end of the line        
                        parent.leftChild = newNode;   // insert on left
                        return;
                    }
                } //end if go left
                else {                                // or go right?
                    current = current.rightChild;
                    if (current == null) // if the end of the line
                    {                                 // insert on right
                        parent.rightChild = newNode;
                        return;
                    }
                }
            }
        }
    } // end insert()
}
////////////////////////////////////////////////////////////////

class Huffman {

    public static void main(String[] args) throws IOException {
        BufferedWriter writer = null; //declares reader and writer
        BufferedReader reader = null;

        try {
            File file = new File("input/input.txt"); //sets up the buffer reader
            reader = new BufferedReader(new FileReader(file));
            String line;                //line will hold strings from the input file
            int[] freq = new int[256];  //frequency array, size 256 will hold any char we need
            int LF = -1;                //Integer that counts the number of line feeds
            System.out.println("The message is:");
            while ((line = reader.readLine()) != null) { //reads in line by line
                LF++; //updates line feed for every line read

                System.out.println(line);
                for (char c = ' '; c <= '~'; c++) { //incrament through necessary ascii chars
                    for (int i = 0; i < line.length(); i++) { //incrament through each char in line
                        if (line.charAt(i) == c) {
                            freq[c]++;  //update the frequency, using the chars ascii value as an index
                        }
                    }
                }
            }
            reader.close();
            System.out.println();
            freq[10] = LF; //Saves linefeed into space 10, which is line feeds ascii value

            System.out.println("Frequency table"); //print freq table
            System.out.println("-------------");
            if (freq[10] > 0) { //manually does linefeed
                System.out.println("Line feed has a frequency of " + freq[10]);
            }
            for (char c = ' '; c < freq.length; c++) { //prints each char and freq if the char occured
                if (c == ' ' && freq[' '] > 0) {
                    System.out.println("Space has a frequency of " + freq[32]); //instead of printing a blank space, we print "Space"
                } else if (freq[c] > 0 && c != ' ') {
                    System.out.println(c + " has a frequency of " + freq[c]);
                }
            }
            System.out.println();

            int j = 0;
            for (int i = 0; i < freq.length; i++) { //counts the number of characters found
                if (freq[i] > 0) {
                    j++;
                }
            }
            int k = 0; //counts the number of trees inserted
            Tree[] PQ = new Tree[j]; //makes an array of trees, of necessary length
            if (freq[10] > 0) { //manually insert a tree for linefeed
                PQ[k] = new Tree();
                PQ[k].insert(freq[10], '\n');
                k++;
            }

            for (char d = ' '; d < freq.length; d++) {
                if (freq[d] > 0) { //inserts trees for the rest of the characters
                    PQ[k] = new Tree();
                    PQ[k].insert(freq[d], d);
                    k++;
                }

            }
                    //Priority Queue, least to greatest
            Tree[] PQsort = sort(PQ, j); //sorts the initial array of trees, from freq least to greatest
            Tree[] fin = buildTree(PQsort, j); //creats new array to hold the built trees
            for (int r = j; r > 2; r--) { //for builds a single huffman tree
                fin = buildTree(fin, r); //fin[0] holds the complete tree

            }

            System.out.println("Code table"); //prints code table
            System.out.println("-------------");
            for (char t = '\n'; t <= '~'; t++) { //incraments through ascii table
                if (freq[t] > 0 && t == '\n') { //finds code for each present char
                    fin[0].findCode(fin[0].root, t, "");
                    System.out.println("Code for Line feed is " + fin[0].codes[t]);
                } else if (freq[t] > 0 && t == ' ') { //finds code for each present char
                    fin[0].findCode(fin[0].root, t, "");
                    System.out.println("Code for Space is " + fin[0].codes[t]);
                } else if (freq[t] > 0) { //finds code for each present char
                    fin[0].findCode(fin[0].root, t, "");
                    System.out.println("Code for " + t + " is " + fin[0].codes[t]);
                }
            }
            File fileAgain = new File("input/Input.txt"); //re read in input file
            reader = new BufferedReader(new FileReader(fileAgain));
            String line2;
            String place = " ";
            String encoded = "";
            int lf = 0;
            while ((line2 = reader.readLine()) != null) { //go through each line
                char temp = ' ';
                for (int g = 0; g < line2.length(); g++) { //builds encoded message

                    temp = line2.charAt(g);

                    place = fin[0].codes[temp];
                    encoded = encoded + place; //appends new part of msg to old
                }
                if (LF - lf > 0) {  //if there is a needed linefeed, append its code
                    encoded = encoded + fin[0].codes['\n'];
                }
                lf++; 
            }
            System.out.println();
            System.out.println("The encoded message is: "); //prints encoded
            System.out.println(encoded);

            String finMsg = decode(encoded, fin[0]); //decodes msg

            try {
                File outfile = new File("output/output.txt"); //file to write too
                writer = new BufferedWriter(new FileWriter(outfile)); //sets up writer
                writer.write(finMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.close(); 
            System.out.println(""); //tells user that program is done
            System.out.println("Decoding complete!! Check output.txt!");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String decode(String s, Tree t) {
        int beg = 0; //represents the beginning of a string
        int end = 1; //end of string
        String temp = "";
        String p = "";

        while (end <= s.length()) {
            for (int i = 0; i < t.codes.length; i++) {
                p = s.substring(beg, end); 
                if (p.equals(t.codes[i])) { //if a code is found

                    temp = temp + (char) i; //update the decoded msg with the char

                    beg = end; //move beginning spot
                }
            }
            end++; //move end spot

        }

        return temp;
    }

    public static Tree[] buildTree(Tree[] s, int j) { //builds the hurrman tree
        Tree temp = new Tree();
        int freq = 0;
        
        Tree[] yes = new Tree[s.length - 1]; 

        if (s.length == 1) { //if only 1 char was found in the msg
            freq = s[0].getFreq();
            temp.insert(freq, '\u0000'); //make tree
            temp.root.leftChild = s[0].root;
            s[0] = temp;
            return s; //return the 2 node (1 leaf) tree
        } else {
            freq = s[0].getFreq(); //greater than 2 chars
            freq += s[1].getFreq(); //find total freq of the children
        }

        temp.insert(freq, '\u0000'); //new node

        temp.root.leftChild = s[0].root; //combines two least freq trees
        temp.root.rightChild = s[1].root;

        yes[0] = temp; //store new tree

        s[0] = null;
        s[1] = null;

        for (int r = 1; r < yes.length; r++) {
            yes[r] = s[r + 1]; //update tree array with the current trees
        }
        sort(yes, yes.length - 1); //sort the trees based on frequency
                                   //maintains priority queue
        return yes;

    }

    public static Tree[] sort(Tree[] s, int n) { //bubble sort

        int inc;
        int ind;

        for (inc = 0; inc < n - 1; inc++) { //sorts freq from least to greatest
            for (ind = 0; ind < n - inc - 1; ind++) {
                if (s[ind].getFreq() > (s[ind + 1].getFreq())) {
                    Tree temp = s[ind]; //swaps the two elements
                    s[ind] = s[ind + 1];
                    s[ind + 1] = temp;
                }
            }
        }
        return s; //sorted tree array
    }

}  // end TreeApp class
////////////////////////////////////////////////////////////////
