package com.practise.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

/**
 * 反射工具类
 */
@Slf4j
public class ReflectionUtils {

    /**
     * 获取类的所有字段（包括私有字段）
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取类的所有方法（包括私有方法）
     */
    public static Method[] getAllMethods(Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            methods.addAll(Arrays.asList(currentClass.getDeclaredMethods()));
            currentClass = currentClass.getSuperclass();
        }
        
        return methods.toArray(new Method[0]);
    }

    /**
     * 获取字段值
     */
    public static Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                return field.get(obj);
            }
        } catch (Exception e) {
            log.error("获取字段值失败: {}", fieldName, e);
        }
        return null;
    }

    /**
     * 设置字段值
     */
    public static boolean setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = getField(obj.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
                return true;
            }
        } catch (Exception e) {
            log.error("设置字段值失败: {}", fieldName, e);
        }
        return false;
    }

    /**
     * 获取字段（包括父类字段）
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取方法（包括父类方法）
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            try {
                return currentClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object obj, String methodName, Object... args) {
        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            
            Method method = getMethod(obj.getClass(), methodName, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(obj, args);
            }
        } catch (Exception e) {
            log.error("调用方法失败: {}", methodName, e);
        }
        return null;
    }

    /**
     * 创建实例
     */
    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            log.error("创建实例失败: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 创建实例（带参数）
     */
    public static <T> T createInstance(Class<T> clazz, Object... args) {
        try {
            Class<?>[] parameterTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                parameterTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
            }
            
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (Exception e) {
            log.error("创建实例失败: {}", clazz.getName(), e);
            return null;
        }
    }

    /**
     * 获取类的所有构造函数
     */
    public static Constructor<?>[] getAllConstructors(Class<?> clazz) {
        return clazz.getDeclaredConstructors();
    }

    /**
     * 判断类是否有指定字段
     */
    public static boolean hasField(Class<?> clazz, String fieldName) {
        return getField(clazz, fieldName) != null;
    }

    /**
     * 判断类是否有指定方法
     */
    public static boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return getMethod(clazz, methodName, parameterTypes) != null;
    }

    /**
     * 获取类的所有注解
     */
    public static Annotation[] getAllAnnotations(Class<?> clazz) {
        List<Annotation> annotations = new ArrayList<>();
        Class<?> currentClass = clazz;
        
        while (currentClass != null && currentClass != Object.class) {
            annotations.addAll(Arrays.asList(currentClass.getDeclaredAnnotations()));
            currentClass = currentClass.getSuperclass();
        }
        
        return annotations.toArray(new Annotation[0]);
    }

    /**
     * 获取字段的所有注解
     */
    public static Annotation[] getFieldAnnotations(Class<?> clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        return field != null ? field.getDeclaredAnnotations() : new Annotation[0];
    }

    /**
     * 获取方法的所有注解
     */
    public static Annotation[] getMethodAnnotations(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = getMethod(clazz, methodName, parameterTypes);
        return method != null ? method.getDeclaredAnnotations() : new Annotation[0];
    }

    /**
     * 判断类是否有指定注解
     */
    public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return clazz.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断字段是否有指定注解
     */
    public static boolean hasFieldAnnotation(Class<?> clazz, String fieldName, Class<? extends Annotation> annotationClass) {
        Field field = getField(clazz, fieldName);
        return field != null && field.isAnnotationPresent(annotationClass);
    }

    /**
     * 判断方法是否有指定注解
     */
    public static boolean hasMethodAnnotation(Class<?> clazz, String methodName, Class<? extends Annotation> annotationClass, Class<?>... parameterTypes) {
        Method method = getMethod(clazz, methodName, parameterTypes);
        return method != null && method.isAnnotationPresent(annotationClass);
    }

    /**
     * 获取类的泛型类型
     */
    public static Type[] getGenericTypes(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            return ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        }
        return new Type[0];
    }

    /**
     * 获取字段的泛型类型
     */
    public static Type[] getFieldGenericTypes(Class<?> clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (field != null) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                return ((ParameterizedType) genericType).getActualTypeArguments();
            }
        }
        return new Type[0];
    }

    /**
     * 获取方法的泛型类型
     */
    public static Type[] getMethodGenericTypes(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        Method method = getMethod(clazz, methodName, parameterTypes);
        if (method != null) {
            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {
                return ((ParameterizedType) genericReturnType).getActualTypeArguments();
            }
        }
        return new Type[0];
    }

    /**
     * 获取类的包名
     */
    public static String getPackageName(Class<?> clazz) {
        Package pkg = clazz.getPackage();
        return pkg != null ? pkg.getName() : "";
    }

    /**
     * 获取类的简单名称
     */
    public static String getSimpleName(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    /**
     * 获取类的完整名称
     */
    public static String getFullName(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * 判断是否为基本类型
     */
    public static boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive();
    }

    /**
     * 判断是否为包装类型
     */
    public static boolean isWrapper(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Character.class || 
               clazz == Byte.class || clazz == Short.class || 
               clazz == Integer.class || clazz == Long.class || 
               clazz == Float.class || clazz == Double.class;
    }

    /**
     * 获取基本类型对应的包装类型
     */
    public static Class<?> getWrapperClass(Class<?> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            return primitiveClass;
        }
        
        if (primitiveClass == boolean.class) return Boolean.class;
        if (primitiveClass == char.class) return Character.class;
        if (primitiveClass == byte.class) return Byte.class;
        if (primitiveClass == short.class) return Short.class;
        if (primitiveClass == int.class) return Integer.class;
        if (primitiveClass == long.class) return Long.class;
        if (primitiveClass == float.class) return Float.class;
        if (primitiveClass == double.class) return Double.class;
        
        return primitiveClass;
    }

    /**
     * 获取包装类型对应的基本类型
     */
    public static Class<?> getPrimitiveClass(Class<?> wrapperClass) {
        if (wrapperClass.isPrimitive()) {
            return wrapperClass;
        }
        
        if (wrapperClass == Boolean.class) return boolean.class;
        if (wrapperClass == Character.class) return char.class;
        if (wrapperClass == Byte.class) return byte.class;
        if (wrapperClass == Short.class) return short.class;
        if (wrapperClass == Integer.class) return int.class;
        if (wrapperClass == Long.class) return long.class;
        if (wrapperClass == Float.class) return float.class;
        if (wrapperClass == Double.class) return double.class;
        
        return wrapperClass;
    }

    /**
     * 将对象转换为Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj == null) {
            return map;
        }
        
        Field[] fields = getAllFields(obj.getClass());
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            } catch (Exception e) {
                log.error("转换字段失败: {}", field.getName(), e);
            }
        }
        
        return map;
    }

    /**
     * 将Map转换为对象
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        
        T obj = createInstance(clazz);
        if (obj == null) {
            return null;
        }
        
        Field[] fields = getAllFields(clazz);
        for (Field field : fields) {
            Object value = map.get(field.getName());
            if (value != null) {
                setFieldValue(obj, field.getName(), value);
            }
        }
        
        return obj;
    }
}