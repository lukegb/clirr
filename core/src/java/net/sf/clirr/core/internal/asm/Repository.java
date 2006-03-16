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
    private static final Pattern PRIMITIVE_PATTERN = Pattern.compile("(int|float|long|double|boolean|char|short)(\\[\\])*");

    private static final class PrimitiveType implements JavaType
    {
        private final String basicName;
        private final int dimension;

        private PrimitiveType(String name, int dimension)
        {
            this.basicName = name;
            this.dimension = dimension;
        }
        
        public String getBasicName()
        {
            return basicName;
        }

        public String getName()
        {
            String name = basicName;
            for (int i = 0; i < getArrayDimension(); i++)
            {
                name += "[]";
            }
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
            return dimension;
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

    public JavaType findTypeByName(String typeName)
    {
        JavaType type = (JavaType) nameTypeMap.get(typeName);
        if (type != null)
        {
            return type;
        }
        final Matcher matcher = PRIMITIVE_PATTERN.matcher(typeName);
        if (matcher.matches())
        {
            final String basicType = matcher.group(1);
            final String arrayBrackets = matcher.group(2);
            final int dimension = arrayBrackets == null ? 0 : arrayBrackets.length() / 2;
            JavaType primitive = new PrimitiveType(basicType, dimension);
            nameTypeMap.put(typeName, primitive);
            return primitive; 
        }
        String resourceName = typeName.replace('.', '/') + ".class";
        InputStream is = classLoader.getResourceAsStream(resourceName);
        if (is == null)
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
        try
        {
            return readJavaTypeFromStream(is);
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

}
