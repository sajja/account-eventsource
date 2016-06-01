package com.example


import java.util.concurrent.TimeUnit

import akka.actor.{Props, ActorRef, Actor}
import akka.actor.Actor.Receive
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Future

/**
  * Created by sajith on 5/31/16.
  */
trait ReadStoreModule extends Events with Domain {
  def readStore: ReadStore

  trait ReadStore extends EventListener {
    def get: Subscriber

    def query(event: Events#Event): Future[Any]
  }

}

trait ReadStorageModule extends Domain {
  def readStorage: ReadStorage

  trait ReadStorage {
    def get(accNo: String): Option[Account]

    def store(account: Account): Unit
  }

}

trait InMemoryReadStorageModule extends ReadStorageModule {

  override def readStorage: ReadStorage = InMemoryReadStorage

  object InMemoryReadStorage extends ReadStorage {
    var accounts: Map[String, Account] = Map()

    override def get(accNo: String): Option[Account] = accounts.get(accNo)

    override def store(account: Account): Unit = accounts = accounts + (account.accountNo -> account)
  }

}

trait ActorBasedReadStore extends ReadStoreModule with EventActorSystem {
  this: ReadStorageModule =>

  override def readStore: ReadStore = readStoreActor

  override type Subscriber = ActorRef

  object readStoreActor extends ReadStore {
    implicit val timeout = Timeout(2, TimeUnit.SECONDS)
    lazy val delegate = system.actorOf(Props(new ReadStoreActor(readStorage)), "ReadStoreActor")

    override def get: ActorRef = delegate

    override def query(event: Events#Event): Future[Any] = delegate ? event
  }


  class ReadStoreActor(storage: ReadStorage) extends Actor {
    override def receive: Receive = {
      case AccountCreatedEvent(accNo, name) =>
        storage.store(Account(accNo, name, 0, 0))

      case CashDepositeEvent(accNo, amount) =>
        storage.get(accNo) match {
          case Some(account) => storage.store(Account(accNo, account.name, amount + account.balance))
          case None =>
        }
      case CashWithdrawEvent(accNo, amount) => storage.get(accNo) match {
        case Some(account) if account.balance > amount => storage.store(Account(accNo, account.name, account.balance - amount))
        case _ =>
      }

      case FindAccountEvent(accNo) =>
        sender ! storage.get(accNo)

      case _ => println("HORROR")
    }
  }

}
