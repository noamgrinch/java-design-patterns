package com.iluwatar.activeobject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ActiveCreature class is the base of the active object example.
 * @author Noam Greenshtain
 *
 */
public abstract class ActiveCreature {
  
  private final Logger logger = LoggerFactory.getLogger(ActiveCreature.class.getName());

  private BlockingQueue<Runnable> requests;
  
  private String name;
  
  private Thread thread; // Thread of execution.
  
  private int status; // status of the thread of execution.

  /**
   * Constructor and initialization.
   */
  protected ActiveCreature(String name) {
    this.name = name;
    this.status = 0;
    this.requests = new LinkedBlockingQueue<>();
    thread = new Thread(() -> {
      boolean infinite = true;
      while (infinite) {
        try {
          requests.take().run();
        } catch (InterruptedException e) {
          if (this.status != 0) {
            logger.error("Thread was interrupted. -->" + e.getMessage()); 
          }
          infinite = false;
          Thread.currentThread().interrupt();
        }
      }
    });
    thread.start();
  }

  /**
   * Eats the porridge.
   * @throws InterruptedException due to firing a new Runnable.
   */
  public void eat() throws InterruptedException {
    requests.put(() -> {
      logger.info("{} is eating!",name());
      logger.info("{} has finished eating!",name());
    });
  }

  /**
   * Roam in the wastelands.
   * @throws InterruptedException due to firing a new Runnable.
   */
  public void roam() throws InterruptedException {
    requests.put(() ->
        logger.info("{} has started to roam in the wastelands.",name())
    );
  }
  
  /**
   * Returns the name of the creature.
   * @return the name of the creature.
   */
  public String name() {
    return this.name;
  }
  
  /**
   * Kills the thread of execution.
   * @param status of the thread of execution. 0 == OK, the rest is logging an error.
   */
  public void kill(int status) {
    this.status = status;
    this.thread.interrupt();
  }
  
  /**
   * Returns the status of the thread of execution.
   * @return the status of the thread of execution.
   */
  public int getStatus() {
    return this.status;
  }
}
