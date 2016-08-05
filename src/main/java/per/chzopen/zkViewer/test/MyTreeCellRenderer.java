package per.chzopen.zkViewer.test;

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 欢迎和我联系：772333621
 * 
 * @author FL
 */
public class MyTreeCellRenderer extends DefaultTreeCellRenderer
{
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (leaf)
		{
			setLeafIconByValue(value);
		}
		return this;
	}

	public void setLeafIconByValue(Object value)
	{
		ImageIcon ii;
		int length = value.toString().length();
		if (length > 4)
		{
			ii = new ImageIcon("e:/0.jpg");
		}
		else
		{
			ii = new ImageIcon("e:/1.jpg");
		}
		System.out.println(value.toString());
		this.setIcon(ii);
	}
}
