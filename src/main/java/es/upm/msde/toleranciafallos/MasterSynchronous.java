package es.upm.msde.toleranciafallos;

import java.io.IOException;
import java.lang.Exception;
import java.lang.String;
import java.lang.Thread;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
public class MasterSynchronous implements Watcher {

    private static Logger logger = LogManager.getLogger(MasterSynchronous.class);

    private ZooKeeper zk;
    private String hostPort;
    private boolean leader = false;
    private String serverId = "";

    public MasterSynchronous(String hostPort) {
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

    public void process(WatchedEvent e) { //Callback interface
        logger.info("Master::Process");
        logger.info(e);
    }

    public boolean isLeader() throws InterruptedException {
        //4. Check if there is a master
        logger.info("Master::isLeader");
        while(true) {
            try {
                Stat stat = new Stat();
                byte[] zkNodeData = zk.getData("/election",false,stat);
                leader = new String(zkNodeData).equals(serverId);
                return true;
            } catch (NoNodeException e) {
                return false;
            } catch (KeeperException e) {
            
            }
        }
    }

    public void runLeader() throws InterruptedException {
        logger.info("Master::runLeader");
        while (true) {  
            try {
                zk.create("/election", serverId.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL); 
                leader = true;
                return;
            } catch (NodeExistsException  e)  {
              leader = false;
              return;
            } catch (KeeperException  e)  {

            }
            
            if (isLeader()) {
                return;
            }
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
        MasterSynchronous m = new MasterSynchronous(args[0]);
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
