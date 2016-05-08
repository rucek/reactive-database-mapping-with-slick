package org.kunicki.conference.db

import org.kunicki.conference.domain.{Room, Speaker, Talk, TalkSpeaker}
import slick.driver.H2Driver.api._

import scala.concurrent.Future

trait InitialData {
  self: DatabaseSchema =>

  def db: Database

  def insertInitialData(): Future[Unit] = {
    db.run(DBIO.seq(
      votes.delete, talksSpeakers.delete, talks.delete, speakers.delete, rooms.delete,

      rooms += Room(1, "Room 1"),
      rooms += Room(2, "Room 2"),

      speakers ++= Seq(
        Speaker(1, "Speaker 1"),
        Speaker(2, "Speaker 2"),
        Speaker(3, "Speaker 3")
      ),

      talks ++= Seq(
        Talk(1, 1, "Talk 1"),
        Talk(2, 1, "Talk 2"),
        Talk(3, 2, "Talk 3"),
        Talk(4, 2, "Talk 4")
      ),

      talksSpeakers ++= Seq(
        TalkSpeaker(1, 3),
        TalkSpeaker(2, 1),
        TalkSpeaker(3, 2),
        TalkSpeaker(4, 3)
      )
    ))
  }
}
