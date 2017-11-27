package com.luxoft.lxsd;

import com.luxoft.lxsd.XSDSchema.XsdField;
import com.luxoft.lxsd.XSDSchema.XsdNode;
import com.luxoft.lxsd.XSDSchema.XsdType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 * Генерирует файл маппинга идентификаторов
 * Формат файла - обычный .properties
 * 
 * @author skondurushkin
 */
public class ExtractionVisitor implements XSDSchema.XsdVisitor {

    OutputStreamWriter osWriter;
    
    public ExtractionVisitor(OutputStreamWriter osWriter) throws IOException {
        Objects.requireNonNull(osWriter);
        this.osWriter = osWriter;
    }
    
    void writeln(String text) throws IOException {
        this.osWriter.append(text).append('\n');
    }
    void writeOriginalName(String name) throws IOException {
        this.osWriter.append("# XSD Name: ").append(name).append('\n');
    }
    void writeProperty(String name) throws IOException {
        this.osWriter.append(name).append('=').append('\n');
    }
    void writeProperty(String name, String value) throws IOException {
        this.osWriter.append(name).append('=').append(value).append('\n');
    }
    void writeDocumentation(List<String> docs) throws IOException {
        boolean first = true;
        for(String docString : docs) {
            docString = docString.trim();
            if (StringUtils.isNotBlank(docString)) {
                String parts[] = StringUtils.split(docString, "\n");
                for(String part : parts ) {
                    part = part.trim();
                    if (StringUtils.isNotBlank(part)) {
                        if (first) {
                           this.osWriter.append("# DOC: ");
                        } else {
                           this.osWriter.append("#      ");
                        }
                        this.osWriter.append(part).append('\n');
                        first = false;
                    }
                }
            }
        }
    }
    
    @Override
    public void visit(XsdField f) {
        // System.out.println("visit field " + f.getFullName());
        try {
            writeln("# FIELD");
            writeOriginalName(f.getOrigName());
            writeDocumentation(f.getDocumentation());
            writeProperty(f.getFullName(), f.getMappedName());
            writeln("");
            this.osWriter.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void visit(XsdType t) {
        // System.out.println("visit type " + t.getFullName());
        try {
            writeln("# TYPE");
            writeOriginalName(t.getOrigName());
            writeDocumentation(t.getDocumentation());
            writeProperty(t.getFullName(), t.getMappedName());
            writeln("");
            this.osWriter.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        for(XsdType inner : t.getInnerTypes()) {
            inner.accept(this);
        }
        for(XsdField field : t.getFields()) {
            field.accept(this);
        }
    }

    @Override
    public void visit(XsdNode n) {
        
    }

}
