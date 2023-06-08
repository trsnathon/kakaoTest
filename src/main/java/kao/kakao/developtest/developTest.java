package kao.kakao.developtest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class developTest {

    @ResponseBody
    @GetMapping("/hello")
    public String hello(){
        return "hello test";
    }
}
