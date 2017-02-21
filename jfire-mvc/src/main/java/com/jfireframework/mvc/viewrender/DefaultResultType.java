package com.jfireframework.mvc.viewrender;

public class DefaultResultType
{
    /**
     * 返回的是一个pojo对象,通过json序列化,写入到输出流中.
     */
    public static final String Json     = "json";
    /**
     * 返回的是一个ModeAndView对象,内部包含模板路径和变量参数,通过Beetl模板引擎渲染后,将渲染结果输出到输出流
     */
    public static final String Beetl    = "beetl";
    /**
     * 返回结果是一个字符串,是一个需要forward的路径
     */
    public static final String Html     = "html";
    /**
     * 返回结果是一个字符串,是一个需要重定向的地址路径
     */
    public static final String Redirect = "redirect";
    /**
     * 不做任何处理,此时方式的返回值应该是void,否则框架会尝试推测具体的类型
     * 推测的原则是,<br>
     * 如果返回值是String则按照String规则,<br>
     * 如果是ModeAndView则按照Beetl规则,<br>
     * 如果是byte[]则按照Bytes规则,<br>
     * 都不是则按照json规则
     */
    public static final String None     = "none";
    /**
     * 返回结果是一个字符串,内容直接写入到输出流中
     */
    public static final String Str      = "string";
    /**
     * 返回的结果是一个byte[]类型,内容直接写入到输出流
     */
    public static final String Bytes    = "bytes";
}
