package es.upm.msde.toleranciafallos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.Exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class MasterTests {

    private static Logger logger = LogManager.getLogger(MasterTests.class);


    @Test
    void zkCliWith1500ms() throws Exception {
        logger.info("Test Zk Client with timeout 1500 ms");
        Master m = new Master("127.0.0.2:2181");
        Master.threadSummary();
        m.startZK();
        Master.threadSummary();
        logger.info("Main::Thread sleep 60 s");
        Thread.sleep(5000); //wait for a bit
        m.stopZK();
        Master.threadSummary();
    }

    @Test
    void zkSynchronizedMaster() throws Exception {
        logger.info("Test Zk Client with timeout 1500 ms");
        MasterSynchronous m = new MasterSynchronous("127.0.0.2:2181");
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

    @Test
    void zkAsynchronizedMaster() throws Exception {
        logger.info("Test Zk Client with timeout 1500 ms");
        MasterAsynchronous m = new MasterAsynchronous("127.0.0.2:2181");
        m.startZK();
        m.runLeader();
        logger.info("Main::Thread sleep 60 s");
        m.stopZK();
    }
}
