package in.tech_camp.chat_app.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.repository.UserRepository;
import lombok.AllArgsConstructor;

/**
 * ユーザーに関するビジネスロジック（業務処理）を担当するサービス
 */
@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // パスワードを暗号化するためのツール
    private final PasswordEncoder passwordEncoder;

    /**
     * パスワードを暗号化した上で、ユーザーを新規登録する
     * @param userEntity 画面から渡された、生のパスワードが入ったユーザー情報
     */
    public void createUserWithEncryptedPassword(UserEntity userEntity) {
        // 1. 生のパスワードを暗号化（ハッシュ化）する
        String encodedPassword = encodePassword(userEntity.getPassword());
        
        // 2. エンティティのパスワードを、暗号化済みのものに上書きする
        userEntity.setPassword(encodedPassword);
        
        // 3. 安全になった状態でリポジトリ経由でDBに保存する
        userRepository.insert(userEntity);
    }

    /**
     * 文字列を暗号化する内部メソッド
     * @param password 生のパスワード（例: "password123"）
     * @return 暗号化された文字列（例: "$2a$10$x..."）
     */
    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }
}