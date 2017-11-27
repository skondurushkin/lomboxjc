package com.luxoft.lxsd;

import com.luxoft.lxsd.XSDSchema.XsdNode;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
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
