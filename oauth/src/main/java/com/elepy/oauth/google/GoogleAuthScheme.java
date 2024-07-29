package com.elepy.oauth.google;

import com.elepy.oauth.AuthScheme;
import com.elepy.oauth.openid.OpenIDEmailExtractor;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;

public class GoogleAuthScheme extends AuthScheme {

    private final static String ICON = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAADe0lEQVRIS7VWbWhbZRR+znvv3ZqkH1upWRg2wymOOnHTtBVRWdaPyZT6sdk6Yf51Q4bUD1A7hTk69Yfijykign8G62hHWdetjq61EZSupFFaOkFBf6yw2nYOsvQuTe+950gyG5ImWbuNvb8u73nO85wPznkv4SZHgkF9Wr/eopibQBSAwMckq5XQjBAmycGgEpyu+CkcLkRD+QyXmwJuY07bL4pbAaq8WRApm/DQAutt94ZGLizF5ghcbQj4LUYvkXpkWeIsgLDD0u6r2HiYurqcRVOWwNTT1bXKoB4i+G6N/AZahE562f0qhUJ2jsClxk3ri6R0AoK1hcmFBYgTyLMUk488iUlnMFNX3QeinTmOkOsgOmrN2x3iFP1ZOTwc/7uuaoPHKXmMdP4AoJpC5GmBq69U7bGvFHfkNIhoRGzzRW/o4j/5shKArgRrn6uA+1xmWTKxqQwWzhsRs88fs8bKnwRB/x8wqhM3lA9EorfTj3QPEn14SK0yLiYvEn+VjZud960jEa8Qb103+Ov4nZCnSmT1q3egtM8Xidg0/jVPbjxefmzizXzkdR9O38+uVSUrEdZMY4rsfu2oKHUg08ERvFHUaH2Tj2T7kdgAgepXIiDgL8nq13qg1POZDrYl21077dCdCgB86u4KMM7c3RIxf0fWgHob0L5YLMesvXr2PbPmxLHdQ/mb3B5rBmRDTvlIexyQl7Pu2fmYEudRpcj4PWn4ed479n6s1mcTedlaePS3186MraSZSUx9+9w5ITyThbfs+tSgJQaMyCexLdfOJvxPAXRj0ASjoqmGSEvXsoO27dNojWJthDJWDyDRaEXxPSmBl7p37JlcKMlZFYCEJYoXIvu6pwplsu3wtSc0jX4AqCwTw6CvQwc9B9LLLnBi11kCPZuHKA7mr2xdP+4C/hhu6YoHOpvLVEI2Q5PX9fjWza7pVj+EvIu+AokZlvlA/yHfTFpgU0fj+lKUTggVXtciYCIxAcqaZGWvnfFMHpki9mxJrQeS/YNtJd+mvjMjru5srgVzD3AbD44o23X5rV+M+YdHfzxY+m562S0tSc33Oyodd3GvAqWiuYXjEDkfhVtOfQaCFBRIGgK9TW6a0/cJqVYC/MuJCGTIstE2vrd7+Uc/kyx4KKjPPbimWaA1EUkAIJ8QigSYVcyXSLRBCJ8O7+0u+NvyHw9QdlSUNDUVAAAAAElFTkSuQmCC";

    public GoogleAuthScheme(String clientId, String clientSecret) {
        super("Google",
                ICON, new ServiceBuilder(clientId)
                        .defaultScope("openid email")
                        .apiSecret(clientSecret)
                        .build(GoogleApi20.instance()), new OpenIDEmailExtractor());
    }
}
