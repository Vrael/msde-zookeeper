package es.upm.msde.toleranciafallos;

import java.io.IOException;
import java.lang.Exception;
import java.lang.String;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.WatchedEvent;


/**
 * Practice 1. Synchronous Master
 *
 */
public class Master implements Watcher {

    private static Logger logger = LogManager.getLogger(Master.class);

    private ZooKeeper zk;
    private String hostPort;

    public Master(String hostPort) {
        // 1.Initialize the hostPort
        this.hostPort = hostPort;
    }

    void startZK() throws IOException {
        //2. Create a Zk Client and establishes the session
        logger.info("Master::startZK on hostPort: " + hostPort);
        this.zk = new ZooKeeper(hostPort,1500,this);
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

    public static void main(String[] args) throws Exception {
        logger.info("Main::Master");
        Master m = new Master(args[0]);
        m.startZK();
        logger.info("Main::Thread sleep 60 s");
        Thread.sleep(60000); //wait for a bit
        m.stopZK();
    } 
}
