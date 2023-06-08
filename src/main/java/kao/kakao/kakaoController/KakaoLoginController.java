package kao.kakao.kakaoController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kao.kakao.model.OAuthToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class KakaoLoginController {

    @GetMapping("/login")
    public String kakaoLogin(){
        return "redirect:https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=72e9341cc2cc0a95542c9ced81f6e72e&redirect_uri=http://localhost:8080/oauth/kakao/callback";
    }

    @ResponseBody
    @GetMapping("/oauth/kakao/callback")
    public String kakaoCallback(@RequestParam("code") String code){

        //POST 방식으로 Key value 데이터 카카오에 요청
        RestTemplate rt = new RestTemplate();

        //Httpheader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "72e9341cc2cc0a95542c9ced81f6e72e");
        params.add("redirect_uri", "http://localhost:8080/oauth/kakao/callback");
        params.add("code", code);

        // http 헤더와 바디 오브젝트 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String,String>> kakaoTokenRequest = new HttpEntity<>(params,headers);
        // Http 요청하기
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class // 응답 받을 타입
        );

        OAuthToken oAuthToken = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            oAuthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println("카카오 엑세스 토큰" + oAuthToken);


        //POST 방식으로 Key value 데이터 카카오에 요청
        RestTemplate rt2 = new RestTemplate();

        //Httpheader2 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer "+oAuthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        // http 헤더와 바디 오브젝트 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String,String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);
        // Http 요청하기
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        return response.getBody();
        //return response2.getBody();
    }


}
