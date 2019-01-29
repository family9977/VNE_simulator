package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class VirtualNetworkPanel extends JPanel{
	
	private JScrollPane VNRArea = new JScrollPane();
	private JPanel backgroundPanel;
	private Vector VNRs = new Vector<VirtualNetwork>();
	
	private int VNRCount = 0;

	public VirtualNetworkPanel() {
		// TODO Auto-generated constructor stub
		this.setPreferredSize(new Dimension(220,768));
		this.setLayout(new BorderLayout());
		this.add(this.VNRArea,BorderLayout.CENTER);
		this.backgroundPanel = new JPanel();
	}

	public void drawPanel() {
		this.backgroundPanel.removeAll();
		this.backgroundPanel.setLayout(new BoxLayout(this.backgroundPanel, BoxLayout.Y_AXIS));
		for(int i=0; i<this.VNRs.size(); i++) {
			this.backgroundPanel.add((JPanel)this.VNRs.get(i));
		}
		this.VNRArea.setViewportView(this.backgroundPanel);
		JScrollBar bar = this.VNRArea.getVerticalScrollBar();
		bar.setUnitIncrement(40);
	}
	
	public void addVNR(int st, int lt) {
		this.VNRs.add(new VirtualNetwork(this.VNRCount, st, lt));
		//System.out.println("vnrcount: "+this.VNRCount);
		this.VNRCount++;
		drawPanel();
	}
	
	public Vector getVNRs() {
		return this.VNRs;
	}
	
	public void deleteVNR(int time, SubstrateNetwork sn) {
		for(int i=this.VNRs.size()-1; i>=0; i--) {
			VirtualNetwork vnr = (VirtualNetwork)this.VNRs.get(i);
			if(vnr.getEndTime() == time && !vnr.rejected) {
				System.out.println("VNR "+vnr.number+" is being deleted.");
				vnr.deleted =true;
				vnr.setBackground(Color.ORANGE);
				Vector vls = vnr.releaseResource();
				for(int j=0; j<vls.size(); j++) {
					VirtualLink vl = (VirtualLink)vls.get(j);
					sn.getBackBWResource(vl);
				}
			}
		}
	}
	
	public void initiation() {
		this.VNRCount = 0;
	}
}
