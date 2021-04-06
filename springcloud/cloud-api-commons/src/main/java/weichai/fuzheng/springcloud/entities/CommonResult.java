package weichai.fuzheng.springcloud.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommonResult<T> {
    private Integer code;
    private String message;
    private T data;

    //通用的返回
    public CommonResult(Integer code, String message){
        this(code, message, null);
    }
}
