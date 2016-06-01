package com.example

import scala.util.{Success, Try}


trait EventWriterModule extends Events {
  def eventStore: EventStore

  trait EventStore {
    def store(event: Events#Event): Try[Unit]
  }

}

trait InMemoryEventWriterModule extends EventWriterModule {
  this: EventQueModule =>

  override def eventStore = InMemoryEventStore

  object InMemoryEventStore extends EventStore {
    var events: Map[String, List[Events#Event]] = Map()


    override def store(event: Events#Event): Try[Unit] = {
      events.get(event.aggregateId) match {
        case Some(e) => events = events + (event.aggregateId -> (event :: e))
        case None => events = events + (event.aggregateId -> List(event))
      }
      eventQueue.publishEvent(event)
      Success()
    }
  }

}
