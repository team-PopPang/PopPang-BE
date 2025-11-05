package com.poppang.be.common.util;

import org.springframework.util.StringUtils;

import java.text.Normalizer;

public final class StringNormalizer {

    private StringNormalizer() {}

    // 모든 문자열 공통 정규화 (공백·유니코드 등)
    public static String normalizeBasic(String input) {
        if (!StringUtils.hasText(input)) return null;
        String s = Normalizer.normalize(input, Normalizer.Form.NFC);
        return s.trim();
    }

    // 지역/구 이름 정규화
    public static String normalizeDistrict(String district) {
        String s = normalizeBasic(district);
        if (s == null) return null;
        if ("전체".equals(s) || "all".equalsIgnoreCase(s)) return null;
        // 특수문자 제거 + “구” 자동 보정
        s = s.replaceAll("[^가-힣0-9]", "");
        if (!s.endsWith("구") && s.length() <= 4) {
            s += "구";
        }
        return s;
    }

}
