package com.iot.Actors

import akka.actor.{Actor, ActorLogging, Props}

object IotSupervisor {
  def props(): Props = Props(new IotSupervisor)
}

class IotSupervisor extends Actor with ActorLogging {
  override def preStart(): Unit = log.info("IoT Application Started")

  override def postStop(): Unit = log.info("IoT Application Stopped")

  //No need to handle any messages
  //this class only acts as the head supervisor
  override def receive: Receive = Actor.emptyBehavior
}
