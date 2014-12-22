package display;
import java.awt.*;  

import javax.swing.*;


public class MyPanel extends JPanel {  
      
    public MyPanel() {  
        super(new GridLayout(1, 1));  
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        Component panel1 = new MyComponent1();  
        tabbedPane.addTab("已发布状态", panel1);  
        tabbedPane.setSelectedIndex(0);  
  
        Component panel2 = new MyComponent2();  
        tabbedPane.addTab("关注", panel2);  
  
        Component panel3 = new MyComponent3();  
        tabbedPane.addTab("被关注", panel3);  
  
        Component panel4 = new MyComponent4();  
        tabbedPane.addTab("最新消息", panel4); 

        Component panel6 = new MyComponent6();
        tabbedPane.addTab("好友推荐", panel6);
        
        Component panel5 = new MyComponent5();
        tabbedPane.addTab("图数据分析", panel5);
  
        
        // 将选项卡添加到panl中  
        add(tabbedPane);  
    }  
}  