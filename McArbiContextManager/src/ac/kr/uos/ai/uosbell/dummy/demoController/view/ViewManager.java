package ac.kr.uos.ai.uosbell.dummy.demoController.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewManager {

	private JFrame frame;
	private JPanel panel;
	private JButton PrepareStoringStation18;
	private JButton PrepareStoringStation19;
	private JButton StoringStation18;
	private JButton StoringStation19;
	private JButton PrepareUnstoringStation22;
	private JButton UnstoringStation22;
	private JButton MultiRobotDemo;
	
	public ViewManager(ActionListener listner) {
		PrepareStoringStation18 = new JButton("PrepareStoring at Station18");		
		PrepareStoringStation19 = new JButton("PrepareStoring at Station19");
		StoringStation18 = new JButton("Storing at Station18");
		StoringStation19 = new JButton("Storing at Station19");
		PrepareUnstoringStation22 = new JButton("PrepareUnstoring at Station22");
		UnstoringStation22 = new JButton("Unstoring at Station22");
		MultiRobotDemo =  new JButton("Multi Robot Demo");

		PrepareStoringStation18.setFont(new Font("Arial", Font.BOLD, 18));
		PrepareStoringStation19.setFont(new Font("Arial", Font.BOLD, 18));
		StoringStation18.setFont(new Font("Arial", Font.BOLD, 18));
		StoringStation19.setFont(new Font("Arial", Font.BOLD, 18));
		PrepareUnstoringStation22.setFont(new Font("Arial", Font.BOLD, 18));
		UnstoringStation22.setFont(new Font("Arial", Font.BOLD, 18));
		MultiRobotDemo.setFont(new Font("Arial", Font.BOLD, 18));
		
		PrepareStoringStation18.addActionListener(listner);
		PrepareStoringStation19.addActionListener(listner);
		StoringStation18.addActionListener(listner);
		StoringStation19.addActionListener(listner);
		PrepareUnstoringStation22.addActionListener(listner);
		UnstoringStation22.addActionListener(listner);
		MultiRobotDemo.addActionListener(listner);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(3, 3));
		panel.add(PrepareStoringStation18);
		panel.add(PrepareStoringStation19);
		panel.add(StoringStation18);
		panel.add(StoringStation19);
		panel.add(PrepareUnstoringStation22);
		panel.add(UnstoringStation22);
		panel.add(MultiRobotDemo);

		frame = new JFrame("DemoController");
		frame.setSize(new Dimension(1000, 300));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(panel);
		}
	public void showFrame() {
		frame.setVisible(true);
	}
	public static void main(String[] args) {
		ViewManager view = new ViewManager(null);
		view.showFrame();
	}
}
