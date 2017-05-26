package com.abinail.crawler.products;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import com.abinail.crawler.models.events.Event;
import com.abinail.crawler.models.events.LinkProcessedEvent;
import com.abinail.crawler.models.events.NewLinkFoundEvent;
import javax.annotation.Resource;

@Component("eventProcessor")
public class EventProcessor {

  @Resource(name = "messageQueue")
  private BlockingQueue<Event> eventBlockingQueue;
  private Consumer<String> linkProcessedEventHandler;
  private Consumer newLinkFoundEventHandler;
  private Consumer endProcessEventHandler;
  Thread selfThread;

  public void setLinkProcessedEventHandler(
      Consumer<String> linkProcessedEventHandler) {
    this.linkProcessedEventHandler = linkProcessedEventHandler;
  }

  public void setNewLinkFoundEventHandler(
      Consumer newLinkFoundEventHandler) {
    this.newLinkFoundEventHandler = newLinkFoundEventHandler;
  }

  public void setEndProcessEventHandler(Consumer endProcessEventHandler) {
    this.endProcessEventHandler = endProcessEventHandler;
  }

  public void start() {
    selfThread = new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            log();
          } catch (InterruptedException e) {
            System.out.println("logger interupted");
            endProcessEventHandler.accept(null);
            return;
          }
        }
      }
    });
    selfThread.start();
  }

  public void interrupt() {
    if (selfThread != null) {
      selfThread.interrupt();
    }
  }

  private void log() throws InterruptedException {
    Event event = eventBlockingQueue.take();
    Class<? extends Event> clazz = event.getClass();
    if (clazz == LinkProcessedEvent.class) {
      linkProcessedEventHandler.accept(((LinkProcessedEvent) event).getUrlProcessed().toString());
    } else if (clazz == NewLinkFoundEvent.class) {
      newLinkFoundEventHandler.accept(null);
    } else {
      throw new InterruptedException();
    }
  }


}
