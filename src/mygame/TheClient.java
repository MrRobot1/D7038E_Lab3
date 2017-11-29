package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.renderer.RenderManager;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mygame.Util.*;

/**
 * This program demonstrates networking in JMonkeyEngine using SpiderMonkey, and
 * contains the client.
 *
 *
 * @author hj
 */
public class TheClient extends SimpleApplication {


    // the connection back to the server
    private Client serverConnection;
    // the scene contains just a rotating box
    private final String hostname; // where the server can be found
    private final int port; // the port att the server that we use
    private Game game;
    private float time = 30f;
    private PlayerDisk localPlayer1, localPlayer2, localPlayer3;
    
    private MessageQueue messageQueue = new MessageQueue();
    private boolean keysInitialized = false;
    private boolean running = false;
    private final float ACCFACTOR = 0.9f;
    
    private float player1AccelerationLeft, player1AccelerationRight, player1AccelerationUp, 
            player1AccelerationDown;
    private float player2AccelerationLeft, player2AccelerationRight, player2AccelerationUp, 
            player2AccelerationDown;
    private float player3AccelerationLeft, player3AccelerationRight, player3AccelerationUp, 
            player3AccelerationDown;
    
    

    public static void main(String[] args) {
        Util.initialiseSerializables();
        TheClient app = new TheClient(Util.HOSTNAME, Util.PORT);
        app.start();
    }

    public TheClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        game = new Game();
        game.setEnabled(false);
        stateManager.attach(game);
        running=false;
    }
    private void initGame(int numberOfPlayers, Vector3f[] startingPositions, 
                            Vector3f[] startingVelocities, float time) {

        running=true;
        game.spawnPlayers(numberOfPlayers);
        
        for (int i=0; i<startingPositions.length; i++) {
            game.getDisks().get(i).setPosition(startingPositions[i]);
            game.getDisks().get(i).setVelocity(startingVelocities[i]);
        }
        this.time=time;
    }
    private void initKeys() {
        if (keysInitialized) {
            return;
        }
        
        inputManager.addMapping("Left1",  new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Up1",  new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down1",  new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Right1",  new KeyTrigger(KeyInput.KEY_D));
        
        inputManager.addMapping("Left2",  new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("Up2",  new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Down2",  new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("Right2",  new KeyTrigger(KeyInput.KEY_H));
        
        inputManager.addMapping("Left3",  new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Up3",  new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Down3",  new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Right3",  new KeyTrigger(KeyInput.KEY_L));
        
               
        inputManager.addListener(analogListener, "Left1", "Right1", "Up1", "Down1",
                "Left2", "Right2", "Up2", "Down2","Left3", "Right3", "Up3", "Down3");
        
        keysInitialized=true;
    }
    
    
    
    
    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void simpleInitApp() {
        Util.print("Initializing");
        cam.setLocation(new Vector3f(-84f, 0.0f, 720f));
        cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
        setDisplayStatView(false);
        setDisplayFps(false);

        try {
            Util.print("Opening server connection");
            serverConnection = Network.connectToServer(hostname, port);
            Util.print("Server is starting networking");
            Util.print("Building scene graph");

            // TODO build scene graph


            Util.print("Adding network listener");
            // this make the client react on messages when they arrive by
            // calling messageReceived in ClientNetworkMessageListener
            serverConnection
                    .addMessageListener(new ClientNetworkMessageListener(),
                            ChangeMessage.class,
                            AckMessage.class,
                            HeartMessage.class,
                            HeartAckMessage.class,
                            StartGameMessage.class,
                            StopGameMessage.class,
                            ChangeVelocityMessage.class,
                            UpdateMessage.class);

 
            
            setPauseOnLostFocus(false);
            // disable the flycam which also removes the key mappings
            getFlyByCamera().setEnabled(false);

            serverConnection.start();
            new Thread(new NetWrite()).start();

            Util.print("Client communication back to server started");
        } catch (IOException ex) {
            ex.printStackTrace();
            this.destroy();
            this.stop();
        }

    }

        private final AnalogListener analogListener = new AnalogListener() {
        @Override
            public void onAnalog(String name, float value, float tpf) {
                if (running) {

                    if(name.equals("Left1")){
                        localPlayer1.accLeft(tpf,400f);
                        player1AccelerationLeft+=tpf;
                        if (player1AccelerationLeft>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer1.id, 
                            player1AccelerationLeft, "Left", localPlayer1.desiredVelocity));
                        }
                        //todo check if greater than something

                    }

                    if(name.equals("Down1")){
                        localPlayer1.accDown(tpf,400f);
                        player1AccelerationDown+=tpf;
                        if (player1AccelerationDown>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer1.id, 
                            player1AccelerationDown, "Down", localPlayer1.desiredVelocity));
                        }

                    }

                    if(name.equals("Right1")){
                        localPlayer1.accRight(tpf, 400f);
                        player1AccelerationRight+=tpf;
                        if (player1AccelerationRight>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer1.id, 
                            player1AccelerationRight, "Right", localPlayer1.desiredVelocity));
                        }

                    }

                    if(name.equals("Up1")){
                        localPlayer1.accUp(tpf, 400f);
                        player1AccelerationUp+=tpf;
                        if (player1AccelerationUp>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer1.id, 
                            player1AccelerationUp, "Up", localPlayer1.desiredVelocity));
                        }

                    }
                    if(name.equals("Left2")){
                        localPlayer2.accLeft(tpf,400f);
                        player2AccelerationLeft+=tpf;
                        if (player2AccelerationLeft>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer2.id, 
                            player2AccelerationLeft, "Left", localPlayer2.desiredVelocity));
                        }

                    }

                    if(name.equals("Down2")){
                        localPlayer2.accDown(tpf, 400f);
                        player2AccelerationDown+=tpf;
                        if (player2AccelerationDown>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer2.id, 
                            player2AccelerationDown, "Down", localPlayer2.desiredVelocity));
                        }
                    }

                    if(name.equals("Right2")){
                        localPlayer2.accRight(tpf, 400f);
                        player2AccelerationRight+=tpf;
                        if (player2AccelerationRight>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer2.id, 
                            player2AccelerationRight, "Right", localPlayer2.desiredVelocity));
                        }

                    }

                    if(name.equals("Up2")){
                        localPlayer2.accUp(tpf, 400f);
                        player2AccelerationUp+=tpf;
                        if (player2AccelerationUp>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer2.id, 
                            player2AccelerationUp, "Up", localPlayer2.desiredVelocity));
                        }

                    }
                    if(name.equals("Left3")){
                        localPlayer3.accLeft(tpf,400f);
                        player3AccelerationLeft+=tpf;
                        if (player3AccelerationLeft>(10*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer3.id, 
                            player3AccelerationLeft, "Left", localPlayer3.desiredVelocity));
                        }

                    }

                    if(name.equals("Down3")){
                        localPlayer3.accDown(tpf,400f);
                        player3AccelerationDown+=tpf;
                        if (player3AccelerationDown>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer3.id, 
                            player3AccelerationDown, "Down", localPlayer3.desiredVelocity));
                        }

                    }

                    if(name.equals("Right3")){
                        localPlayer3.accRight(tpf, 400f);
                        player3AccelerationRight+=tpf;
                        if (player3AccelerationRight>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer3.id, 
                            player3AccelerationRight, "Right", localPlayer3.desiredVelocity));
                        }

                    }

                    if(name.equals("Up3")){
                        localPlayer3.accUp(tpf, 400f);
                        player3AccelerationUp+=tpf;
                        if (player3AccelerationUp>(ACCFACTOR*tpf)) {
                            messageQueue.enqueue(new ChangeVelocityMessage(localPlayer3.id, 
                            player3AccelerationUp, "Up", localPlayer3.desiredVelocity));
                        }

                    }
                }
            }
    };


  

    @Override
    public void simpleUpdate(float tpf) {
       if (running) {
           time-=tpf;
           game.time=time;
            if (time <= 0) {           
                time = 0f;
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    // This class is a packet handler
    private class ClientNetworkMessageListener
            implements MessageListener<Client> {

        // this method is called whenever network packets arrive
        @Override
        public void messageReceived(Client source, final Message m) {
            // these if statements is a clumsy but simple (and working) 
            // solution; better would be to code behavour in the message 
            // classes and call them on the message
            if (m instanceof ChangeMessage) {
                // 1) carry out the change and 2) send back an ack to sender
                ChangeMessage msg = (ChangeMessage) m;
                int originalSender = msg.getSenderID();
                int messageID = msg.getMessageID();
                Util.print("Getting ChangeMessage from "
                        + originalSender
                        + " with messageID "
                        + messageID);
                // Enqueue a callable to main thread that changes box color

                // NB! ALL CHANGES TO THE SCENE GRAPH MUST BE DONE IN THE 
                // MAIN THREAD! THE TECHNIQUE IS TO SEND OVER A PIECE OF CODE 
                // (A "CALLABLE") FROM THE NETWORKING THREAD (THIS THREAD) TO 
                // THE MAIN THREAD (THE ONE WITH THE SCENE GRAPH) AND HAVE 
                // THE MAIN THREAD EXECUTE IT. (This is part of how threads 
                // communicate in Java and NOT something specific to 
                // JMonkeyEngine)
                                
                Future result = TheClient.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        
                        return true;
                    }
                });
                
                // Send ack to original sender via the server
                int thisClient = serverConnection.getId();
                serverConnection.send(new AckMessage(originalSender,
                        thisClient, messageID));
                Util.print("Sending AckMessage back via server to "
                        + originalSender
                        + " regarding their messageID "
                        + messageID);
            } else if (m instanceof AckMessage) {
                // no need to do anything
                AckMessage msg = (AckMessage) m;
                Util.print("Getting AckMessage from "
                        + msg.getSenderID()
                        + " regarding my messageID "
                        + msg.getMessageID());
            } else if (m instanceof HeartMessage) {
                // send back an ack to server
                HeartMessage msg = (HeartMessage) m;
                Util.print("Getting HeartMessage from the server "
                        + "- sending HeartAckMessage back");
                serverConnection.
                        send(new HeartAckMessage(serverConnection.getId()));
            } else if (m instanceof HeartAckMessage) {
                // must be a programming error(!)
                throw new RuntimeException("Client got HeartAckMessage "
                        + "- should not be possible!");
            } else if (m instanceof StartGameMessage) {
                 Future result = TheClient.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        game.setEnabled(true);
                        running=true;
                        int numberOfPlayers = ((StartGameMessage) m).playerIDs.length;
                        String[] myIDs = ((StartGameMessage) m).yourIDs;
                        Vector3f[] startingPositions = ((StartGameMessage) m).startPositions;
                        Vector3f[] startingVelocities = ((StartGameMessage) m).startVelocities;
                        float serverTime = ((StartGameMessage) m).time;
                        
                        Util.print("starting game");
                        initGame(numberOfPlayers, startingPositions,
                                startingVelocities, serverTime);
                        
                        for (int i=0; i< ((StartGameMessage) m).playerIDs.length; i++) {
                            System.out.println(((StartGameMessage) m).playerIDs[i]);
                        }
                        System.out.println("changing localplayers");
                        localPlayer1 = game.getPlayerById(myIDs[0]);
                        localPlayer2 = game.getPlayerById(myIDs[1]);
                        localPlayer3 = game.getPlayerById(myIDs[2]);
                                                
                        initKeys();

                        return true;
                    }
                });
               
                
            }
            else if (m instanceof StopGameMessage) {
                 Future result = TheClient.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                         Util.print("stopping game");
                         game.setEnabled(false);
                         running=false;                         
                         return true;
                    }
                });                
            }else if (m instanceof ChangeVelocityMessage) {
                 Future result = TheClient.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return true;
                    }
                });                
            }else if (m instanceof UpdateMessage) {
                 Future result = TheClient.this.enqueue(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        for (int i=0; i<((UpdateMessage) m).positions.length; i++) {
                            game.getDisks().get(i).setPosition(((UpdateMessage) m).positions[i]);
                            game.getDisks().get(i).setVelocity(((UpdateMessage) m).velocities[i]);
                        
                            Disk a = game.getDisks().get(i);
                                a.desiredVelocity = ((UpdateMessage) m).desiredVelocities[i];
                            
                        }
                        return true;
                    }
                });      
                
                
            } else {
                // must be a programming error(!)
                throw new RuntimeException("Unknown message.");
            }
        }
    }

    // takes down all communication channels gracefully, when called
    @Override
    public void destroy() {
        serverConnection.close();
        super.destroy();
    }
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
                    m.setReliable(false);
                    serverConnection.send(m);
                }
                
            }
        }
    }
}
