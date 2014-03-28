package ru.bio4j.service.sql.query.parser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * @author rad
 * @title Разобранный запрос
 */
public class ParsedQueryImpl implements ParsedQuery {

    /**
     * @title Строитель разобранного запроса
     */
    public static class Builder {
        private final List<ParsedParameter> parsedParameters = new ArrayList<>();
        private String query;

        /**
         * @param query
         * @return Экземпляр строителя разобранного запроса
         * @title Установка строки запроса и получение строителя разобранного запроса
         */
        public Builder query(String query) {
            this.query = query;
            return this;
        }

        /**
         * @param parameter
         * @return Экземпляр строителя разобранного запроса
         * @title Добавление параметра и получение строителя разобранного запроса
         */
        public Builder addParameter(ParsedParameter parameter) {
            parsedParameters.add(parameter);
            return this;
        }

        /**
         * @return Количество параметров
         * @title Получение количества параметров
         */
        public int getParametersCount() {
            return parsedParameters.size();
        }

        /**
         * @return Экземпляр разобранного запроса
         * @title Построение разобранного запроса
         */
        public ParsedQueryImpl build() {
            return new ParsedQueryImpl(unmodifiableList(parsedParameters), query);
        }
    }

    private final List<ParsedParameter> parsedParameters;
    private final String query;

    /**
     * @return Строитель разобранного запроса
     * @title Получение строителя разобранного запроса
     */
    public static Builder builder() {
        return new Builder();
    }

    private ParsedQueryImpl(List<ParsedParameter> parsedParameters, String query) {
        this.parsedParameters = parsedParameters;
        this.query = query;
    }

    /**
     * @return Строка запроса
     * @title Получение строки запроса
     */
    @Override
    public String getQuery() {
        return query;
    }

    /**
     * @return Список параметров
     * @title Получение списка параметров
     */
    @Override
    public List<ParsedParameter> getParameters() {
        return parsedParameters;
    }

    @Override
    public String toString() {
        return "ParsedQueryImpl{" +
                "parsedParameters=" + parsedParameters +
                ", query='" + query + '\'' +
                '}';
    }
}
