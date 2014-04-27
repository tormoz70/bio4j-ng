package ru.bio4j.ng.model.transport.jstore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Сортировка
 */
public class Sort implements Iterable<Sort.Field> {

    private List<Field> fields = new ArrayList<>();

    public enum Direction {
        ASC, DESC;

        public int getCode() {
            return ordinal();
        }
    }

    public static class Field {
        private String fieldName;
        private Direction direction;

        public Field() {

        }

        public Field(String fieldName,
                     Direction direction) {
            this.fieldName = fieldName;
            this.direction = direction;
        }

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

    @Override
    public Iterator<Sort.Field> iterator() {
        return this.fields.iterator();
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public Sort add(String fieldName, Direction direction) {
        this.fields.add(new Field(fieldName, direction));
        return this;
    }
}
