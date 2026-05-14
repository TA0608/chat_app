package in.tech_camp.chat_app.form;

import in.tech_camp.chat_app.validation.ValidationPriority1;
import in.tech_camp.chat_app.validation.ValidationPriority2;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ユーザー情報の編集画面用フォームオブジェクト
 */
@Data
public class UserEditForm {

    /**
     * 更新対象のユーザーID
     * どのユーザーを編集するかを識別するために使用
     */
    private Integer id;

    /**
     * ユーザー名
     * ValidationPriority1: まず最初に「空でないか」をチェック
     */
    @NotBlank(message = "Name can't be blank", groups = ValidationPriority1.class)
    private String name;

    /**
     * メールアドレス
     * ValidationPriority1: 空文字チェック
     * ValidationPriority2: 空でなければ、正しいメール形式かをチェック
     */
    @NotBlank(message = "Email can't be blank", groups = ValidationPriority1.class)
    @Email(message = "Email should be valid", groups = ValidationPriority2.class)
    private String email;
}