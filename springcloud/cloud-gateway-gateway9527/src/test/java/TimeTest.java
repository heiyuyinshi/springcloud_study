import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class TimeTest {
    @Test
    public void test(){
        ZonedDateTime now = ZonedDateTime.now();
        System.out.println(now);
    }
}
