package ru.bio4j.ng.commons.utils;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Enumeration;

public class Bundles4WAR {
    private static final Logger LOG = LoggerFactory.getLogger(Bundles4WAR.class);

    public static <T> T findBundleByInterface(Class<T> serviceType) {
        try {
            BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
            return ServiceHelper.lookupService(bundleContext, serviceType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createLocalServiceByName(String serviceName) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<T> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true) {
                    String elem = classes.nextElement().toString();
                    //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                    final String csClassNameBgn = "WEB-INF/classes/";
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn) + csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    if (Strings.compare(className, serviceName, true)) {
                        Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                        if (currClazz != null) {
                            resultClazz = (Class<T>)currClazz;
                            break;
                        }
                        if (!classes.hasMoreElements()) break;
                    }
                }
            }
            if(LOG.isDebugEnabled()) {
                if (resultClazz != null)
                    LOG.debug(String.format(" --- Found Clazz: %s, in bundle:%s", resultClazz.getName(), bundleContext.getBundle().getSymbolicName()));
                LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", serviceName, bundleContext.getBundle().getSymbolicName()));
            }
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by name %s!", bundleContext.getBundle().getSymbolicName(), serviceName), e);
            resultClazz = null;
        }
        try {
            return resultClazz != null ? resultClazz.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T createServiceByName(String serviceName) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<T> resultClazz = null;
        try {
            resultClazz = (Class<T>)bundleContext.getBundle().loadClass(serviceName);
        } catch (ClassNotFoundException e) {
            LOG.error(String.format("Class not found by name %s!", serviceName), e);
            resultClazz = null;
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class by name %s!", serviceName), e);
            resultClazz = null;
        }
        try {
            return resultClazz != null ? resultClazz.newInstance() : null;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> findLocalServiceByAnnotation(Class<? extends Annotation> annotationType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<?> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true){
                    String elem = classes.nextElement().toString();
                    //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                    final String csClassNameBgn = "WEB-INF/classes/";
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn)+csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                    if(currClazz != null) {
                        boolean isAnnoted = currClazz.isAnnotationPresent(annotationType);
                        if(LOG.isDebugEnabled())LOG.debug(String.format(" --- found Clazz: %s, isAnnoted: %s, in bundle:%s", currClazz.getName(), isAnnoted, bundleContext.getBundle().getSymbolicName()));
                        if(isAnnoted && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        if(LOG.isDebugEnabled())LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            if(LOG.isDebugEnabled())LOG.debug(String.format("Found %s: %s", annotationType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by annotation %s!", bundleContext.getBundle().getSymbolicName(), annotationType), e);
            resultClazz = null;
        }
        return resultClazz;
    }
    public static Class<?> findLocalServiceByInterface(Class interfaceType) {
        BundleContext bundleContext = Utl.getBundleContext(ServletContextHolder.getServletContext());
        Class<?> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true){
                    String elem = classes.nextElement().toString();
                    //bundle://95.0:0/WEB-INF/classes/ru/fk/ekb/rapi/restful/models/FilmStat.class
                    final String csClassNameBgn = "WEB-INF/classes/";
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn)+csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                    if(currClazz != null) {
                        boolean isService = Utl.typeHasInterface(currClazz, interfaceType);
                        if(LOG.isDebugEnabled())LOG.debug(String.format(" --- found Clazz: %s, isServise: %s, in bundle:%s", currClazz.getName(), isService, bundleContext.getBundle().getSymbolicName()));
                        if(isService && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                    if(LOG.isDebugEnabled())LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            if(LOG.isDebugEnabled())LOG.debug(String.format("Found %s: %s", interfaceType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by interface %s!", bundleContext.getBundle().getSymbolicName(), interfaceType), e);
            resultClazz = null;
        }
        return resultClazz;
    }

    public static Class<?> findLocalServiceByInterface(BundleContext context, Class interfaceType) {
        BundleContext bundleContext = context;
        if(bundleContext == null) {
            LOG.error("BundleContext not defined!");
        }

        Class<?> resultClazz = null;
        try {
            Enumeration<?> classes = bundleContext.getBundle().findEntries("/", "*.class", true);
            if(classes.hasMoreElements()) {
                while (true){
                    Long bundleId = bundleContext.getBundle().getBundleId();
//                    String bundleVer = bundleContext.getBundle().getVersion().toString();
//                    String bundleSymbolicName = bundleContext.getBundle().getSymbolicName();
//                    String bundleLocation = bundleContext.getBundle().getLocation();
//                    LOG.debug(String.format("bundleId: %d, bundleVer: %s, bundleSymbolicName: %s, bundleLocation: %s", bundleId, bundleVer, bundleSymbolicName, bundleLocation));
                    String elem = classes.nextElement().toString();
                    //bundle://96.0:0/ru/fk/ekb/security/module/impl/CurUserProvider.class
                    final String csClassNameBgn = String.format("bundle://%d.0:0/", bundleContext.getBundle().getBundleId());
                    final String csClassNameEnd = ".class";
                    String className = elem.substring(elem.indexOf(csClassNameBgn)+csClassNameBgn.length(), elem.indexOf(csClassNameEnd)).replaceAll("/", ".");
                    Class<?> currClazz = bundleContext.getBundle().loadClass(className);
                    if(currClazz != null) {
                        boolean isService = Utl.typeHasInterface(currClazz, interfaceType);
                        if(LOG.isDebugEnabled())LOG.debug(String.format(" --- found Clazz: %s, isServise: %s, in bundle:%s", currClazz.getName(), isService, bundleContext.getBundle().getSymbolicName()));
                        if(isService && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                    if(LOG.isDebugEnabled())LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            if(LOG.isDebugEnabled())LOG.debug(String.format("Found %s: %s", interfaceType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by interface %s!", bundleContext.getBundle().getSymbolicName(), interfaceType), e);
            resultClazz = null;
        }
        return resultClazz;
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
