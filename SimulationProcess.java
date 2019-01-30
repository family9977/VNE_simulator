package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

public class SimulationProcess {

	private SubstrateNetwork sn;
	private VirtualNetworkPanel vnp;
	private int time = 1000;
	private int algorithmChoice;
	private double arrivalRate;
	private double[] ratio = new double[time];
	private double[] nodeLoad = new double[time];
	private double[] nodeSD = new double[time];
	private double[] linkLoad = new double[time];
	private double[] linkSD = new double[time];
	private int totalRequests;
	private int reject;
	private int[] settings;
	/*
	 * [0] = SubstrateNode Number
	 * [1] = SubstrateNode CPU Lower Bound
	 * [2] = SubstrateNode CPU Upper Bound
	 * [3] = SubstrateLink Bandwidth Lower Bound
	 * [4] = SubstrateLink Bandwidth Upper Bound
	 * [5] = VirtualNode Number Lower Bound
	 * [6] = VirtualNode Number Upper Bound
	 * [7] = VirtualNode CPU Request Lower Bound
	 * [8] = VirtualNode CPU Request Upper Bound
	 * [9] = VirtualLink Bandwidth Request LowerBound
	 * [10] = VirtualLink Bandwidth Request Upper Bound
	 * [11] = Total Time
	 * [12] = VNR Arrival Rate
	 */
	private int timewindow;
	private Algorithm algorithm;
	
	private Random rand;
	
	private RandomGraphGenerator GenerateSubstrateNetwork;
	private RandomGraphGenerator GenerateVirtualNetwork;
	
	public SimulationProcess(SubstrateNetwork s, VirtualNetworkPanel v, int[] info, int c) {
		// TODO Auto-generated constructor stub
		this.sn = s;
		this.vnp = v;
		this.settings = info;
		this.rand = new Random();
		this.algorithm = new Algorithm(s, v);
		this.reject = 0;
		this.totalRequests = 0;
		this.time = info[11];
		this.arrivalRate = ((double)info[12])/100;
		this.algorithmChoice = c;
		
		this.GenerateSubstrateNetwork = new RandomGraphGenerator(this.sn, this.settings[0],  this.settings[1], this.settings[2], this.settings[3], this.settings[4]);
		this.GenerateVirtualNetwork = new RandomGraphGenerator(this.vnp, this.settings[5], this.settings[6], this.settings[7], this.settings[8], this.settings[9], this.settings[10]);
	}	
	
	public void mainProcess() {
		int nextTime = 1;
		int cumlativeBWUsage = 0;
		int totalVNRBW = 0;
		for(int i=0; i<this.time; i++) {
			nextTime--;
			this.vnp.deleteVNR(i,this.sn);
			while(nextTime == 0) {
				nextTime = (int)nextArrival(this.arrivalRate);
				if(i != 0) {
					int lifetime = 200 + rand.nextInt(100);
					System.out.println(i + " " + lifetime);
					this.GenerateVirtualNetwork.generateVNR(i, lifetime);
					this.totalRequests++;
					//Use one algorithm to check if the newest VNR can be served
					if(this.algorithmChoice == 0) {
						if(!this.algorithm.Greedy((VirtualNetwork)this.vnp.getVNRs().get(this.vnp.getVNRs().size()-1))) {
							System.out.println("Reject");
							this.vnp.getVNRs().remove(this.vnp.getVNRs().size()-1);
							this.reject++;
						}
					}
					else {
						if(!this.algorithm.ParticleSwarm((VirtualNetwork)this.vnp.getVNRs().get(this.vnp.getVNRs().size()-1))) {
							System.out.println("Reject");
							this.vnp.getVNRs().remove(this.vnp.getVNRs().size()-1);
							this.reject++;
						}
					}
					Vector vls = ((VirtualNetwork)this.vnp.getVNRs().get(this.vnp.getVNRs().size()-1)).getVLs();
					for(int v=0; v<vls.size(); v++) {
						totalVNRBW += ((VirtualLink)vls.get(v)).getRequest();
					}
				}
			}
			if(this.totalRequests != 0) {
				this.ratio[i] = (double)(this.totalRequests-this.reject)/(double)(this.totalRequests) * 100;
			}
			else
				this.ratio[i] = 0;
		}
		new ShowAcceptionRate(this.ratio);
	}
	
	public double nextArrival(double lamda) {
		return  Math.log(1-rand.nextDouble())/(-lamda);
	}

	public double[] calculateMeanAndSD(double[] loads) {
		double[] info = new double[2];//  [0] is mean, [1] is variance
		//Calculate mean
		double sum = 0;
		for(int i=0; i<loads.length; i++)
			sum += loads[i];
		info[0] = sum/(double)loads.length;
		//Calculate variance
		sum = 0;
		for(int i=0; i<loads.length; i++)
			sum += Math.pow(loads[i] - info[0], 2);
		info[1] = Math.sqrt(sum/(double)loads.length);
		return info;
	}
	
	public void readSNFile() throws IOException {
		FileReader f = new FileReader("SN_20181207_1.txt");
		BufferedReader br = new BufferedReader(f); 
		  
		this.sn.getSNs().clear();
		this.sn.getSLs().clear();
		String st; 
		st = br.readLine();
		int nodeNumber = Integer.parseInt(st);
		for(int i=0; i<nodeNumber; i++) {
			st = br.readLine();
			this.sn.addSN(Integer.parseInt(st));
		}
		
		int[][] LinkState = new int[nodeNumber][nodeNumber];
		for(int i=0; i<nodeNumber; i++) {
			st = br.readLine();
			String[] linkstate = st.split(" ");
			for(int j=0; j<linkstate.length; j++)
				LinkState[i][j] = Integer.parseInt(linkstate[j]);
		}
		
		for(int i=0; i<nodeNumber; i++) {
			for(int j=i+1; j<nodeNumber; j++) {
				if(LinkState[i][j] == 1) {
					st = br.readLine();
					this.sn.addSL(i, j, Integer.parseInt(st));
				}
			}
		}
		this.sn.localization();
	}
	
	public void readVNFile() throws IOException {
		FileReader f = new FileReader("VN_strategy1_20181207_1.txt");
		BufferedReader br = new BufferedReader(f); 
		this.vnp.getVNRs().clear();
		
		String st;
		while(true) {
			st = br.readLine();
			if(st == null)
				break;
			String[] time = st.split(" ");
			System.out.println(time[0]+" "+time[1]);
			this.vnp.addVNR(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
			VirtualNetwork VNR = (VirtualNetwork)(this.vnp.getVNRs().get(this.vnp.getVNRs().size()-1));
			st = br.readLine();
			int nodeNumber = Integer.parseInt(st);
			for(int i=0; i<nodeNumber; i++) {
				st = br.readLine();
				VNR.addVN(Integer.parseInt(st));
			}
			
			int[][] LinkState = new int[nodeNumber][nodeNumber];
			for(int i=0; i<nodeNumber; i++) {
				st = br.readLine();
				String[] linkstate = st.split(" ");
				for(int j=0; j<linkstate.length; j++)
					LinkState[i][j] = Integer.parseInt(linkstate[j]);
			}
			
			for(int i=0; i<nodeNumber; i++) {
				for(int j=i+1; j<nodeNumber; j++) {
					if(LinkState[i][j] == 1) {
						st = br.readLine();
						VNR.addVL(i, j, Integer.parseInt(st));
					}
				}
			}
			VNR.localization();
		}
	}
}
class ShowAcceptionRate extends JFrame implements MouseMotionListener{
	
	private double[] ratio;
	private int length;
	private int indicatorX, indicatorY;
	
	public ShowAcceptionRate(double[] r) {
		this.ratio = r;
		this.length = r.length;
		this.setTitle("Acception Ratio");
		this.setSize(800, 600);
		this.setVisible(true);
		this.addMouseMotionListener(this);
		this.setAlwaysOnTop(true);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.BLACK);
		g.drawLine(50, 50, 50, 550);
		g.drawLine(50, 550, 750, 550);
		
		for(int i=0; i<this.length; i++) {
			int x = 50 + (int)((double)700/(double)this.length * i);
			int y = 550 - (int)(this.ratio[i]*5);
			g.setColor(Color.RED);
			g.fillOval(x-2, y-2, 4, 4);
			
			if(this.indicatorX > x-(double)700/(double)this.length/2 && this.indicatorX < x+(double)700/(double)this.length/2) {
				g.setColor(Color.BLUE);
				g.drawLine(x, 0, x, this.getHeight());
				g.drawString(Double.toString(this.ratio[i])+"%", x, y-10);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		this.indicatorX = e.getX();
		this.indicatorY = e.getY();
		repaint();
	}
	
}

class ShowLoad extends JFrame implements MouseMotionListener{
	//This class can show the load of nodes and links
	double[] nodes;
	double[] links;
	int length;
	
	public ShowLoad(double[] ns, double[] ls) {
		this.nodes = ns;
		this.links = ls;
		this.length = ns.length;
		this.setTitle("Acception Ratio");
		this.setSize(800, 600);
		this.setVisible(true);
		this.addMouseMotionListener(this);
		this.setAlwaysOnTop(true);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		g.setColor(Color.BLACK);
		g.drawLine(50, 50, 50, 550);
		g.drawLine(50, 550, 750, 550);
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}