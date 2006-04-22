package net.sf.clirr.core.internal.asm;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Method;
import net.sf.clirr.core.spi.Scope;

import org.objectweb.asm.ClassReader;

/**
 * Stores all known JavaTypes, used to implement crossreferences between types.
 *  
 * @author lkuehne
 */
class Repository
{
    private static final Pattern PRIMITIVE_PATTERN = Pattern.compile("(int|float|long|double|boolean|char|short|byte)");
    private static final Pattern ARRAY_PATTERN = Pattern.compile("(\\[\\])+$");

    private static final class PrimitiveType implements JavaType
    {
        private final String basicName;

        private PrimitiveType(String name)
        {
            this.basicName = name;
        }
        
        public String getBasicName()
        {
            return basicName;
        }

        public String getName()
        {
            return basicName;
        }

        public JavaType getContainingClass()
        {
            return null;
        }

        public JavaType[] getSuperClasses()
        {
            return new JavaType[0];
        }

        public JavaType[] getAllInterfaces()
        {
            return new JavaType[0];
        }

        public JavaType[] getInnerClasses()
        {
            return new JavaType[0];
        }

        public Method[] getMethods()
        {
            return new Method[0];
        }

        public Field[] getFields()
        {
            return new Field[0];
        }

        public int getArrayDimension()
        {
            return 0;
        }

        public boolean isPrimitive()
        {
            return true;
        }

        public boolean isFinal()
        {
            return true;
        }

        public boolean isAbstract()
        {
            return false;
        }

        public boolean isInterface()
        {
            return false;
        }

        public Scope getDeclaredScope()
        {
            return Scope.PUBLIC;
        }

        public Scope getEffectiveScope()
        {
            // TODO Auto-generated method stub
            return null;
        }
        
        public String toString()
        {
            return getName();
        }
    }

    private final ClassLoader classLoader;
    private Map nameTypeMap = new HashMap();

    public Repository(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * @param is
     * @return
     * @throws IOException
     */
    AsmJavaType readJavaTypeFromStream(InputStream is) throws IOException
    {
        ClassReader parser = new ClassReader(is);
        
        ClassInfoCollector infoCollector = new ClassInfoCollector(this);
        
        parser.accept(infoCollector, true);
        
        final AsmJavaType javaType = infoCollector.getJavaType();
        
        nameTypeMap.put(javaType.getName(), javaType);
        return javaType;
    }

    public JavaType findTypeByName(String fullTypeName)
    {
        // separate basic typename and array brackets
        final Matcher arrayMatcher = ARRAY_PATTERN.matcher(fullTypeName);
        final String typeName;
        final int dimension;
        if (arrayMatcher.find())
        {
            String brackets = arrayMatcher.group();
            typeName = fullTypeName.substring(0, fullTypeName.length() - brackets.length());
            dimension = brackets.length() / 2;
        }
        else
        {
            typeName = fullTypeName;
            dimension = 0;
        }
        
        // search cache for basic typename
        JavaType type = (JavaType) nameTypeMap.get(typeName);
        if (type != null)
        {
            return wrapInArrayTypeIfRequired(dimension, type);
        }
        
        // OK, typename is not in the cache. Is it a primitive type?
        final Matcher primitiveMatcher = PRIMITIVE_PATTERN.matcher(typeName);
        if (primitiveMatcher.matches())
        {
            JavaType primitive = new PrimitiveType(typeName);
            nameTypeMap.put(typeName, primitive);
            return wrapInArrayTypeIfRequired(dimension, primitive); 
        }
        
        // it must be a normal class then, load it as a resource
        String resourceName = typeName.replace('.', '/') + ".class";
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null)
        {
            reportTypeUnknownInClassLoader(typeName);
        }
        try
        {
            final AsmJavaType javaType = readJavaTypeFromStream(is);
            return wrapInArrayTypeIfRequired(dimension, javaType);
        }
        catch (IOException ex)
        {
            throw new IllegalStateException();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * @param dimension
     * @param javaType
     * @return
     */
    private JavaType wrapInArrayTypeIfRequired(final int dimension, final JavaType javaType)
    {
        if (dimension == 0)
        {
            return javaType;
        }
        final ArrayType arrayType = new ArrayType(javaType, dimension);
        return arrayType;
    }

    /**
     * @param typeName
     */
    private void reportTypeUnknownInClassLoader(final String typeName)
    {
        String clDetails;
        if (classLoader instanceof URLClassLoader)
        {
            URLClassLoader ucl = (URLClassLoader) classLoader;
            clDetails = String.valueOf(Arrays.asList(ucl.getURLs()));
        }
        else
        {
            clDetails = String.valueOf(classLoader);
        }
        throw new IllegalArgumentException("Type " + typeName + " is unknown in classLoader " + clDetails);
    }

}
