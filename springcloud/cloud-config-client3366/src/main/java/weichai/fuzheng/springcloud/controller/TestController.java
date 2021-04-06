package weichai.fuzheng.springcloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class TestController {
    @Value("${info.file}")
    private String filename;

    @Value("${info.version}")
    private String version;

    @GetMapping("/config")
    public String getFilename(){
        return filename + " version:" + version;
    }
}
