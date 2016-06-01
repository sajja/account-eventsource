package com.example

trait Events {

  abstract class Event(val aggregateId: String)

  case class AccountCreatedEvent(accountNo: String, customerName: String) extends Event(accountNo)

  case class FindAccountEvent(accountNo: String) extends Event(accountNo)

  case class CashWithdrawEvent(accountNo: String, amount: Int) extends Event(accountNo)

  case class CashDepositeEvent(accountNo: String, amount: Int) extends Event(accountNo)

  case class AccountNotFoundEvent(accountNo: String)

  case class ConflitDetectedEvent()


  case class Subscribe(subscriber: Domain#EventListener)

}
