package main;

import java.util.*;

public class Algorithm {

	private SubstrateNetwork substrateNetwork;
	private VirtualNetworkPanel virtualNetworkPanel;
	private Vector sns = new Vector<SubstrateNode>();
	private Vector sls = new Vector<SubstrateLink>();
	private Vector vnr = new Vector<VirtualNetwork>();
	
	public Algorithm(SubstrateNetwork sn, VirtualNetworkPanel vnp) {
		// TODO Auto-generated constructor stub
		this.substrateNetwork = sn;
		this.sns = sn.getSNs();
		this.sls = sn.getSLs();
		
		this.virtualNetworkPanel = vnp;
		this.vnr = this.virtualNetworkPanel.getVNRs();
	}

	public boolean Greedy(VirtualNetwork VNR) {
		boolean success = true;
		//Sort the substrate nodes according to their available resource
		SubstrateNode[] snsInOrder = new SubstrateNode[this.sns.size()];
		for(int i=0; i<this.sns.size(); i++)
			snsInOrder[i] = (SubstrateNode)this.sns.get(i);
			//Selection Sort
		for(int i=0; i<this.sns.size(); i++) {
			for(int j=i+1; j<this.sns.size(); j++) {
				if(snsInOrder[i].getInformation()[3]*snsInOrder[i].getTotalBW() < snsInOrder[j].getInformation()[3]*snsInOrder[j].getTotalBW()) {
					SubstrateNode temp = snsInOrder[i];
					snsInOrder[i] = snsInOrder[j];
					snsInOrder[j] = temp;
				}
			}
		}
		//*************************************************************************
		//VirtualNetwork VNR = (VirtualNetwork)this.vnr.get(this.vnr.size()-1);
		Vector vns = VNR.getVNs();
		Vector vls = VNR.getVLs();
		//Sort the virtual nodes according to their requests
		VirtualNode[] vnsInOrder = new VirtualNode[vns.size()];
		for(int i=0; i<vns.size(); i++)
			vnsInOrder[i] = (VirtualNode)vns.get(i);
			//Selection Sort
		for(int i=0; i<vns.size(); i++) {
			for(int j=i+1; j<vns.size(); j++) {
				if(vnsInOrder[i].getRequest()*vnsInOrder[i].getTotalBW() < vnsInOrder[j].getRequest()*vnsInOrder[j].getTotalBW()) {
					VirtualNode temp = vnsInOrder[i];
					vnsInOrder[i] = vnsInOrder[j];
					vnsInOrder[j] = temp;
				}
			}
		}
		//**************************************************************************
		//Node Mapping
		boolean[] snIfMapped = new boolean[this.sns.size()];
		for(int i=0; i<this.sns.size(); i++) snIfMapped[i] = false;
		boolean[] vnIfMapped = new boolean[vns.size()];
		for(int i=0; i<vns.size(); i++) vnIfMapped[i] = false;
		
		for(int i=0; i<vns.size(); i++) {
			for(int j=0; j<this.sns.size(); j++) {
				if(!snIfMapped[j]) {
					if(snsInOrder[j].getInformation()[3] >= vnsInOrder[i].getRequest()) {
						vnsInOrder[i].allocated(snsInOrder[j]);
						snsInOrder[j].addVNList(vnsInOrder[i]);
						snIfMapped[j] = true;
						vnIfMapped[i] = true;
						break;
					}
				}
			}
		}
		
		for(int i=0; i<vns.size(); i++) {
			if(vnIfMapped[i] == false) {
				success = false;
				break;
			}
		}
		if(!success) {
			for(int i=0; i<vns.size(); i++) {
				if(vnIfMapped[i])
					vnsInOrder[i].getMappedInto().deleteVN(vnsInOrder[i]);
			}
			return success;
		}
		//**************************************************************************
		//Link Mapping
		for(int i=0; i<vls.size(); i++) {
			if(!Dijkstra((VirtualLink)vls.get(i))) {
				success = false;
				break;
			}
			else {
				//Update Substrate Network Bandwidth
				this.substrateNetwork.costBW((VirtualLink)vls.get(i));
			}
		}
		//**************************************************************************
		//If Node Mapping or Link Mapping fails, need to get back the resources occupied by the rejected VNR
		if(!success) {
			for(int i=0; i<vns.size(); i++) {
				if(vnIfMapped[i])
					vnsInOrder[i].getMappedInto().deleteVN(vnsInOrder[i]);
			}
			for(int i=0; i<vls.size(); i++) {
				this.substrateNetwork.getBackBWResource((VirtualLink)vls.get(i));
			}
			return success;
		}
		return success;
	}
	
	public boolean ExactSolutionsForLeastBW() {
		boolean success = true;
		
		return success;
	}
	
	public boolean ParticleSwarm(VirtualNetwork VNR) {
		//Initialization
		//VirtualNetwork VNR = (VirtualNetwork)this.vnr.get(this.vnr.size()-1);
		Vector vns = VNR.getVNs();
		Vector vls = VNR.getVLs();
		VirtualNode[] vnsInOrder = new VirtualNode[vns.size()];
		SubstrateNode[] gBest = new SubstrateNode[vns.size()];
		
		for(int i=0; i<vns.size(); i++)
			vnsInOrder[i] = (VirtualNode)vns.get(i);
			//Selection Sort
		for(int i=0; i<vns.size(); i++) {
			for(int j=i+1; j<vns.size(); j++) {
				if(vnsInOrder[i].getRequest()*vnsInOrder[i].getTotalBW() < vnsInOrder[j].getRequest()*vnsInOrder[j].getTotalBW()) {
					VirtualNode temp = vnsInOrder[i];
					vnsInOrder[i] = vnsInOrder[j];
					vnsInOrder[j] = temp;
				}
			}
		}
		
		Particle[] particles = new Particle[5];
		int fv_gBest = Integer.MAX_VALUE;
		for(int i=0; i<5; i++) //Initialize particles
			particles[i] = new Particle(vns.size());
		for(int i=0; i<5; i++) {  //Loop for every particle
			for(int k=0; k<this.sns.size(); k++)  //Set all substrate nodes parameter "allocated" false for L2S2
				((SubstrateNode)this.sns.get(k)).allocated = false;
			//System.out.print("Position:  ");
			for(int j=0; j<vns.size(); j++) {
				vnsInOrder[j].allocated(null);
				particles[i].position[j] = L2S2(vnsInOrder[j]);
				if(particles[i].position[j] == null)
					return false;
				//System.out.print((particles[i].position[j]).getInformation()[0] + " ");
				particles[i].pBest[j] = particles[i].position[j];
			}
			for(int l=0; l<vls.size(); l++) {
				((VirtualLink)vls.get(l)).path.clear();
				if(!Dijkstra((VirtualLink)vls.get(l)))
					particles[i].fitnessValue = Integer.MAX_VALUE;
				else { //Calculate fitness value
					particles[i].fitnessValue = ((VirtualLink)vls.get(l)).getBWCost();
				}
			}
			if(particles[i].fitnessValue!=Integer.MAX_VALUE) {
				if(particles[i].fitnessValue < fv_gBest) {
					fv_gBest = particles[i].fitnessValue;
					particles[i].fitnessValue_pBest = particles[i].fitnessValue;
					for(int k=0; k<vns.size(); k++)
						gBest[k] = particles[i].position[k];
				}
			}
			//System.out.print("\n");
		}
		//************************************************
		//Update position and velocity
		float p1 = (float) 0.1, p2 = (float) 0.2, p3 = (float) 0.7;
		Random rand = new Random();
		float prob = 0;
		for(int iteration=0; iteration<100; iteration++) {  //iteration = 100
			for(int p=0; p<5; p++) { //For every particle
				if(particles[p].fitnessValue != Integer.MAX_VALUE) {
					boolean firstTerm, secondTerm, thirdTerm;
					//System.out.print("Position: ");
					for(int i=0; i<vns.size(); i++) {
						//**************************************  Equation(15): v(t+1) = p1*v(t) + p2*(pBest-x(t)) + p3*(gBest-x(t))
						firstTerm = particles[p].velocity[i];
						if(particles[p].pBest[i] == particles[p].position[i])
							secondTerm = true;
						else
							secondTerm = false;
						if(gBest[i] == particles[p].position[i])
							thirdTerm = true;
						else
							thirdTerm = false;
						prob = rand.nextFloat();
						if(prob < p1)
							particles[p].velocity[i] = firstTerm;
						else if(prob < (p1+p2))
							particles[p].velocity[i] = secondTerm;
						else
							particles[p].velocity[i] = thirdTerm;
						//**************************************  Equation(16): x(t+1) = x(t)*v(t)
						if(!particles[p].velocity[i]) {
							for(int k=0; k<this.sns.size(); k++)  //Set all substrate nodes parameter "allocated" false for L2S2
								((SubstrateNode)this.sns.get(k)).allocated = false;
							for(int k=0; k<vns.size(); k++)
								particles[p].position[k].allocated = true;
							SubstrateNode temp = L2S2(vnsInOrder[i]);
							if(temp != null)
								particles[p].position[i] = temp;
						}
						//System.out.print((particles[p].position[i]).getInformation()[0] + " ");
					}
					//System.out.print("\n");
				}
				else {
					for(int i=0; i<vns.size(); i++) {//Re-initialization
						for(int j=0; j<this.sns.size(); j++) //Set all SNs' parameter "allocated" false
							((SubstrateNode)sns.get(j)).allocated = false;
						particles[p].position[i] = L2S2(vnsInOrder[i]);
						particles[p].velocity[i] = rand.nextBoolean();
					}
				}
				//Calculate fitness value
				for(int i=0; i<vns.size(); i++)
					vnsInOrder[i].allocated(particles[p].position[i]); //Set vn's parameter allocate for Dijkstra
				for(int i=0; i<vls.size(); i++) {
					((VirtualLink)vls.get(i)).path.clear();
					if(!Dijkstra((VirtualLink)vls.get(i))) {
						particles[p].fitnessValue = Integer.MAX_VALUE;
						break;
					}
					else {
						particles[p].fitnessValue += ((VirtualLink)vls.get(i)).getBWCost();
					}
				}
				if(particles[p].fitnessValue != Integer.MAX_VALUE) {//All virtual links can be mapped into the substrate network
					if(particles[p].fitnessValue < particles[p].fitnessValue_pBest) {
						particles[p].fitnessValue_pBest = particles[p].fitnessValue;
						for(int k=0; k<vns.size(); k++)
							particles[p].pBest[k] = particles[p].position[k];
					}
					if(particles[p].fitnessValue < fv_gBest) {
						fv_gBest = particles[p].fitnessValue;
						for(int k=0; k<vns.size(); k++)
							gBest[k] = particles[p].position[k];
					}
				}
			}
			
		}
		//************************************************
		//Update the Substrate Network resources
		if(fv_gBest != Integer.MAX_VALUE) {
			for(int i=0; i<vns.size(); i++) {
				System.out.println("VN "+vnsInOrder[i].number+" in VNR "+VNR.number+" is mapped into node"+gBest[i].getInformation()[0]);
				vnsInOrder[i].allocated(gBest[i]);
				gBest[i].addVNList(vnsInOrder[i]);
			}
			for(int i=0; i<vls.size(); i++) {
				((VirtualLink)vls.get(i)).path.clear();
				Dijkstra((VirtualLink)vls.get(i));
				this.substrateNetwork.costBW((VirtualLink)vls.get(i));
			}
		}
		else
			return false;
		return true;
	}
	
	private SubstrateNode L2S2(VirtualNode vn) {
		Vector SNs = new Vector<SubstrateNode>();
		for(int i=0; i<this.sns.size(); i++) {//Choose the substrate nodes that have enough capacity
			SubstrateNode sn = (SubstrateNode)this.sns.get(i);
			if(!sn.allocated && sn.getInformation()[3] >= vn.getRequest() && sn.getTotalBW() != 0) //Two virtual nodes in the same network cannot be mapped into a substrate node
				SNs.add(sn);
		}
		if(SNs.size() == 0)//check if the candidate list is empty
			return null;
		//Calculate the NR value
		int[] NRvalue = new int[SNs.size()];
		float[] prob = new float[SNs.size()];
		float sum = 0;
		for(int j=0; j<SNs.size(); j++) {
			NRvalue[j] = ((SubstrateNode)SNs.get(j)).getInformation()[3]*((SubstrateNode)SNs.get(j)).getTotalBW();
			sum += (float)NRvalue[j];
		}
		//Calculate the prob.
		for(int k=0; k<SNs.size(); k++) {
			prob[k] = ((float)NRvalue[k])/sum;
		}
		//Decide which sn to allocate by the prob.
		Random rand = new Random();
		float r = rand.nextFloat();
		int index = 0;
		float cdf = 0;
		while(true) {
			cdf += prob[index];
			if(r < cdf)
				break;
			index++;
		}
		
		vn.allocated((SubstrateNode)SNs.get(index));   //Mark the sn for following algorithm Dijkstra
		((SubstrateNode)SNs.get(index)).allocated = true;
		return ((SubstrateNode)SNs.get(index));
	}
	
	private boolean Dijkstra(VirtualLink vl) {
		//**************************************************************************
		//Prune the substrate links that don't have enough bandwidth
		int bwRequest = vl.getRequest();
		Vector candidateLinks = new Vector<SubstrateLink>();
		for(int i=0; i<this.sls.size(); i++) {
			SubstrateLink sl = (SubstrateLink)this.sls.get(i);
			if(sl.getInformation()[3] >= bwRequest)
				candidateLinks.add(sl);
		}
		if(candidateLinks.size() == 0)
			return false;
		//**************************************************************************
		for(int i=0; i<this.sns.size(); i++) {
			((SubstrateNode)this.sns.get(i)).initiationForDijkstra();
		}
		SubstrateNode start = vl.getFromAndTo()[0].getMappedInto();
		SubstrateNode goal = vl.getFromAndTo()[1].getMappedInto();
		if(start == null || goal == null)
			return false;
		Vector unsettledSet = new Vector<SubstrateNode>();
		Vector settledSet = new Vector<SubstrateNode>();
		start.hopCount = 0;
		SubstrateNode currentNode = start;
		settledSet.add(currentNode);
		currentNode.settled = true;
		while(settledSet.size() <= this.sns.size()) {
			Vector neighbors = currentNode.getNeighbors();
			Vector links = currentNode.getLinks();
			for(int i=0; i<neighbors.size(); i++) {
				if(vl.getRequest() <= ((SubstrateLink)links.get(i)).getInformation()[3])
					if(currentNode.hopCount + 1 < ((SubstrateNode)neighbors.get(i)).hopCount) {
						((SubstrateNode)neighbors.get(i)).hopCount = currentNode.hopCount + 1;
						((SubstrateNode)neighbors.get(i)).predecessor = currentNode;
					}
			}
			int count = Integer.MAX_VALUE;
			for(int i=0; i<this.sns.size(); i++) { //choose the smallest hop count node
				if(!((SubstrateNode)this.sns.get(i)).settled) {
					if(((SubstrateNode)this.sns.get(i)).hopCount < count) {
						count = ((SubstrateNode)this.sns.get(i)).hopCount;
						currentNode = (SubstrateNode)this.sns.get(i);
					}
				}
			}
			settledSet.add(currentNode);
			currentNode.settled = true;
		}
		//finish finding a path
		//**************************************************************************
		//Trace the path
		SubstrateNode p = goal;
		while(p != null) {
			vl.path.add(p);
			p = p.predecessor;
		}
		if(vl.path.size() <= 1)
			return false;
		return true;
	}
}

class Particle {
	//Every particle has position, velocity, pBest and gBest
	public SubstrateNode[] position;
	public Boolean[] velocity;
	public int fitnessValue;
	public SubstrateNode[] pBest;
	public int fitnessValue_pBest;
	
	public Particle(int amountOfVN) {
		// TODO Auto-generated constructor stub
		this.position = new SubstrateNode[amountOfVN];
		this.velocity = new Boolean[amountOfVN];
		this.fitnessValue = Integer.MAX_VALUE;
		this.pBest = new SubstrateNode[amountOfVN];
		
		Random rand = new Random();
		for(int i=0; i<amountOfVN; i++)
			this.velocity[i] = rand.nextBoolean();
	}
}
