# MSDE Fault Tolerance: Zookeeper

Practices:
-   I. Synchronous Master (2015_Zookeper_Chapter3.pdf): Master.java
-  II. Getting Mastership (2015_Zookeper_Chapter3.pdf): Master.java
- III. Synchronous Master Election (2016_Zookeeper_Chapter3_2.pdf): MasterSynchronous.java
-  IV. Interface Watcher (2016_Zookeeper_Chapter3_2.pdf): README.md
-   V. Asynchronous Master Election (2016_Zookeeper_Chapter3_2.pdf) MasterAsynchronous.java
---

Installation:

This is a Java Project 1.8 which uses Maven 3.5.4
The practices are implemented using a JUnit Test Case so to be able run it you just need perform `mvn install` after up zookeeper on 127.0.0.2:2181 

Console log displays the output for both practices on Test maven section.
---

## IV. Interface Watcher
[JAVADOC Zookeeper 3.5.4-beta](http://www.javadoc.io/doc/org.apache.zookeeper/zookeeper/3.5.4-beta)

1. Enumerate the states the Zookeeper service may be at the event
org.apache.zookeeper.Watcher.Event.KeeperState:
    - AuthFailed
    - ConnectedReadOnly
    - Disconnected
    - Expired
    - NoSyncConnected (Deprecated)
    - SaslAuthenticated
    - SyncConnected
    - Unknown (Deprecated)

2. Enumerate the types of events of Zookeeper tree
org.apache.zookeeper.Watcher.Event.EventType:
    - ChildWatchRemoved 
    - DataWatchRemoved 
    - NodeChildrenChanged 
    - NodeCreated 
    - NodeDataChanged 
    - NodeDeleted 
    - None

All sources are on: [Github](https://github.com/Vrael/msde-zookeeper)
