package com.example.demo.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SPAのサンプルプログラムから呼び出されるRESTコントローラ。
 */
@RestController
@RequestMapping(path = "/api/initial-data")
public class InitialDataController {

    /** サンプルのウエイト(ms)。 */
    private static final int WAIT_MS = 1000;

    /**
     * アプリケーションの初期データを取得する。
     * 
     * @param userDetails {@link UserDetails}
     * @return {@link InitialData}
     */
    @GetMapping
    public InitialData load(@AuthenticationPrincipal UserDetails userDetails) {
        // サンプルなのでわざと重いデータを読み込んでいることを演出します
        try {
            Thread.sleep(WAIT_MS);
        } catch (Exception e) {
        }
        return new InitialData("user", "山田 太郎");
    }

    /**
     * アプリケーションの初期データ。
     * 
     * @param user     ユーザーID
     * @param username ユーザー名
     */
    public record InitialData(String user, String username) {
    }

}
