package com.iot.Actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.iot.Actors.DeviceManager.RequestTrackDevice

object DeviceGroup {
  def props(groupId: String): Props = Props(new DeviceGroup(groupId))
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {
  var deviceIdToActor = Map.empty[String, ActorRef]

  override def receive: Receive = {
    case trackMsg@RequestTrackDevice(`groupId`, _) =>
      deviceIdToActor.get(trackMsg.deviceId) match {
        case Some(deviceActor) => deviceActor.forward(trackMsg)
        case None =>
          log.info("Creating device actor for {}", trackMsg.deviceId)
          val deviceActor = context.actorOf(Device.props(groupId, trackMsg.deviceId),
            s"device-${trackMsg.deviceId}")
          deviceIdToActor += trackMsg.deviceId -> deviceActor
          deviceActor.forward(trackMsg)
      }

    case RequestTrackDevice(groupId, deviceId) =>
      log.warning("Ignoring TrackDevice reqest for {}. This actor is responsible for {}.",
        groupId, deviceId)
  }
}
