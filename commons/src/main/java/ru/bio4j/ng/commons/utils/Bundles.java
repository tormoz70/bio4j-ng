package ru.bio4j.ng.commons.utils;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Enumeration;

public class Bundles {
    private static final Logger LOG = LoggerFactory.getLogger(Bundles.class);

    public static Class<?> findServiceInWarBundleByAnnotation(Class<? extends Annotation> annotationType) {
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
                        LOG.debug(String.format(" --- found Clazz: %s, isAnnoted: %s, in bundle:%s", currClazz.getName(), isAnnoted, bundleContext.getBundle().getSymbolicName()));
                        if(isAnnoted && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            LOG.debug(String.format("Found %s: %s", annotationType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by annotation %s!", bundleContext.getBundle().getSymbolicName(), annotationType), e);
            resultClazz = null;
        }
        return resultClazz;
    }
    public static Class<?> findServiceInWarBundleByInterface(Class interfaceType) {
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
                        LOG.debug(String.format(" --- found Clazz: %s, isServise: %s, in bundle:%s", currClazz.getName(), isService, bundleContext.getBundle().getSymbolicName()));
                        if(isService && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            LOG.debug(String.format("Found %s: %s", interfaceType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by interface %s!", bundleContext.getBundle().getSymbolicName(), interfaceType), e);
            resultClazz = null;
        }
        return resultClazz;
    }

    public static Class<?> findServiceInBundleByInterface(BundleContext context, Class interfaceType) {
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
                        LOG.debug(String.format(" --- found Clazz: %s, isServise: %s, in bundle:%s", currClazz.getName(), isService, bundleContext.getBundle().getSymbolicName()));
                        if(isService && resultClazz == null)
                            resultClazz = currClazz;
                    } else
                        LOG.debug(String.format(" --- Not found Clazz: %s, in bundle:%s", className, bundleContext.getBundle().getSymbolicName()));
                    if(!classes.hasMoreElements()) break;
                }
            }
            LOG.debug(String.format("Found %s: %s", interfaceType, resultClazz));
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while finding class in bundle %s by interface %s!", bundleContext.getBundle().getSymbolicName(), interfaceType), e);
            resultClazz = null;
        }
        return resultClazz;
    }

    public static <T> T createServiceImplInWAR(Class<T> interfaceType, Class<?> defaultImpl) {
        T result = null;
        Class<?> implClass = findServiceInWarBundleByInterface(interfaceType);
        if (implClass == null)
            implClass = defaultImpl;
        try {
            result = (T)implClass.newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while creating %s: %s", interfaceType.getName(), implClass.getName()));
        }
        LOG.debug(String.format("Found implementation for %s: %s", interfaceType.getName(), implClass.getName()));
        return result;
    }

    public static <T> T createServiceImplInBundle(BundleContext context, Class<T> interfaceType, Class<?> defaultImpl) {
        T result = null;
        Class<?> implClass = findServiceInBundleByInterface(context, interfaceType);
        if (implClass == null)
            implClass = defaultImpl;
        try {
            result = (T)implClass.newInstance();
        } catch (Exception e) {
            LOG.error(String.format("Unexpected error while creating %s: %s", interfaceType.getName(), implClass.getName()));
        }
        LOG.debug(String.format("Found implementation for %s: %s", interfaceType.getName(), implClass.getName()));
        return result;
    }
}
