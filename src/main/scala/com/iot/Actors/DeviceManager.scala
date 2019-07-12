package com.iot.Actors

import akka.actor.{Actor, Props}

object DeviceManager {
  def props: Props = Props(new DeviceManager)

  //the type of messages send and received by the DeviceManager
  final case class RequestTrackDevice(groupId: String, deviceId: String)

  case object DeviceRegistered

}

class DeviceManager extends Actor {


  override def receive: Receive = {
    Actor.emptyBehavior
  }
}
