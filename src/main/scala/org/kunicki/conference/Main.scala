package org.kunicki.conference

import org.kunicki.conference.db.{DatabaseSchema, InitialData}
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
