package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComponent;

public class SubstrateNetwork extends JComponent implements MouseListener,MouseMotionListener{
	
	private Vector SNs = new Vector<SubstrateNode>();
	private Vector SLs = new Vector<SubstrateLink>();
	
	private int nodeCount = 0;
	private int linkCount = 0;
	//Parameters for painting
	private Graphics2D g2;
	private static Font font;
	private static Font font2;
	
	private SubstrateNode currentNode;
	
	private int[][] connections;
	
	public SubstrateNetwork() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.g2 = (Graphics2D) g;
		
		for(int i=0; i<this.SNs.size(); i++) {
			SubstrateNode currentNode = (SubstrateNode)this.SNs.get(i);
			g.setColor(Color.RED);
			g.fillOval(currentNode.X-10, currentNode.Y-10, 20, 20);
			g.drawString(Integer.toString(currentNode.getInformation()[0]), currentNode.X-5, currentNode.Y-10);
		}
		for(int j=0; j<this.SLs.size(); j++) {
			SubstrateLink currentLink = (SubstrateLink)this.SLs.get(j);
			SubstrateNode from = (SubstrateNode)currentLink.getFromAndTo()[0];
			SubstrateNode to = (SubstrateNode)currentLink.getFromAndTo()[1];
			g.setColor(Color.RED);
			g.drawLine(from.X, from.Y, to.X, to.Y);
		}
	}
	
	public void addSN(int capacity) {
		//System.out.println("nodecount: "+this.nodeCount);
		this.SNs.add(new SubstrateNode(this.nodeCount, capacity));
		this.nodeCount++;
	}
	
	public void addSL(int from, int to, int bwr) {
		SubstrateNode f = (SubstrateNode)this.SNs.get(from);
		SubstrateNode t = (SubstrateNode)this.SNs.get(to);
		this.SLs.add(new SubstrateLink(this.linkCount, f, t, bwr));
		this.linkCount++;
		System.out.println(bwr);
		//System.out.println("SL info: ("+from+","+to+") BW: "+bwr+" linksize: "+this.SLs.size());
		//update node's information
		f.addNeighbors(t);
		f.addLink((SubstrateLink)this.SLs.get(this.SLs.size()-1));
		t.addNeighbors(f);
		t.addLink((SubstrateLink)this.SLs.get(this.SLs.size()-1));
	}
	
	public void saveConnectionMatrix(int[][] m) {
		this.connections = m;
	}
	
	public Vector getSNs() {
		return this.SNs;
	}
	
	public Vector getSLs(){
		return this.SLs;
	}
	
	public void localization() {
		Random rand = new Random();
		for(int i=0; i<this.SNs.size(); i++) {
			SubstrateNode sn = (SubstrateNode)this.SNs.get(i);
			sn.X = 10+rand.nextInt(770);
			sn.Y = 10+rand.nextInt(670);
			repaint();
		}
	}

	public void initiation() {
		this.nodeCount = 0;
		this.linkCount = 0;
	}
	
	public void costBW(VirtualLink vl) {
		Vector list = vl.path;
		SubstrateNode f, t;
		for(int i=0; i<list.size()-1; i++) {
			int j=i+1;
			if(((SubstrateNode)list.get(i)).getInformation()[0] < ((SubstrateNode)list.get(j)).getInformation()[0]) {
				f = (SubstrateNode)list.get(i);
				t = (SubstrateNode)list.get(j);
			}
			else {
				f = (SubstrateNode)list.get(j);
				t = (SubstrateNode)list.get(i);
			}
			for(int k=0; k<this.SLs.size(); k++) {
				SubstrateLink sl = (SubstrateLink)this.SLs.get(k);
				if(sl.getFromAndTo()[0] == f && sl.getFromAndTo()[1] == t) {
					System.out.print("From Node "+f.getInformation()[0]+" To Node "+t.getInformation()[0]+" residualBW: "+sl.getInformation()[3]);
					sl.updateBandwidth(vl.getRequest());
					System.out.print(" After allocation: "+sl.getInformation()[3]);
				}
			}
			System.out.println("");
		}
	}
	
	public void getBackBWResource(VirtualLink vl) {
		if(vl.path.size() > 1) {
			Vector list = vl.path;
			SubstrateNode f, t;
			for(int i=0; i<list.size()-1; i++) {
				int j=i+1;
				if(((SubstrateNode)list.get(i)).getInformation()[0] < ((SubstrateNode)list.get(j)).getInformation()[0]) {
					f = (SubstrateNode)list.get(i);
					t = (SubstrateNode)list.get(j);
				}
				else {
					f = (SubstrateNode)list.get(j);
					t = (SubstrateNode)list.get(i);
				}
				for(int k=0; k<this.SLs.size(); k++) {
					SubstrateLink sl = (SubstrateLink)this.SLs.get(k);
					if(sl.getFromAndTo()[0] == f && sl.getFromAndTo()[1] == t) {
						System.out.print("From Node "+f.getInformation()[0]+" To Node "+t.getInformation()[0]+" residualBW: "+sl.getInformation()[3]);
						sl.updateBandwidth(-vl.getRequest());
						System.out.print(" After getBack: "+sl.getInformation()[3]);
					}
				}
				System.out.println("");
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		for(int i=0; i<this.SNs.size(); i++) {
			SubstrateNode sn = (SubstrateNode)this.SNs.get(i);
			if(sn.X-10 < e.getX() && sn.X+10 > e.getX() && sn.Y-10 < e.getY() && sn.Y+10 > e.getY())
				this.currentNode = sn;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(this.currentNode != null)
			this.currentNode = null;
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(this.currentNode != null) {
			this.currentNode.X = arg0.getX();
			this.currentNode.Y = arg0.getY();
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}

class SubstrateNode{
	
	private int number;
	private int totalCapacity;
	private int usedCapacity;
	private int residualCapacity;
	private double load;
	
	private Vector neighbors = new Vector<SubstrateNode>();
	private Vector links = new Vector<SubstrateLink>();
	
	private Vector vnList = new Vector<VirtualNode>();
	
	public int X,Y;
	
	public SubstrateNode predecessor;
	public int hopCount;
	public boolean settled;
	public boolean inUnsettledSet;
	//Parameters for PSO
	public boolean allocated;
	
	public SubstrateNode(int n) {
		this.number = n;
		this.totalCapacity = 0;
		this.usedCapacity = 0;
		this.residualCapacity = 0;
	}
	
	public SubstrateNode(int n, int c) {
		this.number = n;
		this.totalCapacity = c;
		this.usedCapacity = 0;
		this.residualCapacity = c;
	}
	
	public int[] getInformation() {
		int[] info = new int[5];
		info[0] = this.number;
		info[1] = this.totalCapacity;
		info[2] = this.usedCapacity;
		info[3] = this.residualCapacity;
		info[4] = (int)(this.load*100);
		
		return info;
	}
	
	public void updateNumber(int n) {
		this.number = n;
	}
	//Input new amount of memory being used
	public void updateCapacity(int c) {
		this.residualCapacity -= c;
		this.usedCapacity += c;
		updateLoad();
	}
	
	private void updateLoad() {
		this.load = (double)(this.usedCapacity)/(double)(this.totalCapacity);
	}
	
	public double getLoad() {
		return this.load;
	}
	
	public void addNeighbors(SubstrateNode neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public Vector getNeighbors() {
		return this.neighbors;
	}
	
	public void addLink(SubstrateLink link) {
		this.links.add(link);
	}
	
	public Vector getLinks() {
		return this.links;
	}
	
	public void addVNList(VirtualNode vn) {
		this.vnList.add(vn);
		System.out.print("Node: "+this.number+"  residual CPU: "+this.residualCapacity+"  VN request: "+vn.getRequest());
		this.updateCapacity(vn.getRequest());
		System.out.println("  After allocation: "+this.residualCapacity+"   VNListSize:"+this.vnList.size());
	}
	
	public void deleteVN(VirtualNode vn) {
		int i=0;
		while(true) {
			if((VirtualNode)this.vnList.get(i) == vn) {
				System.out.print("Now Residual:"+this.residualCapacity);
				updateCapacity(-vn.getRequest());
				this.vnList.remove(i);
				System.out.println("   After getback:"+this.residualCapacity+"   VNListSize:"+this.vnList.size());
				break;
			}
			i++;
			if(i >= this.vnList.size()) {
				System.out.println("Not found");
				//break;
			}
		}
	}
	
	public int getTotalBW() {
		int sum = 0;
		SubstrateLink sl;
		for(int i=0; i<this.links.size() ;i++) {
			sl = (SubstrateLink)this.links.get(i);
			sum += sl.getInformation()[3];
		}
		return sum;
	}
	
	public void initiationForDijkstra() {
		this.predecessor = null;
		this.hopCount = Integer.MAX_VALUE;
		this.settled = false;
		this.inUnsettledSet = false;
	}
}

class SubstrateLink{
	
	private int number;
	private int totalBandwidth;
	private int usedBandwidth;
	private int residualBandwidth;
	private SubstrateNode from, to;
	
	public SubstrateLink(int n, SubstrateNode f, SubstrateNode t) {
		this.number = n;
		this.totalBandwidth = 0;
		this.usedBandwidth = 0;
		this.residualBandwidth = 0;
		this.from = f;
		this.to = t;
	}
	
	public SubstrateLink(int n, SubstrateNode f, SubstrateNode t, int b) {
		this.number = n;
		this.totalBandwidth = b;
		this.usedBandwidth = 0;
		this.residualBandwidth = b;
		this.from = f;
		this.to = t;
	}
	
	public int[] getInformation() {
		int[] info = new int[4];
		info[0] = this.number;
		info[1] = this.totalBandwidth;
		info[2] = this.usedBandwidth;
		info[3] = this.residualBandwidth;
		return info;
	}
	public SubstrateNode[] getFromAndTo() {
		SubstrateNode[] info = new SubstrateNode[2];
		info[0] = this.from;
		info[1] = this.to;
		return info;
	}
	
	public void updateNumber(int n) {
		this.number = n;
	}
	
	public void updateBandwidth(int b) {
		this.residualBandwidth -= b;
		this.usedBandwidth += b;
	}

	public double getLoad() {
		return (double)this.usedBandwidth/(double)this.totalBandwidth;
	}
}