package net.sf.clirr.core;

import org.apache.bcel.classfile.JavaClass;

/**
 * Created by IntelliJ IDEA.
 * User: lk
 * Date: Mar 6, 2005
 * Time: 3:56:35 PM
 * To change this template use Options | File Templates.
 */
public interface ClassFilter
{
    boolean isSelected(JavaClass clazz);
}
