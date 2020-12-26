import java.util.*;
import java.io.*;
/**
 * CS 4375 
 * Project 2
 * @author Max Xie
 * date 9/22/2018
 */

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
	private ArrayList<String> attributes;			//	instance variables
	private ArrayList<DataLine> list;
	
	private double[] prob0;
	private double[] prob1;
	
	/**
	 * Creates allData Object with attributeList, DataLine ArrayList, and 2 probability arrays
	 */
	allData() {
		attributes = new ArrayList<>();
		list = new ArrayList<>();
		prob0 = new double[1];
		prob1 = new double[1];
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
		prob0 = new double[(attributes.size() * 2) - 1];
		prob1 = new double[(attributes.size() * 2) - 1];
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
	 * return the size of the DataLine ArrayList
	 * @return size of DataLine ArrayList
	 */
	int getSize() { return list.size(); }
	
	/**
	 * calculates probability of the class values followed by conditional probabilities given the class value
	 * Also stores class value probability in the array
	 */
	void calcProbs() {
		allData class0 = new allData(attributes);
		allData class1 = new allData(attributes);
		for(int i = 0; i < list.size(); i++) {
			DataLine line = list.get(i);
			if(line.getClassValue() == 0) 
				class0.addDataLine(line);
			else 
				class1.addDataLine(line);	
		}
		prob0[0] = (double)class0.list.size()/list.size();
		prob1[0] = (double)class1.list.size()/list.size();
		if(!class0.list.isEmpty()) {
			System.out.printf("P(class = 0) = %.2f",(double)class0.list.size()/list.size());
			calcCondProb(class0);
		}
		if(!class1.list.isEmpty()) {
			System.out.printf("P(class = 1) = %.2f",(double)class1.list.size()/list.size());			
			calcCondProb(class1);
		}
	}
	
	/**
	 * calculates conditional probabilities given a class value and stores conditional probabilities in array
	 * @param data allData object that contains all instances in the original allData with the same class value
	 */
	void calcCondProb(allData data) {
		int classVal = data.list.get(0).getClassValue();
		for(int i = 0; i < data.attributes.size() - 1; i++) {
			String att = data.attributes.get(i);
			int count0 = 0;
			int count1 = 0;
			for(int j = 0; j < data.list.size(); j++) 
				if(data.list.get(j).getAttValue(i) == 0) 
					count0++;
				else
					count1++;
			System.out.printf(" P(%s = 0|%d) = %.2f",att,classVal,(double) count0/(count1 + count0));
			System.out.printf(" P(%s = 1|%d) = %.2f",att,classVal,(double) count1/(count1 + count0));
			if(classVal == 0) {
				prob0[(i * 2) + 1] = (double) count0/(count1 + count0);
				prob0[(i * 2) + 2] = (double) count1/(count1 + count0);
			}
			else {
				prob1[(i * 2) + 1] = (double) count0/(count1 + count0);
				prob1[(i * 2) + 2] = (double) count1/(count1 + count0);
			}
		}
		System.out.println();
	}
	
	/**
	 * tests the probabilities on the test information
	 * @param test test instances
	 * @return the percentage correct
	 */
	double test(allData test) {
		int correct = 0;
		int total = 0;
		for(int lcv = 0; lcv < test.list.size(); lcv++) {
			if(predictClass(test.list.get(lcv)) == test.list.get(lcv).getClassValue()) 
				correct++;
			total++;
		}
		return (double) correct/total;
	}
	
	/**
	 * predicts the Class based on Naive Bayes
	 * @param testLine test instance to test on
	 * @return the class value from the tree
	 */
	int predictClass(DataLine testLine) {
		double isZero = prob0[0];
		double isOne = prob1[0];
		for(int i = 0; i < attributes.size() - 1; i++) {
			if(testLine.getAttValue(i) == 0) {
				isZero *= prob0[(i * 2) + 1];
				isOne *= prob1[(i * 2) + 1];
			}
			else {
				isZero *= prob0[(i * 2) + 2];
				isOne *= prob1[(i * 2) + 2];
			}
		}
		if(isZero > isOne)
			return 0;
		else
			return 1;
	}
	
	/**
	 * prints the probabilities in the arrays
	 */
	void printProbs() {
		for(double zero: prob0)
			System.out.printf("%.2f ",zero);
		System.out.println();
		for(double one: prob1)
			System.out.printf("%.2f ",one);
		System.out.println();
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
	
	
	static allData getInfo(ArrayList<String> attributes, Scanner scan) {
		allData data = new allData(attributes);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String [] tokens = line.split(" ");
			DataLine example = new DataLine(tokens);
			data.addDataLine(example);	
		}
		return data;
	}
	
	public static void main(String[] args) throws IOException {
		//java Main <train file> <test file>
		File trainFile = new File(args[0]);
		File testFile = new File(args[1]);
		
		Scanner scan = new Scanner(trainFile);
		//Scanner scan = new Scanner(new File("train1.txt"));
		
		String classes = scan.nextLine();
		ArrayList<String> attributes = new ArrayList<>(Arrays.asList(classes.split(" ")));
		
		allData trainData = getInfo(attributes,scan);
		
		trainData.calcProbs();
		System.out.printf("\nAccuracy on training set (%d instances): %.2f%%\n\n",trainData.getSize(),trainData.test(trainData) * 100);		
		scan.close(); 
		
		scan = new Scanner(testFile);
		//scan = new Scanner(new File("test1.txt"));
		scan.nextLine();
	
		allData testData = getInfo(attributes,scan);
		
		System.out.printf("Accuracy on test set (%d instances): %.2f%%",testData.getSize(),trainData.test(testData) * 100);
		scan.close();
	}
}
