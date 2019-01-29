package main;

import java.awt.*;
import javax.swing.*;

public class Main {

	static MainFrame mainFrame;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		mainFrame = new MainFrame("VNE Simulator");
		mainFrame.setSize(1024, 768);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public static class MainFrame extends JFrame{
		
		private Container container; 
		private JComponent backgroudPanel = new JPanel();
		private SubstrateNetwork snCanvas = new SubstrateNetwork();
		private VirtualNetworkPanel vnPanel = new VirtualNetworkPanel();
		private ToolBar toolBar = new ToolBar(snCanvas, vnPanel);
		
		public MainFrame(String s) {
			container=this.getContentPane();
			container.setBackground(Color.WHITE);
			
			this.setTitle(s);
			
			this.backgroudPanel.setLayout(new BorderLayout());
			this.backgroudPanel.setBackground(Color.WHITE);
			this.backgroudPanel.add(this.snCanvas,BorderLayout.CENTER);
			this.backgroudPanel.add(this.vnPanel,BorderLayout.EAST);
			this.container.add(this.backgroudPanel);
			
			this.container.add(toolBar,BorderLayout.NORTH);
		}
	}
}


