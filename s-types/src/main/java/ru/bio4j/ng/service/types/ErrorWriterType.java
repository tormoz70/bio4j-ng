package ru.bio4j.ng.service.types;

import ru.bio4j.ng.service.api.ErrorWriter;

import javax.servlet.http.HttpServletResponse;

public enum ErrorWriterType {
    Json {
        @Override
        public ErrorWriter createImpl() {
            return new ErrorWriterJsonImpl();
        }
    },
    Std {
        @Override
        public ErrorWriter createImpl() {
            return new ErrorWriterStdImpl();
        }
    },
    Skip {
        @Override
        public ErrorWriter createImpl() {
            return new ErrorWriterSkipImpl();
        }
    };
    public abstract ErrorWriter createImpl();
}