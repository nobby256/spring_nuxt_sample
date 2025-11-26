package com.example.demo.library.spa;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.function.ServerRequest;

@ExtendWith(MockitoExtension.class)
public class IndexHtlmRouterFunctionTest {

    @Nested
    class PatternTest {
        @Mock
        ServerRequest request;

        @Test
        void case1() {
            doReturn("/").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isTrue();
        }

        @Test
        void case2() {
            doReturn("/hoo").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isTrue();
        }

        @Test
        void case3() {
            doReturn("/hoo/").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isTrue();
        }

        @Test
        void case4() {
            doReturn("/hoo/bar").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isTrue();
        }

        @Test
        void case5() {
            doReturn("/hoo.png").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isFalse();
        }

        @Test
        void case6() {
            doReturn("/hoo/bar.png").when(request).path();
            assertThat(IndexHtlmRouterFunction.match().test(request)).isFalse();
        }
    }
}
