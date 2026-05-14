package in.tech_camp.chat_app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.form.LoginForm;
import in.tech_camp.chat_app.form.UserEditForm;
import in.tech_camp.chat_app.form.UserForm;
import in.tech_camp.chat_app.repository.UserRepository;
import in.tech_camp.chat_app.service.UserService;
import in.tech_camp.chat_app.validation.ValidationOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * ユーザー新規登録画面を表示する
     */
    @GetMapping("/users/sign_up")
    public String showSignUp(Model model){
        model.addAttribute("userForm", new UserForm());
        return "users/signUp";
    }

    /**
     * ユーザー新規登録を実行する
     */
    @PostMapping("/user")
    public String createUser(@ModelAttribute("userForm") @Validated(ValidationOrder.class) UserForm userForm, BindingResult result, Model model) {
        // 1. パスワード一致チェック（Formクラス内で定義された独自メソッド）
        userForm.validatePasswordConfirmation(result);
        
        // 2. メールアドレスの重複チェック
        if (userRepository.existsByEmail(userForm.getEmail())) {
            result.rejectValue("email", "null", "Email already exists");
        }

        // エラーがある場合はメッセージを抽出して登録画面に戻す
        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());

            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("userForm", userForm);
            return "users/signUp";
        }

        // Entityに詰め替えて、サービス層で暗号化して保存
        UserEntity userEntity = new UserEntity();
        userEntity.setName(userForm.getName());
        userEntity.setEmail(userForm.getEmail());
        userEntity.setPassword(userForm.getPassword());

        try {
            // パスワード暗号化を含む保存処理
            userService.createUserWithEncryptedPassword(userEntity);
        } catch (Exception e) {
            System.out.println("エラー：" + e);
            model.addAttribute("userForm", userForm);
            return "users/signUp";
        }

        return "redirect:/";
    }

    /**
     * ログイン画面を表示する
     */
    @GetMapping("/users/login")
    public String loginForm(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "users/login";
    }

    /**
     * ログイン失敗時などにエラーメッセージ付きで画面を表示する
     * @param error クエリパラメータ "?error" がある場合に受け取る
     */
    @GetMapping("/login")
    public String showLoginWithError(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null){
            model.addAttribute("loginError","Invalid email or password.");
        }
        return "users/login";
    }

    /**
     * ユーザー編集画面を表示する
     */
    @GetMapping("/users/{userId}/edit")
    public String editUserForm(@PathVariable("userId") Integer userId, Model model) {
        UserEntity user = userRepository.findById(userId);

        // Entityの情報をFormオブジェクトに詰め替えて画面に渡す
        UserEditForm userForm = new UserEditForm();
        userForm.setId(user.getId());
        userForm.setName(user.getName());
        userForm.setEmail(user.getEmail());

        model.addAttribute("user", userForm);
        return "users/edit";
    }

    /**
     * ユーザー情報を更新する
     */
    @PostMapping("/users/{userId}")
    public String updateUser(@PathVariable("userId") Integer userId, @ModelAttribute("user") @Validated(ValidationOrder.class) UserEditForm userEditForm, BindingResult result, Model model) {
        
        // 1. メールアドレス重複チェック（自分以外のユーザーが使っていないか）
        String newEmail = userEditForm.getEmail();
        if (userRepository.existsByEmailExcludingCurrent(newEmail, userId)) {
            result.rejectValue("email", "error.user", "Email already exists");
        }

        if (result.hasErrors()) {
            List<String> errorMessages = result.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .collect(Collectors.toList());
            model.addAttribute("errorMessages", errorMessages);
            model.addAttribute("user", userEditForm);
            return "users/edit";
        }
        
        // 既存のEntityを取得して、新しい値に書き換えて保存（update）
        UserEntity user = userRepository.findById(userId);
        user.setName(userEditForm.getName());
        user.setEmail(userEditForm.getEmail());

        try {
            userRepository.update(user);
        } catch (Exception e) {
            System.out.println("エラー：" + e);
            model.addAttribute("user", userEditForm);
            return "users/edit";
        }

        return "redirect:/";
    }
}