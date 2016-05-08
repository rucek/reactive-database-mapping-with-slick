package org.kunicki.conference
import org.kunicki.conference.db.DatabaseSchema
import slick.driver.H2Driver.api._
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.Success

trait Magic {
  self: DatabaseSchema =>

  def db: Database

  def printResults[T](f: Future[Iterable[T]]): Unit = {
    Await.result(f, Duration.Inf).foreach(println)
    println()
  }

  def createSchemaIfNotExists: Future[Unit] = {
    db.run(MTable.getTables).flatMap(tables =>
      if (tables.isEmpty) {
        db.run(allSchemas.create).andThen {
          case Success(_) => println("Schema created\n")
        }
      } else {
        println("Schema already exists\n")
        Future.successful()
      }
    )
  }
}
