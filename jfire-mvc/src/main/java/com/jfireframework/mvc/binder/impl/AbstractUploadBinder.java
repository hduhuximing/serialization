package com.jfireframework.mvc.binder.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.mvc.binder.UploadItem;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;

public class AbstractUploadBinder
{
    private final int version;
    
    public AbstractUploadBinder()
    {
        ServletContext context = EasyMvcDispathServlet.CONTEXT.get();
        int majorVersion = context.getEffectiveMajorVersion();
        int minorVersion = context.getEffectiveMinorVersion();
        version = majorVersion * 10 + minorVersion;
    }
    
    public List<UploadItem> resolveMany(HttpServletRequest request) throws IOException, ServletException
    {
        List<UploadItem> list = new ArrayList<UploadItem>();
        switch (version)
        {
            case 30:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && part.getHeader("content-disposition").indexOf("filename=") != -1)
                    {
                        UploadItem item = buildUploadItemForServlet3(part);
                        list.add(item);
                    }
                }
                break;
            case 31:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && StringUtil.isNotBlank(part.getSubmittedFileName()))
                    {
                        UploadItem item = buildUploadItemForServlet31(part);
                        list.add(item);
                    }
                }
                break;
            default:
                break;
        }
        return list;
    }
    
    public UploadItem resolveOne(HttpServletRequest request) throws IOException, ServletException
    {
        switch (version)
        {
            case 30:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && part.getHeader("content-disposition").indexOf("filename=") != -1)
                    {
                        UploadItem item = buildUploadItemForServlet3(part);
                        return item;
                    }
                }
                return null;
            case 31:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && StringUtil.isNotBlank(part.getSubmittedFileName()))
                    {
                        UploadItem item = buildUploadItemForServlet31(part);
                        return item;
                    }
                }
                return null;
            default:
                return null;
        }
    }
    
    public UploadItem resolveOne(String fieldName, HttpServletRequest request) throws IOException, ServletException
    {
        switch (version)
        {
            case 30:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && part.getHeader("content-disposition").indexOf("filename=") != -1)
                    {
                        UploadItem item = buildUploadItemForServlet3(part);
                        if (item.getFieldName().equals(fieldName))
                        {
                            return item;
                        }
                    }
                }
                return null;
            case 31:
                for (Part part : request.getParts())
                {
                    /**
                     * 该字段是上传文件的文件名，如果该字段为空，则意味着该字段是一个普通的表单字段，表单字段的信息仍然可以通过
                     * request.getParameterMap()得到. 只有在该字段是文件字段时才进行处理
                     */
                    if (part.getSize() > 0 && StringUtil.isNotBlank(part.getSubmittedFileName()))
                    {
                        UploadItem item = buildUploadItemForServlet31(part);
                        if (item.getFieldName().equals(fieldName))
                        {
                            return item;
                        }
                    }
                }
                return null;
            default:
                return null;
        }
    }
    
    /**
     * 通过part生成我们需要的uploadItme
     * 
     * @param part
     * @return
     */
    private UploadItem buildUploadItemForServlet31(Part part)
    {
        /**
         * 该信息是文件的原始文件名。
         * 在google和firefox浏览器下，会是直接的文件名，而在ie浏览器下，则是一个文件的文件路径，类似F:\jquery.pdf
         */
        String fileName = part.getSubmittedFileName();
        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
        String fieldName = part.getName();
        UploadItem item = new UploadItem(part, fileName, fieldName);
        return item;
    }
    
    private UploadItem buildUploadItemForServlet3(Part part)
    {
        String header = part.getHeader("content-disposition");
        /*
         * String[] tempArr1 =
         * header.split(";");代码执行完之后，在不同的浏览器下，tempArr1数组里面的内容稍有区别
         * 火狐或者google浏览器下：tempArr1={form-data,name="file",filename=
         * "snmp4j--api.zip"}
         * IE浏览器下：tempArr1={form-data,name="file",filename="E:\snmp4j--api.zip"}
         */
        String[] tempArr1 = header.split(";");
        /**
         * 火狐或者google浏览器下：tempArr2={filename,"snmp4j--api.zip"}
         * IE浏览器下：tempArr2={filename,"E:\snmp4j--api.zip"}
         */
        String[] tempArr2 = tempArr1[2].split("=");
        // 获取文件名，兼容各种浏览器的写法
        String fileName = tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");
        String fieldName = part.getName();
        UploadItem item = new UploadItem(part, fileName, fieldName);
        return item;
    }
    
}
