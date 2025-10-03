package com.poppang.be.domain.auth.apple.util;

// Nimbus JOSE + JWT 라이브러리: JWS 헤더/서명/알고리즘/JWT 생성에 사용
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

// yml에서 로드한 Apple 설정값(clientId, teamId, keyId, privateKeyPath 등)

// classpath(/resources)에서 파일을 읽기 위한 스프링 유틸
import com.poppang.be.domain.auth.apple.config.AppleProperties;
import org.springframework.core.io.ClassPathResource;

// 문자 인코딩을 다룰 때 표준 Charset을 제공
import java.nio.charset.StandardCharsets;

// 암호화 키를 생성하거나 변환하는 팩토리 클래스
import java.security.KeyFactory;

// 타원 곡선(Elliptic Curve, EC) 알고리즘용 PrivateKey를 나타내는 인터페이스
// Apple 로그인에서 쓰이는 .p8 키는 EC 키이기 때문에, 최종적으로 ECPrivateKey로 변환해서 서명에 사용
import java.security.interfaces.ECPrivateKey;

// PKCS #8 형식(PrivateKey 저장 표준)으로 인코딩된 키 데이터를 다루는 클래스.
// Apple에서 발급한 .p8 키 파일은 이 형식으로 인코딩되어 있어서, new PKCS8EncodedKeySpec(decodedBytes) 식으로 불러와서 KeyFactory에 전달.
import java.security.spec.PKCS8EncodedKeySpec;

// Base64 인코딩/디코딩 제공.
// Apple .p8 키는 PEM 형식(= Base64 문자열)으로 되어 있기 때문에, Base64.getDecoder().decode(pemKey) 같은 식으로 원래 바이트 배열을 얻어야 함.
import java.util.Base64;

// JWT 발급 시간(iat), 만료 시간(exp) 등 시간을 다룰 때 사용.
import java.util.Date;


public class AppleJwtUtil {
    /*
    client_secret 생성 메서드
    - Apple “Sign in with Apple” 토큰 교환 시 필요한 client_secret(JWT)을 ES256으로 서명해서 생성
     */
    public static String createClientSecret(AppleProperties properties) throws Exception {
        // 1) .p8 개인키 읽기
        // - application.yml의 apple.private-key-path 값을 이용
        // - 현재 구현은 classpath: 경로만 지원하도록 가정
        String privateKeyPath = properties.getPrivateKeyPath();
        String privateKeyPem;

        if (privateKeyPath.startsWith("classpath:")) {
            // "classpath:" 접두어 제거 후 /resources 아래에서 파일 읽기
            String path = privateKeyPath.replace("classpath:", "");
            ClassPathResource resource = new ClassPathResource(path);
            privateKeyPem = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            // 보안/운영상 외부 경로를 사용할 수도 있으나, 이 유틸은 일단 classpath만 허용
            throw new IllegalArgumentException("Only classpath: resource loading is supported in this setup.");
        }

        // 2) PEM 텍스트 정리
        // - -----BEGIN/END PRIVATE KEY----- 헤더/푸터 제거
        // - 공백/개행 모두 제거 → 순수 Base64 본문만 남김
        privateKeyPem = privateKeyPem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        // 3) PKCS#8 바이너리를 PrivateKey 객체로 변환
        // - Apple의 .p8은 PKCS#8 포맷의 EC(서명 알고리즘: ES256) 개인키
        byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(privateKeyPem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        ECPrivateKey privateKey = (ECPrivateKey) KeyFactory.getInstance("EC").generatePrivate(keySpec);

        // 4) JWT Header 구성
        // - alg: ES256 (P-256 + SHA-256) ← Apple이 요구
        // - kid: Apple 개발자 콘솔의 Key ID
        // - typ: JWT
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(properties.getKeyId())
                .type(JOSEObjectType.JWT)
                .build();

        // 5) JWT Claims 구성
        // - iss: Apple Developer Team ID
        // - iat: 발급시각
        // - exp: 만료시각 (예: 30분)  *Apple은 최대 6개월까지 허용하지만, 짧게 가져가면 보안상 유리
        // - aud: 고정값 "https://appleid.apple.com"
        // - sub: client_id (iOS는 bundle id, Web은 Service ID)
        long now = System.currentTimeMillis() / 1000; // 초 단위
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(properties.getTeamId())                   // iss
                .issueTime(new Date(now * 1000))                  // iat (ms 단위 Date)
                .expirationTime(new Date((now + 1800) * 1000))    // exp = 30분(1800초) 후
                .audience("https://appleid.apple.com")            // aud
                .subject(properties.getClientId())                // sub
                .build();

        // 6) JWT 서명
        // - 위 Header + Claims를 합쳐 SignedJWT 생성
        // - ECDSASigner에 EC 개인키를 넣어 ES256으로 서명
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        signedJWT.sign(new ECDSASigner(privateKey));

        // 7) 직렬화(문자열)하여 반환 → 이 문자열이 client_secret
        return signedJWT.serialize();
    }
}

