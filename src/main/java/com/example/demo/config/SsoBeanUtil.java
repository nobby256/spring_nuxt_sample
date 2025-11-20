package com.example.demo.config;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.AbstractConfiguredSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * コンポーネント取得関連のユーティリティクラス。
 *
 * @author hashimoto
 * @since 2.1.2
 */
public final class SsoBeanUtil {

    /**
     * コンストラクタ。
     */
    private SsoBeanUtil() {
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

}
