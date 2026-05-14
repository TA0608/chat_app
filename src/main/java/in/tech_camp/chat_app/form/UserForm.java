package in.tech_camp.chat_app.form;

import org.hibernate.validator.constraints.Length;
import org.springframework.validation.BindingResult;

import in.tech_camp.chat_app.validation.ValidationPriority1;
import in.tech_camp.chat_app.validation.ValidationPriority2;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ユーザー新規登録画面の入力内容を保持するクラス
 */
@Data
public class UserForm {
  
  /** ユーザー名 */
  @NotBlank(message = "Name can't be blank", groups = ValidationPriority1.class)
  private String name;

  /** メールアドレス */
  @NotBlank(message = "Email can't be blank", groups = ValidationPriority1.class)
  @Email(message = "Email should be valid", groups = ValidationPriority2.class)
  private String email;

  /** * パスワード 
   * ValidationPriority2: 空でなければ、6〜128文字以内かをチェック
   */
  @NotBlank(message = "Password can't be blank", groups = ValidationPriority1.class)
  @Length(min = 6, max = 128, message = "Password should be between 6 and 128 characters", groups = ValidationPriority2.class)
  private String password;

  /** 確認用パスワード（バリデーション用の一時保持） */
  private String passwordConfirmation;

  /**
   * パスワードと確認用パスワードが一致するか検証する
   * UserControllerから呼び出される
   * * @param result バリデーション結果を格納するオブジェクト
   */
  public void validatePasswordConfirmation(BindingResult result) {
    // パスワードと確認用が一致しない場合
    if (!password.equals(passwordConfirmation)) {
      // passwordConfirmation項目に対してエラーメッセージを紐付ける
      result.rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match Password");
    }
  }
}