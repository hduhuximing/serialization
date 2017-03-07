package com.jfireframework.swaggertool;

import java.util.HashMap;
import java.util.Map;

public class SwaggerObject
{
    private String      swagger = "2.0";
    private Info        info;
    // 链接目标服务的ip（也可以包含端口号）
    private String      host;
    // 基本路径，路径必须以/开头
    private String      basePath;
    private PathsObject paths;
    private Tag[]       tags;
    
    public Tag[] getTags()
    {
        return tags;
    }
    
    public void setTags(Tag[] tags)
    {
        this.tags = tags;
    }
    
    public String getSwagger()
    {
        return swagger;
    }
    
    public void setSwagger(String swagger)
    {
        this.swagger = swagger;
    }
    
    public Info getInfo()
    {
        return info;
    }
    
    public void setInfo(Info info)
    {
        this.info = info;
    }
    
    public String getHost()
    {
        return host;
    }
    
    public void setHost(String host)
    {
        this.host = host;
    }
    
    public String getBasePath()
    {
        return basePath;
    }
    
    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }
    
    public PathsObject getPaths()
    {
        return paths;
    }
    
    public void setPaths(PathsObject paths)
    {
        this.paths = paths;
    }
    
    public static class Tag
    {
        private String name;
        private String description;
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
    }
    
    public static class Info
    {
        private String title;
        private String description;
        private String version;
        
        public String getTitle()
        {
            return title;
        }
        
        public void setTitle(String title)
        {
            this.title = title;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getVersion()
        {
            return version;
        }
        
        public void setVersion(String version)
        {
            this.version = version;
        }
        
    }
    
    public static class PathItemObject
    {
        private OperationObject get;
        private OperationObject put;
        private OperationObject post;
        private OperationObject delete;
        
        public OperationObject getGet()
        {
            return get;
        }
        
        public void setGet(OperationObject get)
        {
            this.get = get;
        }
        
        public OperationObject getPut()
        {
            return put;
        }
        
        public void setPut(OperationObject put)
        {
            this.put = put;
        }
        
        public OperationObject getPost()
        {
            return post;
        }
        
        public void setPost(OperationObject post)
        {
            this.post = post;
        }
        
        public OperationObject getDelete()
        {
            return delete;
        }
        
        public void setDelete(OperationObject delete)
        {
            this.delete = delete;
        }
        
    }
    
    public static class PathsObject extends HashMap<String, PathItemObject>
    {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
    }
    
    public static class OperationObject
    {
        /**
         * 标签
         */
        private String[]          tags;
        /**
         * 简要说明，可以理解为标题
         */
        private String            summary;
        /**
         * 详细描述
         */
        private String            description;
        /**
         * 全局唯一的识别码
         */
        private String            operationId;
        /**
         * 入参编码格式，定义约束为MIME
         */
        private String[]          consumes;
        /**
         * 响应格式，定义约束为MIME
         */
        private String[]          produces;
        private ParameterObject[] parameters;
        private ResponsesObject   responses;
        private String[]          schemes;
        private boolean           deprecated = false;
        
        public String[] getTags()
        {
            return tags;
        }
        
        public void setTags(String[] tags)
        {
            this.tags = tags;
        }
        
        public String getSummary()
        {
            return summary;
        }
        
        public void setSummary(String summary)
        {
            this.summary = summary;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getOperationId()
        {
            return operationId;
        }
        
        public void setOperationId(String operationId)
        {
            this.operationId = operationId;
        }
        
        public String[] getConsumes()
        {
            return consumes;
        }
        
        public void setConsumes(String[] consumes)
        {
            this.consumes = consumes;
        }
        
        public String[] getProduces()
        {
            return produces;
        }
        
        public void setProduces(String[] produces)
        {
            this.produces = produces;
        }
        
        public ParameterObject[] getParameters()
        {
            return parameters;
        }
        
        public void setParameters(ParameterObject[] parameters)
        {
            this.parameters = parameters;
        }
        
        public ResponsesObject getResponses()
        {
            return responses;
        }
        
        public void setResponses(ResponsesObject responses)
        {
            this.responses = responses;
        }
        
        public String[] getSchemes()
        {
            return schemes;
        }
        
        public void setSchemes(String[] schemes)
        {
            this.schemes = schemes;
        }
        
        public boolean isDeprecated()
        {
            return deprecated;
        }
        
        public void setDeprecated(boolean deprecated)
        {
            this.deprecated = deprecated;
        }
        
    }
    
    public static class ParameterObject
    {
        
        private String  name;
        private String  in;
        private String  description;
        private boolean required;
        private String  type;
        private String  format;
        private String  collectionFormat;
        
        public String getName()
        {
            return name;
        }
        
        public void setName(String name)
        {
            this.name = name;
        }
        
        public String getIn()
        {
            return in;
        }
        
        public void setIn(String in)
        {
            this.in = in;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public boolean isRequired()
        {
            return required;
        }
        
        public void setRequired(boolean required)
        {
            this.required = required;
        }
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public String getFormat()
        {
            return format;
        }
        
        public void setFormat(String format)
        {
            this.format = format;
        }
        
        public String getCollectionFormat()
        {
            return collectionFormat;
        }
        
        public void setCollectionFormat(String collectionFormat)
        {
            this.collectionFormat = collectionFormat;
        }
        
    }
    
    public static class SchemaObject
    {
        private String                type;
        private String                format;
        private String                description;
        private String                title;
        private Map<String, Property> properties;
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public String getFormat()
        {
            return format;
        }
        
        public void setFormat(String format)
        {
            this.format = format;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getTitle()
        {
            return title;
        }
        
        public void setTitle(String title)
        {
            this.title = title;
        }
        
        public Map<String, Property> getProperties()
        {
            return properties;
        }
        
        public void setProperties(Map<String, Property> properties)
        {
            this.properties = properties;
        }
        
    }
    
    public static class Property
    {
        private String type;
        private String description;
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
    }
    
    public static class ResponsesObject extends HashMap<String, ResponseObject>
    {
        
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
    }
    
    public static class ResponseObject
    {
        private String         description;
        private SchemaObject   schema;
        private HeaderObject[] headers;
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public SchemaObject getSchema()
        {
            return schema;
        }
        
        public void setSchema(SchemaObject schema)
        {
            this.schema = schema;
        }
        
        public HeaderObject[] getHeaders()
        {
            return headers;
        }
        
        public void setHeaders(HeaderObject[] headers)
        {
            this.headers = headers;
        }
        
    }
    
    public class HeaderObject
    {
        private String type;
        private String description;
        private String format;
        private String defaultValue;
        
        public String getType()
        {
            return type;
        }
        
        public void setType(String type)
        {
            this.type = type;
        }
        
        public String getDescription()
        {
            return description;
        }
        
        public void setDescription(String description)
        {
            this.description = description;
        }
        
        public String getFormat()
        {
            return format;
        }
        
        public void setFormat(String format)
        {
            this.format = format;
        }
        
        public String getDefaultValue()
        {
            return defaultValue;
        }
        
        public void setDefaultValue(String defaultValue)
        {
            this.defaultValue = defaultValue;
        }
        
    }
    
}
