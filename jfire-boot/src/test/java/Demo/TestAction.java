package Demo;

import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.config.ResultType;

@Controller
public class TestAction
{
    @RequestMapping(value = "/test", resultType = ResultType.String)
    public String test()
    {
        return "ssss";
    }
}
