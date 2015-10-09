package org.kunicki.conference.db

import org.kunicki.conference.domain.{TalkWithRoom, Vote}
import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConferenceDao(db: Database) extends DatabaseSchema {

  def addVote(vote: Vote): Future[Vote] = {
    val voteWithId = votes returning votes.map(_.id) into ((vote, id) => vote.copy(id = Some(id))) += vote
    db.run(voteWithId)
  }

  def makeAllVotesPositive(): Future[Int] = {
    val query = votes.filterNot(_.positive).map(_.positive).update(true)
    db.run(query)
  }

  def deleteNegativeVotes(): Future[Int] = {
    val query = votes.filterNot(_.positive).delete
    db.run(query)
  }

  def findAllVotes: Future[Seq[Vote]] = {
    db.run(votes.result)
  }

  def talksWithRooms: Future[Seq[(String, String)]] = {
    val query = for {
      t <- talks
      r <- rooms if r.id === t.roomId // or just: r <- r.room
    } yield (t.title, r.name)

    query.result.statements.foreach(println)
    db.run(query.result)
  }

  def talksWithRooms2: Future[Seq[TalkWithRoom]] = {
    val query = for {
      (t, r) <- talks join rooms on (_.roomId === _.id)
    } yield (t, r)

    db.run(query.result).map(_.map(TalkWithRoom.tupled))
  }
}
