package ru.bio4j.service;

public abstract class AbstractDependencyService {

	protected abstract void start() throws Exception;

	protected abstract void stop() throws Exception;
}
