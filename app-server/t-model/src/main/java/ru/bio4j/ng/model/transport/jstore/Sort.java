package ru.bio4j.ng.model.transport.jstore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Сортировка
 */
public class Sort {

    public enum Direction {
        ASC, DESC;

        public int getCode() {
            return ordinal();
        }
    }

    private String fieldName;
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
