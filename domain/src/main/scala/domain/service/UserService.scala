package domain.service

import domain.entity.User
import domain.repository.UserRepository
import domain.repository.scalikejdbc.UserRepositoryImpl
import fujitask.scalikejdbc._
import scala.concurrent.Future

object UserService {

  val userRepository: UserRepository = UserRepositoryImpl

  def create(name: String): Future[User] =
    userRepository.create(name).run()

  def read(id: Long): Future[Option[User]] =
    userRepository.read(id).run()

  def readAll: Future[List[User]] =
    userRepository.readAll.run()

  def update(user: User): Future[Unit] =
    userRepository.update(user).run()

  def delete(id: Long): Future[Unit] =
    userRepository.delete(id).run()

  def create3(name1: String, name2: String, name3: String): Future[(User, User, User)] =
    (for {
      user1 <- userRepository.create(name1)
      user2 <- userRepository.create(name2)
      user3 <- userRepository.create(name3)
    } yield (user1, user2, user3)).run()

  import scala.concurrent.ExecutionContext.Implicits.global

  def create3CommitEach(name1: String, name2: String, name3: String): Future[(User, User, User)] =
    for {
      user1 <- userRepository.create(name1).run()
      user2 <- userRepository.create(name2).run()
      user3 <- userRepository.create(name3).run()
    } yield (user1, user2, user3)

}
