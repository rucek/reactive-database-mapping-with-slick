package org.kunicki.conference.db

import org.kunicki.conference.domain.TalkWithRoom
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConferenceDao(db: Database) extends DatabaseSchema {

  def talksWithRooms: Future[Seq[(String, String)]] = {
    val query = for {
      t <- talks
      r <- t.room
    } yield (t.title, r.name)

    query.result.statements.foreach(println)
    db.run(query.result)
  }

  def talksWithRooms2: Future[Seq[TalkWithRoom]] = {
    val query = for {
      (t, r) <- talks join rooms on (_.roomId === _.id)
    } yield (t, r)

    query.result.statements.foreach(println)
    db.run(query.result).map(_.map(TalkWithRoom.tupled))
  }
}
