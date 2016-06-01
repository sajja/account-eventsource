package com.example

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

trait EventActorSystem {
  lazy val system = ActorSystem("AccountEventStore")
}

trait EventQueModule extends Events with Domain {

  def eventQueue: EventQueue

  trait EventQueue {
    def publishEvent(event: Events#Event)

    def subscribe(subscriber: Domain#EventListener)
  }

}

trait ActorBasedEventQueueModule extends EventQueModule {
  this: EventActorSystem =>

  override type Subscriber = ActorRef
  private lazy val eventQueuActor = system.actorOf(Props(new EventQueueActor(List())), "EventQueueActor")

  override def eventQueue = ActorEventQueue

  object ActorEventQueue extends EventQueue {
    override def publishEvent(event: Events#Event): Unit = eventQueuActor ! event

    override def subscribe(subscriber: Domain#EventListener): Unit = eventQueuActor ! Subscribe(subscriber)
  }

  class EventQueueActor(var subscribers: List[ActorRef]) extends Actor with Domain {
    override type Subscriber = ActorRef

    override def receive: Actor.Receive = {
      case e: Event => subscribers.foreach(_ ! e)
      case actor: ActorRef => subscribers = actor :: subscribers
      case _ =>
    }
  }

}