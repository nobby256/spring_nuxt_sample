package com.example.demo.library.security.configurer;

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.web.server.autoconfigure.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * HttpSecurity設定のユーティリティクラス。
 */
public final class HttpSecurityCustomizeUtil {

    /**
     * コンストラクタ。
     */
    private HttpSecurityCustomizeUtil() {
    }

    /**
     * {@link AbstractConfiguredSecurityBuilder#getSharedObject(Class)}もしくは{@link ApplicationContext#getBean(Class)}からコンポーネントを取得する。
     * 
     * @param <C>   コンポーネントの型
     * @param http  {@link HttpSecurity}
     * @param clazz コンポーネントの型
     * @return コンポーネント
     */
    @Nullable
    public static <C> C getSharedOrBean(HttpSecurity http, Class<C> clazz) {
        C shared = http.getSharedObject(clazz);
        if (shared != null) {
            return shared;
        }
        return getBeanOrNull(http, clazz);
    }

    /**
     * {@link ApplicationContext#getBean(Class)}からコンポーネントを取得する。
     * 
     * @param <C>   コンポーネントの型
     * @param http  {@link HttpSecurity}
     * @param clazz コンポーネントの型
     * @return コンポーネント
     */
    @Nullable
    public static <C> C getBeanOrNull(HttpSecurity http, Class<C> clazz) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        if (context == null) {
            return null;
        }
        try {
            return context.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ex) {
            return null;
        }
    }

    /**
     * ログアウト時に削除するクッキー名の配列にセッションIDのクッキー名を追加する。
     *
     * @param http    {@link HttpSecurity}
     * @param cookies クッキー名
     * @return クッキーの配列
     */
    public static String[] createDeleteCookies(HttpSecurity http, String... cookies) {
        String sessionCookie = sessionCookieName(http);
        String[] combineCookies;
        if (cookies != null) {
            combineCookies = new String[cookies.length + 1];
            combineCookies[0] = sessionCookie;
            System.arraycopy(cookies, 0, combineCookies, 1, cookies.length);
        } else {
            combineCookies = new String[] { sessionCookie };
        }
        return combineCookies;
    }

    /**
     * セッションIDのクッキー名を取得する。
     *
     * @param http {@link HttpSecurity}
     * @return セッションIDのクッキー名
     */
    private static String sessionCookieName(HttpSecurity http) {
        ServerProperties properties = getBeanOrNull(http, ServerProperties.class);
        Objects.requireNonNull(properties);
        String cookieName = properties.getServlet().getSession().getCookie().getName();
        if (cookieName == null) {
            cookieName = "JSESSIONID";
        }
        return cookieName;
    }

}