package net.sf.clirr.core.internal.asm;

import org.objectweb.asm.Opcodes;

import net.sf.clirr.core.spi.Scope;
import net.sf.clirr.core.spi.Scoped;

abstract class AbstractAsmScoped implements Scoped
{

    private final int access;

    AbstractAsmScoped(int access)
    {
        this.access = access;
    }
    
    public Scope getDeclaredScope()
    {
        if (checkFlag(Opcodes.ACC_PRIVATE))
        {
            return Scope.PRIVATE;
        }
        else if (checkFlag(Opcodes.ACC_PROTECTED))
        {
            return Scope.PROTECTED;
        }
        else if (checkFlag(Opcodes.ACC_PUBLIC))
        {
            return Scope.PUBLIC;
        }
        return Scope.PACKAGE;
    }

    /**
     * @return whether access field has mask set
     */
    protected boolean checkFlag(int mask)
    {
        return (access & mask) != 0;
    }
}
