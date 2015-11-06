package domain.repository.scalikejdbc

import fujitask.Task
import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction
import fujitask.scalikejdbc._
import domain.entity.User
import domain.repository.UserRepository
import scalikejdbc._

object UserRepositoryImpl extends UserRepository {

  def create(name: String): Task[ReadWriteTransaction, User] =
    ask.map { implicit session =>
      val sql = sql"""insert into users (name) values ($name)"""
      val id = sql.updateAndReturnGeneratedKey.apply()
      User(id, name)
    }

  def read(id: Long): Task[ReadTransaction, Option[User]] =
    ask.map { implicit session =>
      val sql = sql"""select * from users where id = $id"""
      sql.map(rs => User(rs.long("id"), rs.string("name"))).single.apply()
    }

  def readAll: Task[ReadTransaction, List[User]] =
    ask.map { implicit session =>
      val sql = sql"""select * from users"""
      sql.map(rs => User(rs.long("id"), rs.string("name"))).list.apply()
    }

  def update(user: User): Task[ReadWriteTransaction, Unit] =
    ask.map { implicit session =>
      val sql = sql"""update users set name = ${user.name} where id = ${user.id}"""
      sql.update.apply()
    }

  def delete(id: Long): Task[ReadWriteTransaction, Unit] =
    ask.map { implicit session =>
      val sql = sql"""delete users where id = $id"""
      sql.update.apply()
    }

}
