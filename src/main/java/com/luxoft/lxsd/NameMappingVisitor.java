package com.luxoft.lxsd;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author skondurushkin
 */
public class NameMappingVisitor implements XSDSchema.XsdVisitor {
    
    @Override
    public void visit(XSDSchema.XsdNode n) {
    }

    @Override
    public void visit(XSDSchema.XsdField f) {
        String mapped =  f.getSchema().findMappedName(f);
        if (StringUtils.isNotBlank(mapped))
            f.setMappedName(mapped);
    }

    @Override
    public void visit(XSDSchema.XsdType t) {
        String mapped =  t.getSchema().findMappedName(t);
        if (StringUtils.isNotBlank(mapped))
            t.setMappedName(mapped);
        for(XSDSchema.XsdType inner : t.getInnerTypes()) {
            inner.accept(this);
        }
        for(XSDSchema.XsdField field : t.getFields()) {
            field.accept(this);
        }
    }
}
