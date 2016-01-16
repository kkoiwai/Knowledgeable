package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._

case class UserPassword(
  id: Long,
  userid: String,
  password: String,
  hash_version: Integer,
  disabled: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)

object UserPassword extends SkinnyCRUDMapper[UserPassword] with TimestampsFeature[UserPassword] {

  override lazy val defaultAlias = createAlias("up")

  /*
   * If you're familiar with ScalikeJDBC/Skinny ORM, using #autoConstruct makes your mapper simpler.
   * (e.g.)
   * override def extract(rs: WrappedResultSet, rn: ResultName[UserPassword]) = autoConstruct(rs, rn)
   *
   * Be aware of excluding associations like this:
   * (e.g.)
   * case class Member(id: Long, companyId: Long, company: Option[Company] = None)
   * object Member extends SkinnyCRUDMapper[Member] {
   *   override def extract(rs: WrappedResultSet, rn: ResultName[Member]) =
   *     autoConstruct(rs, rn, "company") // "company" will be skipped
   * }
   */
  override def extract(rs: WrappedResultSet, rn: ResultName[UserPassword]): UserPassword = new UserPassword(
    id = rs.get(rn.id),
    userid = rs.get(rn.userid),
    password = rs.get(rn.password),
    hash_version = rs.get(rn.hash_version),
    disabled = rs.get(rn.disabled),
    createdAt = rs.get(rn.createdAt),
    updatedAt = rs.get(rn.updatedAt)
  )
}
