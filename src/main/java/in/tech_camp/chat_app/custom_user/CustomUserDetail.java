package in.tech_camp.chat_app.custom_user;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.tech_camp.chat_app.entity.UserEntity;
import lombok.Data;

@Data
/**
 * Spring Securityの認証システムで利用するユーザー詳細クラス
 * 独自のUserEntityをラップして、Securityが理解できる形式に変換する
 */
public class CustomUserDetail implements UserDetails {

    // アプリ独自のユーザー実体
    private final UserEntity user;
    
    public CustomUserDetail(UserEntity user){
        this.user = user;
    }

    /**
     * ユーザーに付与されている権限（ロール）を返す
     * 今回は権限管理を行わないため空のリストを返却
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.emptyList();
    }

    /**
     * 認証に利用するパスワードを返す
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 認証に利用するユーザー名（今回はメールアドレス）を返す
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * ログインユーザーのIDを返す（独自追加メソッド）
     */
    public Integer getId() {
        return user.getId();
    }

    /**
     * ログインユーザーの名前を返す（独自追加メソッド）
     */
    public String getName() {
        return user.getName();
    }

    // --- 以下、アカウントの有効性を判定するメソッド群 ---
    // 今回は全てシンプルに true (有効) を返す設定にしています。

    /** アカウントの有効期限が切れていないか */
    @Override
    public boolean isAccountNonExpired(){
        return true;
    }

    /** アカウントがロックされていないか */
    @Override
    public boolean isAccountNonLocked(){
        return true;
    }

    /** 資格情報（パスワード）の有効期限が切れていないか */
    @Override
    public boolean isCredentialsNonExpired(){
        return true;
    }

    /** ユーザーが有効化されているか */
    @Override
    public boolean isEnabled(){
        return true;
    }
}