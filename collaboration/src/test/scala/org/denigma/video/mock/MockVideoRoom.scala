package org.denigma.video.mock

import org.denigma.video.rooms.VideoRoom
import akka.testkit.TestProbe
import play.api.libs.json.JsValue
import org.denigma.actors.models.RequestInfo

/**
 * forwards everything to probes
 */
class MockVideoRoom extends VideoRoom {

var probe:TestProbe = null


  def other(any:Any):Unit= {
    if(probe!=null) probe.ref forward any
  }

  override def receive:this.Receive = super.receive orElse {
  case testprobe:TestProbe=> probe = testprobe
  case any:Any => other(any)

  }

}
