package com.linkkou.mybatis.log;

import javassist.*;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * 用于构建完整的SQL，彻底解决通过？替换的问题存在的不正确的情况
 *
 * @author lk
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}), @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}), @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class})})
public class LogInterceptor implements Interceptor {

    public LogInterceptor() {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = null;
        try {
            classPool.insertClassPath(new ClassClassPath(this.getClass()));
            //获取类
            ctClass = classPool.get("org.apache.ibatis.scripting.defaults.RawSqlSource");
            //添加新的字段
            CtField ctField = new CtField(classPool.getCtClass("java.lang.String"), "_rootSqlNode_", ctClass);
            ctField.setModifiers(Modifier.PUBLIC);
            ctField.setModifiers(Modifier.FINAL);
            ctClass.addField(ctField);
            //获取构造
            final CtConstructor declaredConstructor = ctClass.getDeclaredConstructor(new CtClass[]{classPool.getCtClass("org.apache.ibatis.session.Configuration"), classPool.getCtClass("java.lang.String"), classPool.getCtClass("java.lang.Class")});
            declaredConstructor.insertAfter("{$0._rootSqlNode_ = $2;}");
            //写入
            ctClass.writeFile();
            //加载该类的字节码（不能少）
            ctClass.toClass(LogInterceptor.class.getClassLoader(), LogInterceptor.class.getProtectionDomain());
            ctClass.detach();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LogInterceptor.class);

    /**
     * 对象名称
     */
    private static final String ROOTSQLNODE = "rootSqlNode";
    private static final String ROOTSQLNODE2 = "_rootSqlNode_";
    /**
     * 参数数量
     */
    private static final Integer ARGSNUMBER = 2;

    private Pair<MappedStatement, Object> getArgs(Invocation invocation) {
        final Object[] args = invocation.getArgs();
        return Pair.with((MappedStatement) args[0], args[1]);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (invocation.getArgs().length >= ARGSNUMBER) {
            final Pair<MappedStatement, Object> args = getArgs(invocation);
            MappedStatement mappedStatement = args.getValue0();
            Object parameter = args.getValue1();
            final String originalSql = this.getOriginalSql(mappedStatement, parameter);
            if (originalSql != null) {
                BoundSql boundSql = mappedStatement.getBoundSql(parameter);
                Configuration configuration = mappedStatement.getConfiguration();
                // 通过配置信息和BoundSql对象来生成带值得sql语句
                String sql = getCompleteSql(configuration, boundSql, originalSql);
                // 打印sql语句
                LOGGER.debug(" ==>  DaoStructure: " + mappedStatement.getId());
                LOGGER.debug(" ==>  SQLStructure: " + sql);
            }
        }
        return invocation.proceed();
    }


    /**
     * 生成对应的带有值得sql语句
     *
     * @param configuration 配置
     * @param boundSql      对象
     * @param originalSql   sql
     * @return String
     */
    private String getCompleteSql(Configuration configuration, BoundSql boundSql, String originalSql) {
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if (parameterMappings.size() > 0 && parameterObject != null) {
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty();
                if (metaObject.hasGetter(propertyName)) {
                    Object obj = metaObject.getValue(propertyName);
                    originalSql = originalSql.replaceFirst("#\\{" + propertyName + "}", getParameterValue(obj));
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    Object obj = boundSql.getAdditionalParameter(propertyName);
                    originalSql = originalSql.replaceFirst("#\\{" + propertyName + "}", getParameterValue(obj));
                }
            }
        }
        return originalSql.replaceAll("[\\s]+", " ");
    }


    private String getOriginalSql(MappedStatement mappedStatement, Object parameter) {
        final SqlSource sqlSource = mappedStatement.getSqlSource();
        if (sqlSource instanceof DynamicSqlSource) {
            return getDynamicSqlSource(mappedStatement, parameter);
        }
        if (sqlSource instanceof RawSqlSource) {
            return getRawSqlSource(sqlSource);
        }
        return null;
    }


    private String getDynamicSqlSource(MappedStatement mappedStatement, Object parameter) {
        final DynamicContext dynamicContext = new DynamicContext(mappedStatement.getConfiguration(), parameter);
        final SqlNode sqlNode = getSqlNode(mappedStatement.getSqlSource());
        if (sqlNode != null && sqlNode.apply(dynamicContext)) {
            return dynamicContext.getSql();
        }
        return null;
    }

    private String getRawSqlSource(SqlSource sqlSource) {
        final RawSqlSource dynamicSqlSource = (RawSqlSource) sqlSource;
        Field[] declaredFields = dynamicSqlSource.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (ROOTSQLNODE2.equals(declaredField.getName())) {
                try {
                    declaredField.setAccessible(true);
                    return (String) declaredField.get(dynamicSqlSource);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    /**
     * 构建 SqlNode
     *
     * @param sqlSource 对象
     * @return SqlNode
     */
    private SqlNode getSqlNode(SqlSource sqlSource) {
        if (sqlSource instanceof DynamicSqlSource) {
            final DynamicSqlSource dynamicSqlSource = (DynamicSqlSource) sqlSource;
            Field[] declaredFields = dynamicSqlSource.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (ROOTSQLNODE.equals(declaredField.getName())) {
                    try {
                        declaredField.setAccessible(true);
                        return (SqlNode) declaredField.get(dynamicSqlSource);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

    /**
     * 如果是字符串对象则加上单引号返回，如果是日期则也需要转换成字符串形式，如果是其他则直接转换成字符串返回。
     *
     * @param obj 对象
     * @return String
     */
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(obj) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "''";
            }
        }
        return value;
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        LOGGER.debug("Creating attribute values\n:" + properties);
    }

}