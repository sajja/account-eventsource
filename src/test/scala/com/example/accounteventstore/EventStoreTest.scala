package com.example.accounteventstore

import com.example._
import org.scalatest.{BeforeAndAfterAll, FlatSpec}

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class EventStoreTest extends FlatSpec with BeforeAndAfterAll with org.scalatest.Matchers {

  "Create account" should "be able to be quired" in {
    val accountSystem = new EventSystem with ActorBasedEventQueueModule with ActorBasedReadStore with InMemoryReadStorageModule with EventActorSystem with InMemoryEventWriterModule {}
    val query = accountSystem.QueryEngine
    val commandModule = accountSystem.commandModule

    val accountNo = commandModule.createAccount("Sajith's account")
    accountNo should not be null
    delay()
    Await.result(query.findAccount(accountNo), 1 seconds) match {
      case Some(account: accountSystem.Account) =>
        account.accountNo should equal(accountNo)
      case _ => fail("No matching account")
    }
  }

  "Withdraw on empty account" should "fail" in {
    val accountSystem = new EventSystem with ActorBasedEventQueueModule with ActorBasedReadStore with InMemoryReadStorageModule with EventActorSystem with InMemoryEventWriterModule {}
    val query = accountSystem.QueryEngine
    val commandModule = accountSystem.commandModule

    val accountNo = commandModule.createAccount("Sajith's account")
    commandModule.withdraw(accountNo, 100) match {
      case Success(_) => fail("Cannot withdraw on an empty account")
      case Failure(_) =>
    }
  }

  "Deposite" should "increase balance" in {
    val accountSystem = new EventSystem with ActorBasedEventQueueModule with ActorBasedReadStore with InMemoryReadStorageModule with EventActorSystem with InMemoryEventWriterModule {}
    val query = accountSystem.QueryEngine
    val commandModule = accountSystem.commandModule

    val accountNo = commandModule.createAccount("Sajith's account")
    delay()
    commandModule.deposite(accountNo, 100) match {
      case Success(_) =>
        delay()
        Await.result(query.findAccount(accountNo), 1 seconds) match {
          case Some(account: accountSystem.Account) =>
            account.balance should equal(100)
          case _ => fail("Account shoult be able to be created")
        }
      case Failure(x) => x.printStackTrace(); fail()
    }
  }

  "Withdraw " should "deduct balance" in {
    val accountSystem = new EventSystem with ActorBasedEventQueueModule with ActorBasedReadStore with InMemoryReadStorageModule with EventActorSystem with InMemoryEventWriterModule {}
    val query = accountSystem.QueryEngine
    val commandModule = accountSystem.commandModule

    val accountNo = commandModule.createAccount("Sajith's account")
    commandModule.deposite(accountNo, 100)
    delay(500)
    commandModule.withdraw(accountNo, 10) match {
      case Success(_) =>
        findAccount(query, accountNo) match {
          case Some(account) => account.balance should equal(90)
          case None =>
        }
      case Failure(ex) => fail("Should be able to withdraw");ex.printStackTrace()
    }
  }

  def findAccount(queryModule: EventSystem#Query, accNo: String): Option[EventSystem#Account] = {
    Await.result(queryModule.findAccount(accNo), 1 seconds) match {
      case Some(account: EventSystem#Account) => Some(account)
      case _ => None
    }
  }

  def delay(time: Int = 10) = Thread.sleep(time)
}
