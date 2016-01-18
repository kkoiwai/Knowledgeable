package controller

import skinny._
import skinny.controller.AssetsController

object Controllers {

  def mount(ctx: ServletContext): Unit = {
    user_passwordUserPassword.mount(ctx)
    users.mount(ctx)
    root.mount(ctx)
    AssetsController.mount(ctx)
  }

  object root extends RootController with Routes {
    val indexUrl = get("/?")(index).as('index)
  }

  object users extends _root_.controller.UsersController with Routes {

    override val updateUrl = post("/users/:id") {
      params.getAs[Long]("id").map(id => updateResource(id)).getOrElse(haltWithBody(404))
    }.as('update)

    override val destroyUrl = delete("/users/:id") {
      params.getAs[Long]("id").map(id => destroyResource(id)).getOrElse(haltWithBody(404))
    }.as('destroy)

    override val indexUrl = get("/users")(showResources).as('index)
  }


  object user_passwordUserPassword extends UserPasswordController with Routes {
    val indexUrl = get("/user_password")(index).as('index)
  }

}
