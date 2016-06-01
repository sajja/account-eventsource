package com.example

/**
  * Created by sajith on 5/30/16.
  */
trait Commands {


  abstract class AbstractCommand

  case class CreateAccountCommand(customerName: String) extends AbstractCommand

  case class WithdrawCommand(accountNo: String, amount: Int) extends AbstractCommand

  case class DepositeCommand(accountNo: String, amount: Int) extends AbstractCommand

}
