package com.jfireframework.swaggertool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.aliasanno.AnnotationUtil;
import com.jfireframework.baseutil.uniqueid.AutumnId;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.annotation.RequestMapping;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.core.action.ActionInitListener;
import com.jfireframework.swaggertool.SwaggerObject.Info;
import com.jfireframework.swaggertool.SwaggerObject.OperationObject;
import com.jfireframework.swaggertool.SwaggerObject.ParameterObject;
import com.jfireframework.swaggertool.SwaggerObject.PathItemObject;
import com.jfireframework.swaggertool.SwaggerObject.PathsObject;
import com.jfireframework.swaggertool.SwaggerObject.Property;
import com.jfireframework.swaggertool.SwaggerObject.ResponseObject;
import com.jfireframework.swaggertool.SwaggerObject.ResponsesObject;
import com.jfireframework.swaggertool.SwaggerObject.SchemaObject;
import com.jfireframework.swaggertool.anno.ApiOperation;
import com.jfireframework.swaggertool.anno.ApiParameter;
import com.jfireframework.swaggertool.anno.ApiParameters;
import com.jfireframework.swaggertool.anno.ApiProperty;
import com.jfireframework.swaggertool.anno.ApiResponse;
import com.jfireframework.swaggertool.anno.ApiResponses;
import com.jfireframework.swaggertool.anno.ApiSchema;
import com.jfireframework.swaggertool.anno.ApiSchemas;
import com.jfireframework.swaggertool.anno.MIME;

public abstract class SwaggerJsonListerner implements ActionInitListener
{
    protected SwaggerObject swaggerObject;
    
    public SwaggerJsonListerner()
    {
        swaggerObject = new SwaggerObject();
        swaggerObject.setInfo(info());
        swaggerObject.setHost(host());
        swaggerObject.setBasePath(basePath());
        swaggerObject.setPaths(new PathsObject());
    }
    
    protected abstract Info info();
    
    protected abstract String host();
    
    protected abstract String basePath();
    
    public void init(Action action)
    {
        Map<String, ApiSchema> schemas = new HashMap<String, ApiSchema>();
        RequestMapping requestMapping = AnnotationUtil.getAnnotation(RequestMapping.class, action.getMethod());
        ApiOperation apiOperation = AnnotationUtil.getAnnotation(ApiOperation.class, action.getMethod());
        if (apiOperation == null)
        {
            return;
        }
        ApiSchemas apiSchemas = AnnotationUtil.getAnnotation(ApiSchemas.class, action.getMethod());
        if (apiSchemas != null)
        {
            for (ApiSchema each : apiSchemas.value())
            {
                schemas.put(each.name(), each);
            }
        }
        apiSchemas = AnnotationUtil.getAnnotation(ApiSchemas.class, action.getMethod().getDeclaringClass());
        if (apiSchemas != null)
        {
            for (ApiSchema each : apiSchemas.value())
            {
                schemas.put(each.name(), each);
            }
        }
        ApiSchema apiSchema = AnnotationUtil.getAnnotation(ApiSchema.class, action.getMethod().getDeclaringClass());
        if (apiSchema != null)
        {
            schemas.put(apiSchema.name(), apiSchema);
        }
        apiSchema = AnnotationUtil.getAnnotation(ApiSchema.class, action.getMethod());
        if (apiSchema != null)
        {
            schemas.put(apiSchema.name(), apiSchema);
        }
        OperationObject operationObject = new OperationObject();
        PathItemObject pathItemObject = new PathItemObject();
        if (StringUtil.isNotBlank(apiOperation.path()))
        {
            swaggerObject.getPaths().put(apiOperation.path(), pathItemObject);
        }
        else
        {
            swaggerObject.getPaths().put(requestMapping.value(), pathItemObject);
        }
        String httpMethod = apiOperation.method();
        if (StringUtil.isNotBlank(httpMethod))
        {
            httpMethod = requestMapping.method().name();
        }
        if (httpMethod.equals("get"))
        {
            pathItemObject.setGet(operationObject);
        }
        else if (httpMethod.equals("post"))
        {
            pathItemObject.setPost(operationObject);
        }
        else if (httpMethod.equals("put"))
        {
            pathItemObject.setPut(operationObject);
        }
        else if (httpMethod.equals("delete"))
        {
            pathItemObject.setDelete(operationObject);
        }
        operationObject.setResponses(new ResponsesObject());
        operationObject.setTags(apiOperation.tags());
        operationObject.setSummary(apiOperation.summary());
        operationObject.setDescription(apiOperation.description());
        operationObject.setOperationId(AutumnId.instance().generateDigits());
        List<String> mimes = new ArrayList<String>();
        for (MIME each : apiOperation.consumes())
        {
            mimes.add(each.type());
        }
        operationObject.setConsumes(mimes.toArray(new String[mimes.size()]));
        mimes.clear();
        for (MIME each : apiOperation.produces())
        {
            mimes.add(each.type());
        }
        operationObject.setProduces(mimes.toArray(new String[mimes.size()]));
        operationObject.setSchemes(apiOperation.schemes());
        operationObject.setDeprecated(apiOperation.deprecated());
        List<ParameterObject> list = new ArrayList<SwaggerObject.ParameterObject>();
        if (AnnotationUtil.isPresent(ApiParameters.class, action.getMethod()))
        {
            ApiParameter[] apiParameters = AnnotationUtil.getAnnotation(ApiParameters.class, action.getMethod()).value();
            for (ApiParameter parameter : apiParameters)
            {
                ParameterObject parameterObject = new ParameterObject();
                parameterObject.setName(parameter.name());
                parameterObject.setIn(parameter.in().toString());
                parameterObject.setRequired(parameter.required());
                parameterObject.setType(parameter.type());
                if (StringUtil.isNotBlank(parameter.description()))
                {
                    parameterObject.setDescription(parameter.description());
                }
                if (StringUtil.isNotBlank(parameter.format()))
                {
                    parameterObject.setFormat(parameter.format());
                }
                list.add(parameterObject);
            }
        }
        operationObject.setParameters(list.toArray(new ParameterObject[list.size()]));
        ResponsesObject responsesObject = new ResponsesObject();
        for (ApiResponse response : AnnotationUtil.getAnnotation(ApiResponses.class, action.getMethod()).value())
        {
            String schemaName = response.schema();
            ApiSchema schema = schemas.get(schemaName);
            SchemaObject schemaObject = new SchemaObject();
            schemaObject.setType(schema.type());
            schemaObject.setDescription(schema.description());
            schemaObject.setTitle(schema.type());
            Map<String, Property> properties = new HashMap<String, SwaggerObject.Property>();
            for (ApiProperty each : schema.properties())
            {
                Property property = new Property();
                property.setType(each.type());
                property.setDescription(each.description());
                properties.put(each.name(), property);
            }
            schemaObject.setProperties(properties);
            ResponseObject responseObject = new ResponseObject();
            responseObject.setSchema(schemaObject);
            responsesObject.put(response.code(), responseObject);
        }
        operationObject.setResponses(responsesObject);
    }
    
    public void initFinish()
    {
        Set<String> set = new HashSet<String>();
        for (PathItemObject each : swaggerObject.getPaths().values())
        {
            if (each.getGet() != null)
            {
                for (String tag : each.getGet().getTags())
                {
                    set.add(tag);
                }
            }
            if (each.getDelete() != null)
            {
                for (String tag : each.getDelete().getTags())
                {
                    set.add(tag);
                }
            }
            if (each.getPut() != null)
            {
                for (String tag : each.getPut().getTags())
                {
                    set.add(tag);
                }
            }
            if (each.getPost() != null)
            {
                for (String tag : each.getPost().getTags())
                {
                    set.add(tag);
                }
            }
        }
        onFinish(JsonTool.write(swaggerObject));
    }
    
    protected abstract void onFinish(String value);
}
