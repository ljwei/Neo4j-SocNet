package display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.neo4j.graphdb.Transaction;

import socnet.Person;
import socnet.StatusUpdate;

public class MyComponent6 extends JPanel {
	private JTextField inputField;
	private JTextField inputField2;
	private int amount = 20;
	private JTextArea outputArea;
	public MyComponent6() {
		
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		
		JLabel inputL = new JLabel("请输入用户名：");
		inputField = new JTextField("", 6);
		JLabel inputL2 = new JLabel("要推荐的好友数量:");
		inputField2 = new JTextField("", 6);
		JButton runButton = new JButton("查询");
		runButton.addActionListener(runListener);
		
		panelUp.add(inputL);
		panelUp.add(inputField);
		panelUp.add(inputL2);
		panelUp.add(inputField2);
		panelUp.add(runButton);
		
		JLabel output = new JLabel("输出:");
		outputArea = new JTextArea(30, 63);
		outputArea.setLineWrap(true);
		outputArea.setWrapStyleWord(true);
		
		JScrollPane scrollPane = 
		    new JScrollPane(outputArea,
		                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
		                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		outputArea.setEditable(false);
		
		panelDown.add(output);
		panelDown.add(scrollPane);
		
		setLayout(new BorderLayout());
		add(panelUp, BorderLayout.NORTH);
		add(panelDown, BorderLayout.CENTER);
	}
	
    
	ActionListener runListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String input = inputField.getText().trim();
			amount = Integer.parseInt(inputField2.getText().trim());
			
	        Person p = Main.pr.getPersonByName(input);
	        outputArea.append("根据你好友的好友对你进行的推荐\n");
	        if (p == null) {
	        	outputArea.append("用户名" + input + "不存在" + "\n\n");
	        } else {
		        outputArea.append("用户名" + "\t\t" + "关注人数" + "\t\t" + "粉丝数" + "\n");	  
		        int count = 0;
		        try ( Transaction tx = Main.ctn.getGraphDb().beginTx() )
		        {
		        	for (Person friend : p.getFriendRecommendation(amount)) {
			        	count++;
			        	outputArea.append(friend.getName() + "\t\t" + friend.getNrOfFriends() + "\t\t" +friend.getNrOfFans() + "\n");
			        }
		
		        	tx.success();
		        }
	        }
	        outputArea.append("\n");
		}
	};
}
