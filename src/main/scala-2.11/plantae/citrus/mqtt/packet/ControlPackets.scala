package plantae.citrus.mqtt.packet

import scodec.Codec
import scodec.bits.ByteVector
import scodec.codecs._

sealed trait ControlPacket {
}
object ControlPacket {
  implicit val discriminated: Discriminated[ControlPacket, Short] = Discriminated(ushort(4))
}

case class ConnectPacket(
                          fixedHeader: FixedHeader = FixedHeader(),
                          variableHeader: ConnectVariableHeader,
                          clientId: String,
                          willTopic: Option[String],
                          willMessage: Option[ByteVector],
                          userName: Option[String],
                          password: Option[String]
                          ) extends ControlPacket
object ConnectPacket {
  implicit val discriminator: Discriminator[ControlPacket, ConnectPacket, Short] = Discriminator(1)
  implicit val codec: Codec[ConnectPacket] = (
    fixedHeaderCodec ::
      variableSizeBytes(
        remainingLengthCodec,
        connectVariableHeaderCodec >>:~ {
          (vh) =>
            clientIdCodec ::
              conditional(vh.willFlag, topicCodec) ::
              conditional(vh.willFlag, messageCodec) ::
              conditional(vh.userNameFlag, userCodec) ::
              conditional(vh.passwordFlag, passwordCodec)
        })).as[ConnectPacket]
}

case class ConnAckPacket(
                          fixedHeader: FixedHeader = FixedHeader(),
                          sessionPresentFlag: Boolean,
                          returnCode: Short
                          ) extends ControlPacket
object ConnAckPacket {
  implicit val discriminator: Discriminator[ControlPacket, ConnAckPacket, Short] = Discriminator(2)
  implicit val codec: Codec[ConnAckPacket] = (
    fixedHeaderCodec ::
      variableSizeBytes(
        remainingLengthCodec,
        connectAcknowledgeCodec :~>:
          sessionPresentFlagCodec ::
          returnCodeCodec
      )).as[ConnAckPacket]
}

case class PublishPacket(
                        fixedHeader: FixedHeader,
                        topic: String,
                        packetId: Option[Int],
                        payload: ByteVector
                          ) extends ControlPacket
object PublishPacket {
  implicit val discriminator: Discriminator[ControlPacket, PublishPacket, Short] = Discriminator(3)
  implicit val codec: Codec[PublishPacket] = (
    fixedHeaderCodec >>:~ {
      (fh) => variableSizeBytes(
        remainingLengthCodec,
        topicCodec ::
          conditional(fh.qos != 0, packetIdCodec) ::
          payloadCodec
      )
    }).as[PublishPacket]
}

case class PubAckPacket(
                       fixedHeader: FixedHeader = FixedHeader(),
                       packetId: Int
                         ) extends ControlPacket
object PubAckPacket {
  implicit val discriminator: Discriminator[ControlPacket, PubAckPacket, Short] = Discriminator(4)
  implicit val codec: Codec[PubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
      packetIdCodec)).as[PubAckPacket]
}

case class PubRecPacket(
                       fixedHeader: FixedHeader = FixedHeader(),
                       packetId: Int
                         ) extends ControlPacket
object PubRecPacket {
  implicit val discriminator: Discriminator[ControlPacket, PubRecPacket, Short] = Discriminator(5)
  implicit val codec: Codec[PubRecPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubRecPacket]
}

case class PubRelPacket(
                       fixedHeader: FixedHeader = FixedHeader(qos=1),
                       packetId: Int
                         ) extends ControlPacket
object PubRelPacket {
  implicit val discriminator: Discriminator[ControlPacket, PubRelPacket, Short] = Discriminator(6)
  implicit val codec: Codec[PubRelPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubRelPacket]
}

case class PubCompPacket(
                        fixedHeader: FixedHeader = FixedHeader(),
                        packetId: Int
                          ) extends ControlPacket
object PubCompPacket {
  implicit val discriminator: Discriminator[ControlPacket, PubCompPacket, Short] = Discriminator(7)
  implicit val codec: Codec[PubCompPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[PubCompPacket]
}

case class SubscribePacket(
                          fixedHeader: FixedHeader = FixedHeader(qos=1),
                          packetId : Int,
                          topicFilter : List[(String, Short)]
                            ) extends ControlPacket
object SubscribePacket {
  implicit val discriminator: Discriminator[ControlPacket, SubscribePacket, Short] = Discriminator(8)
  implicit val codec: Codec[SubscribePacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec ::
    subscribeTopicFilterCodec)).as[SubscribePacket]
}

case class SubAckPacket(
                       fixedHeader: FixedHeader = FixedHeader(),
                       packetId: Int,
                       returnCode: List[Short]
                         ) extends ControlPacket
object SubAckPacket {
  implicit val discriminator: Discriminator[ControlPacket, SubAckPacket, Short] = Discriminator(9)
  implicit val codec: Codec[SubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec :: topicReturnCodeCodec
    )).as[SubAckPacket]
  // success maximum qos 0 => 0
  // success maximum qos 2 => 2
  // Failure => 128
}

case class UnsubscribePacket(
                            fixedHeader: FixedHeader = FixedHeader(qos=1),
                            packetId: Int,
                            topicFilter : List[String]
                              ) extends ControlPacket
object UnsubscribePacket {
  implicit val discriminator: Discriminator[ControlPacket, UnsubscribePacket, Short] = Discriminator(10)
  implicit val codec: Codec[UnsubscribePacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec :: unsubscribeTopicFilterCodec)).as[UnsubscribePacket]
}

case class UnsubAckPacket(
                         fixedHeader: FixedHeader = FixedHeader(),
                         packetId: Int
                           ) extends ControlPacket
object UnsubAckPacket {
  implicit val discriminator: Discriminator[ControlPacket, UnsubAckPacket, Short] = Discriminator(11)
  implicit val codec: Codec[UnsubAckPacket] = (
    fixedHeaderCodec ::
    variableSizeBytes(remainingLengthCodec,
    packetIdCodec)).as[UnsubAckPacket]
}

case class PingReqPacket(
                        fixedHeader: FixedHeader = FixedHeader(qos=1)
                          ) extends ControlPacket
object PingReqPacket {
  implicit val discriminator: Discriminator[ControlPacket, PingReqPacket, Short] = Discriminator(12)
  implicit val codec: Codec[PingReqPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[PingReqPacket]
}

case class PingRespPacket(
                         fixedHeader: FixedHeader = FixedHeader()
                           ) extends ControlPacket
object PingRespPacket {
  implicit val discriminator: Discriminator[ControlPacket, PingRespPacket, Short] = Discriminator(13)
  implicit val codec: Codec[PingRespPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[PingRespPacket]
}

case class DisconnectPacket(
                           fixedHeader: FixedHeader = FixedHeader()
                             ) extends ControlPacket
object DisconnectPacket {
  implicit val discriminator: Discriminator[ControlPacket, DisconnectPacket, Short] = Discriminator(14)
  implicit val codec: Codec[DisconnectPacket] = (
    fixedHeaderCodec :: ignore(8)
    ).dropUnits.as[DisconnectPacket]
}
