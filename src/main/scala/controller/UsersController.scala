package controller

import skinny._
import skinny.validator._
import scalikejdbc._
import _root_.controller._
import _root_.validator._
import model.{UserPassword, User}

class UsersController extends SkinnyResource with ApplicationController {
  protectFromForgery()

  override def model = User
  override def resourcesName = "users"
  override def resourceName = "user"

  override def resourcesBasePath = s"/${toSnakeCase(resourcesName)}"
  override def useSnakeCasedParamKeys = true

  override def viewsDirectoryPath = s"/${resourcesName}"

  override def createParams = Params(params)
  override def createForm = validation(createParams,
    paramKey("userid") is required & maxLength(8) & _root_.validator.unique("userid",ParamType.String,params.getAs("id"), model),
    paramKey("name") is required & maxLength(512),
    paramKey("email") is required & maxLength(512) & email,
    paramKey("authority") is required & numeric & intValue,
    paramKey("disabled") is required,
    paramKey("password") is required & minLength(8),
    param("password" -> (params.getAs[String]("password"), params.getAs[String]("confirm_password"))) are same
  )
  override def createFormStrongParameters = Seq(
    "userid" -> ParamType.String,
    "name" -> ParamType.String,
    "email" -> ParamType.String,
    "authority" -> ParamType.Int,
    "disabled" -> ParamType.Boolean
  )

  override def updateParams = Params(params)
  override def updateForm = validation(updateParams,
    paramKey("userid") is required & maxLength(8) & unique("userid", ParamType.String, params.getAs("id"), model),
    paramKey("name") is required & maxLength(512),
    paramKey("email") is required & maxLength(512) & email,
    paramKey("authority") is required & numeric & intValue,
    paramKey("disabled") is required,
    paramKey("password") is requiredIfChanged("userid", params.getAs[String]("userid"), params.getAs("id"), model),
    paramKey("password") is  minLength(8), // not required (unless password or _userid_ is changing)
    param("password" -> (params.getAs[String]("password"), params.getAs[String]("confirm_password"))) are same

  )
  override def updateFormStrongParameters = Seq(
    "userid" -> ParamType.String,
    "name" -> ParamType.String,
    "email" -> ParamType.String,
    "authority" -> ParamType.Int,
    "disabled" -> ParamType.Boolean
  )

  def showResources = {
    if (enablePagination) {
      val pageNo: Int = params.getAs[Int]("page").getOrElse(1)
      val pageSize: Int = 20
      val totalCount: Long = User.countBy(sqls.eq(User.column.disabled,false))
      val totalPages: Int = (totalCount / pageSize).toInt + (if (totalCount % pageSize == 0) 0 else 1)
      set("items", User.findAllByWithPagination(sqls.eq(User.defaultAlias.field("disabled"), false),Pagination.page(pageNo).per(pageSize)))
      set("totalPages" -> totalPages)
    } else {
      set("items", User.findAllBy(sqls.eq(User.column.disabled,false)))
    }
    render(s"/users/index")
  }


  def updateResource(id: Long) = {
    debugLoggingParameters(updateForm, Some(id))
    User.findById(id).map { _ =>
      if (updateForm.validate()) {
        val parameters = updateParams.permit(updateFormStrongParameters: _*)
        debugLoggingPermittedParameters(parameters, Some(id))

        // disable old record
        User.updateById(id).withAttributes('disabled -> true)

        // create new record with updated attributes
        val newid = {
          User.createWithPermittedAttributes(parameters)
        }

        // change password if not empty
        if (updateParams.getAs[String]("password").toString.length > 0) {
          UserPassword.setPassword(
            updateParams.getAs[String]("userid").getOrElse(""),
            updateParams.getAs[String]("password").getOrElse("")
          )
          flash += ("notice" -> createI18n().get("user_password.flash.updated").getOrElse("The password was updated."))
        }

        status = 200
        flash += ("notice" -> createI18n().get("user.flash.updated").getOrElse("The user was updated."))
        set("item", User.findById(newid).getOrElse(haltWithBody(404)))
        redirect302(s"/users/${newid}")
      } else {
        status = 400
        render("/users/edit")
      }
    } getOrElse haltWithBody(404)
  }

  def destroyResource(id: Long) = {
    User.findById(id).map { _ =>

      // disable old record
      User.updateById(id).withAttributes('disabled -> true)

      flash += ("notice" -> createI18n().get("user.flash.deleted").getOrElse("The user was deleted."))
      status = 200
    } getOrElse haltWithBody(404)
  }
}
