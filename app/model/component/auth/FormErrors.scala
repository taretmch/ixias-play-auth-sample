package model.component.auth

import play.api.data.FormError

object FormErrors {
  
  // Email Duplicated Error
  val errorEmailDuplicated = FormError("email", "このメールアドレスはすでに登録されています")

  // User Not Found Error
  val errorUserNotFound    = FormError("email", "メールアドレスが間違っています")

  // Password Invalid Error
  val errorPasswordInvalid = FormError("password", "パスワードが間違っています")

  // Slug Duplicated Error
  val errorSlugDuplicated  = FormError("slug", "この名前はすでに登録されています")
}

