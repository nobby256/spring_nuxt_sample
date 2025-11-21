package com.example.demo.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SPAのサンプルプログラム（エラーハンドリング）から呼び出されるRESTコントローラ。
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
        String username = userDetails.getUsername();
        List<String> authorities = userDetails.getAuthorities().stream().map(it -> it.getAuthority()).toList();
        return new InitialData(username, authorities);
    }

    /**
     * アプリケーションの初期データ。
     * 
     * @param username ユーザー名
     * @param authorities 権限のリスト
     */
    public record InitialData(String username, List<String> authorities) {
    }

}
