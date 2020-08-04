package model.component.auth

import play.api.data.FormError

object FormErrors {
  
  // Email Duplicated Error
  val errorEmailDuplicated = FormError("email", "このメールアドレスはすでに登録されています")

  // Login Error
  val errorLoginEmail      = FormError("email",    "メールアドレスまたはパスワードが間違っています")
  val errorLoginPassword   = FormError("password", "メールアドレスまたはパスワードが間違っています")
}

