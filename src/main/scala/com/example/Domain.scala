package com.example

/**
  * Created by sajith on 5/30/16.
  */
trait Domain {

  type Subscriber

  class Account(val accountNo: String, val name: String, val balance: Int, val version: Int) {
    override def toString = s"Account No:$accountNo Name: $name Balance: $balance"
  }

  trait EventListener {
    def get:Subscriber
  }

  object Account {
    def apply(accountNo: String, name: String, balance: Int, version: Int = 0) = new Account(accountNo, name, balance, version)

    def apply(account: Account, balance: Int) = new Account(account.accountNo, account.name, account.balance + balance, account.version + 1)
  }
}
