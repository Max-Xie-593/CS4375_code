import java.util.*;
import java.io.*;
/**
 * CS 4375
 * Project 3
 * @author Max Xie
 * date 10/6/2018
 */
class Node {
	private String action;		// instance variables
	private ArrayList<String> neighbors;
	private ArrayList<Double> probs;
	private Node next;
	
	/**
	 * Creates a Node Object with a given action
	 * @param action action to take
	 */
	Node(String action) {
		this.action = action;
		this.neighbors = new ArrayList<>();
		this.probs = new ArrayList<>();
	}
	
	/**
	 * Creates a Node Object with a given action, neighbor, and probability
	 * @param action action to take
	 * @param neighbor neighbor the action goes to
	 * @param prob chance for the action to go to said neighbor
	 */
	Node(String action, String neighbor, double prob) {
		this.action = action;
		this.neighbors = new ArrayList<>();
		neighbors.add(neighbor);
		this.probs = new ArrayList<>();
		probs.add(prob);
	}
	
	/**
	 * Adds the neighbor and probability to the Node
	 * @param neighbor neighbor the action goes to
	 * @param prob chance for the action to go to said neighbor
	 */
	void addNeighbor(String neighbor, double prob) {
		neighbors.add(neighbor);
		probs.add(prob);
	}
	
	/**
	 * returns the Next Node Object
	 * @return next Node Object
	 */
	Node getNext() { return next; }
	
	/**
	 * sets the next Node Object
	 * @param nxt Node Object to set
	 */
	void setNext(Node nxt) { next = nxt; }
	
	/**
	 * returns the action of the Node Object
	 * @return String action of the Node
	 */
	String getAction() { return action; }
	
	/**
	 * returns an ArrayList of Neighbors
	 * @return String ArrayList of neighbors
	 */
	ArrayList<String> getNeighbors() { return neighbors; }
	
	/**
	 * returns an ArrayList of Probabilities
	 * @return Double ArrayList of probabilities
	 */
	ArrayList<Double> getProbs() { return probs; }
	
	/**
	 * prints the Node Object
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		str.append("(" + action + " ");
		for(int lcv = 0; lcv < neighbors.size(); lcv++) {
			str.append(neighbors.get(lcv) + " " + probs.get(lcv));
			if(lcv != neighbors.size() - 1)
				str.append(" ");
		}
		str.append(")");
		return str.toString();
	}
}

class Neighbors {
	private String state;		// instance variables
	private int reward;
	private Node head;
	
	/**
	 * creates a single linked Linked List
	 * @param state name of the state
	 * @param reward reward of the state
	 */
	Neighbors(String state, int reward) { 
		this.state = state;
		this.reward = reward;
		head = null; 
	}
	
	/**
	 * inserts a Node Object in the Linked List
	 * @param action action to take
	 * @param neighbor neighbor action goes to
	 * @param prob chance of the action to go to neighbor
	 */
	void insert(String action, String neighbor, double prob) {
		Node temp = findAction(action);
		if(temp != null) {
			temp.addNeighbor(neighbor, prob);
			return;
		}
		temp = new Node(action,neighbor,prob);
		if(head == null) 
			head = temp;
		else {
			Node hold = head;
			while(hold.getNext() != null)
				hold = hold.getNext();
			hold.setNext(temp);
		}
	}
	
	/**
	 * determines if there is a Node with the same action as the action given
	 * @param action action to take
	 * @return returns the Node with the same action as the action given, null otherwise
	 */
	Node findAction(String action) {
		Node temp = head;
		while(temp != null) {
			if(temp.getAction().equals(action))
				return temp;
			temp = temp.getNext();
		}
		return null;
	}
	
	/**
	 * returns the name of the state
	 * @return name of the state
	 */
	String getState() { return state; }
	
	/**
	 * returns the reward of the state
	 * @return reward of the state
	 */
	int getReward() { return reward; }
	
	/**
	 * returns the Linked List
	 * @return Linked List of the neighbors
	 */
	Node getNeighbors() { return head; }
	
	/**
	 * prints the Neighbors Object
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		str.append(state + " " + reward + " ");
		Node temp = head;
		while(temp != null) {
			str.append(temp.toString() + " ");
			temp = temp.getNext();
		}
		return str.toString();
	}
}

class AdList {
	private ArrayList<Neighbors> list;	// instance variables
	
	/**
	 * creates an Adjacency List Object
	 */
	public AdList() { list = new ArrayList<>(); }
	
	/**
	 * adds a new Neighbor object with the given state and reward
	 * @param state state to add
	 * @param reward reward associated with the state
	 */
	void addState(String state, int reward) { list.add(new Neighbors(state,reward)); }
	
	/**
	 * adds a new Node Object with the given state, action, neighbor, and probability
	 * @param state name of the state
	 * @param action action to take in the state
	 * @param neighbor neighbor action goes to
	 * @param prob chance of the action to go to the neighbor
	 */
	void addNeighbor(String state, String action, String neighbor, double prob) {
		Neighbors node = list.get(indexOfState(state));
		node.insert(action, neighbor, prob);
	}
	
	/**
	 * returns the index the Neighbors object with the same state as the given state
	 * @param state name of the state
	 * @return index of the Neighbors object with the same state name, -1 otherwise
	 */
	int indexOfState(String state) {
		for(int lcv = 0; lcv < list.size(); lcv++)
			if(list.get(lcv).getState().equals(state))
				return lcv;
		return -1;
	}
	
	/**
	 * returns the number of states in the Adjacency List
	 * @return
	 */
	int getNumState() { return list.size(); }
	
	/**
	 * returns the state at the given index
	 * @param index index in the ArrayList
	 * @return Neighbor Object at the given index
	 */
	Neighbors getState(int index) { return list.get(index); }
	
	/**
	 * prints the Adjacency List
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		for(Neighbors state : list) 
			str.append(state.toString() + "\n");
		return str.toString();
	}
}

class JValue {
	private String bestAction;	// instance variables
	private double totalReward;
	
	/**
	 * creates the JValue object
	 */
	JValue() {
		bestAction = "";
		totalReward = Double.NEGATIVE_INFINITY;
	}
	
	/**
	 * returns the bestAction for the JValue
	 * @return bestAction for the state
	 */
	String getBestAction() { return bestAction; }
	
	/**
	 * returns the totalReward for the state
	 * @return totalReward for the state
	 */
	double getTotalReward() { return totalReward; }
	
	/**
	 * sets the bestAction to the action
	 * @param action String to replace bestAction
	 */
	void setBestAction(String action) { bestAction = action; }
	
	/**
	 * sets the totalReward to the action
	 * @param reward value to replace totalReward
	 */
	void setTotalReward(double reward) { totalReward = reward; }
	
	/**
	 * prints the JValue Object
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		str.append(bestAction + " " + String.format("%.4f", totalReward));
		return str.toString();
	}
}

class JTable {
	private AdList list;		// instance variables
	private double gamma;
	private JValue[][] table;
	
	/**
	 * creates a JTable with the given Adjacency List to perform Value iteration
	 * @param list Adjacency List to read from
	 */
	JTable(AdList list, double gamma) {
		this.list = list;
		this.gamma = gamma;
		table = new JValue[20][list.getNumState()];
		for(int i = 0; i < table.length; i++)
			for(int j = 0; j < table[i].length; j++)
				table[i][j] = new JValue();
		valueIteration();
	}
	
	/**
	 * performs the Value iteration Algorithm on the adjacency list
	 */
	void valueIteration() {
		for(int i = 0; i < table.length; i++) {
			for(int j = 0; j < table[i].length; j++) {
				Neighbors state = list.getState(j);	
				double reward = state.getReward();
				if(i == 0) {	// base case of Value iteration
					table[i][j].setTotalReward(reward);
					table[i][j].setBestAction(state.getNeighbors().getAction());
				}
				else {	// inductive case of Value iteration
					double max = Double.NEGATIVE_INFINITY;	// sets lowest possible value for value iteration
					String bestAction = "";
					Node temp = state.getNeighbors();
					while(temp != null) { // loops through all Nodes in the Linked List
						String action = temp.getAction();
						double actionReward = 0.0;
						ArrayList<String> neighbors = temp.getNeighbors();
						ArrayList<Double> probs = temp.getProbs();
						for(int lcv = 0; lcv < neighbors.size(); lcv++) // loops through all neighbors and probabilities
							actionReward += (table[i - 1][list.indexOfState(neighbors.get(lcv))].getTotalReward() * probs.get(lcv));
						if(actionReward > max) { // determines the max reward out of all actions
							max = actionReward;
							bestAction = action;
						}
						temp = temp.getNext();
					}
					table[i][j].setTotalReward((max * gamma) + reward); // sets the new reward with the discount
					table[i][j].setBestAction(bestAction);
				}
			}
		}
	}
	
	/**
	 * prints the JTable with the desired format
	 */
	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("");
		for(int i = 0; i < table.length; i++) {
			str.append(String.format("After iteration %d: ", i + 1));
			for(int j = 0; j < table[i].length; j++) 
				str.append("(" + list.getState(j).getState() + " " + table[i][j].toString() + ") ");
			str.append("\n");
		}
		return str.toString();
	}
}

public class Main {
	
	/**
	 * creates the Adjacency List the represents the graph
	 * @param scan Scanner that reads the file
	 * @return Adjacency List
	 */
	static AdList getInfo(Scanner scan) {
		AdList list = new AdList();
		while(scan.hasNextLine()) {
			String line = scan.nextLine();	// grabs the line
			String [] tokens = line.split("\\s\\(|\\)\\s?(\\()?"); // splits the line with the regular expression
			String state = "";
			for(int lcv = 0; lcv < tokens.length; lcv++) {
				Scanner read = new Scanner(tokens[lcv]); // new Scanner to read each token
				if(lcv == 0) { // contains the state and reward
					state = read.next();
					int reward = read.nextInt();
					list.addState(state, reward);
				}
				else { // contains the action, neighbor and probability
					String action = read.next();
					String neighbor = read.next();
					double prob = read.nextDouble();
					list.addNeighbor(state, action, neighbor, prob);
				}
				read.close();
			}
		}
		return list;
	}
	
	public static void main(String[] args) throws IOException {
		//java Main <testFile> <Gamma>
		File testFile = new File(args[0]);
		double gamma = Double.parseDouble(args[1]);

		//Scanner scan = new Scanner(new File("test1.txt"));
		Scanner scan = new Scanner(testFile);
		
		AdList list = getInfo(scan);
		JTable table = new JTable(list,gamma);
		System.out.print(table);
		scan.close();
	}
}
