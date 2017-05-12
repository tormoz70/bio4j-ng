package ru.bio4j.ng.model.transport.jstore;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Сортировка
 */
@XStreamAlias("sort")
public class Sort {

    public enum Direction {
        ASC, DESC;

        public int getCode() {
            return ordinal();
        }
    }

    @XStreamAsAttribute
    private String fieldName;
    @XStreamAsAttribute
    private Direction direction;

    public String getFieldName() {
        return fieldName;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

}
