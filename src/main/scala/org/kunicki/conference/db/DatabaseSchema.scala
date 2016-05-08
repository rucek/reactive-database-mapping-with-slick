package org.kunicki.conference.db

import java.sql.Timestamp
import java.time.{LocalDateTime, ZoneOffset}

import org.kunicki.conference.domain._
import slick.driver.H2Driver.api._

trait DatabaseSchema {

  class Rooms(tag: Tag) extends Table[Room](tag, "ROOMS") {

    def id = column[Int]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (id, name) <> (Room.tupled, Room.unapply)
  }

  val rooms = TableQuery[Rooms]

  class Speakers(tag: Tag) extends Table[Speaker](tag, "SPEAKERS") {

    def id = column[Int]("ID", O.PrimaryKey)

    def name = column[String]("NAME")

    def * = (id, name) <>(Speaker.tupled, Speaker.unapply)
  }

  val speakers = TableQuery[Speakers]

  class Talks(tag: Tag) extends Table[Talk](tag, "TALKS") {

    def id = column[Int]("ID", O.PrimaryKey)

    def roomId = column[Int]("ROOM_ID")

    def title = column[String]("TITLE")

    def room = foreignKey("FK_ROOM", roomId, rooms)(_.id)

    def * = (id, roomId, title) <>(Talk.tupled, Talk.unapply)
  }

  val talks = TableQuery[Talks]

  class TalksSpeakers(tag: Tag) extends Table[TalkSpeaker](tag, "TALKS_SPEAKERS") {

    def talkId = column[Int]("TALK_ID")

    def speakerId = column[Int]("SPEAKER_ID")

    def talk = foreignKey("FK_TALKS_SPEAKERS_TALK", talkId, talks)(_.id)

    def speaker = foreignKey("FK_TALKS_SPEAKERS_SPEAKER", speakerId, speakers)(_.id)

    def * = (talkId, speakerId) <>(TalkSpeaker.tupled, TalkSpeaker.unapply)
  }

  val talksSpeakers = TableQuery[TalksSpeakers]

  implicit val localDateTimeMapping = MappedColumnType.base[LocalDateTime, Timestamp](
    localDateTime => Timestamp.from(localDateTime.toInstant(ZoneOffset.UTC)),
    _.toLocalDateTime
  )

  class Votes(tag: Tag) extends Table[Vote](tag, "VOTES") {

    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)

    def talkId = column[Int]("TALK_ID")

    def castedAt = column[LocalDateTime]("CASTED_AT")

    def positive = column[Boolean]("POSITIVE")

    def talk = foreignKey("FK_TALK", talkId, talks)(_.id)

    def * = (id.?, talkId, castedAt, positive) <>(Vote.tupled, Vote.unapply)
  }

  val votes = TableQuery[Votes]

  val allSchemas = rooms.schema ++ speakers.schema ++ talks.schema ++ talksSpeakers.schema ++ votes.schema
}
