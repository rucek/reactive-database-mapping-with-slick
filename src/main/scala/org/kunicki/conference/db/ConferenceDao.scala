package org.kunicki.conference.db

import org.kunicki.conference.domain.{Talk, TalkWithRoom, Vote}
import slick.backend.StaticDatabaseConfig
import slick.driver.H2Driver.api._
import slick.jdbc.GetResult

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

  def countPositiveVotesByTalk: Future[Map[String, Int]] = {
    val talksWithPositiveVotes = for {
      (t, v) <- talks join votes.filter(_.positive) on (_.id === _.talkId)
    } yield (t.title, v.id)

    val grouped = talksWithPositiveVotes.groupBy(_._1)

    val counted = grouped.map { case (title, voteIds) => (title, voteIds.size) }

    val sorted = counted.sortBy(_._2.desc)
    sorted.result.statements.foreach(println)

    val result: Future[Seq[(String, Int)]] = db.run(sorted.result)

    result.map(_.toMap)
  }

  def findTalksWithPlainSql: Future[Seq[Talk]] = {
    implicit val getTalkResult = GetResult(r => Talk(r.nextInt(), r.nextInt(), r.nextString()))

    val action = sql"SELECT * FROM TALKS".as[Talk]
    db.run(action)
  }

  @StaticDatabaseConfig("file:src/main/resources/application.conf#tsql")
  def findTalksWithTypedSql: Future[Seq[Talk]] = {
    val typedAction = tsql"select * from talks"
    db.run(typedAction).map(_.map(Talk.tupled))
  }
}
