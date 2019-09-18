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

    public static SQLNamedParametersStatement prepareStatement(Connection connection, String query) throws SQLException {
        DbNamedParametersStatement sttmnt = new DbNamedParametersStatement(query);
        sttmnt.statement = connection.prepareStatement(sttmnt.parsedQuery);
        return sttmnt;
    }

    public static SQLNamedParametersStatement prepareCall(Connection connection, String query) throws SQLException {
        DbNamedParametersStatement sttmnt = new DbNamedParametersStatement(query);
        sttmnt.statement = connection.prepareCall(sttmnt.parsedQuery);
        return sttmnt;
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
    public void setObjectAtName(String name, Object value) throws SQLException {
        setObjectAtName(name, value, -999);
    }

    @Override
    public void setObjectAtName(String name, Object value, int targetSqlType) throws SQLException {
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
    }

    @Override
    public void setStringAtName(String name, String value) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.VARCHAR));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setString(indexes[i], value);
        }
    }

    @Override
    public void setIntAtName(String name, int value) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.INTEGER));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setInt(indexes[i], value);
        }
    }

    @Override
    public void setLongAtName(String name, long value) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.BIGINT));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setLong(indexes[i], value);
        }
    }

    @Override
    public void setTimestampAtName(String name, Timestamp value) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.TIMESTAMP));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setTimestamp(indexes[i], value);
        }
    }

    @Override
    public void setDateAtName(String name, Date value) throws SQLException {
        paramValues.put(name.toLowerCase(), value);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.DATE));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setDate(indexes[i], value);
        }
    }


    @Override
    public void setNullAtName(String name) throws SQLException {
        paramValues.put(name.toLowerCase(), null);
        paramTypes.put(name.toLowerCase(), DbUtils.getInstance().getSqlTypeName(Types.NULL));

        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setNull(indexes[i], Types.VARCHAR);
        }
    }

    @Override
    public void registerOutParameter(String paramName, int sqlType) throws SQLException {
        outParamTypes.put(paramName.toLowerCase(), DbUtils.getInstance().getSqlTypeName(sqlType));

        if (statement instanceof CallableStatement) {
            int[] indexes = getIndexes(paramName);
            for (int i = 0; i < indexes.length; i++) {
                ((CallableStatement) statement).registerOutParameter(indexes[i], sqlType);
            }
        }
    }

    @Override
    public Object getObject(String paramName) throws SQLException {
        if (statement instanceof CallableStatement) {
            int[] indexes = getIndexes(paramName);
            for (int i = 0; i < indexes.length; i++) {
                return ((CallableStatement) statement).getObject(indexes[i]);
            }
        }
        return null;
    }

    @Override
    public PreparedStatement getStatement() {
        return statement;
    }


    @Override
    public boolean execute() throws SQLException {
        return statement.execute();
    }


    @Override
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return statement.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return statement.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        statement.close();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return statement.getMaxFieldSize();
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        statement.setMaxFieldSize(max);
    }

    @Override
    public int getMaxRows() throws SQLException {
        return statement.getMaxRows();
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        statement.setMaxRows(max);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        statement.setEscapeProcessing(enable);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return statement.getQueryTimeout();
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        statement.setQueryTimeout(seconds);
    }

    @Override
    public void cancel() throws SQLException {
        statement.cancel();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return statement.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        statement.clearWarnings();
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        statement.setCursorName(name);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return statement.execute();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return statement.getResultSet();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return statement.getUpdateCount();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return statement.getMoreResults();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        statement.setFetchDirection(direction);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return statement.getFetchDirection();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        statement.setFetchSize(rows);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return statement.getFetchSize();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return statement.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return statement.getResultSetType();
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        statement.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        statement.clearBatch();
    }


    public void addBatch() throws SQLException {
        statement.addBatch();
    }


    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return statement.getConnection();
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return statement.getMoreResults(current);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return statement.getGeneratedKeys();
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return statement.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return statement.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return statement.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return statement.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return statement.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return statement.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return statement.getResultSetHoldability();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return statement.isClosed();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        statement.setPoolable(poolable);
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return statement.isPoolable();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        statement.closeOnCompletion();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return statement.isCloseOnCompletion();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return statement.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return statement.isWrapperFor(iface);
    }

    public List<String> getParamNames() {
        return paramNames;
    }

}