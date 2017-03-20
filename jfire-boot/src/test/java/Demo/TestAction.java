package Demo;

import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.annotation.RequestMapping.RequestMethod;
import com.jfireframework.mvc.viewrender.DefaultResultType;

@Controller
public class TestAction
{
    @RequestMapping(value = "/test", resultType = DefaultResultType.Str,method=RequestMethod.GET)
    public String test()
    {
        return "ssss";
    }
}
