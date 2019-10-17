package ru.bio4j.ng.commons.utils;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.commons.types.DelegateAction1;

import java.lang.annotation.Annotation;
import java.util.Enumeration;

public class Bundles4WAR {
    private static final Logger LOG = LoggerFactory.getLogger(Bundles4WAR.class);

    public static <T> T findBundleByInterface(Class<T> serviceType) {
        try {
            BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
            return ServiceHelper.lookupService(bundleContext, serviceType);
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static <T> Class<T> scanLocalClasses(final BundleContext bundleContext, final DelegateAction1<Class<?>, Boolean> callback) {
        Class<T> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            while (classes.hasMoreElements()) {
                String elem = classes.nextElement().toString();
                //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                final String csClassNameBgn = "WEB-INF/classes/";
                final String csClassNameEnd = ".class";
                String className = elem.substring(elem.indexOf(csClassNameBgn) + csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                Class<?> curClazz = (Class<T>)bundleContext.getBundle().loadClass(className);
                if (callback.callback(curClazz)) {
                    resultClazz = (Class<T>)curClazz;
                    break;
                }
            }
            return resultClazz;
        } catch (Exception e) {
            throw Utl.wrapErrorAsRuntimeException(String.format("Unexpected error while scan classes in bundle %s!", bundleContext.getBundle().getSymbolicName()), e);
        }
    }

    public static <T> T createLocalServiceByName(final String serviceName) {
        final BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<T> resultClazz = scanLocalClasses(bundleContext, (Class<?> clazz) -> {
            return Strings.compare(clazz.getName(), serviceName, true);
        });
        if (LOG.isDebugEnabled()) {
            if (resultClazz != null)
                LOG.debug(String.format(" --- Found Clazz: %s, in bundle:%s", resultClazz.getName(), bundleContext.getBundle().getSymbolicName()));
            else
                LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", serviceName, bundleContext.getBundle().getSymbolicName()));
        }
        try {
            return resultClazz != null ? resultClazz.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static <T> Class<T> findServiceTypeByName(String serviceTypeName) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<T> resultClazz = null;
        try {
            resultClazz = (Class<T>) bundleContext.getBundle().loadClass(serviceTypeName);
        } catch (ClassNotFoundException e) {
            if (LOG.isDebugEnabled()) LOG.debug(String.format("Class not found by name %s!", serviceTypeName), e);
            resultClazz = null;
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class by name %s!", serviceTypeName), e);
            resultClazz = null;
        }
        return resultClazz;
    }

    public static <T> T findServiceByName(String serviceTypeName) {
        Class<T> resultClazz = findServiceTypeByName(serviceTypeName);
        try {
            return resultClazz != null ? resultClazz.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw Utl.wrapErrorAsRuntimeException(e);
        }
    }

    public static <T> Class<T> findLocalServiceByAnnotation(Class<? extends Annotation> annotationType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        return scanLocalClasses(bundleContext, (Class<?> clazz) -> {
            return clazz.isAnnotationPresent(annotationType);
        });
    }

    public static Class<?> findLocalServiceByInterface(BundleContext bundleContext, Class interfaceType) {
        return scanLocalClasses(bundleContext, (Class<?> clazz) -> {
            return Utl.typeHasInterface(clazz, interfaceType);
        });

    }
    public static Class<?> findLocalServiceByInterface(Class interfaceType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        return findLocalServiceByInterface(bundleContext, interfaceType);

    }

    public static <T> T createLocalServiceImpl(Class<T> interfaceType, Class<?> defaultImpl) {
        T result = null;
        Class<?> implClass = findLocalServiceByInterface(interfaceType);
        if (implClass == null)
            implClass = defaultImpl;
        try {
            result = (T)implClass.newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while creating %s: %s", interfaceType.getName(), implClass.getName()));
        }
        if(LOG.isDebugEnabled())LOG.debug(String.format("Found implementation for %s: %s", interfaceType.getName(), implClass.getName()));
        return result;
    }

    public static <T> T createLocalServiceImpl(BundleContext context, Class<T> interfaceType, Class<?> defaultImpl) {
        T result = null;
        Class<?> implClass = findLocalServiceByInterface(context, interfaceType);
        if (implClass == null)
            implClass = defaultImpl;
        try {
            result = (T)implClass.newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while creating %s: %s", interfaceType.getName(), implClass.getName()));
        }
        if(LOG.isDebugEnabled())LOG.debug(String.format("Found implementation for %s: %s", interfaceType.getName(), implClass.getName()));
        return result;
    }


}
