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

/**
  * if targetValue is changed from original, it should exist
  *
  * @param fieldName フィールド名
  * // @param paramType パラメータの型
  * @param targetValue 対象モデルの主キー値
  * @param id 対象モデルの主キー値
  * @param model モデル
  */
case class requiredIfChanged[Entity](fieldName: String, targetValue: Option[String],
                                     id: Option[Long], model: SkinnyCRUDMapper[Entity]) extends ValidationRule {
  def name = "requiredIfChanged"

  def isValid(v: Any): Boolean =
    // if the previous value of fieldName is same as targetValue, always valid.
    if (model.countBy(sqls.eq(model.primaryKeyField, id.getOrElse(-1)).
      and.eq(model.defaultAlias.field(fieldName), targetValue.getOrElse("")).
      and.eq(model.defaultAlias.field("disabled"), false)) == 1) true
      //if not, this field is required.
    else !isEmpty(v)

}