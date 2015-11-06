package domain.service

import domain.entity.Message
import domain.repository.UserRepository
import domain.repository.MessageRepository
import domain.repository.scalikejdbc.UserRepositoryImpl
import domain.repository.scalikejdbc.MessageRepositoryImpl
import fujitask.scalikejdbc._
import scala.concurrent.Future

object MessageService {

  val userRepository: UserRepository = UserRepositoryImpl

  val messageRepository: MessageRepository = MessageRepositoryImpl

  def create(message: String, userName: String): Future[Message] =
    messageRepository.create(message, userName).run()

  def read(id: Long): Future[Option[Message]] =
    messageRepository.read(id).run()

  def readAll: Future[List[Message]] =
    messageRepository.readAll.run()

  def update(message: Message): Future[Unit] =
    messageRepository.update(message).run()

  def delete(id: Long): Future[Unit] =
    messageRepository.delete(id).run()

  def createByUserId(message: String, userId: Long): Future[Message] = {
    val task =
      for {
        userOpt <- userRepository.read(userId)
        user = userOpt.getOrElse(throw new IllegalArgumentException("User Not Found"))
        message <- messageRepository.create(message, user.name)
      } yield message

    task.run()
  }
}
