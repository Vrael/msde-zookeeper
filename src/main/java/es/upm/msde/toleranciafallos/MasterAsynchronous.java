package es.upm.msde.toleranciafallos;

import java.io.IOException;
import java.lang.Exception;
import java.lang.String;
import java.lang.Thread;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.Create2Callback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.WatchedEvent;



/**
 * Practice 2. Synchronous Master Election
 *
 */
public class MasterAsynchronous implements Watcher, AsyncCallback.Create2Callback{

    private static Logger logger = LogManager.getLogger(MasterAsynchronous.class);

    private ZooKeeper zk;
    private String hostPort;
    private boolean leader = false;
    private String serverId = "";
	private static final String ROOT = "/master";

    public MasterAsynchronous(String hostPort) {
        // 1.Initialize the hostPort
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        //2. Create a Zk Client and establishes the session
        logger.info("Master::startZK on hostPort: " + hostPort);
        this.zk = new ZooKeeper(hostPort,1500,this);
        this.serverId = UUID.randomUUID().toString();
    }

    void stopZK() throws Exception {
       //3. Close the zk Client session
       logger.info("Master::closeZK");
       this.zk.close();
    }

    @Override
    public void process(WatchedEvent event) {
        logger.info("Master::Process");
        logger.info(event);
    }

	@Override
    public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
        logger.info("Master::ProcessResult [" + " path: " + path + " name: " + name + "]");

		try {
			if(rc == KeeperException.Code.Ok) {
				if(isLeader())
					leader = true;
				else
					leader = false;
					runLeader();	
			} else {
				leader = false;
			}
		} catch (InterruptedException e) {
			logger.error(e);
		}
    }

    public boolean isLeader() throws InterruptedException {
        //4. Check if there is a master
        logger.info("Master::isLeader");
		try {
			Stat stat = new Stat();
			byte[] zkNodeData = zk.getData("/election",false,stat);
			leader = new String(zkNodeData).equals(serverId);
			logger.info("This znode is the leader");
			return true;
		} catch (NoNodeException e) {
			return false;
		} catch (KeeperException e) {
		
		}
		return false;
    }

    public void runLeader() {
        logger.info("Master::runLeader");
		try {
			Stat stat = zk.exists(ROOT, false);
			zk.create(ROOT, serverId.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, this, null); 
		} catch(InterruptedException e) {
			logger.error(e);
		} catch (KeeperException e) {
			logger.error(e);
		}
    }

    public boolean getIsLeader() {
        return leader;
    }

    public static void threadSummary() {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        logger.info("Threads number: " + threadSet.size() + " names: " +
                threadSet.stream().map(Thread::getName).collect(Collectors.toList()));
    }

    public static void main(String[] args) throws Exception {
        logger.info("Main::Master");
        MasterAsynchronous m = new MasterAsynchronous(args[0]);
        m.startZK();
        m.runLeader();
        if (m.getIsLeader()) {
            logger.info("This znode is the leader");
            Thread.sleep(5000);
        }   else {
            logger.info("Other znode is the leader");
        }
        logger.info("Main::Thread sleep 60 s");
        m.stopZK();
    } 
}
