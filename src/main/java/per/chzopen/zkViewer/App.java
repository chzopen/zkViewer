package per.chzopen.zkViewer;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
    	RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    	CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.2.228:2181", retryPolicy);
    	client.start();
    	
//    	String rt = client.create().forPath("/chz");
//    	System.out.println(rt);
    	
    	Stat stat = new Stat();
//    	client.getZookeeperClient().getZooKeeper().getData("/consumers/console-consumer-33428/offsets/test", false, stat);
//    	System.out.println(String.format("%X", stat.getCzxid()));
    	
    	System.out.println(new String(client.getData().storingStatIn(stat).forPath("/consumers/console-consumer-33428/offsets/test/0")));
    	System.out.println(String.format("%X", stat.getCzxid()));
    	
//    	
//    	List<String> children = client.getChildren().forPath("/");
//    	for( String child : children )
//    	{
//    		System.out.println(child);
//    	}
    }
}