package per.chzopen.zkViewer.test;

import javax.swing.JFrame;
import javax.swing.JTree;

/**
 *
 * @author FL
 */
public class MainFrame extends JFrame
{
	public static void main(String[] args)
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setSize(200, 300);
		JTree jTree = new JTree();
		jTree.setCellRenderer(new MyTreeCellRenderer());
		mainFrame.getContentPane().add(jTree);
		mainFrame.setVisible(true);
	}
}
