package org.kunicki.conference

import org.kunicki.conference.db.ConferenceDao
import org.kunicki.conference.domain.Vote

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Success

class Voter(conferenceDao: ConferenceDao) {

  def vote: Future[Seq[Vote]] = {
    val newVotes = Seq(
      Vote(talkId = 1, positive = true),
      Vote(talkId = 2, positive = true),
      Vote(talkId = 3, positive = true),
      Vote(talkId = 4, positive = true),
      Vote(talkId = 1, positive = true),
      Vote(talkId = 1, positive = true),
      Vote(talkId = 1, positive = true),
      Vote(talkId = 2, positive = true),
      Vote(talkId = 1, positive = false),
      Vote(talkId = 2, positive = false),
      Vote(talkId = 4, positive = false),
      Vote(talkId = 3, positive = false),
      Vote(talkId = 2, positive = false)
    )

    Future.sequence(newVotes.map(conferenceDao.addVote)).andThen {
      case Success(_) => println("Voting complete")
    }
  }
}
