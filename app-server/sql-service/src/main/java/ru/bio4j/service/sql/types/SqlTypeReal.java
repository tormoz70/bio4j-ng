package ru.bio4j.service.sql.types;

public class SqlTypeReal implements SqlType {
    private String typeName;
    private int sqlType;
    private int size;
    private String literalPrefix;
    private String literalSuffix;
    private String createParams;
    private boolean caseSensitive;
    private boolean unsigned;
    private boolean fixedPrecisionScale;
    private boolean autoIncrement;
    private String localTypeName;
    private int minimumScale;
    private int maximumScale;
    private int numberPrecisionRadix;
    private Boolean nullable;

    /**
     * can it be used for an auto-increment value.
     * @title Проверка автоинкремента значения
     * @return true, если автоинкремент значения доступен
     */
    @Override
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * can it be used for an auto-increment value.
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * is it case sensitive.
     */
    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * is it case sensitive.
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * parameters used in creating the type (may be null)
     */
    @Override
    public String getCreateParams() {
        return createParams;
    }

    /**
     * parameters used in creating the type (may be null)
     */
    public void setCreateParams(String createParams) {
        this.createParams = createParams;
    }

    /**
     * can it be a money value.
     */
    @Override
    public boolean isFixedPrecisionScale() {
        return fixedPrecisionScale;
    }

    /**
     * can it be a money value.
     */
    public void setFixedPrecisionScale(boolean fixedPrecisionScale) {
        this.fixedPrecisionScale = fixedPrecisionScale;
    }

    /**
     * prefix used to quote a literal (may be null)
     */
    @Override
    public String getLiteralPrefix() {
        return literalPrefix;
    }

    /**
     * prefix used to quote a literal (may be null)
     */
    public void setLiteralPrefix(String literalPrefix) {
        this.literalPrefix = literalPrefix;
    }

    /**
     * suffix used to quote a literal (may be null)
     */
    @Override
    public String getLiteralSuffix() {
        return literalSuffix;
    }

    /**
     * suffix used to quote a literal (may be null)
     */
    public void setLiteralSuffix(String literalSuffix) {
        this.literalSuffix = literalSuffix;
    }

    /**
     * localized version of type name (may be null)
     */
    @Override
    public String getLocalTypeName() {
        return localTypeName;
    }

    /**
     * localized version of type name (may be null)
     */
    public void setLocalTypeName(String localTypeName) {
        this.localTypeName = localTypeName;
    }

    /**
     * maximum scale supported
     */
    @Override
    public int getMaximumScale() {
        return maximumScale;
    }

    /**
     * maximum scale supported
     */
    public void setMaximumScale(int maximumScale) {
        this.maximumScale = maximumScale;
    }

    /**
     * minimum scale supported
     */
    @Override
    public int getMinimumScale() {
        return minimumScale;
    }

    /**
     * minimum scale supported
     */
    public void setMinimumScale(int minimumScale) {
        this.minimumScale = minimumScale;
    }

    /**
     * usually 2 or 10
     */
    @Override
    public int getNumberPrecisionRadix() {
        return numberPrecisionRadix;
    }

    /**
     * usually 2 or 10
     */
    public void setNumberPrecisionRadix(int numberPrecisionRadix) {
        this.numberPrecisionRadix = numberPrecisionRadix;
    }

    /**
     * maximum precision
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * maximum precision
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * SQL data type from java.sql.Types
     */
    @Override
    public int getSQLType() {
        return sqlType;
    }

    /**
     * SQL data type from java.sql.Types
     */
    public void setSQLType(int sqlDataType) {
        this.sqlType = sqlDataType;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * is it unsigned.
     */
    @Override
    public boolean isUnsigned() {
        return unsigned;
    }

    /**
     * is it unsigned.
     */
    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }
}
