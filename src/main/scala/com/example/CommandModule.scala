package com.example

import java.util.UUID

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

trait CommandModule extends Commands with Domain {
  this: QueryModule with EventWriterModule =>

  def commandModule = new CommandSink()

  class CommandSink {
    def createAccount(customerName: String): String = {
      val accNo = UUID.randomUUID().toString
      eventStore.store(new AccountCreatedEvent(accNo, customerName))
      accNo
    }

    def deposite(accountNo: String, amount: Int): Try[Unit] = {
      Await.result(QueryEngine.findAccount(accountNo), 1 seconds) match {
        case Some(account: Account) =>
          eventStore.store(CashDepositeEvent(accountNo, amount))
          Success()
        case None => Failure(new Exception("Account not found"))
      }
    }

    def withdraw(accountNo: String, amount: Int): Try[Unit] = {
      Await.result(QueryEngine.findAccount(accountNo), 1 seconds) match {
        case Some(account: Account) =>
          if (account.balance > amount)
            eventStore.store(CashWithdrawEvent(accountNo, amount))
          else
            Failure(new Exception("Not enough credit"))
        case _ => Failure(new Exception("Account not found"))
      }
    }
  }

}
