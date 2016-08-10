package per.chzopen.zkViewer.gui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZkTree extends JTree
{

	public static Logger logger = LoggerFactory.getLogger(ZkTree.class);
	
	public static ImageIcon IMAGEICON_QUESTION = new ImageIcon(ClassLoader.getSystemClassLoader().getResource("images/question.png"));

	private static final long serialVersionUID = 1L;

	private ZkTree _this = this;

	private DefaultMutableTreeNode root;
	
	private ListenerHelper listenerHelper = new ListenerHelper();
	
	private TreePath selectedTreePath;
	private ZkTreeNode selectedTreeNode;
	
	private JPopupMenu popupMenu;
	private JMenuItem menuItemRefresh;
	
	private CuratorFramework curatorClient;
	
	
	
	public ZkTree()
	{
		this.setCellRenderer(new MyTreeCellRenderer());
		
		// 右键菜单相关
		{
			popupMenu = new JPopupMenu();
			menuItemRefresh = new JMenuItem("刷新");
			popupMenu.add(menuItemRefresh);
		}
		
		initEvent();
	}
	
	public void setMessage(String message)
	{
		clear();
		root = new DefaultMutableTreeNode(message);
		this.setModel(new DefaultTreeModel(root));
	}
	
	public void setZookeeper(CuratorFramework curatorClient)
	{
		clear();
		this.curatorClient = curatorClient;
		root = new ZkTreeNode("/", "/");
		this.setModel(new DefaultTreeModel(root));
	}
	
	private void clear()
	{
		if( curatorClient!=null )
		{
			curatorClient.close();
		}
	}

	public void addListener(Listener listener)
	{
		listenerHelper.addListener(listener);
	}
	
	private void initEvent()
	{
		// 鼠标事件
		this.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				try
				{
					selectedTreePath = _this.getPathForLocation(event.getX(), event.getY());
					if( selectedTreePath==null )
					{
						return ;
					}
					TreeNode treeNode = (TreeNode) selectedTreePath.getLastPathComponent();
					if( !(treeNode instanceof ZkTreeNode) )
					{
						return ;
					}
					
					//
					selectedTreeNode = (ZkTreeNode)treeNode;
					if( event.getClickCount()==1 )
					{
						// 左键单击
						if( event.getButton()==MouseEvent.BUTTON1 )
						{
							Stat stat = new Stat();
							byte[] data = curatorClient.getData().storingStatIn(stat).forPath(selectedTreeNode.getZkPath());
							listenerHelper.fireOnNodeClicked(event, selectedTreeNode, stat, data);
						}
						// 右键单击
						else if( event.getButton()==MouseEvent.BUTTON3 )
						{
							popupMenu.show(_this, event.getX(), event.getY());
							_this.getSelectionModel().setSelectionPath(selectedTreePath);
							_this.grabFocus();
						}
					}
					// 双击
					else if (event.getClickCount() == 2)
					{
						if (selectedTreeNode.isLoaded() == false)
						{
							refreshTreeNode(selectedTreeNode);
							_this.expandPath(selectedTreePath);
							_this.updateUI();
						}
					}
				}
				catch (Exception e)
				{
					logger.error("", e);
				}
			}
		});
		
		//
		menuItemRefresh.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					Stat stat = new Stat();
					byte[] data = curatorClient.getData().storingStatIn(stat).forPath(selectedTreeNode.getZkPath());
					listenerHelper.fireOnNodeClicked(event, selectedTreeNode, stat, data);
					
					refreshTreeNode(selectedTreeNode);
					_this.expandPath(selectedTreePath);
					_this.updateUI();
				}
				catch (Exception e)
				{
					logger.error("", e);
				}
			}
		});
	}
	
	private void refreshTreeNode(ZkTreeNode treeNode) throws Exception
	{
		selectedTreeNode.setLoaded(true);
		treeNode.removeAllChildren();
		List<String> children = curatorClient.getChildren().forPath("" + treeNode.getZkPath());
		for (String child : children)
		{
			String zkPath = treeNode.getZkPath().endsWith("/") ? 	treeNode.getZkPath() + child : 
																	treeNode.getZkPath() + "/" + child;
			treeNode.insert(new ZkTreeNode(zkPath, child), treeNode.getChildCount());
		}
	}

	/**
	 * 
	 */
	public static class ZkTreeNode extends DefaultMutableTreeNode
	{
		private static final long serialVersionUID = 1L;

		private String zkPath;
		private boolean loaded = false;

		public ZkTreeNode(String zkPath, String name)
		{
			super(name);
			this.zkPath = zkPath;
		}

		public String getZkPath()
		{
			return zkPath;
		}

		public boolean isLoaded()
		{
			return loaded;
		}

		public void setLoaded(boolean loaded)
		{
			this.loaded = loaded;
		}
	}

	/**
	 * 
	 */
	public class MyTreeCellRenderer extends DefaultTreeCellRenderer
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if( value instanceof ZkTreeNode )
			{
				ZkTreeNode treeNode = (ZkTreeNode)value;
				if( !treeNode.isLoaded() )
				{
					setLeafIconByValue(treeNode);
				}
			}
			return this;
		}

		public void setLeafIconByValue(ZkTreeNode treeNode)
		{
			this.setIcon(IMAGEICON_QUESTION);
		}
	}

	/**
	 * 
	 */
	public static interface Listener
	{
		public void onNodeClicked(AWTEvent event, ZkTreeNode treeNode, Stat stat, byte[] data) throws Exception;
	}
	
	/**
	 * 
	 */
	public static class ListenerHelper
	{
		public LinkedHashMap<Listener, Object> listeners = new LinkedHashMap<>();
		
		public void addListener(Listener listener)
		{
			listeners.put(listener, listener);
		}
		
		public void fireOnNodeClicked(AWTEvent event, ZkTreeNode treeNode, Stat stat, byte[] data)
		{
			for( Entry<ZkTree.Listener,Object> entry: listeners.entrySet() )
			{
				try
				{
					entry.getKey().onNodeClicked(event, treeNode, stat, data);
				}
				catch (Exception e)
				{
					logger.error("", e);
				}
			}
		}
	}
}













