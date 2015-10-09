package org.kunicki.conference.db

import org.kunicki.conference.domain.{Room, Speaker, Talk, TalkSpeaker}
import slick.dbio.DBIO
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait InitialData {
  self: DatabaseSchema =>

  def db: Database

  def insertInitialData(): Future[Unit] = {
    val setup = DBIO.seq(
      votes.delete, talksSpeakers.delete, talks.delete, speakers.delete, rooms.delete,

      rooms += Room(1, "Room 1"),
      rooms += Room(2, "Room 2"),

      speakers ++= Seq(
        Speaker(1, "Bolesław Dawidowicz"),
        Speaker(2, "Tomasz Dziurko"),
        Speaker(3, "Jacek Kunicki")
      ),

      talks ++= Seq(
        Talk(1, 1, "Reactive database mapping"),
        Talk(2, 1, "Security in modern applications"),
        Talk(3, 2, "Piękny pan od HR"),
        Talk(4, 2, "ORMs FTW")
      ),

      talksSpeakers ++= Seq(
        TalkSpeaker(1, 3),
        TalkSpeaker(2, 1),
        TalkSpeaker(3, 2),
        TalkSpeaker(4, 3)
      )
    )

    db.run(setup).andThen {
      case Success(_) => println("Initial data inserted")
      case Failure(e) => println(s"Initial data not inserted: ${e.getMessage}")
    }
  }
}
