package per.chzopen.zkViewer.zookeeper;
//package per.chzopen.zkviewer.zookeeper;
//
//import java.util.concurrent.TimeUnit;
//
//import org.apache.curator.RetryPolicy;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.framework.state.ConnectionState;
//import org.apache.curator.framework.state.ConnectionStateListener;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//
//import per.chzopen.zkviewer.exception.ZkViewerException;
//
//public class CuratorHolder
//{
//
//	public static CuratorFramework client;
//	
//	public static void init(String zookeeper)
//	{
//		if( client!=null )
//		{
//			throw new ZkViewerException("client initialized already");
//		}
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//    	client = CuratorFrameworkFactory.newClient(zookeeper, retryPolicy);
//    	client.start();
//	}
//	
//	public static CuratorFramework get()
//	{
//		return client;
//	}
//	
//	
//	public static void main(String[] args) throws Exception
//	{
//
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//    	client = CuratorFrameworkFactory.newClient("192.168.2.228:2181", retryPolicy);
//    	client.start();
//    	
//    	client.getConnectionStateListenable().addListener(new ConnectionStateListener()
//		{
//			public void stateChanged(CuratorFramework client, ConnectionState newState)
//			{
//				System.out.println("stateChanged: " + newState);
//			}
//		});
//    	
//    	client.blockUntilConnected(3, TimeUnit.SECONDS);
//    	
//    	System.out.println(client.getChildren().forPath("/"));
//    	
//    	//client.close();
//	}
//}
