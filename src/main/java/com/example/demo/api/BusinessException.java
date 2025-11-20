package com.example.demo.api;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;

/**
 * 業務例外クラス。
 * 
 */
public class BusinessException extends RuntimeException implements MessageSourceResolvable {

    /** {@link MessageSourceResolvable}。 */
    private final MessageSourceResolvable resolvableMessage;

    /**
     * コンストラクタ。
     *
     * @param resolvableMessage {@link MessageSourceResolvable}
     */
    public BusinessException(MessageSourceResolvable resolvableMessage) {
        this(resolvableMessage, null);
    }

    /**
     * コンストラクタ。
     *
     * @param resolvableMessage {@link MessageSourceResolvable}
     * @param cause 原因となった例外クラス
     */
    public BusinessException(MessageSourceResolvable resolvableMessage, @Nullable Throwable cause) {
        super(resolvableMessage.getDefaultMessage() != null ? resolvableMessage.getDefaultMessage()
                        : resolvableMessage.toString(), cause);
        this.resolvableMessage = resolvableMessage;
    }

    /**
     * コンストラクタ。
     *
     * @param message メッセージ。
     */
    public BusinessException(@Nullable String message) {
        this(message, null);
    }

    /**
     * コンストラクタ。
     *
     * @param cause 原因となった例外
     */
    public BusinessException(@Nullable Throwable cause) {
        this(cause == null ? null : cause.toString(), cause);
    }

    /**
     * コンストラクタ。
     *
     * @param message メッセージ。
     * @param cause 原因となった例外
     */
    // @SuppressFBWarnings("NP")
    public BusinessException(@Nullable String message, @Nullable Throwable cause) {
        this(new DefaultMessageSourceResolvable(null, null, message), cause);
    }

    /**
     * 代表となるメッセージコードを取得する。
     *
     * @return 代表のメッセージコード
     */
    @Nullable
    public String getCode() {
        return getCode(this);
    }

    /**
     * メッセージコードをを取得します。
     * <p>
     * 代表となる単一のコードを取得したい場合は{@link #getCode()}を使用してください。
     * </p>
     */
    @Override
    @Nullable
    public String[] getCodes() {
        return resolvableMessage.getCodes();
    }

    /**
     * メッセージ解決に利用する引数を取得します。
     *
     * @return メッセージ解決時に利用する引数
     */
    @Override
    @Nullable
    public Object[] getArguments() {
        return resolvableMessage.getArguments();
    }

    /**
     * デフォルトメッセージを取得します。
     * 
     * @return デフォルトメッセージ
     */
    @Override
    public @Nullable String getDefaultMessage() {
        return resolvableMessage.getDefaultMessage();
    }

    /**
     * 代表となるメッセージコードを取得する。
     * <p>
     * {@link DefaultMessageSourceResolvable#getCode()}と同じ仕様でメッセージコードを取得する。<br/>
     * （{@link MessageSourceResolvable#getCodes()}で取得した配列の末尾を返す）
     * </p>
     * <p>
     * {@link MessageSourceResolvable#getCodes()}がnullならば戻り値はnull。
     * </p>
     *
     * @param resolvable {@link MessageSourceResolvable}
     * @return 代表のメッセージコード
     */
    private @Nullable String getCode(MessageSourceResolvable resolvable) {
        String code;
        if (resolvable instanceof DefaultMessageSourceResolvable defaultResolvable) {
            code = defaultResolvable.getCode();
        } else {
            String[] codes = resolvable.getCodes();
            code = codes != null && codes.length > 0 ? codes[codes.length - 1] : null;
        }
        return code;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
