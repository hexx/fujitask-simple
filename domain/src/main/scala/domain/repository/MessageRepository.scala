package domain.repository

import domain.entity.Message
import fujitask.Task
import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction

trait MessageRepository {

  def create(message: String, userName: String): Task[ReadWriteTransaction, Message]

  def read(id: Long): Task[ReadTransaction, Option[Message]]

  def readAll: Task[ReadTransaction, List[Message]]

  def update(user: Message): Task[ReadWriteTransaction, Unit]

  def delete(id: Long): Task[ReadWriteTransaction, Unit]

}
