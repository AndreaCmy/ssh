package com.cmy.framework.impl;
  
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
//import org.apache.avalon.framework.configuration.Configuration;
//import org.apache.avalon.framework.configuration.ConfigurationException;
//import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.beanutils.PropertyUtils;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.Velocity;
//import org.apache.velocity.context.Context;
//import org.fl.noodle.common.dbseparate.aop.AbstractDbseparateMethodInterceptor;
//import org.fl.noodle.common.mvc.vo.PageVo;
//import org.fl.noodle.common.util.json.JsonTranslator;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
//import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.BeansDtdResolver;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.util.Assert;
import org.xml.sax.XMLReader;

public class DynamicSqlTemplateImpl implements DynamicSqlTemplate, InitializingBean, ResourceLoaderAware {
    private ResourceLoader resourceLoader;
    private List<String> fileNameList;
    private final Map<String, String> nameSqlMap = new HashMap();
    private boolean isRefresh = false;
    private final Map<String, Long> fileNameTimeMap = new HashMap();
    private HibernateTemplate hibernateTemplate;
    private int pageSize;
    
    /*@Autowired(
        required = false
    )
    private AmqpTemplate copylogAmqpTemplate;*/
    private boolean copyLogSwitch;

    public DynamicSqlTemplateImpl() {
    }

    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

 /*   private DefaultConfigurationBuilder getBuilder() throws Exception {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        saxParserFactory.setValidating(false);
        SAXParser saxParser = saxParserFactory.newSAXParser();
        XMLReader parser = saxParser.getXMLReader();
        parser.setEntityResolver(new BeansDtdResolver());
        return new DefaultConfigurationBuilder(parser);
    }

    public void afterPropertiesSet() throws Exception {
        this.buildNameSqlMap();
        if(this.isRefresh) {
            this.buildFileNameTimeMap();
        }

    }

    private void buildNameSqlMap() throws Exception {
        DefaultConfigurationBuilder builder = this.getBuilder();

        for(int i = 0; i < this.fileNameList.size(); ++i) {
            String fileName = ((String)this.fileNameList.get(i)).trim();
            if(this.resourceLoader instanceof ResourcePatternResolver) {
                try {
                    Resource[] resource = ((ResourcePatternResolver)this.resourceLoader).getResources(fileName);
                    this.buildNameSqlMap(builder, resource);
                } catch (IOException var5) {
                    throw new ConfigurationException("Could not resolve sql definition resource pattern [" + fileName + "]", var5);
                }
            } else {
                Resource var6 = this.resourceLoader.getResource(fileName);
                this.buildNameSqlMap(builder, new Resource[]{var6});
            }
        }

    }

    private void buildNameSqlMap(DefaultConfigurationBuilder builder, Resource[] resources) throws Exception {
        Resource[] var3 = resources;
        int var4 = resources.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Resource resource = var3[var5];
            Configuration config = builder.build(resource.getInputStream());
            Configuration[] querys = config.getChildren("query");
            Configuration[] var9 = querys;
            int var10 = querys.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Configuration query = var9[var11];
                String queryName = query.getAttribute("name");
                if(queryName.equals("")) {
                    throw new ConfigurationException("Sql name is essential attribute in a <query>.");
                }

                this.nameSqlMap.put(queryName, query.getValue());
            }
        }

    }

    public void setRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    private void buildFileNameTimeMap() throws Exception {
        for(int i = 0; i < this.fileNameList.size(); ++i) {
            String fileName = ((String)this.fileNameList.get(i)).trim();
            if(this.resourceLoader instanceof ResourcePatternResolver) {
                try {
                    Resource[] resource = ((ResourcePatternResolver)this.resourceLoader).getResources(fileName);
                    this.buildFileNameTimeMap(resource);
                } catch (IOException var4) {
                    throw new ConfigurationException("Could not resolve sql definition resource pattern [" + fileName + "]", var4);
                }
            } else {
                Resource var5 = this.resourceLoader.getResource(fileName);
                this.buildFileNameTimeMap(new Resource[]{var5});
            }
        }

    }

    private void buildFileNameTimeMap(Resource[] resources) throws Exception {
        Resource[] var2 = resources;
        int var3 = resources.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Resource resource = var2[var4];
            String fileName = resource.getFilename();
            long fileTime = resource.lastModified();
            this.fileNameTimeMap.put(fileName, Long.valueOf(fileTime));
        }

    }

    private String getSqlByName(String key) throws Exception {
        if(this.isRefresh) {
            this.refreshNameSqlMap();
        }

        return (String)this.nameSqlMap.get(key);
    }

    private void refreshNameSqlMap() throws Exception {
        DefaultConfigurationBuilder builder = this.getBuilder();

        for(int i = 0; i < this.fileNameList.size(); ++i) {
            String fileName = ((String)this.fileNameList.get(i)).trim();
            if(this.resourceLoader instanceof ResourcePatternResolver) {
                try {
                    Resource[] resource = ((ResourcePatternResolver)this.resourceLoader).getResources(fileName);
                    this.refreshNameSqlMap(builder, resource);
                } catch (IOException var5) {
                    throw new ConfigurationException("Could not resolve sql definition resource pattern [" + fileName + "]", var5);
                }
            } else {
                Resource var6 = this.resourceLoader.getResource(fileName);
                this.refreshNameSqlMap(builder, new Resource[]{var6});
            }
        }

    }

    private void refreshNameSqlMap(DefaultConfigurationBuilder builder, Resource[] resources) throws Exception {
        Resource[] var3 = resources;
        int var4 = resources.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Resource resource = var3[var5];
            String filename = resource.getFilename();
            long fileTimeNow = resource.lastModified();
            Map var10 = this.fileNameTimeMap;
            synchronized(this.fileNameTimeMap) {
                long fileTime = this.fileNameTimeMap.get(filename) != null?((Long)this.fileNameTimeMap.get(filename)).longValue():0L;
                if(fileTime != fileTimeNow) {
                    this.buildNameSqlMap(builder, resource);
                    this.fileNameTimeMap.put(filename, Long.valueOf(fileTimeNow));
                }
            }
        }

    }

    private void buildNameSqlMap(DefaultConfigurationBuilder builder, Resource resource) throws Exception {
        Configuration config = builder.build(resource.getInputStream());
        Configuration[] querys = config.getChildren("query");
        Configuration[] var5 = querys;
        int var6 = querys.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Configuration query = var5[var7];
            String queryName = query.getAttribute("name");
            if(queryName.equals("")) {
                throw new ConfigurationException("Sql name is essential attribute in a <query>.");
            }

            this.nameSqlMap.put(queryName, query.getValue());
        }

    }

    public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
        this.hibernateTemplate = hibernateTemplate;
    }

    public void setFileNameList(List<String> fileNameList) {
        this.fileNameList = fileNameList;
    }

    public List<String> getFileNameList() {
        return this.fileNameList;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }

    public boolean getIsRefresh() {
        return this.isRefresh;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    private static String removeOrders(String sql) {
        Assert.hasText(sql);
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", 2);
        Matcher m = p.matcher(sql);
        StringBuffer sb = new StringBuffer();

        while(m.find()) {
            m.appendReplacement(sb, "");
        }

        m.appendTail(sb);
        return sb.toString();
    }

    private static String removeSelect(String sql) {
        Assert.hasText(sql);
        Pattern pattern = Pattern.compile("from\\s");
        Matcher matcher = pattern.matcher(sql.toLowerCase());
        boolean findResult = matcher.find();
        Assert.isTrue(findResult, " hql : " + sql + " must has a keyword \'from\'");
        return sql.substring(matcher.start());
    }

    private Context generateVelocityContext(Map<String, Object> params) {
        VelocityContext context = new VelocityContext();
        if(null == params) {
            return context;
        } else {
            Iterator iterator = params.keySet().iterator();

            while(iterator.hasNext()) {
                String key = (String)iterator.next();
                Object value = params.get(key);
                context.put(key, value);
            }

            return context;
        }
    }

    private void setProperties(Query query, Context context, String[] namedParams) throws HibernateException {
        for(int i = 0; i < namedParams.length; ++i) {
            String namedParam = namedParams[i];
            Object object = context.get(namedParam);
            if(object != null) {
                if(object instanceof Collection) {
                    query.setParameterList(namedParam, (Collection)object);
                } else {
                    query.setParameter(namedParam, object);
                }
            }
        }

    }

    *//** @deprecated *//*
    @Deprecated
    public <T> PageVo<T> queryPage(String sqlName, Map<String, Object> params, int pageIndex, int pageSize, Class<T> clazz) throws Exception {
        return this.queryPageBySql(sqlName, params, pageIndex, pageSize, clazz, false);
    }

    *//** @deprecated *//*
    @Deprecated
    public <T> List<T> queryList(String sqlName, Map<String, Object> params, Class<T> clazz) throws Exception {
        return this.queryPageBySql(sqlName, params, 0, 0, clazz, false).getData();
    }

    *//** @deprecated *//*
    @Deprecated
    public <T> T queryOne(String sqlName, Map<String, Object> params, Class<T> clazz) throws Exception {
        List list = this.queryList(sqlName, params, clazz);
        return list.size() > 0?list.get(0):null;
    }

    public <T> PageVo<T> queryPageBySql(String sqlName, Map<String, Object> params, final int pageIndex, final int pageSize, final Class<T> clazz, boolean allFlag) throws Exception {
        String sqlOriginal = this.getSqlByName(sqlName);
        if(sqlOriginal == null) {
            throw new Exception("QueryPageBySql -> The sql is not find, Sql: " + sqlName);
        } else {
            if(!allFlag) {
                sqlOriginal = this.setDefaultWhereCondition(sqlOriginal, params, clazz);
            }

            final Context context = this.generateVelocityContext(params);
            StringWriter writer = new StringWriter();
            Velocity.evaluate(context, writer, "Hibernate", sqlOriginal);
            final String sql = writer.toString();
            Long total = Long.valueOf(0L);
            if(pageIndex > 0 && pageSize > 0) {
                final String pageSizeFinal = "select sum(num) from (select count(*) as num " + removeSelect(removeOrders(sql)) + ") as total";
                total = (Long)this.hibernateTemplate.execute(new HibernateCallback() {
                    public Long doInHibernate(Session session) throws HibernateException, SQLException {
                        SQLQuery query = session.createSQLQuery(pageSizeFinal);
                        DynamicSqlTemplateImpl.this.setProperties(query, context, query.getNamedParameters());
                        Number count = (Number)query.uniqueResult();
                        return Long.valueOf(count != null?count.longValue():0L);
                    }
                });
            }

            if(pageSize <= 0) {
                if(this.pageSize > 0) {
                    pageSize = this.pageSize;
                } else {
                    pageSize = 10;
                }
            }

            List list = (List)this.hibernateTemplate.execute(new HibernateCallback() {
                public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(sql);
                    query.setResultTransformer(DynamicSqlTemplateImpl.this.new MyResultTransformer(clazz));
                    DynamicSqlTemplateImpl.this.setProperties(query, context, query.getNamedParameters());
                    if(pageIndex > 0 && pageSize > 0) {
                        query.setFirstResult((pageIndex - 1) * pageSize);
                        query.setMaxResults(pageSize);
                    }

                    return query.list();
                }
            });
            long start = pageIndex > 0 && pageSize > 0?(long)((pageIndex - 1) * pageSize):0L;
            return new PageVo(start, total.longValue(), (long)pageSize, list);
        }
    }

    public <T> PageVo<T> queryPageByStrSql(String strSql, Map<String, Object> params, final int pageIndex, final int pageSize, final Class<T> clazz) throws Exception {
        final Context context = this.generateVelocityContext(params);
        StringWriter writer = new StringWriter();
        Velocity.evaluate(context, writer, "Hibernate", strSql);
        final String sql = writer.toString();
        Long total = Long.valueOf(0L);
        if(pageIndex > 0 && pageSize > 0) {
            final String pageSizeFinal = "select sum(num) from (select count(*) as num " + removeSelect(removeOrders(sql)) + ") as total";
            total = (Long)this.hibernateTemplate.execute(new HibernateCallback() {
                public Long doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(pageSizeFinal);
                    DynamicSqlTemplateImpl.this.setProperties(query, context, query.getNamedParameters());
                    Number count = (Number)query.uniqueResult();
                    return Long.valueOf(count != null?count.longValue():0L);
                }
            });
        }

        if(pageSize <= 0) {
            if(this.pageSize > 0) {
                pageSize = this.pageSize;
            } else {
                pageSize = 10;
            }
        }

        List list = (List)this.hibernateTemplate.execute(new HibernateCallback() {
            public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                query.setResultTransformer(DynamicSqlTemplateImpl.this.new MyResultTransformer(clazz));
                DynamicSqlTemplateImpl.this.setProperties(query, context, query.getNamedParameters());
                if(pageIndex > 0 && pageSize > 0) {
                    query.setFirstResult((pageIndex - 1) * pageSize);
                    query.setMaxResults(pageSize);
                }

                return query.list();
            }
        });
        long start = pageIndex > 0 && pageSize > 0?(long)((pageIndex - 1) * pageSize):0L;
        return new PageVo(start, total.longValue(), (long)pageSize, list);
    }

    public <T> T insert(Object vo, Class<T> clazz) throws Exception {
        StringUtils.CorrectString(vo);
        Object md = clazz.newInstance();
        PropertyUtils.copyProperties(md, vo);
        this.setForeignMd(vo, md, clazz);
        this.setBaseValue(vo, md);
        this.hibernateTemplate.save(md);
        if(this.businessRollBackUtil != null && AbstractDbseparateMethodInterceptor.getIsRecord() && AbstractDbseparateMethodInterceptor.getTransactionKey() != null && !AbstractDbseparateMethodInterceptor.getTransactionKey().isEmpty()) {
            this.businessRollBackUtil.saveBusinessRollBackFromInsert(AbstractDbseparateMethodInterceptor.getTransactionKey(), TraceInterceptor.getTraceKey(), md, clazz);
        }

        this.printInsertDeleteLog(vo, md, clazz, "insert");
        if(this.copylogAmqpTemplate != null && this.copyLogSwitch) {
            this.setCopyLogFromInsert(vo, md);
        } else {
            logger.debug("copylogAmqpTemplate is null or copyLogSwitch is false");
        }

        return md;
    }

    public <T> void update(Object vo, Class<T> clazz) throws Exception {
        StringUtils.CorrectString(vo);
        Object md = clazz.newInstance();
        PropertyUtils.copyProperties(md, vo);
        this.setForeignMd(vo, md, clazz);
        this.updateBaseValue(vo, md);
        this.printUpdateLog(vo, clazz, (String)null, new HashMap(), "update");
        if(this.businessRollBackUtil != null && AbstractDbseparateMethodInterceptor.getIsRecord() && AbstractDbseparateMethodInterceptor.getTransactionKey() != null && !AbstractDbseparateMethodInterceptor.getTransactionKey().isEmpty()) {
            this.businessRollBackUtil.saveBusinessRollBackFromUpdate(AbstractDbseparateMethodInterceptor.getTransactionKey(), TraceInterceptor.getTraceKey(), vo, clazz, (Map)null);
        }

        this.hibernateTemplate.update(md);
    }

    public <T> int updateExcept(Object vo, Class<T> clazz, String[] fieldNameArray) throws Exception {
        return this.updateByCreateSql(vo, clazz, false, fieldNameArray, (String[])null, (String[])null);
    }

    public <T> int updateInclude(Object vo, Class<T> clazz, String[] fieldNameArray) throws Exception {
        return this.updateByCreateSql(vo, clazz, false, (String[])null, (String[])null, fieldNameArray);
    }

    public <T> int updateNonull(Object vo, Class<T> clazz) throws Exception {
        return this.updateByCreateSql(vo, clazz, true, (String[])null, (String[])null, (String[])null);
    }

    public <T> int updateNonullNoById(Object vo, Class<T> clazz, String[] fieldNameArray) throws Exception {
        return this.updateByCreateSql(vo, clazz, true, (String[])null, fieldNameArray, (String[])null);
    }

    public <T> int updateByCreateSql(Object vo, Class<T> clazz, boolean isNonull, String[] exceptFieldNameArray, String[] selectFieldNameArray, String[] includeFieldNameArray) throws Exception {
        StringUtils.CorrectString(vo);
        HashSet exceptFieldNameSet = new HashSet();
        int tableName;
        if(exceptFieldNameArray != null) {
            String[] includeFieldNameSet = exceptFieldNameArray;
            int table = exceptFieldNameArray.length;

            for(tableName = 0; tableName < table; ++tableName) {
                String sqlBuilder = includeFieldNameSet[tableName];
                exceptFieldNameSet.add(sqlBuilder);
            }
        }

        HashSet var31 = new HashSet();
        if(includeFieldNameArray != null) {
            String[] var32 = includeFieldNameArray;
            tableName = includeFieldNameArray.length;

            for(int var35 = 0; var35 < tableName; ++var35) {
                String sqlSetBuilder = var32[var35];
                var31.add(sqlSetBuilder);
            }
        }

        Table var33 = (Table)clazz.getAnnotation(Table.class);
        String var34 = var33.name();
        StringBuilder var36 = new StringBuilder("UPDATE " + var34);
        StringBuilder var37 = new StringBuilder(" SET ");
        StringBuilder sqlWhereBuilder = new StringBuilder(" WHERE 1=1");
        HashMap paramMap = new HashMap();
        HashSet fieldSet = new HashSet();
        HashSet fieldPrimarySet = new HashSet();
        Method[] methods = clazz.getMethods();
        Method[] clazzVo = methods;
        int fields = methods.length;

        for(int baseValue = 0; baseValue < fields; ++baseValue) {
            Method context = clazzVo[baseValue];
            HashSet sql = null;
            Id returnInt = (Id)context.getAnnotation(Id.class);
            if(returnInt != null) {
                sql = fieldPrimarySet;
            } else {
                sql = fieldSet;
            }

            ManyToOne fieldName = (ManyToOne)context.getAnnotation(ManyToOne.class);
            if(fieldName != null) {
                JoinColumns fieldValue = (JoinColumns)context.getAnnotation(JoinColumns.class);
                if(fieldValue != null) {
                    JoinColumn[] field = fieldValue.value();
                    JoinColumn[] fieldValue1 = field;
                    int field1 = field.length;

                    for(int fieldValue2 = 0; fieldValue2 < field1; ++fieldValue2) {
                        JoinColumn joinColumn = fieldValue1[fieldValue2];
                        sql.add(joinColumn.name().toUpperCase());
                    }
                } else {
                    JoinColumn var59 = (JoinColumn)context.getAnnotation(JoinColumn.class);
                    sql.add(var59.name().toUpperCase());
                }
            } else {
                Column var55 = (Column)context.getAnnotation(Column.class);
                if(var55 != null) {
                    sql.add(var55.name().toUpperCase());
                }
            }
        }

        Class var38 = vo.getClass();
        Field[] var39 = var38.getDeclaredFields();
        AccessibleObject.setAccessible(var39, true);
        int var42;
        int var49;
        if(selectFieldNameArray != null) {
            String[] var40 = selectFieldNameArray;
            var42 = selectFieldNameArray.length;

            for(var49 = 0; var49 < var42; ++var49) {
                String var47 = var40[var49];
                boolean var51 = false;
                Field[] var56 = var39;
                int var61 = var39.length;

                for(int var64 = 0; var64 < var61; ++var64) {
                    Field var66 = var56[var64];
                    if(var66.getName().equals(var47)) {
                        var51 = true;
                        Object var67 = var66.get(vo);
                        if(var67 == null) {
                            throw new Exception("UpdateByCreateSql -> Select field is null in vo, Class: " + var38.getName() + ", Field: " + var47);
                        }

                        sqlWhereBuilder.append(" AND ");
                        sqlWhereBuilder.append(var66.getName());
                        sqlWhereBuilder.append(" = :");
                        sqlWhereBuilder.append(var66.getName());
                        paramMap.put(var66.getName(), var67);
                        break;
                    }
                }

                if(!var51) {
                    throw new Exception("UpdateByCreateSql -> Select field is not exist in vo, Class: " + var38.getName() + ", Field: " + var47);
                }
            }
        } else {
            Iterator var41 = fieldPrimarySet.iterator();

            while(var41.hasNext()) {
                String var44 = (String)var41.next();
                boolean var53 = false;
                Field[] var48 = var39;
                int var54 = var39.length;

                for(int var58 = 0; var58 < var54; ++var58) {
                    Field var63 = var48[var58];
                    if(var63.getName().toUpperCase().equals(var44)) {
                        var53 = true;
                        Object var65 = var63.get(vo);
                        if(var65 == null) {
                            throw new Exception("UpdateByCreateSql -> Id field is null in vo, Class: " + var38.getName() + ", Field: " + var44);
                        }

                        sqlWhereBuilder.append(" AND ");
                        sqlWhereBuilder.append(var63.getName());
                        sqlWhereBuilder.append(" = :");
                        sqlWhereBuilder.append(var63.getName());
                        paramMap.put(var63.getName(), var65);
                        break;
                    }
                }

                if(!var53) {
                    throw new Exception("UpdateByCreateSql -> Id field is not exist in vo, Class: " + var38.getName() + ", Field: " + var44);
                }
            }
        }

        Field[] var43 = var39;
        var42 = var39.length;

        for(var49 = 0; var49 < var42; ++var49) {
            Field var50 = var43[var49];
            String var57 = var50.getName();
            if(fieldSet.contains(var57.toUpperCase()) && (exceptFieldNameArray == null || exceptFieldNameArray != null && !exceptFieldNameSet.contains(var57)) && (includeFieldNameArray == null || includeFieldNameArray != null && var31.contains(var57))) {
                Object var62 = var50.get(vo);
                if(var62 != null) {
                    var37.append(var57);
                    var37.append(" = :");
                    var37.append(var57);
                    var37.append(", ");
                    paramMap.put(var57, var62);
                } else if(!isNonull) {
                    var37.append(var57);
                    var37.append(" = NULL");
                    var37.append(", ");
                }
            }
        }

        String var45 = this.updateBaseValueBySql(vo, clazz, paramMap);
        if(var45 != null && var45.length() > 0) {
            var37.append(var45);
        }

        if(var37.length() <= 5) {
            return 0;
        } else {
            var36.append(var37.substring(0, var37.length() - 2).toString());
            var36.append(sqlWhereBuilder.toString());
            final Context var46 = this.generateVelocityContext(paramMap);
            final String var60 = var36.toString();
            this.printUpdateLog(vo, clazz, var60, paramMap, "updateByCreateSql");
            if(this.businessRollBackUtil != null && AbstractDbseparateMethodInterceptor.getIsRecord() && AbstractDbseparateMethodInterceptor.getTransactionKey() != null && !AbstractDbseparateMethodInterceptor.getTransactionKey().isEmpty()) {
                this.businessRollBackUtil.saveBusinessRollBackFromUpdate(AbstractDbseparateMethodInterceptor.getTransactionKey(), TraceInterceptor.getTraceKey(), vo, clazz, paramMap);
            }

            Integer var52 = (Integer)this.hibernateTemplate.execute(new HibernateCallback() {
                public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(var60);
                    String[] namedParams = query.getNamedParameters();
                    DynamicSqlTemplateImpl.this.setProperties(query, var46, namedParams);
                    return Integer.valueOf(query.executeUpdate());
                }
            });
            if(this.copylogAmqpTemplate != null && this.copyLogSwitch) {
                this.setCopyLogFromUpdate(var34, sqlWhereBuilder.toString(), vo, clazz, var46);
            } else {
                logger.debug("copylogAmqpTemplate is null or copyLogSwitch is false");
            }

            return var52.intValue();
        }
    }

    *//** @deprecated *//*
    @Deprecated
    public int updateSql(String sqlName, Map<String, Object> params) throws Exception {
        final Context context = this.generateVelocityContext(params);
        StringWriter writer = new StringWriter();
        String sqlOriginal = this.getSqlByName(sqlName);
        if(sqlOriginal == null) {
            throw new Exception("UpdateSql -> The sql is not find: " + sqlName);
        } else {
            Velocity.evaluate(context, writer, "Hibernate", sqlOriginal);
            final String sql = writer.toString();
            return ((Integer)this.hibernateTemplate.execute(new HibernateCallback() {
                public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(sql);
                    String[] namedParams = query.getNamedParameters();
                    DynamicSqlTemplateImpl.this.setProperties(query, context, namedParams);
                    return Integer.valueOf(query.executeUpdate());
                }
            })).intValue();
        }
    }

    public <T> void delete(Object vo, Class<T> clazz) throws Exception {
        Object md = clazz.newInstance();
        PropertyUtils.copyProperties(md, vo);
        this.setForeignMd(vo, md, clazz);
        if(this.businessRollBackUtil != null && AbstractDbseparateMethodInterceptor.getIsRecord() && AbstractDbseparateMethodInterceptor.getTransactionKey() != null && !AbstractDbseparateMethodInterceptor.getTransactionKey().isEmpty()) {
            this.businessRollBackUtil.saveBusinessRollBackFromDelete(AbstractDbseparateMethodInterceptor.getTransactionKey(), TraceInterceptor.getTraceKey(), md, clazz);
        }

        if(this.copylogAmqpTemplate != null && this.copyLogSwitch) {
            this.setCopyLogFromDelete(vo, clazz);
        } else {
            logger.debug("copylogAmqpTemplate is null or copyLogSwitch is false");
        }

        this.hibernateTemplate.delete(md);
        this.printInsertDeleteLog(vo, md, clazz, "delete");
    }

    public <T> void deleteNoById(Object vo, Class<T> clazz, String[] selectFieldNameArray) throws Exception {
        if(selectFieldNameArray != null && selectFieldNameArray.length >= 1) {
            Table table = (Table)clazz.getAnnotation(Table.class);
            String tableName = table.name();
            StringBuilder sqlBuilder = new StringBuilder("DELETE FROM " + tableName);
            StringBuilder sqlWhereBuilder = new StringBuilder(" WHERE 1=1");
            HashMap paramMap = new HashMap();
            Class clazzVo = vo.getClass();
            Field[] fields = ReflectionUtils.getAllDeclaredFields(clazzVo);
            AccessibleObject.setAccessible(fields, true);
            String[] context = selectFieldNameArray;
            int sql = selectFieldNameArray.length;

            for(int var13 = 0; var13 < sql; ++var13) {
                String fieldPrimaryName = context[var13];
                boolean bool = false;
                Field[] var16 = fields;
                int var17 = fields.length;

                for(int var18 = 0; var18 < var17; ++var18) {
                    Field field = var16[var18];
                    if(field.getName().equals(fieldPrimaryName)) {
                        bool = true;
                        Object fieldValue = field.get(vo);
                        if(fieldValue == null) {
                            throw new Exception("DeleteNoById -> Select field is null in vo, Class: " + clazzVo.getName() + ", Field: " + fieldPrimaryName);
                        }

                        sqlWhereBuilder.append(" AND ");
                        sqlWhereBuilder.append(fieldPrimaryName);
                        sqlWhereBuilder.append(" = :");
                        sqlWhereBuilder.append(fieldPrimaryName);
                        paramMap.put(fieldPrimaryName, fieldValue);
                        break;
                    }
                }

                if(!bool) {
                    throw new Exception("DeleteNoById -> Select field is not exist in vo, Class: " + clazzVo.getName() + ", Field: " + fieldPrimaryName);
                }
            }

            sqlBuilder.append(sqlWhereBuilder.toString());
            final Context var21 = this.generateVelocityContext(paramMap);
            final String var22 = sqlBuilder.toString();
            this.hibernateTemplate.execute(new HibernateCallback() {
                public Integer doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(var22);
                    String[] namedParams = query.getNamedParameters();
                    DynamicSqlTemplateImpl.this.setProperties(query, var21, namedParams);
                    return Integer.valueOf(query.executeUpdate());
                }
            });
        } else {
            throw new Exception("DeleteNoById -> selectFieldNameArray is null, it is not supported to clean up the table");
        }
    }

    public <T> T load(Object vo, Class<T> clazz) throws Exception {
        return this.loadBySql(vo, clazz);
    }

    public <T> T loadBySql(Object vo, Class<T> clazz) throws Exception {
        HashMap primaryFieldMap = new HashMap();
        Method[] methods = clazz.getMethods();
        Method[] clazzVo = methods;
        int fields = methods.length;

        for(int primaryFieldSet = 0; primaryFieldSet < fields; ++primaryFieldSet) {
            Method method = clazzVo[primaryFieldSet];
            Id primaryFieldName = (Id)method.getAnnotation(Id.class);
            if(primaryFieldName != null) {
                ManyToOne bool = (ManyToOne)method.getAnnotation(ManyToOne.class);
                if(bool != null) {
                    JoinColumns var23 = (JoinColumns)method.getAnnotation(JoinColumns.class);
                    if(var23 != null) {
                        JoinColumn[] var25 = var23.value();
                        JoinColumn[] var13 = var25;
                        int field = var25.length;

                        for(int fieldValue = 0; fieldValue < field; ++fieldValue) {
                            JoinColumn joinColumn1 = var13[fieldValue];
                            primaryFieldMap.put(joinColumn1.name().toUpperCase(), joinColumn1.name().toUpperCase());
                        }
                    } else {
                        JoinColumn joinColumn = (JoinColumn)method.getAnnotation(JoinColumn.class);
                        primaryFieldMap.put(joinColumn.name().toUpperCase(), joinColumn.name().toUpperCase());
                    }
                } else {
                    Column column = (Column)method.getAnnotation(Column.class);
                    if(column != null) {
                        primaryFieldMap.put(column.name().toUpperCase(), column.name().toUpperCase());
                    }
                }
            }
        }

        Class var17 = vo.getClass();
        Field[] var18 = ReflectionUtils.getAllDeclaredFields(var17);
        AccessibleObject.setAccessible(var18, true);
        Set var19 = primaryFieldMap.keySet();
        Iterator var20 = var19.iterator();

        String var21;
        boolean var22;
        do {
            if(!var20.hasNext()) {
                return this.loadBySql((Map)primaryFieldMap, clazz);
            }

            var21 = (String)var20.next();
            var22 = false;
            Field[] var24 = var18;
            int var26 = var18.length;

            for(int var27 = 0; var27 < var26; ++var27) {
                Field var28 = var24[var27];
                if(var28.getName().toUpperCase().equals(var21)) {
                    var22 = true;
                    Object var29 = var28.get(vo);
                    if(var29 == null) {
                        throw new Exception("LoadBySql -> Id field is null in vo, Class: " + var17.getName() + ", Field: " + var21);
                    }

                    primaryFieldMap.put(var21, var29);
                    break;
                }
            }
        } while(var22);

        throw new Exception("LoadBySql -> Id field is not exist in vo, Class: " + var17.getName() + ", Field: " + var21);
    }

    public <T> T loadBySql(Map<String, Object> map, final Class<T> clazz) throws Exception {
        Table table = (Table)clazz.getAnnotation(Table.class);
        String tableName = table.name();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1 ");
        Set fieldNameSet = map.keySet();
        Iterator context = fieldNameSet.iterator();

        final String sql;
        while(context.hasNext()) {
            sql = (String)context.next();
            sqlBuilder.append("AND ");
            sqlBuilder.append(sql.toUpperCase());
            sqlBuilder.append(" = :");
            sqlBuilder.append(sql.toUpperCase());
            sqlBuilder.append(" ");
        }

        final Context context1 = this.generateVelocityContext(map);
        sql = sqlBuilder.toString();
        Object object = this.hibernateTemplate.execute(new HibernateCallback() {
            public T doInHibernate(Session session) throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql).addEntity(clazz);
                String[] namedParams = query.getNamedParameters();
                DynamicSqlTemplateImpl.this.setProperties(query, context1, namedParams);
                List list = query.list();
                return list.size() != 0?list.get(0):null;
            }
        });
        return object;
    }

    private <T> void setForeignMd(Object vo, Object md, Class<T> clazz) throws Exception {
        Method[] methods = clazz.getMethods();
        Method[] var5 = methods;
        int var6 = methods.length;

        label81:
        for(int var7 = 0; var7 < var6; ++var7) {
            Method method = var5[var7];
            ManyToOne manyToOne = (ManyToOne)method.getAnnotation(ManyToOne.class);
            if(manyToOne != null) {
                HashMap foreignNameMap = new HashMap();
                JoinColumns joinColumns = (JoinColumns)method.getAnnotation(JoinColumns.class);
                if(joinColumns != null) {
                    JoinColumn[] foreignNameSet = joinColumns.value();
                    JoinColumn[] methodName = foreignNameSet;
                    int returnType = foreignNameSet.length;

                    for(int mdForeign = 0; mdForeign < returnType; ++mdForeign) {
                        JoinColumn methodSet = methodName[mdForeign];
                        if(methodSet.referencedColumnName() != null && !methodSet.referencedColumnName().equals("")) {
                            foreignNameMap.put(methodSet.referencedColumnName().toUpperCase(), methodSet.name().toUpperCase());
                        } else {
                            foreignNameMap.put(methodSet.name().toUpperCase(), methodSet.name().toUpperCase());
                        }
                    }
                } else {
                    JoinColumn var23 = (JoinColumn)method.getAnnotation(JoinColumn.class);
                    if(var23.referencedColumnName() != null && !var23.referencedColumnName().equals("")) {
                        foreignNameMap.put(var23.referencedColumnName().toUpperCase(), var23.name().toUpperCase());
                    } else {
                        foreignNameMap.put(var23.name().toUpperCase(), var23.name().toUpperCase());
                    }
                }

                Set var24 = foreignNameMap.keySet();
                Iterator var25 = var24.iterator();

                while(true) {
                    while(true) {
                        while(var25.hasNext()) {
                            String var27 = (String)var25.next();
                            String var29 = (String)foreignNameMap.get(var27);
                            Class var31 = vo.getClass();
                            Field[] fields = ReflectionUtils.getAllDeclaredFields(var31);
                            AccessibleObject.setAccessible(fields, true);
                            Field[] var18 = fields;
                            int var19 = fields.length;

                            for(int var20 = 0; var20 < var19; ++var20) {
                                Field field = var18[var20];
                                if(field.getName().toUpperCase().equals(var29)) {
                                    Object fieldValue = field.get(vo);
                                    if(fieldValue != null && !fieldValue.equals("")) {
                                        foreignNameMap.put(var27, fieldValue);
                                        break;
                                    }

                                    foreignNameMap.remove(var27);
                                    break;
                                }
                            }
                        }

                        if(foreignNameMap.keySet().size() > 0) {
                            String var26 = method.getName();
                            Class var28 = method.getReturnType();
                            Object var30 = this.loadBySql((Map)foreignNameMap, var28);
                            if(var30 == null) {
                                throw new Exception("SetForeignMd -> The foreign record not exit");
                            }

                            Method var32 = clazz.getMethod("set" + var26.substring(3), new Class[]{var28});
                            var32.invoke(md, new Object[]{var30});
                        }
                        continue label81;
                    }
                }
            }
        }

    }

    public void flush() {
        this.hibernateTemplate.flush();
    }

    public <T> T insertOrUpdate(Object vo, Class<T> clazz) throws Exception {
        Object md = clazz.newInstance();
        PropertyUtils.copyProperties(md, vo);
        this.setForeignMd(vo, md, clazz);
        this.setBaseValue(vo, md);
        this.printUpdateLog(vo, clazz, (String)null, new HashMap(), "insertOrUpdate");
        this.hibernateTemplate.saveOrUpdate(md);
        return md;
    }

    private ResultTransformer getResultTransformer(Class<?> clazz) {
        return new DynamicSqlTemplateImpl.MyResultTransformer(clazz);
    }

    private <T> void printUpdateLog(Object vo, Class<T> clazz, String queryString, Map<String, Object> paramMap, String updateMethod) throws Exception {
        if(this.businessLogUtil != null) {
            ResultTransformer resultTransformer = this.getResultTransformer(clazz);
            Context context = this.generateVelocityContext(paramMap);
            this.businessLogUtil.printUpdateLog(vo, clazz, queryString, paramMap, updateMethod, resultTransformer, context);
        }

    }

    private <T> void printInsertDeleteLog(Object vo, Object md, Class<T> clazz, String methodName) throws Exception {
        if(this.businessLogUtil != null) {
            this.businessLogUtil.printInsertDeleteLog(vo, md, clazz, methodName);
        }

    }

    public void setBusinessLogUtil(BusinessLog businessLogUtil) {
        this.businessLogUtil = businessLogUtil;
    }

    private Map<String, Object> getUserFromVo(Object vo, Map<String, Object> map) throws Exception {
        if(IUserInfo.class.isInstance(vo)) {
            Class superClassVo = vo.getClass().getSuperclass();
            Field[] superFields = ReflectionUtils.getAllDeclaredFields(superClassVo);
            if(superFields != null && superFields.length > 0) {
                AccessibleObject.setAccessible(superFields, true);
                Field[] var5 = superFields;
                int var6 = superFields.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    Field field = var5[var7];
                    Iterator var9 = map.keySet().iterator();

                    while(var9.hasNext()) {
                        String str = (String)var9.next();
                        if(field.getName().equalsIgnoreCase(str) && field.get(vo) != null) {
                            map.replace(str, field.get(vo));
                        }
                    }
                }
            }
        }

        return map;
    }

    private void setBaseValue(Object vo, Object md) throws Exception {
        if(BaseModel.class.isInstance(md)) {
            HashMap userMap = new HashMap();
            userMap.put("currentUserId", (Object)null);
            userMap.put("currentUserName", (Object)null);
            if(UserContext.getUserID() <= 0L && UserContext.getUserID() != -98L) {
                logger.info("DynamicSqlTemplateImpl.setBaseValue UserContext鏈彇鍒�");
                this.getUserFromVo(vo, userMap);
            } else {
                logger.info("DynamicSqlTemplateImpl.setBaseValue userId=" + UserContext.getUserID() + ",userName=" + UserContext.getLoginName());
                userMap.replace("currentUserId", Long.valueOf(UserContext.getUserID()));
                userMap.replace("currentUserName", UserContext.getLoginName());
            }

            if(((BaseModel)md).getEnumDataEntityStatus() == null) {
                ((BaseModel)md).setEnumDataEntityStatus(Byte.valueOf(0));
            }

            ((BaseModel)md).setCreateOperator(userMap.get("currentUserName") == null?null:userMap.get("currentUserName").toString());
            ((BaseModel)md).setCreateOperatorId(userMap.get("currentUserId") == null?null:new Long(userMap.get("currentUserId").toString()));
            ((BaseModel)md).setCreateTime(new Date());
            ((BaseModel)md).setLastUpdateOperator(userMap.get("currentUserName") == null?null:userMap.get("currentUserName").toString());
            ((BaseModel)md).setLastUpdateOperatorId(userMap.get("currentUserId") == null?null:Long.valueOf((new Long(userMap.get("currentUserId").toString())).longValue()));
            ((BaseModel)md).setLastUpdateTime(new Date());
        }

    }

    private void updateBaseValue(Object vo, Object md) throws Exception {
        if(BaseModel.class.isInstance(md)) {
            HashMap userMap = new HashMap();
            userMap.put("currentUserId", (Object)null);
            userMap.put("currentUserName", (Object)null);
            if(UserContext.getUserID() <= 0L && UserContext.getUserID() != -98L) {
                logger.info("DynamicSqlTemplateImpl.updateBaseValue UserContext鏈彇鍒�");
                this.getUserFromVo(vo, userMap);
            } else {
                logger.info("DynamicSqlTemplateImpl.updateBaseValue userId=" + UserContext.getUserID() + ",userName=" + UserContext.getLoginName());
                userMap.replace("currentUserId", Long.valueOf(UserContext.getUserID()));
                userMap.replace("currentUserName", UserContext.getLoginName());
            }

            ((BaseModel)md).setLastUpdateOperator(userMap.get("currentUserName") == null?null:userMap.get("currentUserName").toString());
            ((BaseModel)md).setLastUpdateOperatorId(userMap.get("currentUserId") == null?null:Long.valueOf((new Long(userMap.get("currentUserId").toString())).longValue()));
            ((BaseModel)md).setLastUpdateTime(new Date());
        }

    }

    private <T> String updateBaseValueBySql(Object vo, Class<T> clazz, Map<String, Object> paramMap) throws Exception {
        StringBuilder sb = new StringBuilder();
        Object md = clazz.newInstance();
        if(BaseModel.class.isInstance(md)) {
            HashMap map = new HashMap();
            Field[] fields = vo.getClass().getSuperclass().getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            Field[] var8 = fields;
            int str = fields.length;

            for(int fieldName = 0; fieldName < str; ++fieldName) {
                Field field = var8[fieldName];
                String fieldName1 = field.getName();
                if("enumDataEntityStatus".equals(fieldName1)) {
                    Object fieldValue = field.get(vo);
                    if(fieldValue != null) {
                        map.put("enumDataEntityStatus", fieldValue);
                    }
                    break;
                }
            }

            map.put("currentUserId", (Object)null);
            map.put("currentUserName", (Object)null);
            if(UserContext.getUserID() <= 0L && UserContext.getUserID() != -98L) {
                logger.info("DynamicSqlTemplateImpl.updateBaseValueBySql UserContext鏈彇鍒�");
                this.getUserFromVo(vo, map);
            } else {
                logger.info("DynamicSqlTemplateImpl.updateBaseValueBySql userId=" + UserContext.getUserID() + ",userName=" + UserContext.getLoginName());
                map.replace("currentUserId", Long.valueOf(UserContext.getUserID()));
                map.replace("currentUserName", UserContext.getLoginName());
            }

            map.put("lastUpdateTime", new Date());
            Iterator var14 = map.keySet().iterator();

            while(var14.hasNext()) {
                String var15 = (String)var14.next();
                if(map.get(var15) != null) {
                    String var16 = var15;
                    if("currentUserId".equals(var15)) {
                        var16 = "lastUpdateOperatorId";
                    } else if("currentUserName".equals(var15)) {
                        var16 = "lastUpdateOperator";
                    }

                    sb.append(var16);
                    sb.append(" = :");
                    sb.append(var16);
                    sb.append(", ");
                    paramMap.put(var16, map.get(var15));
                }
            }
        }

        return sb.toString();
    }

    public <T> PageVo<T> queryPageBySql(String sqlName, T vo, int pageIndex, int pageSize, Class<T> clazzVo, long vid, boolean allFlag) throws Exception {
        Field[] fields = ReflectionUtils.getAllDeclaredFields(clazzVo);
        AccessibleObject.setAccessible(fields, true);
        HashMap paramsMap = new HashMap();
        Field[] var11 = fields;
        int var12 = fields.length;

        for(int var13 = 0; var13 < var12; ++var13) {
            Field field = var11[var13];
            String fieldName = field.getName();
            Object fieldValue = field.get(vo);
            if(fieldValue != null) {
                paramsMap.put(fieldName, fieldValue);
            }
        }

        if(vid != 0L) {
            paramsMap.put("id", Long.valueOf(vid));
        }

        return this.queryPageBySql(sqlName, paramsMap, pageIndex, pageSize, clazzVo, allFlag);
    }

    public <T> PageVo<T> queryPage(String sqlName, T vo, int pageIndex, int pageSize, Class<T> clazz, boolean allFlag) throws Exception {
        return this.queryPageBySql(sqlName, vo, pageIndex, pageSize, clazz, 0L, allFlag);
    }

    public <T> PageVo<T> queryPage(T vo, Class<?> clazz, int pageIndex, int pageSize, long vid, boolean allFlag) throws Exception {
        String sqlMapKey = clazz.getName() + "autoList";
        String sqlOriginal = this.getSqlByName(sqlMapKey);
        if(sqlOriginal == null) {
            String voclass = System.getProperty("line.separator");
            HashSet fieldSet = new HashSet();
            String primaryKey = "";
            Method[] methods = clazz.getMethods();
            Method[] sqlSelectBuilder = methods;
            int sqlWhereBuilder = methods.length;

            for(int clazzVo = 0; clazzVo < sqlWhereBuilder; ++clazzVo) {
                Method fields = sqlSelectBuilder[clazzVo];
                Id selectCols = (Id)fields.getAnnotation(Id.class);
                Column table = (Column)fields.getAnnotation(Column.class);
                if(selectCols != null) {
                    primaryKey = table.name();
                }

                if(table != null) {
                    fieldSet.add(table.name().toUpperCase());
                }
            }

            StringBuilder var29 = new StringBuilder(" SELECT ");
            StringBuilder var30 = new StringBuilder(" WHERE 1=1 ");
            var30.append(voclass);
            var30.append("#if($id)").append(voclass);
            var30.append(" AND ").append(primaryKey).append(" = :id").append(voclass);
            var30.append("#end ").append(voclass);
            Class var31 = vo.getClass();
            Field[] var32 = ReflectionUtils.getAllDeclaredFields(var31);
            AccessibleObject.setAccessible(var32, true);
            ArrayList var33 = new ArrayList();
            Field[] var34 = var32;
            int tableName = var32.length;

            for(int msql = 0; msql < tableName; ++msql) {
                Field field = var34[msql];
                String fieldName = field.getName();
                if(fieldSet.contains(fieldName.toUpperCase())) {
                    var33.add(fieldName);
                    var30.append(" #if($").append(field.getName()).append(")").append(voclass);
                    var30.append(" AND ").append(field.getName()).append(" = :").append(field.getName()).append(voclass);
                    var30.append(" #end").append(voclass);
                }
            }

            if(var33.size() > 0) {
                var29.append(StringUtils.join(var33, ", "));
            }

            Table var35 = (Table)clazz.getAnnotation(Table.class);
            String var36 = var35.name();
            var29.append(" From ").append(var36);
            String var26 = var29.append(var30).toString();
            Map var27 = this.nameSqlMap;
            synchronized(this.nameSqlMap) {
                this.nameSqlMap.put(sqlMapKey, var26);
            }
        }

        Class var28 = vo.getClass();
        return this.queryPageBySql(sqlMapKey, vo, pageIndex > 0?pageIndex:1, pageSize > 0?pageSize:2000, var28, vid, allFlag);
    }

    public <T> List<T> queryList(String sqlName, T vo, Class<T> clazz, boolean allFlag) throws Exception {
        return this.queryPageBySql(sqlName, vo, 0, 0, clazz, 0L, allFlag).getData();
    }

    public <T> T queryOne(String sqlName, T vo, Class<T> clazz, boolean allFlag) throws Exception {
        List list = this.queryPageBySql(sqlName, vo, 0, 1, clazz, 0L, allFlag).getData();
        return list.size() > 0?list.get(0):null;
    }

    private <T> String setDefaultWhereCondition(String sql, Map<String, Object> paramsMap, Class<T> clazzVo) throws Exception {
        Assert.hasText(sql);
        int wherePos = sql.toLowerCase().indexOf("where ");
        if(sql.toLowerCase().indexOf("join ") == -1 && wherePos != -1 && wherePos == sql.toLowerCase().lastIndexOf("where ")) {
            StringBuilder newSql = new StringBuilder(sql.substring(0, wherePos + 6));
            Object vo = clazzVo.newInstance();
            if(vo instanceof IDataEntity && !paramsMap.containsKey("enumDataEntityStatus")) {
                paramsMap.put("enumDataEntityStatus", Byte.valueOf(0));
                newSql.append("enumDataEntityStatus = :enumDataEntityStatus AND ");
            }

            newSql.append(sql.substring(wherePos + 6));
            return newSql.toString();
        } else {
            return sql;
        }
    }

    public boolean isCopyLogSwitch() {
        return this.copyLogSwitch;
    }

    public void setCopyLogSwitch(boolean copyLogSwitch) {
        this.copyLogSwitch = copyLogSwitch;
    }

    private <T> void setCopyLogFromInsert(Object vo, T md) throws Exception {
        logger.debug("DynamicSqlTemplateImpl.setCopyLogFromInsert is start");
        Object obj = vo.getClass().newInstance();
        PropertyUtils.copyProperties(obj, md);
        CopyLogVo copyLogVo = new CopyLogVo();
        copyLogVo.setVo(obj);
        copyLogVo.setCopyLogOperation("insert");
        if(UserContext.getUserID() > 0L) {
            copyLogVo.setCopyLogOperatorId(Long.valueOf(UserContext.getUserID()));
        } else {
            copyLogVo.setCopyLogOperatorId(new Long(-1L));
        }

        copyLogVo.setCopyLogOperationTime(new Date());
        copyLogVo.setModelClassName(md.getClass().getName());
        FrameMqManager.getInstance().sendMessage("copylogqueue", JsonTranslator.toString(copyLogVo));
    }

    private <T> void setCopyLogFromUpdate(String tableName, String whereSql, Object vo, final Class<T> clazz, final Context context) throws Exception {
        logger.info("DynamicSqlTemplateImpl.setCopyLogFromUpdate is start");
        StringBuilder sb = new StringBuilder();
        ArrayList selectCols = new ArrayList();
        Field[] fields = ReflectionUtils.getAllDeclaredFields(clazz);
        AccessibleObject.setAccessible(fields, true);
        Method[] querySql = clazz.getMethods();
        int copyLogVoList = querySql.length;

        for(int var11 = 0; var11 < copyLogVoList; ++var11) {
            Method voTemp = querySql[var11];
            Column copyLogVo = (Column)voTemp.getAnnotation(Column.class);
            if(copyLogVo != null && copyLogVo.name() != null) {
                Field[] var14 = fields;
                int var15 = fields.length;

                for(int var16 = 0; var16 < var15; ++var16) {
                    Field field = var14[var16];
                    if(field != null && copyLogVo.name().equalsIgnoreCase(field.getName())) {
                        selectCols.add(field.getName());
                    }
                }
            }
        }

        if(!selectCols.isEmpty()) {
            sb.append("SELECT " + StringUtils.join(selectCols, ", ") + " FROM " + tableName);
            sb.append(whereSql);
            final String var18 = sb.toString();
            List var19 = (List)this.hibernateTemplate.execute(new HibernateCallback() {
                public List<T> doInHibernate(Session session) throws HibernateException, SQLException {
                    SQLQuery query = session.createSQLQuery(var18);
                    query.setResultTransformer(DynamicSqlTemplateImpl.this.new MyResultTransformer(clazz));
                    DynamicSqlTemplateImpl.this.setProperties(query, context, query.getNamedParameters());
                    return query.list();
                }
            });
            if(var19 != null && !var19.isEmpty()) {
                Iterator var20 = var19.iterator();

                while(var20.hasNext()) {
                    Object var21 = var20.next();
                    CopyLogVo var22 = new CopyLogVo();
                    var22.setVo(var21);
                    var22.setCopyLogOperation("update");
                    if(UserContext.getUserID() > 0L) {
                        var22.setCopyLogOperatorId(Long.valueOf(UserContext.getUserID()));
                    } else {
                        var22.setCopyLogOperatorId(new Long(-1L));
                    }

                    var22.setCopyLogOperationTime(new Date());
                    var22.setModelClassName(clazz.getName());
                    FrameMqManager.getInstance().sendMessage("copylogqueue", JsonTranslator.toString(var22));
                }
            } else {
                logger.info("DynamicSqlTemplateImpl.setCopyLogFromUpdate 鏈幏鍙栧埌update鏁版嵁");
            }
        } else {
            logger.info("DynamicSqlTemplateImpl.setCopyLogFromUpdate selectCols is null, class=" + clazz.getName());
        }

    }

    private <T> void setCopyLogFromDelete(Object vo, Class<T> clazz) throws Exception {
        logger.debug("DynamicSqlTemplateImpl.setCopyLogFromDelete is start");
        List voList = this.queryPage(vo, clazz, 0, 0, 0L, true).getData();
        if(voList != null && !voList.isEmpty()) {
            Iterator var4 = voList.iterator();

            while(var4.hasNext()) {
                Object queryVo = var4.next();
                Object obj = vo.getClass().newInstance();
                PropertyUtils.copyProperties(obj, queryVo);
                CopyLogVo copyLogVo = new CopyLogVo();
                copyLogVo.setVo(obj);
                copyLogVo.setCopyLogOperation("delete");
                if(UserContext.getUserID() > 0L) {
                    copyLogVo.setCopyLogOperatorId(Long.valueOf(UserContext.getUserID()));
                } else {
                    copyLogVo.setCopyLogOperatorId(new Long(-1L));
                }

                copyLogVo.setCopyLogOperationTime(new Date());
                copyLogVo.setModelClassName(clazz.getName());
                FrameMqManager.getInstance().sendMessage("copylogqueue", JsonTranslator.toString(copyLogVo));
            }
        }

    }*/

   /* class MyResultTransformer implements ResultTransformer {
        private static final long serialVersionUID = 4216528307922649659L;
        Class<?> clazz;

        MyResultTransformer(Class<?> this$0) {
            this.clazz = clazz;
        }

        public List transformList(List arg0) {
            return arg0;
        }

        public Object transformTuple(Object[] arg0, String[] arg1) {
            Object object = null;

            try {
                object = this.clazz.newInstance();

                for(int e = 0; e < arg1.length; ++e) {
                    Field field = ReflectionUtils.findField(this.clazz, arg1[e]);
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, object, this.getByType(field, arg0[e]));
                }

                StringUtils.CorrectString(object);
            } catch (Exception var6) {
                if(DynamicSqlTemplateImpl.logger.isErrorEnabled()) {
                    DynamicSqlTemplateImpl.logger.warn("MyResultTransformer -> transformTuple, Class: " + this.clazz.getName() + " -> " + var6.getMessage());
                }
            }

            return object;
        }

        private Object getByType(Field field, Object paramObject) {
            if(paramObject instanceof BigInteger) {
                BigInteger bigDecimal = (BigInteger)paramObject;
                if(!field.getType().isAssignableFrom(Integer.TYPE) && !field.getType().isAssignableFrom(Integer.class)) {
                    if(!field.getType().isAssignableFrom(Long.TYPE) && !field.getType().isAssignableFrom(Long.class)) {
                        if(field.getType().isAssignableFrom(Date.class)) {
                            paramObject = new Date(bigDecimal.longValue());
                        }
                    } else {
                        paramObject = Long.valueOf(bigDecimal.longValue());
                    }
                } else {
                    paramObject = Integer.valueOf(bigDecimal.intValue());
                }
            } else if(paramObject instanceof BigDecimal) {
                BigDecimal bigDecimal1 = (BigDecimal)paramObject;
                if(!field.getType().isAssignableFrom(Integer.TYPE) && !field.getType().isAssignableFrom(Integer.class)) {
                    if(!field.getType().isAssignableFrom(Long.TYPE) && !field.getType().isAssignableFrom(Long.class)) {
                        if(field.getType().isAssignableFrom(Date.class)) {
                            paramObject = new Date(bigDecimal1.longValue());
                        } else if(!field.getType().isAssignableFrom(Float.TYPE) && !field.getType().isAssignableFrom(Float.class)) {
                            if(field.getType().isAssignableFrom(Double.TYPE) || field.getType().isAssignableFrom(Double.class)) {
                                paramObject = Double.valueOf(bigDecimal1.doubleValue());
                            }
                        } else {
                            paramObject = Float.valueOf(bigDecimal1.floatValue());
                        }
                    } else {
                        paramObject = Long.valueOf(bigDecimal1.longValue());
                    }
                } else {
                    paramObject = Integer.valueOf(bigDecimal1.intValue());
                }
            }

            return paramObject;
        }
    }*/
} 
