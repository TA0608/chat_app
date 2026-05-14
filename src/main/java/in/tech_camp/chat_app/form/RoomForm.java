package in.tech_camp.chat_app.form;

import java.util.List;

import in.tech_camp.chat_app.validation.ValidationPriority1;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * ルーム作成画面の入力内容を保持するクラス（Formオブジェクト）
 */
@Data
public class RoomForm {

    /**
     * ルーム名
     * @NotBlank: 空文字やスペースのみの入力を禁止する
     */
    @NotBlank(message = "Room Name can't be blank", groups = ValidationPriority1.class)
    private String name;

    /**
     * 選択されたメンバーのIDリスト
     * 画面上のチェックボックスなどで選択された複数のユーザーIDが格納される
     * 例: [1, 3, 5] のようにIDが並ぶ
     */
    private List<Integer> memberIds;
    
}