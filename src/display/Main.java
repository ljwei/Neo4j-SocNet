/**
 * 
 */
package display;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.neo4j.graphdb.Transaction;

import socnet.ConnectToNeo4j;
import socnet.Person;
import socnet.PersonRepository;
import socnet.StatusUpdate;


/**
 * @author jw li
 *
 */
public class Main {
	public static PersonRepository pr;
	public static ConnectToNeo4j ctn;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ctn = new ConnectToNeo4j();
				
				pr = new PersonRepository(ctn.getGraphDb());
				
		        try {  
		            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  
		        } catch (Exception e) {  
		              
		        }  
		        
		        // 创建窗体  
		        JFrame frame = new JFrame("Social Network Data Analysis");  
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		        frame.getContentPane().add(new MyPanel(), BorderLayout.CENTER);
		        
		        // 显示窗体  
		        frame.setSize(800, 600);  
		        frame.setVisible(true);  
			}
		});
	}
}
