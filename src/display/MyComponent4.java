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

public class MyComponent4 extends JPanel {
	private JTextField inputField;
	private JTextArea outputArea;
	public MyComponent4() {
		
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		
		JLabel inputL = new JLabel("请输入用户名:");
		inputField = new JTextField("", 6);
		JButton runButton = new JButton("查询");
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
	        	outputArea.append("用户名" + input + "不存在" + "\n\n");
	        } else {
	        	outputArea.append("你关注的人发布的状态列表（按时间排序）");
		        outputArea.append("状态内容" + "\t\t" + "发布人" +  "\t\t" + "发布时间" + "\n");	  
		        int count = 0;
		        try ( Transaction tx = Main.ctn.getGraphDb().beginTx() )
		        {
			        Iterator<StatusUpdate> itStatus = p.friendStatuses();
			        while (itStatus.hasNext())
			        {
			        	count++;
			        	StatusUpdate status = itStatus.next();
			        	outputArea.append(status.getStatusText() + "\t\t" + status.getPerson().getName() + "\t\t" + status.getDate() + "\n");
			        }
					outputArea.append("用户" + p.getName() + "有" + count + "条最新消息" + "\n\n");
		
		        	tx.success();
		        }
	        }
	        
		}
	};
}
