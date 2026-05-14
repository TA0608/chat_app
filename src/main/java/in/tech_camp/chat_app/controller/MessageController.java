package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.MessageEntity;
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.MessageForm;
import in.tech_camp.chat_app.repository.MessageRepository;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor // 全てのフィールドを引数に持つコンストラクタを生成（Dependency Injectionのため）
public class MessageController {

    // 各リポジトリの依存注入（final＋@AllArgsConstructorにより自動で行われる）
    private final UserRepository userRepository;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;

  private final MessageRepository messageRepository;

    /**
     * メッセージ一覧画面を表示する
     * * @param roomId パス変数から取得した現在のチャットルームID
     * @param currentUser ログイン中のユーザー情報
     * @param model ビューにデータを渡すためのオブジェクト
     * @return 表示するHTMLテンプレートのパス
     */
    @GetMapping("/rooms/{roomId}/messages")
    public String showMessages(
        @PathVariable("roomId") Integer roomId,
        @AuthenticationPrincipal CustomUserDetail currentUser, 
        Model model
    ) {
        // 1. ログイン中のユーザー情報を取得し、ビューに渡す（名前表示用など）
        UserEntity user = userRepository.findById(currentUser.getId());
        model.addAttribute("user", user);

        // 2. ログインユーザーが参加しているルーム一覧を取得する
        // 中間テーブルからユーザーIDを元にデータを検索
        List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
        
        // 中間テーブルのリストから、RoomEntity（ルーム本体）のリストに変換
        List<RoomEntity> roomList = roomUserEntities.stream()
            .map(RoomUserEntity::getRoom) // RoomUserEntityからRoomEntityを取り出す
            .collect(Collectors.toList());
        
        // ルーム一覧をビューに渡す（サイドバーの表示用など）
        model.addAttribute("rooms", roomList);

        // 3. メッセージ投稿用の空フォームオブジェクトをビューに渡す（th:object用）
        model.addAttribute("messageForm", new MessageForm());
        
        // 4. 現在開いているルームIDをビューに渡す（投稿先URLの指定用など）
        model.addAttribute("roomId", roomId);

        // messages/index.html を表示
        return "messages/index";
    }
    @PostMapping("/rooms/{roomId}/messages")
    public String saveMessage(@PathVariable("roomId") Integer roomId, @ModelAttribute("messageForm") MessageForm messageForm, @AuthenticationPrincipal CustomUserDetail currentUser) {

      MessageEntity message = new MessageEntity();
      message.setContent(messageForm.getContent());

      UserEntity user = userRepository.findById(currentUser.getId());
      RoomEntity room = roomRepository.findById(roomId);
      message.setUser(user);
      message.setRoom(room);

      try {
          messageRepository.insert(message);
      } catch (Exception e) {
        System.out.println("エラー:" + e);
      }
      return "redirect:/rooms/" + roomId + "/messages";
    }
    
}