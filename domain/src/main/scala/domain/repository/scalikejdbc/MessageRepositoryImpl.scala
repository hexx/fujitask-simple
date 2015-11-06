package domain.repository.scalikejdbc

import domain.entity.Message
import domain.repository.MessageRepository
import fujitask.Task
import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction
import fujitask.scalikejdbc._
import scalikejdbc._

object MessageRepositoryImpl extends MessageRepository {

  def create(message: String, userName: String): Task[ReadWriteTransaction, Message] =
    ask.map { implicit session =>
      val sql = sql"""insert into messages (user_name, message) values ($userName, $message)"""
      val id = sql.updateAndReturnGeneratedKey.apply()
      Message(id, userName, message)
    }

  def read(id: Long): Task[ReadTransaction, Option[Message]] =
    ask.map { implicit session =>
      val sql = sql"""select * from messages where id = $id"""
      sql.map(rs => Message(rs.long("id"), rs.string("user_name"), rs.string("message"))).single.apply()
    }

  def readAll: Task[ReadTransaction, List[Message]] =
    ask.map { implicit session =>
      val sql = sql"""select * from messages"""
      sql.map(rs => Message(rs.long("id"), rs.string("user_name"), rs.string("message"))).list.apply()
    }

  def update(message: Message): Task[ReadWriteTransaction, Unit] =
    ask.map { implicit session =>
      val sql = sql"""update messages set user_name = ${message.userName}, message = ${message.message} where id = ${message.id}"""
      sql.update.apply()
    }

  def delete(id: Long): Task[ReadWriteTransaction, Unit] =
    ask.map { implicit session =>
      val sql = sql"""delete messages where id = $id"""
      sql.update.apply()
    }

}
