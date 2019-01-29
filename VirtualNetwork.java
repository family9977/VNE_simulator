package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class VirtualNetwork extends JPanel implements MouseListener,MouseMotionListener{

	public int number;
	private int lifetime;
	private double revenue;
	private int startTime, endTime;
	private int nodeCount = 0;
	private int linkCount = 0;
	public boolean deleted = false;
	public boolean rejected = false;
	
	private Vector VNs = new Vector<VirtualNode>();
	private Vector VLs = new Vector<VirtualLink>();
	
	private JPanel labelPanel = new JPanel();
	private Graphics2D g2;
	private static Font font;
	private static Font font2;
	private VirtualNode currentNode;
	
	private int[][] connections;
	
	public VirtualNetwork(int n, int s, int l) {
		// TODO Auto-generated constructor stub
		this.setPreferredSize(new Dimension(200, 220));
		this.setMaximumSize(getPreferredSize());
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(new BorderLayout());
		
		this.number = n;
		this.startTime = s;
		this.lifetime = l;
		this.endTime = s+l;
		
		this.labelPanel.setBackground(Color.CYAN);
		this.labelPanel.add(new JLabel("VNR"+(this.number+1)));
		this.add(this.labelPanel,BorderLayout.SOUTH);
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		this.g2 = (Graphics2D) g;
		
		for(int i=0; i<this.VNs.size(); i++) {
			VirtualNode currentNode = (VirtualNode)this.VNs.get(i);
			g.setColor(Color.BLUE);
			g.fillOval(currentNode.X-10, currentNode.Y-10, 20, 20);
		}
		for(int j=0; j<this.VLs.size(); j++) {
			VirtualLink currentLink = (VirtualLink)this.VLs.get(j);
			VirtualNode from = (VirtualNode)currentLink.getFromAndTo()[0];
			VirtualNode to = (VirtualNode)currentLink.getFromAndTo()[1];
			g.setColor(Color.BLUE);
			g.drawLine(from.X, from.Y, to.X, to.Y);
		}
	}
	
	/*public void addVN() {
		count++;
		this.VNs.add(new VirtualNode(count));
	}*/
	public void addVN(int r) {
		System.out.println(/*"request: "+*/r);
		this.VNs.add(new VirtualNode(this.nodeCount, r));
		this.nodeCount++;
	}
	
	public void addVL(int from, int to, int r) {
		VirtualNode f = (VirtualNode)this.VNs.get(from);
		VirtualNode t = (VirtualNode)this.VNs.get(to);
		this.VLs.add(new VirtualLink(this.linkCount, f, t, r));
		this.linkCount++;
		//System.out.println("VL info: ("+from+","+to+")  BWR: "+r+"  linksize: "+this.VLs.size());
		System.out.println(r);
		f.addNeighbors(t);
		f.addLink((VirtualLink)this.VLs.get(this.VLs.size()-1));
		t.addNeighbors(f);
		t.addLink((VirtualLink)this.VLs.get(this.VLs.size()-1));
		//revenueCalculation();
	}
	
	public void saveConnectionMatrix(int[][] m) {
		this.connections = m;
	}
	
	public void localization() {
		Random rand = new Random();
		for(int i=0; i<this.VNs.size(); i++) {
			VirtualNode vn = (VirtualNode)this.VNs.get(i);
			vn.X = 10+rand.nextInt(180);
			vn.Y = 10+rand.nextInt(170);
			repaint();
		}
	}
	
	public void revenueCalculation() {
		/*
		 * Equation depends on papers
		 * */
		int bwRevenue = 0;
		int CPURevenue = 0;
		double alpha = 1;
		double beta = 1;
		for(int i=0; i<this.VNs.size(); i++)
			bwRevenue += ((VirtualNode)this.VNs.get(i)).getRequest();
		for(int j=0; j<this.VLs.size(); j++)
			CPURevenue += ((VirtualLink)this.VLs.get(j)).getRequest();
		this.revenue = alpha*(double)CPURevenue + beta*(double)bwRevenue;
	}
	
	public double getRevenue() {
		return this.revenue;
	}

	public int getStartTime() {
		return this.startTime;
	}
	
	public int getEndTime() {
		return this.endTime;
	}
	
	public Vector getVNs() {
		return this.VNs;
	}
	
	public Vector getVLs() {
		return this.VLs;
	}
	
	public Vector releaseResource() {
		for(int i=0; i<this.VNs.size(); i++) {
			VirtualNode vn = (VirtualNode)this.VNs.get(i);
			System.out.println("Deleted from "+vn.getMappedInto().getInformation()[0]);
			vn.getMappedInto().deleteVN(vn);
		}
		return this.VLs;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(this.currentNode != null) {
			this.currentNode.X = e.getX();
			this.currentNode.Y = e.getY();
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		for(int i=0; i<this.VNs.size(); i++) {
			VirtualNode vn = (VirtualNode)this.VNs.get(i);
			if(vn.X-10 < arg0.getX() && vn.X+10 > arg0.getX() && vn.Y-10 < arg0.getY() && vn.Y+10 > arg0.getY())
				this.currentNode = vn;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(this.currentNode != null)
			this.currentNode = null;
	}
}

class VirtualNode{
	
	public int number;
	private int capacityRequest;
	
	private Vector neighbors = new Vector<VirtualNode>();
	private Vector links = new Vector<VirtualLink>();
	
	private SubstrateNode mappedInto;
	
	public int X, Y;
	
	public VirtualNode(int n) {
		this.number = n;
		this.capacityRequest = 0;
	}
	
	public VirtualNode(int n, int r) {
		this.number = n;
		this.capacityRequest = r;
	}
	
	public void inputRequest(int r) {
		this.capacityRequest = r;
	}
	public int getRequest() {
		return this.capacityRequest;
	}
	
	public void addNeighbors(VirtualNode neighbor) {
		this.neighbors.add(neighbor);
	}
	
	public void addLink(VirtualLink link) {
		this.links.add(link);
	}
	
	public int getTotalBW() {
		int sum = 0;
		VirtualLink vl;
		for(int i=0; i<this.links.size() ;i++) {
			vl = (VirtualLink)this.links.get(i);
			sum += vl.getInformation()[1];
		}
		return sum;
	}
	
	public void allocated(SubstrateNode sn) {
		this.mappedInto = sn;
	}
	
	public SubstrateNode getMappedInto() {
		return this.mappedInto;
	}
}

class VirtualLink{
	
	private int number;
	private int bandwidthRequest;
	private VirtualNode from, to; // from < to
	
	public Vector path = new Vector<SubstrateNode>();
	
	public VirtualLink(int n, VirtualNode f, VirtualNode t) {
		this.number = n;
		this.bandwidthRequest = 0;
		this.from = f;
		this.to = t;
	}
	
	public VirtualLink(int n, VirtualNode f, VirtualNode t, int b) {
		this.number = n;
		this.bandwidthRequest = b;
		this.from = f;
		this.to = t;
	}
	
	public void inputRequest(int b) {
		this.bandwidthRequest = b;
	}
	
	public int getRequest() {
		return this.bandwidthRequest;
	}
	
	public int[] getInformation() {
		int[] info = new int[2];
		info[0] = this.number;
		info[1] = this.bandwidthRequest;
		return info;
	}
	
	public VirtualNode[] getFromAndTo() {
		VirtualNode[] info = new VirtualNode[2];
		info[0] = this.from;
		info[1] = this.to;
		return info;
	}
	
	public int getBWCost() {
		if(this.path.size()==0)
			return 0;
		return this.bandwidthRequest*(this.path.size()-1);
	}
}
