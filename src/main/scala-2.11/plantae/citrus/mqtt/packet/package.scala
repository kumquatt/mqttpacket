package plantae.citrus.mqtt

import scodec.Codec
import scodec.bits._
import scodec.codecs._

package object packet {
  // MQTT 3.1.1 protocol name : MQTT (uint16, utf8)
  val protocolName: BitVector = variableSizeBytes(uint16, utf8).encode("MQTT").require
  // MQTT 3.1.1 protocol level : 4 (uint8)
  val protocolLevel: BitVector = uint8.encode(4).require

  val dupCodec = bool
  val qosCodec = ushort(2) // uint2
  val retainCodec = bool


  val userNameFlagCodec = bool
  val passwordFlagCodec = bool
  val willRetainCodec = bool
  val willQosCodec = ushort(2) //uint2
  val willFlagCodec = bool
  val cleanSessionCodec = bool
  val keepAliveCodec = uint16
  val connectAcknowledgeCodec = constant(bin"0000000")
  val sessionPresentFlagCodec = bool
  val clientIdCodec = variableSizeBytes(uint16, utf8)
  val topicCodec = variableSizeBytes(uint16, utf8)
  val messageCodec = variableSizeBytes(uint16, bytes)
  val userCodec = variableSizeBytes(uint16, utf8)
  val passwordCodec = variableSizeBytes(uint16, utf8)
  val returnCodeCodec = ushort(8) //uint8
  val packetIdCodec = uint16
  val remainingLengthCodec = new RemainingLengthCodec
  val payloadCodec = bytes
  val subscribeTopicFilterCodec: Codec[List[(String, Short)]] = list((topicCodec :: ignore(6) :: qosCodec).dropUnits.as[(String, Short)])
  val unsubscribeTopicFilterCodec: Codec[List[String]] = list(topicCodec)
  val topicReturnCodeCodec: Codec[List[Short]] = list(ushort(8)) //list(uint8)

  val fixedHeaderCodec = (dupCodec :: qosCodec :: retainCodec).as[FixedHeader]
  val connectVariableHeaderCodec = (
    constant(protocolName) :~>: constant(protocolLevel) :~>:
      userNameFlagCodec ::
      passwordFlagCodec ::
      willRetainCodec ::
      willQosCodec ::
      willFlagCodec ::
      cleanSessionCodec ::
      ignore(1) :~>:
      keepAliveCodec).as[ConnectVariableHeader]

}
