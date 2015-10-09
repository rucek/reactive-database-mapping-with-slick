package org.kunicki.conference.domain

import java.time.LocalDateTime

case class Room(id: Int, name: String)

case class Speaker(id: Int, name: String)

case class Talk(id: Int, roomId: Int, title: String)

case class TalkSpeaker(talkId: Int, speakerId: Int)

case class Vote(id: Option[Int] = None, talkId: Int, castedAt: LocalDateTime = LocalDateTime.now(), positive: Boolean)

case class TalkWithRoom(talk: Talk, room: Room)
