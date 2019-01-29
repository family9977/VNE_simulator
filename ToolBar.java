package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ToolBar extends JToolBar{

	private JButton btnNode;
	private ImageIcon SN;
	private JButton btnLink;
	private ImageIcon SL;
	
	private JButton btnSimulate;
	private ImageIcon simulate;
	
	private SubstrateNetwork substrateNetwork;
	private VirtualNetworkPanel virtualNetwork;
	
	public ToolBar(SubstrateNetwork sn, VirtualNetworkPanel vn) {
		this.setBackground(Color.LIGHT_GRAY);
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.substrateNetwork = sn;
		this.virtualNetwork = vn;
		
		this.addSeparator();
		
		this.simulate = new ImageIcon("simulate.png");
		this.btnSimulate = new JButton(this.simulate);
		this.btnSimulate.setPreferredSize(new Dimension(23,23));
		this.btnSimulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AutoSimulate auto = new AutoSimulate(substrateNetwork, virtualNetwork);
			}
		});
		this.add(btnSimulate);
	}

}

class AutoSimulate extends JDialog{
	
	private Container container;
	
	private JPanel[] jp = new JPanel[9];
	private JTextField[] input = new JTextField[13];
	
	private JButton btnOK = new JButton("OK");
	private JButton btnCancel = new JButton("Cancel");
	
	private SubstrateNetwork substrateNetwork;
	private VirtualNetworkPanel virtualNetworkPanel;
	
	public AutoSimulate(SubstrateNetwork sn, VirtualNetworkPanel vn) {
		this.substrateNetwork = sn;
		this.virtualNetworkPanel = vn;
		this.container = this.getContentPane();
		this.setBounds(400, 200, 600, 475);
		this.setTitle("Simulation Settings");
		this.container.setLayout(new BorderLayout());
		for(int i=0;i<9;i++) {
			this.jp[i] = new JPanel();
			if(i==0)
			{
				this.jp[i].setLayout(new GridLayout(7, 1, 2, 2));
				this.container.add(this.jp[i], BorderLayout.CENTER);
			}
			else if(i!=8) {
				this.jp[i].setLayout(new FlowLayout());
				this.jp[0].add(this.jp[i]);
			}
			else
				this.container.add(this.jp[i], BorderLayout.SOUTH);
		}
		for(int i=0; i<13; i++) {
			this.input[i] = new JTextField(5);
		}
		//SubstrateNetwork Settings (jp[1] & jp[2])
		this.jp[1].add(new JLabel("SubstrateNetwork:  "));
		this.jp[1].add(this.input[0]);
		this.jp[1].add(new JLabel("nodes  "));
		this.jp[2].add(new JLabel("CPU: "));
		this.jp[2].add(this.input[1]);
		this.jp[2].add(new JLabel(" ~ "));
		this.jp[2].add(this.input[2]);
		this.jp[2].add(new JLabel("   Bandwidth: "));
		this.jp[2].add(this.input[3]);
		this.jp[2].add(new JLabel(" ~ "));
		this.jp[2].add(this.input[4]);
		
		//VirtualNetworkRequest Settings (jp[3] & jp[4])
		this.jp[3].add(new JLabel("VirtualNetworkRequest:  "));
		this.jp[3].add(this.input[5]);
		this.jp[3].add(new JLabel(" nodes ~ "));
		this.jp[3].add(this.input[6]);
		this.jp[3].add(new JLabel(" nodes"));
		this.jp[4].add(new JLabel("CPU Request: "));
		this.jp[4].add(this.input[7]);
		this.jp[4].add(new JLabel(" ~ "));
		this.jp[4].add(this.input[8]);
		this.jp[4].add(new JLabel("   Bandwidth Request: "));
		this.jp[4].add(this.input[9]);
		this.jp[4].add(new JLabel(" ~ "));
		this.jp[4].add(this.input[10]);
		
		//Time duration & Arrival Rate Settings (jp[5] & jp[6])
		this.jp[5].add(new JLabel("Time Duration (unit):  "));
		this.jp[5].add(this.input[11]);
		this.jp[6].add(new JLabel("Arrival Rate (per 100 units):  "));
		this.jp[6].add(this.input[12]);
		
		//Algorithm choice
		this.jp[7].add(new JLabel("Algorithm:  "));
		String[] Algorithm = {"Greedy","Particle Swarm Optimization","Load Balance"};
		JComboBox AlgorithmChoice = new JComboBox(Algorithm);
		this.jp[7].add(AlgorithmChoice);
		
		this.btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//RandomGraphGenerator snRGG = new RandomGraphGenerator(substrateNetwork, 5, 5, 50, 10, 20);
				
				/*RandomGraphGenerator rgg = new RandomGraphGenerator(virtualNetworkPanel, 2, 10, 5, 20, 20, 30);
				rgg.generateVNR(0, 100);
				rgg.generateVNR(10, 120);*/
				boolean blank = false;
				int[] parameters = new int[13];
				for(int i=0; i<13; i++) {
					if(input[i].getText().length() != 0)
						parameters[i] = Integer.valueOf(input[i].getText());
					else
						blank = true;
				}
				
				substrateNetwork.initiation();
				virtualNetworkPanel.initiation();
				
				SimulationProcess sp = new SimulationProcess(substrateNetwork, virtualNetworkPanel, parameters);
				sp.mainProcess();
				//SimulationProcess sp = new SimulationProcess(substrateNetwork, virtualNetworkPanel);
				//sp.Process();
				dispose();
				/*
				if(!blank) {
					SimulationProcess sp = new SimulationProcess(substrateNetwork, virtualNetworkPanel, parameters);
					dispose();
				}*/
			}
		});
		this.jp[8].setLayout(new FlowLayout(FlowLayout.RIGHT));
		this.jp[8].add(this.btnOK);
		this.btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		this.jp[8].add(this.btnCancel);
		
		this.setModal(true);
		this.setVisible(true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
