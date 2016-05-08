package org.kunicki.conference.db

import org.kunicki.conference.domain.{TalkWithRoom, Vote}
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

  def addVote(vote: Vote): Future[Vote] = {
    val query = (votes returning votes.map(_.id) into ((vote, id) => vote.copy(id = Some(id)))) += vote
    db.run(query)
  }

  def findAllVotes: Future[Seq[Vote]] = db.run(votes.result)

  def makeAllVotesPositive = {
    val query = votes.filterNot(_.positive).map(_.positive).update(true)
    query.statements.foreach(println)
    db.run(query)
  }

  def countPositiveVotesByTalk: Future[Map[String, Int]] = {
    val talksWithPositiveVotes = for {
      (t, v) <- talks join votes.filter(_.positive) on (_.id === _.talkId)
    } yield (t.title, v.id)

    val grouped = talksWithPositiveVotes.groupBy(_._1)

    val counted = grouped.map{case (title, voteIds) => (title, voteIds.size)}

    val sorted = counted.sortBy(_._2.desc)
    sorted.result.statements.foreach(println)

    val result: Future[Seq[(String, Int)]] = db.run(sorted.result)

    result.map(_.toMap)
  }
}
