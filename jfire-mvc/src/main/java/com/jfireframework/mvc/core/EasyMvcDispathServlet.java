package com.jfireframework.mvc.core;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.uniqueid.AutumnId;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.util.ChangeMethodRequest;

/**
 * 充当路径分发器的类，用来根据地址规则转发数据请求
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
@WebServlet(name = "EasyMvcDispathServlet", value = "/*", loadOnStartup = 1, asyncSupported = true)
@MultipartConfig
public class EasyMvcDispathServlet extends HttpServlet
{
    private static final long                       serialVersionUID      = 6091581255799463902L;
    private Logger                                  logger                = ConsoleLogFactory.getLogger();
    private DispathServletHelper                    helper;
    private static final String                     DEFAULT_METHOD_PREFIX = "_method";
    private String                                  encode;
    public static final String                      CONFIG_CLASS_NAME     = AutumnId.instance().generate();
    public static final ThreadLocal<ServletContext> CONTEXT               = new ThreadLocal<ServletContext>();
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException
    {
        logger.debug("初始化Context-mvc Servlet");
        CONTEXT.set(servletConfig.getServletContext());
        helper = new DispathServletHelper(servletConfig);
        encode = helper.getMvcConfig().getEncode();
    }
    
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException
    {
        helper.preHandle();
        req.setCharacterEncoding(encode);
        res.setCharacterEncoding(encode);
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (request.getMethod().equals("POST") && StringUtil.isNotBlank(request.getHeader(DEFAULT_METHOD_PREFIX)))
        {
            String method = request.getHeader(DEFAULT_METHOD_PREFIX).toUpperCase();
            request = new ChangeMethodRequest(method, request);
        }
        Action action = helper.getAction(request);
        if (action == null)
        {
            helper.handleStaticResourceRequest(request, response);
            return;
        }
        try
        {
            action.render(request, response);
        }
        catch (Throwable e)
        {
            logger.error("访问action出现异常,action为{}", action.getRequestUrl(), e);
            response.sendError(500, e.getLocalizedMessage());
        }
    }
}
