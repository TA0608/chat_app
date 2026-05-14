package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
import in.tech_camp.chat_app.validation.ValidationOrder;
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
  public String showMessages(@PathVariable(value = "roomId") Integer roomId,@AuthenticationPrincipal CustomUserDetail currentUser, Model model){
    UserEntity user = userRepository.findById(currentUser.getId());
    model.addAttribute("user", user);
    List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
    List<RoomEntity> roomList = roomUserEntities.stream()
        .map(RoomUserEntity::getRoom)
        .collect(Collectors.toList());
    model.addAttribute("rooms", roomList);

    model.addAttribute("messageForm", new MessageForm());
    
    RoomEntity room = roomRepository.findById(roomId);
    model.addAttribute("room", room);

    List<MessageEntity> messages = messageRepository.findByRoomId(roomId);
    model.addAttribute("messages", messages);
    return "messages/index";
  }
    
    @PostMapping("/rooms/{roomId}/messages")
  public String saveMessage(@PathVariable(value = "roomId") Integer roomId, @ModelAttribute("messageForm") @Validated(ValidationOrder.class) MessageForm messageForm, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetail currentUser) {
    if (bindingResult.hasErrors()) {
      return "redirect:/rooms/" + roomId + "/messages";
    }

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