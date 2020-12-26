import java.util.*;
import java.io.*;
/**
 * CS 4375
 * Project 4
 * @author Max Xie
 * date 10/19/2018
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
	private double[] weights;
	
	/**
	 * Creates allData Object with attributeList, DataLine ArrayList, and 2 probability arrays
	 */
	allData() {
		attributes = new ArrayList<>();
		list = new ArrayList<>();
		weights = new double[0];
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
		weights = new double[attributes.size() - 1];
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
	 * returns the dot product of the training instance
	 * @param line training instance
	 * @return dot product of training instance and weights
	 */
	double dotProduct(DataLine line) {
		double dotprod = 0.0;
		for(int lcv = 0; lcv < line.getLineList().size(); lcv++)
			dotprod += (weights[lcv] * line.getAttValue(lcv));
		return dotprod;
	}
	
	/**
	 * returns the error of the class value and predicted value of training instance
	 * @param line training instance
	 * @param dotProduct predicted value of training instance
	 * @return error
	 */
	double diff(DataLine line, double dotProduct) { return line.getClassValue() - sigmoid(dotProduct);	}
	
	/**
	 * returns the sigmoid value of the weighted sum
	 * @param dotProd weighted sum
	 * @return sigmoid value of weighted sum
	 */
	double sigmoid(double dotProd) { return 1 / (1 + Math.exp(-dotProd)); }
	
	/**
	 * returns the derivative of the sigmoid value
	 * @param dotProd
	 * @return
	 */
	double sigmoidP(double dotProd) { return sigmoid(dotProd) * (1 - sigmoid(dotProd)); }
	
	/**
	 * updates the weights after a training instance
	 * @param line training instance
	 * @param alpha learning rate
	 */
	void updateWeight(DataLine line, double alpha) {
		double dotProd = dotProduct(line);
		for(int lcv = 0; lcv < weights.length; lcv++)
			weights[lcv] += (alpha * line.getAttValue(lcv) * diff(line,dotProd) * sigmoidP(dotProd));
	}
	
	/**
	 * performs the perceptron training algorithm
	 * @param numIterations how many training instances to check
	 * @param alpha learning rate
	 */
	void neuralNet(int numIterations, double alpha) {
		for(int lcv = 0; lcv < numIterations; lcv++) {
			DataLine line = list.get(lcv % list.size());
			updateWeight(line, alpha);
			double sigVal = sigmoid(dotProduct(line));
			System.out.printf("After iteration %d: ", lcv + 1);
			for(int index = 0; index < attributes.size() - 1; index++) 
				System.out.printf("w(%s) = %.4f, ", attributes.get(index), weights[index]);
			System.out.printf("output = %.4f\n", sigVal);
		}
	}
	
	/**
	 * tests the weights on test data
	 * @param data test data to test weights
	 * @return amount correct
	 */
	double test(allData data) {
		int correct = 0;
		for(int lcv = 0; lcv < data.getSize(); lcv++) 
			if(Math.round(sigmoid(dotProduct(data.getData().get(lcv)))) == data.getData().get(lcv).getClassValue())
				correct++;
		return (double) correct / data.getSize();
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

	/**
	 * creates the allData object that holds the data
	 * @param attributes attributes of the data
	 * @param scan Scanner that reads the file
	 * @return allData object
	 */
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
		//java Main <train file> <test file> <learning rate> <numIterations>
		File trainFile = new File(args[0]);
		File testFile = new File(args[1]);
		double alpha = Double.parseDouble(args[2]);
		int numIterations = Integer.parseInt(args[3]);
		
		Scanner scan = new Scanner(trainFile);
		//Scanner scan = new Scanner(new File("train2.txt"));
		
		String classes = scan.nextLine();
		ArrayList<String> attributes = new ArrayList<>(Arrays.asList(classes.split(" ")));
		
		allData trainData = getInfo(attributes,scan);
		trainData.neuralNet(numIterations,alpha);
		
		System.out.printf("\nAccuracy on training set (%d instances): %.2f%%\n\n",trainData.getSize(),trainData.test(trainData) * 100);		
		scan.close(); 
		
		scan = new Scanner(testFile);
		//scan = new Scanner(new File("test2.txt"));
		scan.nextLine();
	
		allData testData = getInfo(attributes,scan);
		System.out.printf("Accuracy on test set (%d instances): %.2f%%",testData.getSize(),trainData.test(testData) * 100);
		
		scan.close();
	}

}
