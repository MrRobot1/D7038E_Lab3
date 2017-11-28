/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.Util.*;

/**
 * This program demonstrates networking in JMonkeyEngine using SpiderMonkey, and
 * contains the server.
 *
 *
 * @author hj extended by olofe
 */
public class TheServer extends SimpleApplication {

    private Server server;
    private final int port;
    private float time = 0f;
    private MessageQueue messageQueue = new MessageQueue();
    private Game game = new Game();


    private int numberOfPlayers;
    private boolean running = false;
    public static void main(String[] args) {
        Util.print("Server initializing");
        Util.initialiseSerializables();
        new TheServer(Util.PORT).start(JmeContext.Type.Headless);


    }

    public TheServer(int port) {
        this.port = port;
        game.setEnabled(true);
        running=false;
        stateManager.attach(game);    

    }
    protected void initGame(int numberOfPlayers, int numberOfConnections) {
        running=true;
        System.out.println("2");
        game.spawnPlayers(numberOfPlayers);
        String[] playerIDs = new String[numberOfPlayers];
        String[] yourIDs = new String[3];
        Vector3f[] startingPositions = new Vector3f[numberOfPlayers];
        
        for (int i=0; i<numberOfPlayers; i++) {
            playerIDs[i] = game.players.get(i).id;
            startingPositions[i] = game.players.get(i).getLocalTranslation();
        }
        for (int i=0; i<numberOfConnections; i++) {
            yourIDs[0] = playerIDs[i];
            yourIDs[1] = playerIDs[i+numberOfConnections];
            yourIDs[2] = playerIDs[i+2*numberOfConnections];

            StartGameMessage m =new StartGameMessage(playerIDs,
                    yourIDs, startingPositions);
            m.destinationID = i;

            messageQueue.enqueue(m);

        }
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void simpleInitApp() {
        // In a game server, the server builds and maintains a perfect 
        // copy of the game and makes use of that copy to make descisions 
       

        try {
            Util.print("Using port " + port);
            // create the server by opening a port
            server = Network.createServer(port);
            server.start(); // start the server, so it starts using the port
        } catch (IOException ex) {
            ex.printStackTrace();
            destroy();
            this.stop();
        }
        Util.print("Server started");
        // create a separat thread for sending "heartbeats" every now and then
        new Thread(new HeartBeatSender()).start();
        new Thread(new NetWrite()).start();
        server.addMessageListener(new ServerListener(), ChangeMessage.class,
                AckMessage.class, HeartMessage.class,
                HeartAckMessage.class);
        // add a listener that reacts on incoming network packets
      
        Util.print("ServerListener aktivated and added to server");
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (running) {
            time = game.getTimeLeft();
            if (time <= 0) {
                System.out.println("RestartGameDemo: simpleUpdate "
                        + "(entering when time is up)");
                game.setEnabled(false);
                time = 0f;
                running = false;
                System.out.println("RestartGameDemo: simpleUpdate "
                        + "(leaving with running==false)");
                messageQueue.enqueue(new StopGameMessage());
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    @Override
    public void destroy() {
        Util.print("Server going down");
        server.close();
        super.destroy();
        Util.print("Server down");
    }

    // this class provides a handler for incoming network packets
    private class ServerListener implements MessageListener<HostedConnection> {
        @Override
        public void messageReceived(HostedConnection source, Message m) {
            
            /*
            if (m instanceof ChangeMessage) {
                // forward copies to all clients except the sender
                ChangeMessage msg = (ChangeMessage) m;
                Util.print("Getting ChangeMessage from " + msg.getSenderID()
                        + ", forwarding to all other clients");
                TheServer.this.server.broadcast(Filters.notEqualTo(source), m);
            } else if (m instanceof AckMessage) {
                // forward the ack to the original sender (of the ChangeMessage)
                AckMessage msg = (AckMessage) m;
                Util.print("Getting AckMessage from " + msg.getSenderID()
                        + ", forwarding to " + msg.finalDestinationID);
                HostedConnection conn
                        = TheServer.this.server
                                .getConnection(msg.finalDestinationID);
                // send it to the client
                conn.send(m);
            } else if (m instanceof HeartMessage) {
                // this indicates a programming error(!)
                HeartMessage msg = (HeartMessage) m;
                throw new RuntimeException("Server got a HeartMessage "
                        +"- should not happen!");
            } else if (m instanceof HeartAckMessage) {
                // a client send back an ack - no need to react
                HeartAckMessage msg = (HeartAckMessage) m;
                Util.print("Getting HeartAckMessage back from " 
                        + msg.getSenderID());
            } else {
                // this indicates a programming error(!)
                throw new RuntimeException("Unknown message.");
            } */
        }
    }

    
    /**
     * Sends out a heart beat to all clients every TIME_SLEEPING seconds, after
     * first having waited INITIAL_WAIT seconds. .
     */
    private class NetWrite implements Runnable {
        public void run() {
            while(true) {
                try {
                    Thread.sleep(10); // ... sleep ...
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                while (!messageQueue.isEmpty()) {
                    MyAbstractMessage m = messageQueue.pop();
                    if (m.destinationID!=-1) {
                        HostedConnection conn
                        = TheServer.this.server
                                .getConnection(m.destinationID);
                        conn.send(m);
                    }
                    else {
                        server.broadcast(m);
                    }
                }
                
            }
        }
    }
    private class HeartBeatSender implements Runnable {

        private final int INITIAL_WAIT = 30000; // time until first loop lap 
        private final int TIME_SLEEPING = 25000; // timebetween heartbeats

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {
            Util.print("Heartbeat sender thread running");
            while (true) {
                try {
                    Thread.sleep(TIME_SLEEPING); // ... sleep ...
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Util.print("Sending one heartbeat to each client");
                messageQueue.enqueue(new HeartMessage());
                
                Util.print("Starting game,..");
                final int numberOfConnections = server.getConnections().size();
                numberOfPlayers = 3 * numberOfConnections;
                Future result = TheServer.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        initGame(numberOfPlayers, numberOfConnections);
                        return true;
                    }
                });
                //System.out.println(game.players.size());
                /*
                String[] playerIDs = new String[numberOfPlayers];
                String[] yourIDs = new String[numberOfConnections];
                Vector2f[] startingPositions = new Vector2f[numberOfPlayers];
                
                for (int i=0; i<numberOfPlayers; i++) {
                    playerIDs[i] = game.players.get(i).id;
                    startingPositions[i] = game.players.get(i).getLocalTranslation();
                }
                for (int i=0; i<numberOfConnections; i++) {
                    yourIDs[0] = playerIDs[i];
                    yourIDs[1] = playerIDs[i+numberOfConnections];
                    yourIDs[2] = playerIDs[i+2*numberOfConnections];
                    
                    StartGameMessage m =new StartGameMessage(playerIDs,
                            yourIDs, startingPositions);
                    m.destinationID = i;

                    messageQueue.enqueue(m);
                    
                }
                */
                //return;
            }
        }
    }

}