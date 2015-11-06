package domain.repository

import fujitask.Task
import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction
import domain.entity.User

trait UserRepository {

  def create(name: String): Task[ReadWriteTransaction, User]

  def read(id: Long): Task[ReadTransaction, Option[User]]

  def readAll: Task[ReadTransaction, List[User]]

  def update(user: User): Task[ReadWriteTransaction, Unit]

  def delete(id: Long): Task[ReadWriteTransaction, Unit]

}
