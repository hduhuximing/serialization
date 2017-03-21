package Demo;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.mvc.annotation.Controller;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.annotation.RequestMapping.RequestMethod;
import com.jfireframework.mvc.viewrender.DefaultResultType;

@Controller
public class TestAction
{
    @RequestMapping(value = "/test", resultType = DefaultResultType.None, method = RequestMethod.GET)
    public void test(HttpServletRequest request)
    {
        AsyncContext asyncContext = request.startAsync();
        try
        {
            asyncContext.getResponse().getOutputStream().write("sss".getBytes());
            asyncContext.complete();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
