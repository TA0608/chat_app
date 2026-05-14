package in.tech_camp.chat_app.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.tech_camp.chat_app.custom_user.CustomUserDetail;
import in.tech_camp.chat_app.entity.UserEntity;
import in.tech_camp.chat_app.repository.UserRepository;
import lombok.AllArgsConstructor;

/**
 * Spring Securityがログイン認証時にユーザー情報を取得するためのサービス
 */
@Service
@AllArgsConstructor
public class UserAuthenticatinService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * ログイン時に入力されたユーザー識別子（今回はemail）を元に、ユーザー情報をロードする
     * @param email フォームから送られてきたメールアドレス
     * @return Spring Securityが認識できる UserDetails オブジェクト
     * @throws UsernameNotFoundException ユーザーが見つからなかった場合にスローする
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. リポジトリを使ってDBからメールアドレスでユーザーを検索
        UserEntity userEntity = userRepository.findByEmail(email);

        // 2. 存在しない場合は、Spring Security専用の例外を投げる
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // 3. 見つかったUserEntityを、以前作成した CustomUserDetail でラップして返却
        // これにより、セキュリティシステムが「ID」や「名前」も扱えるようになる
        return new CustomUserDetail(userEntity);
    }

}