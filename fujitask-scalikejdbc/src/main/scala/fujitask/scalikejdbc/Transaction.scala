package fujitask.scalikejdbc

import fujitask.ReadTransaction
import fujitask.ReadWriteTransaction
import scalikejdbc._

abstract class ScalikeJDBCTransaction(val session: DBSession)

class ScalikeJDBCReadTransaction(session: DBSession) extends ScalikeJDBCTransaction(session) with ReadTransaction

class ScalikeJDBCReadWriteTransaction(session: DBSession) extends ScalikeJDBCTransaction(session) with ReadWriteTransaction
