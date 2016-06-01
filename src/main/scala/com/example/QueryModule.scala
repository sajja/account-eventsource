package com.example

import java.util.concurrent.TimeUnit
import akka.util.Timeout
import scala.concurrent.Future
import akka.pattern.ask

/**
  * Created by sajith on 5/30/16.
  */
trait QueryModule extends Events {
  this: ReadStoreModule =>

  trait Query {
    def findAccount(accId: String): Future[Any]
  }

  object QueryEngine extends Query {

    def findAccount(accountId: String): Future[Any] = {
      readStore.query(FindAccountEvent(accountId))
    }
  }
}



