package net.sf.clirr.core.internal.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import net.sf.clirr.core.spi.Field;
import net.sf.clirr.core.spi.JavaType;
import net.sf.clirr.core.spi.Scope;

class AsmField extends AbstractAsmScoped implements Field
{
    private final String name;
    private final Object value;
    private final Type type;
    private final Repository repository;
    private final AsmJavaType container;

    AsmField(AsmJavaType container, int access, String name, Object value, Type type)
    {
        super(access);
        this.container = container;
        this.repository = container.getRepository();
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public JavaType getType()
    {
        // todo: handle primitive values and arrays
        return repository.findTypeByName(type.getClassName());
    }

    public boolean isFinal()
    {
        return checkFlag(Opcodes.ACC_FINAL);
    }

    public boolean isStatic()
    {
        return checkFlag(Opcodes.ACC_STATIC);
    }

    public boolean isDeprecated()
    {
        return checkFlag(Opcodes.ACC_DEPRECATED);
    }

    public Object getConstantValue()
    {
        return value;
    }

    public String getName()
    {
        return name;
    }

    public Scope getEffectiveScope()
    {
        final Scope containerScope = container.getEffectiveScope();
        final Scope declaredScope = getDeclaredScope();
        return containerScope.isLessVisibleThan(declaredScope) ? containerScope : declaredScope;
    }

}
