package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
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
import in.tech_camp.chat_app.entity.RoomEntity;
import in.tech_camp.chat_app.entity.RoomUserEntity;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.RoomForm;
import in.tech_camp.chat_app.repository.RoomRepository;
import in.tech_camp.chat_app.repository.RoomUserRepository;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
public class RoomController {
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomUserRepository roomUserRepository;

    /**
     * ルーム新規作成画面を表示する
     */
    @GetMapping("/rooms/new")
    public String showRoomNew(@AuthenticationPrincipal CustomUserDetail currentUser, Model model){
        // 自分（ログインユーザー）以外のユーザーリストを取得して、招待候補として画面に渡す
        List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
        model.addAttribute("users", users);
        // 空のフォームオブジェクトを渡す
        model.addAttribute("roomForm", new RoomForm());
        return "rooms/new";
    }

    /**
     * ルームを保存する処理
     */
    @PostMapping("/rooms")
    public String createRoom(
        @ModelAttribute("RoomForm") @Validated(ValidationOrder.class) RoomForm roomForm,
        BindingResult bindingResult,
        @AuthenticationPrincipal CustomUserDetail currentUser,
        Model model
    ){
        // 1. バリデーションエラーがある場合の処理
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
            
            // 再表示用に必要なデータを揃える
            List<UserEntity> users = userRepository.findAllExcept(currentUser.getId());
            model.addAttribute("users", users);
            model.addAttribute("roomForm", roomForm);
            model.addAttribute("errorMessages", errorMessages);
            return "rooms/new";
        }

        // 2. roomsテーブルへ基本情報を保存
        RoomEntity roomEntity = new RoomEntity();
        roomEntity.setName(roomForm.getName());
        try {
            roomRepository.insert(roomEntity); // ここでルームが作成される
        } catch (Exception e) {
            System.out.println("エラー：" + e);
            return "rooms/new"; // エラー時は作成画面に戻す
        }

        // 3. room_usersテーブルへ参加メンバーを保存（中間テーブルへの紐付け）
        List<Integer> memberIds = roomForm.getMemberIds(); // 画面で選択されたユーザーIDのリスト
        for (Integer userId : memberIds) {
            UserEntity userEntity = userRepository.findById(userId);
            RoomUserEntity roomUserEntity = new RoomUserEntity();
            
            roomUserEntity.setRoom(roomEntity); // 作成したルームをセット
            roomUserEntity.setUser(userEntity); // 招待されたユーザーをセット
            
            try {
                roomUserRepository.insert(roomUserEntity);
            } catch (Exception e) {
                System.out.println("エラー：" + e);
                // ※ 本来はここでロールバック（保存の取り消し）を検討するポイント
                return "rooms/new";
            }
        }
        // 全て成功したらトップページへリダイレクト
        return "redirect:/";
    }

    /**
     * トップページ（参加ルーム一覧画面）を表示する
     */
    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetail currentUser, Model model) {
        // ログインユーザーの情報を取得
        UserEntity user = userRepository.findById(currentUser.getId());
        model.addAttribute("user", user);

        // 自分が参加しているルーム一覧を取得してビューに渡す
        List<RoomUserEntity> roomUserEntities = roomUserRepository.findByUserId(currentUser.getId());
        List<RoomEntity> roomList = roomUserEntities.stream()
            .map(RoomUserEntity::getRoom)
            .collect(Collectors.toList());
        
        model.addAttribute("rooms", roomList);
        return "rooms/index";
    }

    @PostMapping("/rooms/{roomId}/delete")
    public String deleteRoom(@PathVariable (value = "roomId") Integer roomId) {
        roomRepository.deleteById(roomId);
        return "redirect:/" ;  
    }
    
}