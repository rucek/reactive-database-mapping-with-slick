package org.kunicki.conference.db

import org.kunicki.conference.domain.Vote
import slick.driver.H2Driver.api._

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
}
