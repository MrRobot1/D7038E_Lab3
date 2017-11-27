package mygame;
import java.util.ArrayList;
import mygame.Util;

    public class MessageQueue {
        private ArrayList<Util.MyAbstractMessage> queue;
        public MessageQueue() {
            queue = new ArrayList<Util.MyAbstractMessage>();
        }
        public synchronized void enqueue(Util.MyAbstractMessage m) {
            this.queue.add(m);
        }
        public synchronized boolean isEmpty() {
            return this.queue.isEmpty();
        }
        public synchronized Util.MyAbstractMessage pop() {
            try {
                return this.queue.remove(0);
            }
            catch (Exception e) {
                return null;
            }
        }
    }