package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
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
    private Game game = new Game();
    private float time = 0f;
    
    private boolean running = true;

    public static void main(String[] args) {
        Util.initialiseSerializables();
        TheClient app = new TheClient(Util.HOSTNAME, Util.PORT);
        app.start();
    }

    public TheClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        game.setEnabled(true);
        stateManager.attach(game);
        System.out.println("asd");
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
                            HeartAckMessage.class);

            // position the cam so the box is clearly visible
            //cam.setLocation(new Vector3f(0, 0, 10));

            Util.print("Mapping keys");
            // map two keys, R and T, two names in the strings REVERSE 
            // and CHANGE
            // TODO map keys
            Util.print("Adding key listener");
            // add a listener that reacts when R or T is pressed
            //TODO add listeners
            
            // make everything continue even if the mouse pointer moves 
            // outside the window or we give the focus to another window
            setPauseOnLostFocus(false);
            // disable the flycam which also removes the key mappings
            getFlyByCamera().setEnabled(false);

            // finally start the communication channel to the server
            serverConnection.start();
            Util.print("Client communication back to server started");
        } catch (IOException ex) {
            ex.printStackTrace();
            this.destroy();
            this.stop();
        }

    }

    /**
     * actionListener reacts to the two kinds of key presses on the keyboard
     * that we mapped in simpelInitApp
     */
    private final ActionListener actionListener = new ActionListener() {
        @Override
        @SuppressWarnings("ConvertToStringSwitch")
        public void onAction(String name, boolean isPressed, float tpf) {
            // every key pressed fires two (2) events: when the key is pressed 
            // and when it is released
            if (isPressed) { // react when a key is presed (not when released)
                Util.print("("
                        + name
                        + " was pressed by local user)");
                if (name.equals("TODO")) {
                    //TODO
                }
                else {
                    // this should not happen because we only have two mappings
                    throw new RuntimeException("actionListener: "
                            + "Unknown event, "
                            + name);
                }
            }
        }
    };

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
        public void messageReceived(Client source, Message m) {
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
}
