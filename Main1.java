import java.util.*;
import java.io.*;
/**
 * CS 4375 
 * Project 1
 * @author Max Xie
 * date 9/9/2018
 */

class TreeNode {
	private allData info;			// private variables
	private double entropy;
	private String attSplit;
	private int entireCommon;
	private TreeNode leftChild;
	private TreeNode rightChild;
	
	/**
	 * Create a TreeNode object with a List of training instances
	 * @param info List of training instances
	 */
	TreeNode(allData info) {
		this.info = info;
		entropy = info.calcEntropy();
		attSplit = "";	
		entireCommon = findClassValue();
		leftChild = null;
		rightChild = null;
	}
	
	/**
	 * Create a TreeNode object with a List of training instances
	 * @param info List of training instances
	 * @param entireCommon most common value in the entire training set
	 */
	TreeNode(allData info, int entireCommon) {
		this.info = info;
		entropy = info.calcEntropy();
		attSplit = "";	
		this.entireCommon = entireCommon;
		leftChild = null;
		rightChild = null;
	}
	
	/**
	 * return List of training instances
	 * @return List of training instances
	 */
	allData getInfo() { return info; }
	
	/**
	 * return the entropy of the List
	 * @return entropy value
	 */
	double getEntropy() { return entropy; }
	
	/**
	 * return the attribute the List split on
	 * @return attribute used to split the List
	 */
	String getAttSplit() { return attSplit; }
	
	/**
	 * return the most common value in the entire training set
	 * @return most common value in the entire training set
	 */
	int getEntireCommon() { return entireCommon; }
	
	/**
	 * returns the left Child
	 * @return left Child of the node
	 */
	TreeNode getLeft() { return leftChild; }
	
	/**
	 * returns the right Child
	 * @return right Child of the node
	 */
	TreeNode getRight() { return rightChild; }
	
	/**
	 * sets the left Child
	 * @param x Node to set as left Child
	 */
	void setLeft(TreeNode x) { leftChild = x; }
	
	/**
	 * set the right Child
	 * @param x Node to set as right Child
	 */
	void setRight(TreeNode x) { rightChild = x; }
	
	/**
	 * sets the String used to split the List
	 * @param att String used to split the List
	 */
	void setAttSplit(String att) { attSplit = att; }
	
	/**
	 * determines if the node is a Leaf Node
	 * @return is the node is a Leaf Node
	 */
	boolean isLeaf() { return leftChild == null && rightChild == null; }
	
	/**
	 * determines if the node is done splitting 
	 * @return determines if the node is done splitting
	 */
	boolean isDone() {
		if(info.getAttList().size() == 1)					//	if the attribute contains only "class"
			return true;
		if(findBestSplit().equals(""))						// 	if the function does not return a best Split
			return true;
		int count0 = 0;										//	initialize counters
		int count1 = 0;
		for(int i = 0; i < info.getData().size(); i++) {	//	sorts the class Values of the List
			DataLine line = info.getData().get(i);
			if(line.getClassValue() == 0) 	
				count0++;
			else 
				count1++;
		}
		return (count0 == 0 || count1 == 0);				// determines if the Node is pure
	}
	
	/**
	 * determines the value that appears the most common in the list
	 * @return value that appears the most common in the list
	 */
	int findClassValue() {
		int count0 = 0;										//	initialize counters
		int count1 = 0;
		for(int i = 0; i < info.getData().size(); i++) {	//	sorts the class Values of the List
			DataLine line = info.getData().get(i);
			if(line.getClassValue() == 0) 	
				count0++;
			else 
				count1++;
		}
		if(count0 == count1)								// returns the value most common in the entire List
			return entireCommon;
		else if(count0 < count1)							// if zeros appear less than ones
			return 1;
		else
			return 0;
	}
	
	/**
	 * determines the attribute that splits the List the best
	 * @return attribute that splits the List the best
	 */
	String findBestSplit() {
		String bestSplit = "";
		double infoGain = -0.1;
		for(int lcv = 0 ; lcv < info.getAttList().size() - 1; lcv++) {
			String att = info.getAttList().get(lcv);		// 	grabs the attribute
			allData class0 = new allData();
			allData class1 = new allData();
			int count0 = 0;
			int count1 = 0;
			for(int i = 0; i < info.getData().size(); i++) {//	sorts the attribute Values of the List
				DataLine line = info.getData().get(i);
				if(line.getAttValue(lcv) == 0) {
					class0.addDataLine(line);
					count0++;
				}
				else {
					class1.addDataLine(line);
					count1++;
				}
			}
			double entropy0 = class0.calcEntropy();			// 	calculate the conditional entropy value of the Node
			double entropy1 = class1.calcEntropy();
			double frac0 = (double)count0/(count0 + count1);
			double frac1 = 1 - frac0;	
			double condEntropy = (frac0 * entropy0) + (frac1 * entropy1);
			if(entropy - condEntropy > infoGain) {			// 	determines if the information gain is better than the previous 
				bestSplit = att;
				infoGain = entropy - condEntropy;
			}					
		}
		return bestSplit;
	}
	
	/**
	 * Prints the TreeNode
	 */
	@Override
	public String toString() { return info.toString(); }
}

class Tree {
	private TreeNode root;			// instance variables
	
	/**
	 * Creates the Tree with a Root
	 */
	Tree() { root = null; }
	
	/**
	 * Creates the TreeNode with a root 
	 * @param info List of training instances
	 */
	Tree(allData info) {
		TreeNode root = new TreeNode(info);
		this.root = root;
	}
	
	/**
	 * Creates the tree by making the tree learn
	 */
	void learn() { learn(root); } 
	
	/**
	 * Creates the tree by making the tree learn Pre-Order Style
	 * @param node Node to Split
	 */
	private void learn(TreeNode node) {
		if(node == null || node.isDone()) 	
			return;
		split(node);
		learn(node.getLeft());
		learn(node.getRight());
	}
	
	/**
	 * Tests the Tree on test information
	 * @param test test instances 
	 * @return the percentage correct
	 */
	double test(allData test) {
		int correct = 0;
		int total = 0;
		for(int lcv = 0; lcv < test.getData().size(); lcv++) {
			if(predictClass(test.getData().get(lcv),test.getAttList()) == test.getData().get(lcv).getClassValue()) 
				correct++;
			total++;
		}
		return (double) correct/total;
	}
	
	/**
	 * predicts the Class based on the test instances
	 * @param testLine test instance to test on
	 * @return the class value from the tree
	 */
	int predictClass(DataLine testLine,ArrayList<String> attributes) { return predictClass(root,testLine,attributes); }
	
	/**
	 * predicts the Class based on the test instance by going through the tree
	 * @param node to check on
	 * @param testLine test instance
	 * @return Class value of the Node
	 */
	private int predictClass(TreeNode node, DataLine testLine, ArrayList<String> attributes) {
		if(node.isLeaf())
			return node.findClassValue();
		String split = node.getAttSplit();
		int index = attributes.indexOf(split);
		if(testLine.getAttValue(index) == 0)
			return predictClass(node.getLeft(),testLine,attributes);
		else
			return predictClass(node.getRight(),testLine,attributes);
	}
	
	/**
	 * Splits the training instances
	 * @param node Node to split on
	 */
	void split(TreeNode node) {
		String att = node.findBestSplit();
		if(att.equals(""))											//	stops if there is no best Split
			return;
		int index = node.getInfo().getAttList().indexOf(att);
		allData class0 = new allData(node.getInfo().getAttList());
		allData class1 = new allData(node.getInfo().getAttList());
		for(int i = 0; i < node.getInfo().getData().size(); i++) {
			DataLine line = node.getInfo().getData().get(i);
			if(line.getAttValue(index) == 0) 
				class0.addDataLine(line);
			else 
				class1.addDataLine(line);
		}
		class0.getAttList().remove(index);							//	removes the attribute and its corresponding values
		class1.getAttList().remove(index);
		for(int lcv = 0; lcv < class0.getData().size(); lcv++) 
			class0.getData().get(lcv).getLineList().remove(index);
		for(int lcv = 0; lcv < class1.getData().size(); lcv++) 
			class1.getData().get(lcv).getLineList().remove(index);
		TreeNode left = new TreeNode(class0,root.getEntireCommon());
		TreeNode right = new TreeNode(class1,root.getEntireCommon());
		node.setLeft(left);											//	sets new TreeNodes to left and right Childs		
		node.setRight(right);
		node.setAttSplit(att);
	}
	
	/**
	 * Prints the tree Pre-Order Value
	 */
	void printTree() { printTree(root,0); }
	
	/**
	 * Prints the tree Pre-Order Value Recursive Helper
	 * @param node current node function is on
	 * @param depth	how deep in the tree
	 */
	private void printTree(TreeNode node, int depth) {
		if(node.isLeaf())
			return;
		int count = depth + 1;
		for(int lcv = 0; lcv < depth; lcv++)
			System.out.print("| ");
		System.out.print(node.getAttSplit() + " = 0 :");
		if(node.getLeft().isDone())
			System.out.println("  " + node.getLeft().findClassValue());
		else
			System.out.println();
		printTree(node.getLeft(),count);
		for(int lcv = 0; lcv < depth; lcv++)
			System.out.print("| ");
		System.out.print(node.getAttSplit() + " = 1 :");
		if(node.getRight().isDone())
			System.out.println("  " + node.getRight().findClassValue());
		else
			System.out.println();
		printTree(node.getRight(),count);
	}
	
	/**
	 * prints the tables of the Nodes
	 */
	void print() { print(root); }
	
	/**
	 * prints the tables of the Nodes 
	 * @param node
	 */
	private void print(TreeNode node) {
		if(node == null)
			return;
		System.out.println(node);
		print(node.getLeft());
		print(node.getRight());
	}
}

class DataLine {
	private ArrayList<Integer> attributeClass;		// instances variables
	private int classClass;
	
	/**
	 * creates a new DataLine
	 */
	DataLine() {
		attributeClass = new ArrayList<>();
		classClass = 0;
	}
	
	/**
	 * Creates a new DataLine given a String Array of the attributes
	 * @param tokens String array of the attribute values
	 */
	DataLine(String [] tokens) {
		attributeClass = new ArrayList<>();
		for(int lcv = 0 ; lcv < tokens.length - 1; lcv++)
			attributeClass.add(Integer.parseInt(tokens[lcv]));
		classClass = Integer.parseInt(tokens[tokens.length - 1]);
	}
	
	/**
	 * Copy Constructor
	 * @param intList original DataLine
	 */
	DataLine(DataLine intList) {
		attributeClass = new ArrayList<>();
		for(int a: intList.attributeClass)
			this.attributeClass.add(a);
		classClass = intList.classClass;
	}
	
	/**
	 * returns the attribute values
	 * @return Integer ArrayList of int Values
	 */
	ArrayList<Integer> getLineList() { return attributeClass; }
	
	/**
	 * returns the attribute value at a index
	 * @param index index value in the attributeList
	 * @return class value at the index
	 */
	int getAttValue(int index) { return attributeClass.get(index); }
	
	/**
	 * returns the class value of the Line
	 * @return int value
	 */
	int getClassValue() { return classClass; }
	
	/**
	 * Prints the Line
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		for(int a: attributeClass)
			str.append(a + "\t");
		str.append(classClass);
		return str.toString();
	}
}

class allData {
	ArrayList<String> attributes;			//	instance variables
	ArrayList<DataLine> list;
	
	/**
	 * Creates allData Object with attributeList and DataLine ArrayList
	 */
	allData() {
		attributes = new ArrayList<>();
		list = new ArrayList<>();
	}
	
	/**
	 * Creates allData Object with ArrayList String values
	 * @param attributes String ArrayList of Attributes
	 */
	allData(ArrayList<String> attributes) {
		this.attributes = new ArrayList<>();
		for(String a: attributes)
			this.attributes.add(a);
		list = new ArrayList<>();
	}
	
	/**
	 * Adds a DataLine to the ArrayList
	 * @param line 
	 */
	void addDataLine(DataLine line) {
		DataLine newLine = new DataLine(line);
		list.add(newLine); 
	}
	
	/**
	 * returns the attributes 
	 * @return String ArrayList of attributes
	 */
	ArrayList<String> getAttList() { return attributes; } 
	
	/**
	 * returns DataLine ArrayList
	 * @return ArrayList of DataLine
	 */
	ArrayList<DataLine> getData() { return list; }
	
	/**
	 * returns log
	 * @param a value of numerator
	 * @param b value of denominator
	 * @return double log a/ log b
	 */
	private double logb(double a, double b) { return Math.log(a)/ Math.log(b); }
	
	/**
	 * returns log base 2
	 * @param a value of the numerator
	 * @return
	 */
	private double log2(double a) {	
		if(a == 0) 
			return 0; 	
		return logb(a,2);
	}
	
	/**
	 * calculates entropy of the List
	 * @return double value of entropy
	 */
	double calcEntropy() {
		int count0 = 0;
		int count1 = 0;
		for(DataLine a: list) 
			if(a.getClassValue() == 0)
				count0++;
			else
				count1++;
		double frac0 = (double) count0 /(count0 +  count1);
		double frac1 = 1 - frac0;
		
		return Math.abs(-((frac0 * log2(frac0)) + (frac1 * log2(frac1))));
	}
	
	/**
	 * Prints the AllData Object
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		for(String att: attributes)
			str.append(att + "\t");
		str.append("\n");
		for(DataLine a: list)
			str.append(a.toString() + "\n");
		return str.toString();
	}
}

public class Main {
	
	public static void main(String[] args) throws IOException {
		//java Main <train file> <test file> <max Training Instances>
		
		File trainFile = new File(args[0]);
		File testFile = new File(args[1]);
		int maxTrainInstances = Integer.parseInt(args[2]);
		
		Scanner scan = new Scanner(trainFile);
		//Scanner scan = new Scanner(new File("train1.txt"));
		
		String classes = scan.nextLine();
		ArrayList<String> attributes = new ArrayList<>(Arrays.asList(classes.split("\t")));
		
		allData trainData = new allData(attributes);
		int instances = 0;
		if(maxTrainInstances <=0) {
			while(scan.hasNextLine()) {
				String line = scan.nextLine();
				String [] tokens = line.split("\t");
				DataLine example = new DataLine(tokens);
				trainData.addDataLine(example);	
				instances++;
			}
		}
		else {
			while(scan.hasNextLine() && instances < maxTrainInstances) {
				String line = scan.nextLine();
				String [] tokens = line.split("\t");
				DataLine example = new DataLine(tokens);
				trainData.addDataLine(example);	
				instances++;
			}
		}
		Tree tree = new Tree(trainData);
		tree.learn();
		tree.printTree();
		System.out.printf("\nAccuracy on training set (%d instances): %.2f%%\n\n",instances,tree.test(trainData) * 100);
		scan.close();
		//scan = new Scanner (new File("test1.txt"));
		scan = new Scanner (testFile);
		scan.nextLine();
		instances = 0;
		allData testData = new allData(attributes);
		while(scan.hasNextLine()) {
			String blah = scan.nextLine();
			String [] tokens = blah.split("\t");
			DataLine example = new DataLine(tokens);
			testData.addDataLine(example);
			instances++;
		}
		System.out.printf("Accuracy on test set (%d instances): %.2f%%",instances,tree.test(testData) * 100);
		scan.close();
	}

}
