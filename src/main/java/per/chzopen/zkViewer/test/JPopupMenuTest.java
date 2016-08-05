package per.chzopen.zkViewer.test;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class JPopupMenuTest extends JFrame
{
	public JPopupMenuTest()
	{
		JButton button = new JButton("登录");
		this.add(button);
		// 为按钮添加单击事件
		button.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				System.out.println("按到了");
			}
		});
		final String id = button.getText();
		// 为按钮创建一个右键菜单
		JPopupMenu pop = new JPopupMenu(id);
		JMenuItem item1 = new JMenuItem("功能1");
		item1.addMouseListener(new MouseAdapter()
		{
			public void mouseReleased(MouseEvent e)
			{
				System.out.println(id);// 输出按钮的文本
			}
		});
		pop.add(item1);
		button.setComponentPopupMenu(pop);// 将按钮与右键菜单关联

		this.setLayout(new FlowLayout());
		this.setBounds(100, 100, 300, 300);
		this.setVisible(true);

	}

	public static void main(String[] args)
	{
		new JPopupMenuTest();

	}

}
