import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.iot.Actors.{Device, DeviceManager}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scala.concurrent.duration._


class DeviceSpec(_system: ActorSystem) extends TestKit(_system)
  with Matchers with WordSpecLike with BeforeAndAfterAll {

  def this() = this(ActorSystem("AkkaQuickstartSpec"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "reply with empty reading if no temperature is known" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    //checking if the temperature has been wrongly set before
    //checking if the read temperature functionality is working fine
    deviceActor.tell(Device.ReadTemperature(requestId = 1), probe.ref)
    val response1 = probe.expectMsgType[Device.RespondTemperature]
    response1.requestId should ===(1L)
    response1.value should ===(None)
  }

  "reply with latest temperature reading" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    //checking if the temperature is being recorded properly
    deviceActor.tell(Device.RecordTemperature(requestId = 1, 24.0), probe.ref)
    probe.expectMsg(Device.TemperatureRecorded(requestId = 1))

    //checking if the read temperature functionality is working fine
    //in the previous test the temperature value was set to be 24
    deviceActor.tell(Device.ReadTemperature(requestId = 2), probe.ref)
    val response2 = probe.expectMsgType[Device.RespondTemperature]
    response2.requestId should ===(2L)
    response2.value should ===(Some(24.0))

    //recording temperature again
    deviceActor.tell(Device.RecordTemperature(requestId = 3, 55.0), probe.ref)
    probe.expectMsg(Device.TemperatureRecorded(requestId = 3))

    //testing if the correct temperature is being read
    deviceActor.tell(Device.ReadTemperature(requestId = 4), probe.ref)
    val response3 = probe.expectMsgType[Device.RespondTemperature]
    response3.requestId should ===(4L)
    response3.value should ===(Some(55.0))
  }


  "reply to registration requests" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    deviceActor.tell(DeviceManager.RequestTrackDevice("group", "device"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered)
    probe.lastSender should ===(deviceActor)
  }

  "ignore wrong registration requests" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    deviceActor.tell(DeviceManager.RequestTrackDevice("wrongGroup", "device"), probe.ref)
    probe.expectNoMessage(500.milliseconds)


    deviceActor.tell(DeviceManager.RequestTrackDevice("group", "WrongdeviceId"), probe.ref)
    probe.expectNoMessage(500.milliseconds)
  }
}
