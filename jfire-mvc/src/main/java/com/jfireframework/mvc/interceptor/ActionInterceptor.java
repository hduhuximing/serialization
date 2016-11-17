package com.jfireframework.mvc.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.order.Order;
import com.jfireframework.mvc.core.action.Action;

/**
 * 方法拦截器接口。方法拦截器有两个地方可以定义规则，这两个规则是并行生效的。
 * 
 * @author linbin
 *
 */
public interface ActionInterceptor extends Order
{
    
    /**
     * 对请求的action进行拦截 如果返回为false，请求无法通过。不予处理
     * 
     * @param request
     * @param response
     */
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action);
    
    /**
     * 返回需要进行前置拦截的路径，*代表拦截所有。匹配的时候是从前到后的匹配方式。多个规则之间可以使用;进行间隔 返回null表明不通过路径判断
     * 
     * @return
     */
    public String includePath();
    
    /**
     * 返回需要排除拦截的路径。返回*表示排除所有，意味着该拦拦截器不会拦截任何action。匹配的时候是从前到后的匹配方式。多个规则之间可以使用;进行间隔。
     * 返回null表明没有需要排除的路径
     * 当excludePath和includePath冲突时，以excludePath为准
     * 
     * @return
     */
    public String excludePath();
    
    /**
     * 如果一个方法上使用了Interceptor注解，则这个方法的返回值与注解值相同，就表示需要拦截 返回null表示不拦截。
     * 
     * @return
     */
    public String tokenRule();
}
