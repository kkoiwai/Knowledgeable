package controller

import skinny._
import skinny.validator._
import _root_.controller._
import model.User

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
    paramKey("userid") is required & maxLength(8),
    paramKey("name") is required & maxLength(512),
    paramKey("email") is required & maxLength(512) & email,
    paramKey("authority") is required & numeric & intValue
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
    paramKey("userid") is required & maxLength(8),
    paramKey("name") is required & maxLength(512),
    paramKey("email") is required & maxLength(512) & email,
    paramKey("authority") is required & numeric & intValue
  )
  override def updateFormStrongParameters = Seq(
    "userid" -> ParamType.String,
    "name" -> ParamType.String,
    "email" -> ParamType.String,
    "authority" -> ParamType.Int,
    "disabled" -> ParamType.Boolean
  )

}
