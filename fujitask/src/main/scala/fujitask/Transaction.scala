package fujitask

trait Transaction

trait ReadTransaction extends Transaction

trait ReadWriteTransaction extends ReadTransaction
