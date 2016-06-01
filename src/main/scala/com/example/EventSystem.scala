package com.example

trait EventSystem extends Events with Domain with QueryModule with CommandModule {
  self: EventQueModule with ReadStoreModule with EventWriterModule =>
  eventQueue.subscribe(readStore)
}
