package fujitask

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/**
 * 『PofEAA』の「Unit of Work」パターンの実装
 *
 * トランザクションとはストレージに対するまとまった処理である
 * トランザクションオブジェクトとはトランザクションを表現するオブジェクトで、
 * 具体的にはデータベースライブラリのセッションオブジェクトなどが該当する
 *
 * @tparam Resource トランザクションオブジェクトの型
 * @tparam A トランザクションを実行して得られる値の型
 */
trait Task[-Resource, +A] { lhs =>
  /**
   * トランザクションの内部で実行される個々の処理の実装
   * このメソッドを実装することでTaskが作られる
   *
   * @param resource トランザクションオブジェクト
   * @param ec ExecutionContext
   * @return トランザクションの内部で実行される個々の処理で得られる値
   */
  def execute(resource: Resource)(implicit ec: ExecutionContext): Future[A]

  /**
   * Taskモナドを合成する
   * その際、変位指定によりResourceの型は両方のTaskのResourceの共通のサブクラスの型になる
   *
   * @param f モナド関数
   * @tparam ExtendedResource トランザクションオブジェクトの型
   * @tparam B 合成されたTaskを実行すると得られる値の型
   * @return 合成されたTask
   */
  def flatMap[ExtendedResource <: Resource, B](f: A => Task[ExtendedResource, B]): Task[ExtendedResource, B] =
    new Task[ExtendedResource, B] {
      def execute(resource: ExtendedResource)(implicit ec: ExecutionContext): Future[B] =
        lhs.execute(resource).map(f).flatMap(_.execute(resource))
    }

  /**
   * 関数をTaskの結果に適用する
   *
   * @param f 適用したい関数
   * @tparam B 関数を適用して得られた値の型
   * @return 関数が適用されたTask
   */
  def map[B](f: A => B): Task[Resource, B] = flatMap(a => Task(f(a)))

  /**
   * TaskRunnerを使ってTaskを実行する
   * implicitによりResourceに合ったTaskRunnerが選ばれる
   *
   * @param runner Taskを実行するためのTaskRunner
   * @tparam ExtendedResource トランザクションオブジェクトの型
   * @return 個々のTaskの処理の結果得られる値
   */
  def run[ExtendedResource <: Resource]()(implicit runner: TaskRunner[ExtendedResource]): Future[A] = runner.run(this)
}

object Task {
  /**
   * Taskのデータコンストラクタ
   *
   * @param a Taskの値
   * @tparam Resource トランザクションオブジェクトの型
   * @tparam A Taskの値の型
   * @return 実行するとaの値を返すTask
   */
  def apply[Resource, A](a: => A): Task[Resource, A] =
    new Task[Resource, A] {
      def execute(resource: Resource)(implicit executor: ExecutionContext): Future[A] =
        Future(a)
    }
}

/**
 * Taskを実行する
 * トランザクションオブジェクトの型ごとにインスタンスを作成すること
 *
 * @tparam Resource トランザクションオブジェクトの型
 */
trait TaskRunner[Resource] {
  /**
   * Taskを実行する
   *
   * @param task 実行するTask
   * @tparam A Task実行すると得られる値の型
   * @return Task実行して得られた値
   */
  def run[A](task: Task[Resource, A]): Future[A]
}
