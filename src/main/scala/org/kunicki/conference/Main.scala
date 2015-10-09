package org.kunicki.conference

import org.kunicki.conference.db.{ConferenceDao, DatabaseSchema, InitialData}
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Success

object Main extends App with DatabaseSchema with InitialData {

  val db = Database.forConfig("h2")

  val future = createSchemaIfNotExists().flatMap(_ => insertInitialData())
  Await.ready(future, Duration.Inf)

  val dao = new ConferenceDao(db)
  val voter = new Voter(dao)

  printResults(voter.vote)

  printResults(dao.makeAllVotesPositive().flatMap(_ => dao.findAllVotes))

  val onlyPositiveVotes = for {
    _ <- db.run(votes.delete)
    _ <- voter.vote
    _ <- dao.deleteNegativeVotes()
    v <- dao.findAllVotes
  } yield v

  printResults(onlyPositiveVotes)

  printResults(dao.talksWithRooms)

  printResults(dao.talksWithRooms2)

  def printResults[T](f: Future[Seq[T]]): Unit = {
    Await.result(f, Duration.Inf).foreach(println)
    println()
  }

  def createSchemaIfNotExists(): Future[Unit] = {
    db.run(MTable.getTables).flatMap {
      case tables if tables.isEmpty =>
        val schema = rooms.schema ++ speakers.schema ++ talks.schema ++ talksSpeakers.schema ++ votes.schema
        db.run(schema.create).andThen {
          case Success(_) => println("Schema created")
        }
      case tables if tables.nonEmpty =>
        println("Schema already exists")
        Future.successful()
    }
  }
}
