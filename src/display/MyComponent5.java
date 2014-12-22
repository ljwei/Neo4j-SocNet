package display;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.neo4j.graphdb.Transaction;

import socnet.Person;
import socnet.StatusUpdate;

public class MyComponent5 extends JPanel {
	private final static int SHOW_COUNT = 20;
	private JTextArea outputArea;
	private String item = "关注人数";
	public MyComponent5() {
		
		JPanel panelUp = new JPanel();
		JPanel panelDown = new JPanel();
		
		JComboBox<Object> jcb = new JComboBox<Object>();
		jcb.addItem(makeObj("关注人数"));
		jcb.addItem(makeObj("粉丝数"));
		jcb.addItem(makeObj("兴趣统计"));
		jcb.addItemListener(itemListener);
		
		JButton runButton = new JButton("查询");
		runButton.addActionListener(runListener);
		
		panelUp.add(jcb);
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
	
	private Object makeObj(final String item)  {
	     return new Object() { public String toString() { return item; } };
	}
	
	private void AnalysisFriendCount() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try ( Transaction tx = Main.ctn.getGraphDb().beginTx() )
        {
	        for (Person p : Main.pr.getAllPersons()) {
	        	map.put(p.getName(), p.getNrOfFriends());
	        }
        	tx.success();
        }
        
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, 
			new Comparator<Map.Entry<String, Integer>>() {
				// 升序排序
				public int compare(
						Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});

    	outputArea.append("关注人数排名 (TOP" + SHOW_COUNT + "):" + "\n");
        outputArea.append("用户名" + "\t\t" + "关注总人数" + "\n");	  

        int count = SHOW_COUNT;
        if (list.size() < SHOW_COUNT) {
        	count = list.size();
        }
        
        for (int i = 0; i < count; i++) {
        	outputArea.append(list.get(i).getKey() + "\t\t" + list.get(i).getValue() + "\n");
        }
        outputArea.append("\n");
	}
	
	private void AnalysisFansCount() {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		try ( Transaction tx = Main.ctn.getGraphDb().beginTx() )
        {
	        for (Person p : Main.pr.getAllPersons()) {
	        	map.put(p.getName(), p.getNrOfFans());
	        }
        	tx.success();
        }
        
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
		Collections.sort(list, 
			new Comparator<Map.Entry<String, Integer>>() {
				// 升序排序
				public int compare(
						Map.Entry<String, Integer> o1,
						Map.Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});

    	outputArea.append("粉丝数排名(前" + SHOW_COUNT + "):" + "\n");
        outputArea.append("用户名" + "\t\t" + "粉丝数" + "\n");	  

        int count = SHOW_COUNT;
        if (list.size() < SHOW_COUNT) {
        	count = list.size();
        }
        
        for (int i = 0; i < count; i++) {
        	outputArea.append(list.get(i).getKey() + "\t\t" + list.get(i).getValue() + "\n");
        }
        outputArea.append("\n");
	}
	
	private void AnalysisInteresting() {
		outputArea.append("未实现");
	}
	
	ItemListener itemListener = new ItemListener(){
		
		@Override
		public void itemStateChanged(ItemEvent evt) {
			if(evt.getStateChange() == ItemEvent.SELECTED){
				item = evt.getItem().toString();
			}
		}
	};
	
	ActionListener runListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (item == "关注人数") {
				AnalysisFriendCount();
			} else if (item == "粉丝数") {
				AnalysisFansCount();
			} else if (item == "兴趣统计") {
				AnalysisInteresting();
			}
		}
	};
}
