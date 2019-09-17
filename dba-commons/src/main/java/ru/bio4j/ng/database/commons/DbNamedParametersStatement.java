package ru.bio4j.ng.database.commons;

import ru.bio4j.ng.commons.utils.Regexs;
import ru.bio4j.ng.commons.utils.Sqls;
import ru.bio4j.ng.commons.utils.Strings;
import ru.bio4j.ng.database.api.SQLNamedParametersStatement;

import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DbNamedParametersStatement implements SQLNamedParametersStatement {
    /** The statement this object is wrapping. */
    private PreparedStatement statement;

    /** Maps parameter names to arrays of ints which are the parameter indices.
     */
    private final List<String> paramNames;
    private final Map<String, String> paramTypes;
    private final Map<String, String> outParamTypes;
    private final Map<String, Object> paramValues;
    private final Map<String, int[]> indexMap;
    private final String origQuery;
    private final String parsedQuery;

    private DbNamedParametersStatement(String query) {
        paramNames = new ArrayList<>();
        paramTypes = new HashMap();
        outParamTypes = new HashMap();
        paramValues = new HashMap();
        indexMap=new HashMap();
        origQuery = query;
        parsedQuery=parse(query, paramNames, indexMap);
        for (String pn : indexMap.keySet()){
            paramValues.put(pn, null);
        }
    }

    public String getParamsAsString(){
        if(paramNames != null) {
            StringBuilder sb = new StringBuilder();
            int indx = 1;
            String paramName = null;
            String paramDir = null;
            Object parVal = null;
            for (String key : paramNames) {
                paramDir = outParamTypes.containsKey(key.toLowerCase()) ?
                        String.format("%s(out)(%s)", key.toLowerCase(), outParamTypes.get(key.toLowerCase())) :
                        String.format("%s(in)(%s)", key.toLowerCase(), paramTypes.get(key.toLowerCase()));
                paramName = "\t" + Strings.padLeft(""+indx, 4) + "-" + Strings.padRight(paramDir, 50).replace(" ", ".");
                parVal = paramValues.get(key.toLowerCase());
                Strings.append(sb, String.format(parVal instanceof String ? "%s\"%s\"" : "%s[%s]", paramName, ""+parVal), ";\n");
                indx++;
            }
            return sb.toString() + ";\n";
        }
        return null;
    }

    public String getOrigQuery(){
        return origQuery;
    }
    public String getParsedQuery(){
        return parsedQuery;
    }

    public static SQLNamedParametersStatement prepareStatement(Connection connection, String query) {
        try {
            DbNamedParametersStatement sttmnt = new DbNamedParametersStatement(query);
            sttmnt.statement = connection.prepareStatement(sttmnt.parsedQuery);
            return sttmnt;
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    public static SQLNamedParametersStatement prepareCall(Connection connection, String query) {
        try {
            DbNamedParametersStatement sttmnt = new DbNamedParametersStatement(query);
            sttmnt.statement = connection.prepareCall(sttmnt.parsedQuery);
            return sttmnt;
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    public static final String parse(String query, List paramNames, Map paramMap) {
        final String doubleDotsPlaceholder = "/$doubleDotsPlaceholder$/";
        final String assignsPlaceholder = "/$assignsPlaceholder$/";
        String preparedQuery = Strings.replace(query, "::", doubleDotsPlaceholder);
        preparedQuery = Strings.replace(preparedQuery, ":=", assignsPlaceholder);
        String clearQuery = Sqls.deleteNonSQLSubstringsInSQL(preparedQuery);

        List<String> paramNamesList = Sqls.extractParamNamesFromSQL(clearQuery);
        if(paramNames != null) {
            paramNames.clear();
            for(String pn : paramNamesList)
                paramNames.add(pn.toLowerCase());
        }

        final String r = "\\:\\b[\\w\\#\\$]+";
        Matcher m = Regexs.match(clearQuery, r, Pattern.MULTILINE + Pattern.CASE_INSENSITIVE);
        int indx = 1;
        while (m.find()) {
            String paramName = m.group().substring(1).toLowerCase();

            List indexList=(List)paramMap.get(paramName);
            if(indexList==null) {
                indexList=new LinkedList();
                paramMap.put(paramName, indexList);
            }
            indexList.add(new Integer(indx));

            indx++;
        }

        // replace the lists of Integer objects with arrays of ints
        for(Iterator itr=paramMap.entrySet().iterator(); itr.hasNext();) {
            Map.Entry entry=(Map.Entry)itr.next();
            List list=(List)entry.getValue();
            int[] indexes=new int[list.size()];
            int i=0;
            for(Iterator itr2=list.iterator(); itr2.hasNext();) {
                Integer x=(Integer)itr2.next();
                indexes[i++]=x.intValue();
            }
            entry.setValue(indexes);
        }

        for(String paramName : paramNamesList){
            preparedQuery = Regexs.replace(preparedQuery, "\\Q:"+paramName+"\\E\\b", "?", Pattern.MULTILINE+Pattern.CASE_INSENSITIVE);
        }

        String unpreparedQuery = Strings.replace(preparedQuery, doubleDotsPlaceholder, "::");
        unpreparedQuery = Strings.replace(unpreparedQuery, assignsPlaceholder, ":=");
        return unpreparedQuery;

    }

    private int[] getIndexes(String name) {
        int[] indexes=indexMap.get(name.toLowerCase());
        if(indexes==null) {
            throw new IllegalArgumentException("Parameter not found: "+name.toLowerCase());
        }
        return indexes;
    }

    @Override
    public void setObjectAtName(String name, Object value) {
        setObjectAtName(name, value, -999);
    }

    @Override
    public void setObjectAtName(String name, Object value, int targetSqlType) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(targetSqlType));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                int indx = indexes[i];
                if (targetSqlType == -999)
                    statement.setObject(indx, value);
                else if (value instanceof InputStream && targetSqlType == Types.BLOB)
                    statement.setBinaryStream(indx, (InputStream) value);
                else if (targetSqlType == Types.CLOB) {
                    Clob clob = statement.getConnection().createClob();
                    clob.setString(1, "" + value);
                    statement.setClob(indx, clob);
                } else {
                    if (value != null && value.getClass() == java.util.Date.class)
                        value = new java.sql.Date(((java.util.Date) value).getTime());
                    statement.setObject(indx, value, targetSqlType);
                }
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setStringAtName(String name, String value) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.VARCHAR));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setString(indexes[i], value);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setIntAtName(String name, int value) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.INTEGER));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setInt(indexes[i], value);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setLongAtName(String name, long value) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.BIGINT));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setLong(indexes[i], value);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setTimestampAtName(String name, Timestamp value) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.TIMESTAMP));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setTimestamp(indexes[i], value);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setDateAtName(String name, Date value) {
        try {
            paramValues.put(name.toLowerCase(), value);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.DATE));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setDate(indexes[i], value);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }


    @Override
    public void setNullAtName(String name) {
        try {
            paramValues.put(name.toLowerCase(), null);
            paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.NULL));

            int[] indexes = getIndexes(name);
            for (int i = 0; i < indexes.length; i++) {
                statement.setNull(indexes[i], Types.VARCHAR);
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void registerOutParameter(String paramName, int sqlType) {
        try {
            outParamTypes.put(paramName.toLowerCase(), DbUtils.getInstance().getSqlTypeName(sqlType));

            if (statement instanceof CallableStatement) {
                int[] indexes = getIndexes(paramName);
                for (int i = 0; i < indexes.length; i++) {
                    ((CallableStatement) statement).registerOutParameter(indexes[i], sqlType);
                }
            }
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public Object getObject(String paramName) {
        try {
            if (statement instanceof CallableStatement) {
                int[] indexes = getIndexes(paramName);
                for (int i = 0; i < indexes.length; i++) {
                    return ((CallableStatement) statement).getObject(indexes[i]);
                }
            }
            return null;
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public PreparedStatement getStatement() {
        return statement;
    }


    @Override
    public boolean execute() {
        try {
            return statement.execute();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }


    @Override
    public ResultSet executeQuery() {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int executeUpdate() {
        try {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public ResultSet executeQuery(String sql) {
        try {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int executeUpdate(String sql) {
        try {
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getMaxFieldSize() {
        try {
            return statement.getMaxFieldSize();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setMaxFieldSize(int max) {
        try {
            statement.setMaxFieldSize(max);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getMaxRows() {
        try {
            return statement.getMaxRows();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setMaxRows(int max) {
        try {
            statement.setMaxRows(max);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setEscapeProcessing(boolean enable) {
        try {
            statement.setEscapeProcessing(enable);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getQueryTimeout() {
        try {
            return statement.getQueryTimeout();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setQueryTimeout(int seconds) {
        try{
            statement.setQueryTimeout(seconds);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void cancel() {
        try {
            statement.cancel();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public SQLWarning getWarnings() {
        try {
            return statement.getWarnings();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void clearWarnings() {
        try {
            statement.clearWarnings();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setCursorName(String name) {
        try {
            statement.setCursorName(name);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean execute(String sql) {
        try {
            return statement.execute();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public ResultSet getResultSet() {
        try {
            return statement.getResultSet();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getUpdateCount() {
        try {
            return statement.getUpdateCount();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean getMoreResults() {
        try {
            return statement.getMoreResults();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setFetchDirection(int direction) {
        try {
            statement.setFetchDirection(direction);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getFetchDirection() {
        try {
            return statement.getFetchDirection();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setFetchSize(int rows) {
        try {
            statement.setFetchSize(rows);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getFetchSize() {
        try {
            return statement.getFetchSize();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getResultSetConcurrency() {
        try {
            return statement.getResultSetConcurrency();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getResultSetType() {
        try {
            return statement.getResultSetType();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void addBatch(String sql) {
        try {
            statement.addBatch(sql);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void clearBatch() {
        try {
            statement.clearBatch();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }


    public void addBatch() {
        try {
            statement.addBatch();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }


    public int[] executeBatch() {
        try {
            return statement.executeBatch();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public Connection getConnection() {
        try {
            return statement.getConnection();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean getMoreResults(int current) {
        try {
            return statement.getMoreResults(current);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public ResultSet getGeneratedKeys() {
        try {
            return statement.getGeneratedKeys();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) {
        try {
            return statement.executeUpdate(sql, autoGeneratedKeys);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) {
        try {
            return statement.executeUpdate(sql, columnIndexes);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) {
        try {
            return statement.executeUpdate(sql, columnNames);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) {
        try {
            return statement.execute(sql, autoGeneratedKeys);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) {
        try {
            return statement.execute(sql, columnIndexes);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean execute(String sql, String[] columnNames) {
        try {
            return statement.execute(sql, columnNames);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public int getResultSetHoldability() {
        try {
            return statement.getResultSetHoldability();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return statement.isClosed();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void setPoolable(boolean poolable) {
        try {
            statement.setPoolable(poolable);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean isPoolable() {
        try {
            return statement.isPoolable();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public void closeOnCompletion() {
        try {
            statement.closeOnCompletion();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean isCloseOnCompletion() {
        try {
            return statement.isCloseOnCompletion();
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        try {
            return statement.unwrap(iface);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        try {
            return statement.isWrapperFor(iface);
        } catch (SQLException e) {
            throw SQLExceptionExt.create(e);
        }
    }

    public List<String> getParamNames() {
        return paramNames;
    }

}