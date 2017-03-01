package com.jfireframework.mvc.viewrender;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.jfire.bean.annotation.field.MapKey;

@Resource
public class RenderManager
{
    @Resource
    @MapKey("renderType")
    private Map<String, ViewRender> renders = new HashMap<String, ViewRender>();
    
    public ViewRender get(String type, Method method)
    {
        ViewRender render = renders.get(type);
        if (render == null)
        {
            throw new NullPointerException("未注册类型为:" + type + "的渲染器，请检查方法:" + method.getDeclaringClass().getName() + "." + method.getName());
        }
        return render;
    }
}
