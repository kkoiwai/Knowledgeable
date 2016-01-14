package validator

import skinny._
import skinny.orm.feature
import skinny.validator._
import scalikejdbc._
import org.joda.time._

/**
  * check if unique
  *
  * @param fieldName フィールド名
  * @param paramType パラメータの型
  * @param id 対象モデルの主キー値
  * @param model モデル
  */
case class unique[Entity](fieldName: String, paramType: ParamType, id: Option[Long],
                          model: SkinnyCRUDMapper[Entity]) extends ValidationRule {

  def name = "unique"

  def isValid(v: Any): Boolean = isEmpty(v) ||
    (model.countBy(sqls.ne(model.primaryKeyField, id.getOrElse(-1)).
      and.eq(model.defaultAlias.field(fieldName), paramType.unapply(v)).
      and.eq(model.defaultAlias.field("disabled"), false)) == 0)
}