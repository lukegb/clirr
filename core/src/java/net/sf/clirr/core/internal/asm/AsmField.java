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

    AsmField(Repository repository, int access, String name, Object value, Type type)
    {
        super(access);
        this.repository = repository;
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
        return getDeclaredScope(); // TODO: FIXME
    }

}
