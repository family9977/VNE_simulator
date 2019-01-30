package main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class RandomGraphGenerator {

	private int nodeNumber;
	private int linkNumber;
	
	private SubstrateNetwork substrateNetwork;
	private VirtualNetworkPanel virtualNetworkPanel;
	
	private int[][] LinkState;
	private ArrayList<Integer> checkList;
	
	private Random Rand = new Random();
	//Constructor for SubstrateNetwork
	public RandomGraphGenerator(SubstrateNetwork sn, int nodeNumber, int cpuLowerBound, int cpuUpperBound, int bwLowerBound, int bwUpperBound) {
		// TODO Auto-generated constructor stub
		this.nodeNumber = nodeNumber;
		this.substrateNetwork = sn;
		this.substrateNetwork.getSNs().clear();
		this.substrateNetwork.getSLs().clear();
		//Generate Nodes
		int capacity;
		for(int i=0; i<this.nodeNumber; i++) {
			capacity = cpuLowerBound + this.Rand.nextInt(cpuUpperBound-cpuLowerBound+1);
			System.out.println(capacity);
			this.substrateNetwork.addSN(capacity);
		}
		//Generate Links
		this.checkList = new ArrayList<>();
		this.LinkState = new int[nodeNumber][nodeNumber];
		do {
			//Random connection between two nodes
			for(int i=0; i<nodeNumber; i++) {
				for(int j=i; j<nodeNumber; j++) {
					if(i == j)
						this.LinkState[i][j] = 0;
					else {
						this.LinkState[i][j] = this.Rand.nextInt(5);
						this.LinkState[j][i] = this.LinkState[i][j];
					}
					
				}
			}
			//Check if the topology is complete
			this.checkList.clear();
			this.checkList.add(0);
			int checkIndex = 0;
			boolean[] ifDuplicate = new boolean[nodeNumber];
			for(int i=0; i<nodeNumber; i++)
				ifDuplicate[i] = false;
			ifDuplicate[0] = true;
			while(checkIndex < this.checkList.size()) {
				for(int j=0; j<nodeNumber; j++) {
					if(this.LinkState[this.checkList.get(checkIndex)][j] == 1 && ifDuplicate[j] == false) {
						this.checkList.add(j);
						ifDuplicate[j] = true;
					}
				}
				checkIndex++;
			}
			if(this.checkList.size() == nodeNumber)
				break;
		}while(true);
		for(int i=0;i<nodeNumber;i++) {
			for(int j=0;j<nodeNumber;j++) {
				System.out.print(this.LinkState[i][j]+" ");
			}
			System.out.println("");
		}
		//establish connections among nodes
		for(int i=0; i<nodeNumber; i++) {
			for( int j=i+1; j<nodeNumber; j++) {
				if(this.LinkState[i][j] == 1) {
					this.substrateNetwork.addSL(i, j, bwLowerBound+this.Rand.nextInt(bwUpperBound-bwLowerBound+1));
				}
			}
		}
		this.substrateNetwork.localization();
	}
	//Constructor for Virtual Network
	private int nodeNumberLowerBound;
	private int nodeNumberUpperBound;
	private int cpuLowerBound;
	private int cpuUpperBound;
	private int bwLowerBound;
	private int bwUpperBound;
	
	public RandomGraphGenerator(VirtualNetworkPanel vnp, int nNLB, int nNUB, int cLB, int cUB, int bLB, int bUB) {
		// TODO Auto-generated constructor stub
		this.virtualNetworkPanel = vnp;
		this.virtualNetworkPanel.getVNRs().clear();
		this.nodeNumberLowerBound = nNLB;
		this.nodeNumberUpperBound = nNUB;
		this.cpuLowerBound = cLB;
		this.cpuUpperBound = cUB;
		this.bwLowerBound = bLB;
		this.bwUpperBound = bUB;
	}
	
	public void generateVNR(int st, int lt) {
		this.virtualNetworkPanel.addVNR(st, lt);
		VirtualNetwork VNR = (VirtualNetwork)(this.virtualNetworkPanel.getVNRs().get(this.virtualNetworkPanel.getVNRs().size()-1));
		int capacityRequest;
		this.nodeNumber = this.nodeNumberLowerBound + this.Rand.nextInt(this.nodeNumberUpperBound-this.nodeNumberLowerBound+1);
		System.out.println(this.nodeNumber);
		for(int i=0; i<this.nodeNumber; i++) {
			capacityRequest = this.cpuLowerBound + this.Rand.nextInt(this.cpuUpperBound-this.cpuLowerBound+1);
			VNR.addVN(capacityRequest);
		}
		//Generate Links
		this.checkList = new ArrayList<>();
		this.LinkState = new int[this.nodeNumber][this.nodeNumber];
		double threshold = 2/((double)this.nodeNumber);
		do {
			//Random connection between two nodes
			for(int i=0; i<this.nodeNumber; i++) {
				for(int j=i; j<this.nodeNumber; j++) {
					if(i == j)
						this.LinkState[i][j] = 0;
					else {
						if(this.Rand.nextDouble() < threshold) {
							this.LinkState[i][j] = 1;
							this.LinkState[j][i] = this.LinkState[i][j];
						}
						else {
							this.LinkState[i][j] = 0;
							this.LinkState[j][i] = this.LinkState[i][j];
						}
					}
					
				}
			}
			//Check if the topology is complete
			this.checkList.clear();
			this.checkList.add(0);
			int checkIndex = 0;
			boolean[] ifDuplicate = new boolean[this.nodeNumber];
			for(int i=0; i<this.nodeNumber; i++)
				ifDuplicate[i] = false;
			ifDuplicate[0] = true;
			while(checkIndex < this.checkList.size()) {
				for(int j=0; j<this.nodeNumber; j++) {
					if(this.LinkState[this.checkList.get(checkIndex)][j] == 1 && ifDuplicate[j] == false) {
						this.checkList.add(j);
						ifDuplicate[j] = true;
					}
				}
				checkIndex++;
			}
			if(this.checkList.size() == this.nodeNumber)
				break;
		}while(true);
		
		for(int i=0;i<this.nodeNumber;i++) {
			for(int j=0;j<this.nodeNumber;j++) {
				System.out.print(this.LinkState[i][j]+" ");
			}
			System.out.println("");
		}
		//establish connections among nodes and save the matrix
		for(int i=0; i<this.nodeNumber; i++) {
			for( int j=i+1; j<this.nodeNumber; j++) {
				if(this.LinkState[i][j] == 1) {
					VNR.addVL(i, j, bwLowerBound+this.Rand.nextInt(bwUpperBound-bwLowerBound+1));
				}
			}
		}
		VNR.localization();
	}
}
