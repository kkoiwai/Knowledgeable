package model

import skinny.orm._, feature._
import scalikejdbc._
import org.joda.time._
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


case class UserPassword(
  id: Long,
  userid: String,
  password: String,
  hash_version: Int,
  disabled: Boolean,
  createdAt: DateTime,
  updatedAt: DateTime
)

object UserPassword extends SkinnyCRUDMapper[UserPassword] with TimestampsFeature[UserPassword] {
  override lazy val tableName = "user_password"

  override lazy val defaultAlias = createAlias("up")

  //override def columnNames = Seq("id", "userid", "password", "hash_version", "disabled", "creted_at", "updated_at")

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

  def setPassword(userid:String, password:String): Unit ={
    val hashedPassword = hashPassword(userid,password)
    UserPassword.updateBy(sqls.eq( UserPassword.column.userid, userid).and.eq(UserPassword.column.disabled, false)).withAttributes('disabled -> true)
    UserPassword.createWithAttributes('userid -> userid, 'password -> hashedPassword, 'hash_version -> 1, 'disabled -> false)
  }


  private def hashPassword(userid:String, password:String): String ={

    val algorithm = "PBKDF2WithHmacSHA512"
    val iterationCount = 10000
    val keyLength = 256
    val saltAlgorithm = "SHA-256"
    val hashedSalt = MessageDigest.getInstance(saltAlgorithm).digest(userid.getBytes)
    val keySpec = new PBEKeySpec(password.toCharArray(), hashedSalt, iterationCount, keyLength)
    SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec).getEncoded.map("%02x".format(_)).mkString

  }
}
