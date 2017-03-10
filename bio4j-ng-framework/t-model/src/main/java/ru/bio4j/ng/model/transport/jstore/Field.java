package ru.bio4j.ng.model.transport.jstore;

//import flexjson.JSON;
import ru.bio4j.ng.model.transport.MetaType;

/**
 * Описание поля пакета данных
 */
public class Field {

    private int id;
    /**
     * Имя поля
     */
    private String name;

    /**
     * Формат отображения
     */
    private String format;

    /**
     * Выравнивание
     */
    private Alignment align;

    /**
     * Заголовок
     */
    private String title;

    /**
     * Отображать всплывающую подсказаку с полным текстом ячейки
     */
    private boolean showTooltip;

    /**
     * Не отображать
     */
    private boolean hidden;

    /**
     * Фильтрация
     */
    private boolean filter;

    /**
     * Только чтение
     */
    private boolean readonly;

    /**
     * Требуется заполнение пользоватлем
     */
    private boolean mandatory;

    /**
     * Первичный ключ
     */
    private boolean pk;

    /**
     * На клиенте не приводить значение null к типу колонки
     */
    private boolean useNull;

    /**
     * Ширина колонки
     */
    private String width;

    /**
     * Значение по умолчанию
     */
    private Object defaultVal;


    /**
     * Тип колонки
     */
    private MetaType metaType = MetaType.UNDEFINED;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Alignment getAlign() {
        return align;
    }

    public void setAlign(Alignment align) {
        this.align = align;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean value) {
        this.hidden = value;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(boolean value) {
        this.readonly = value;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk(boolean value) {
        this.pk = value;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public Object getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(Object defaultVal) {
        this.defaultVal = defaultVal;
    }

    public MetaType getMetaType() {
        return metaType;
    }

    public void setMetaType(MetaType value) {
        this.metaType = value;
    }

    /**
     * Field id starts from 1
     * @return
     */
    public int getId() { return id; }

    /**
     * Field index starts from 0
     * @return
     */
//    @JSON(include = false)
    public int getIndex() { return id-1; }

    public void setId(int id) { this.id = id; }

    public boolean isUseNull() {
        return useNull;
    }

    public void setUseNull(boolean useNull) {
        this.useNull = useNull;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }
}
