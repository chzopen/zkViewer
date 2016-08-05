package per.chzopen.zkViewer.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import per.chzopen.zkViewer.gui.ZkTree.ZkTreeNode;

public class ZkViewerFrame extends JFrame
{

	private static final long serialVersionUID = 1L;

	private ZkViewerFrame _this;
	
	private JMenuItem mItemSwitchZookeeper;
	private JMenuItem mItemAbout;
	
	private JPanel panel;
	
	private ZkTree zkTree;
	private JTextArea textarea;
	
	private JLabel statusLabel;
	
	private String zkAddr = "localhost:2181";
	
	
	public ZkViewerFrame()
	{
		this.setTitle("zkViewer");
		this.setSize(500, 500);
		initMenu();
		initPanel();
		initStatusBar();
		initEvent();
	}
	
	private void initMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		
		// Settings
		{
			JMenu menuSettings = new JMenu("Settings");
			mItemSwitchZookeeper = new JMenuItem("Switch zookeeper");
			menuSettings.add(mItemSwitchZookeeper);	
			menuBar.add(menuSettings);
		}
		
		// Help
		{
			JMenu menuHelp = new JMenu("Help");
			mItemAbout = new JMenuItem("About");
			menuHelp.add(mItemAbout);	
			menuBar.add(menuHelp);
		}
		
		this.setJMenuBar(menuBar);
	}
	
	private void initPanel()
	{
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = null;
		int gridy = -1;

		// row 0
		{
			gridy++;

			gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = gridy;
			panel.add(Box.createRigidArea(new Dimension(10, 10)), gbc);
		}
		
		// row 1
		{
			gridy++;

			zkTree = createZkTree();
			textarea = new JTextArea();
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(zkTree), new JScrollPane(textarea));
			splitPane.setDividerLocation(400);
			panel.add(splitPane, gbc);

			gbc = new GridBagConstraints();
			gbc.gridx = 1;
			gbc.gridy = gridy;
			gbc.weightx = 1;
			gbc.weighty = 1;
			gbc.fill = GridBagConstraints.BOTH;
			panel.add(splitPane, gbc);
		}

		// row -1
		{
			gridy++;

			gbc = new GridBagConstraints();
			gbc.gridx = 99;
			gbc.gridy = gridy;
			panel.add(Box.createRigidArea(new Dimension(10, 10)), gbc);
		}
		
		this.add(panel);
	}
	
	private void initStatusBar()
	{
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusBar.setPreferredSize(new Dimension(30, 30));
		statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));

		statusLabel = new JLabel("Welcome using zkViewer");
		statusBar.add(statusLabel);
		
		this.add(statusBar, BorderLayout.SOUTH);
	}
	
	private ZkTree createZkTree()
	{
		// 创建没有父节点和子节点、但允许有子节点的树节点，并使用指定的用户对象对它进行初始化。
		ZkTree zkTree = new ZkTree();
		zkTree.setMessage("Zookeeper not connected");
		return zkTree;
	}
	
	private void initEvent()
	{
		// 树的事件
		zkTree.addListener(new ZkTree.Listener()
		{
			public void onNodeClicked(AWTEvent event, ZkTreeNode treeNode, Stat stat, byte[] data) throws Exception
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("************************************************\r\n\r\n");
				
				sb.append("path:\r\n");
				sb.append(treeNode.getZkPath());
				sb.append("\r\n\r\n");
				
				sb.append("------------------------------------------------\r\n\r\n");
				
				sb.append("data:\r\n");
				sb.append(data==null ? "null" : new String(data, "utf-8"));
				sb.append("\r\n\r\n");

				sb.append("------------------------------------------------\r\n\r\n");
				
				sb.append("stat:\r\n");
				sb.append(String.format("cZxid: 0x%08X\r\n",          stat.getCzxid()));
				sb.append(String.format("ctime: %s\r\n",              sdf.format(new Date(stat.getCtime()))));
				sb.append(String.format("mZxid: 0x%08X\r\n",          stat.getMzxid()));
				sb.append(String.format("mtime: %s\r\n",              sdf.format(new Date(stat.getMtime()))));
				sb.append(String.format("pZxid: 0x%08X\r\n",          stat.getPzxid()));
				sb.append(String.format("cversion: %d\r\n",           stat.getCversion()));
				sb.append(String.format("dataVersion: %d\r\n",        stat.getVersion()));
				sb.append(String.format("aclVersion: %d\r\n",         stat.getAversion()));
				sb.append(String.format("ephemeralOwner: 0x%08X\r\n", stat.getEphemeralOwner()));
				sb.append(String.format("dataLength: %d\r\n",         stat.getDataLength()));
				sb.append(String.format("numChildren: %d\r\n",        stat.getNumChildren()));
				sb.append("\r\n\r\n");
				
				sb.append("************************************************\r\n");
				
				textarea.setText(sb.toString());
			}
		});
		
		// switch zookeeper
		mItemSwitchZookeeper.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				Object rt = JOptionPane.showInputDialog(_this, "Please input zookeeper address:", "Input", JOptionPane.QUESTION_MESSAGE, null, null, zkAddr);
				if( rt==null )
				{
					return ;
				}
				String str = ""+rt;
				if( StringUtils.isBlank(str) )
				{
					JOptionPane.showMessageDialog(null, "Input content cannot be empty");
					return ;
				}
				String message = String.format("connecting to '%s'", str);
				statusLabel.setText(message);
				zkTree.setMessage(message);
				zkTree.updateUI();
				zkAddr = str;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
							CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(zkAddr, retryPolicy);
							curatorClient.start();
							curatorClient.blockUntilConnected(2, TimeUnit.SECONDS);
							if( curatorClient.getZookeeperClient().isConnected() )
							{
								String message = String.format("connect to zookeeper '%s' succeeded", zkAddr);
								statusLabel.setText(message);
								zkTree.setZookeeper(curatorClient);
							}
							else
							{
								curatorClient.close();
								String message = String.format("connect to zookeeper '%s' failed", zkAddr);
								statusLabel.setText(message);
								zkTree.setMessage(message);
								zkTree.updateUI();
							}
						}
						catch (Exception e)
						{
							String message = String.format("connect to zookeeper '%s' failed", zkAddr);
							statusLabel.setText(message);
							zkTree.setMessage(message);
							zkTree.updateUI();
						}
					}
				});
						
			}
		});
		
		//
		mItemAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StringBuilder sb = new StringBuilder();
				sb.append("Authored by Chzopen.\r\n");
				sb.append("Contact author by mailing to chzopen@163.com");
				JOptionPane.showMessageDialog(_this, sb.toString());
			}
		});
	}
}















