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

public class MyComponent2 extends JPanel {
	private JTextField inputField;
	private JTextArea outputArea;
	public MyComponent2() {
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		
		JLabel inputL = new JLabel("please input name:");
		inputField = new JTextField("", 6);
		JButton runButton = new JButton("Run");
		runButton.addActionListener(runListener);
		
		panelUp.add(inputL);
		panelUp.add(inputField);
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
			
	        Person p = Main.pr.getPersonByName(input);
	        
	        if (p == null) {
	        	outputArea.append("user " + input + " is unfound" + "\n\n");
	        } else {
	    		outputArea.append("FRIEND NAME" + "\t\t\t" + "LATEST STATUS" + "\n");
		        int count = 0;
		        try ( Transaction tx = Main.ctn.getGraphDb().beginTx() )
		        {
	            	for (Person friend : p.getFriends()) {
	            		count++;
	            		for (StatusUpdate status : friend.getStatus()) {
		            		outputArea.append(friend.getName() + "\t\t\t" + status.getStatusText() + "\n");
		            		break;
	            		}
	            	}
					outputArea.append("the amount of " + p.getName() + "'s friends:" + count + "\n\n");
		
		        	tx.success();
		        }
	        }
		}
	};
}
