package com.jfireframework.mvc.util;

import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.annotation.Document;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.core.action.ActionInitListener;

public abstract class AbstractReportMdActionListener implements ActionInitListener
{
    private static final Logger logger  = ConsoleLogFactory.getLogger();
    private String              pattarn = "\r\n"                        //
            + "|请求地址|{}|\r\n"                                           //
            + "|请求方法|{}|\r\n"                                           //
            + "|结果类型|{}|\r\n"                                           //
            + "|方法说明|{}|\r\n"                                           //
            + "|类方法签名|{}|\r\n";
    
    @Override
    public void init(Action action)
    {
        if (filter(action))
        {
            String doc;
            if (action.getMethod().isAnnotationPresent(Document.class))
            {
                doc = action.getMethod().getAnnotation(Document.class).value();
            }
            else
            {
                doc = "无";
            }
            logger.debug(pattarn, //
                    action.getRequestUrl(), //
                    action.getRequestMethod().name(), //
                    action.getViewRender().renderType(), //
                    doc, //
                    action.getMethod().toGenericString());
        }
    }
    
    protected abstract boolean filter(Action action);
}
