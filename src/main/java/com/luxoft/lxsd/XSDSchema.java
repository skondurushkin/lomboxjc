package com.luxoft.lxsd;

import com.sun.xml.bind.api.JAXBRIContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAll;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAnnotationItem;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaAttributeOrGroupRef;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaChoiceMember;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSequenceMember;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.utils.XmlSchemaNamed;
import org.apache.ws.commons.schema.utils.XmlSchemaObjectBase;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author skondurushkin
 */
public class XSDSchema {

    final Path    schemaPath;
    
    XmlSchema schema;
    List<XsdType> roots = new ArrayList<>();

    Properties mapping = new Properties();
    
    // Node registry
    Map<String, XsdNode>    mapNodes = new HashMap<>();
    
    public XSDSchema(Path schemaPath) {
        Objects.requireNonNull(schemaPath);
        this.schemaPath = schemaPath;
    }

    public Properties getMapping() {
        return mapping;
    }
    public Map<String, XsdNode> getRegistry() {
        return Collections.unmodifiableMap(this.mapNodes);
    }
    void register(XsdNode n) {
        mapNodes.putIfAbsent(n.getFullName(), n);
    }
    public XmlSchema getSchema() {
        if (schema == null) {
            try(InputStream is = new FileInputStream(schemaPath.toFile())) {
                XmlSchemaCollection schemaCol = new XmlSchemaCollection();
                this.schema = schemaCol.read(new StreamSource(is));
                
            } catch (IOException ex) {
                throw new RuntimeException (ex);
            }
        }
        return schema;
    }
    
    public XSDSchema withMapping(File mappingFile) throws IOException {
        if (mappingFile.exists() && mappingFile.isFile() && mappingFile.canRead()) {
            try (InputStreamReader isr = new InputStreamReader(
                    new FileInputStream(mappingFile),
                    Charset.forName("utf-8").newDecoder()
            )) {
                mapping.load(isr);
            }
        }
        return this;
    }
    
    public String findMappedName(XsdNode n) {
        return mapping.getProperty(n.getFullName(), n.getMappedName());
    }
    
    public XSDSchema load() {
        List<XmlSchemaObject> topLevelElements = this.getSchema().getItems();
        for (XmlSchemaObject element : topLevelElements) {
            if (element instanceof XmlSchemaComplexType
                    || element instanceof XmlSchemaElement) {
                this.roots.add(new XsdType((XmlSchemaObject) element, null));
            }
        }
        return accept(new NameMappingVisitor());
    }

    public XSDSchema accept(XsdVisitor v) {
        for (XsdType type : roots) {
            type.accept(v);
        }
        return this;
    }

    public interface XsdVisitor {

        void visit(XsdNode n);

        void visit(XsdField f);

        void visit(XsdType t);
    }

    public class XsdNode {

        XsdNode parent;
        final XmlSchemaObject node;

        // Original name from xsd file
        String origName;
        // Normalized name
        String name;
        // Name loaded from mapping file
        String mappedName;

        private List<String> documentation = new ArrayList<String>();

        public XsdNode(XmlSchemaObject node, XsdNode parent) {
            Objects.requireNonNull(node);
            this.parent = parent;
            this.node = node;
            if (node instanceof XmlSchemaNamed) {
                this.origName = ((XmlSchemaNamed) node).getName();
                setName(this.origName);
            }
            getSchema().register(this);
        }

        public XsdNode(XmlSchemaObject node) {
            this(node, null);
        }

        public XmlSchemaObject getSchemaObject() {
            return this.node;
        }
        public XSDSchema getSchema() {
            return XSDSchema.this;
        }
        
        public String getOrigName() {
            return this.origName;
        }
        public String convertName(String text) {
            return StringUtils.replaceChars(text, '.', '_');
        }
        public void setName(String newName) {
            Objects.requireNonNull(newName);
            if (newName.equals(this.name))
                return;
            
            this.name = convertName(newName);
            
            this.mappedName = XSDSchema.transliterate(this.name);
        }
        public XsdNode getParent() {
            return this.parent;
        }

        public String getName() {
            return name;
        }

        public void setMappedName(String mappedName) {
            this.mappedName = mappedName;
        }

        public String getMappedName() {
            return this.mappedName;
        }

        public List<String> getDocumentation() {
            return this.documentation;
        }

        public void setDocumentation(String docString) {
            if (StringUtils.isNotBlank(docString)) {
                this.documentation.add(docString);
            }
        }

        public void accept(XsdVisitor v) {
            v.visit(this);
        }

        String getFullName() {
            return this.name;
        }
    }

    public static String getFieldName(String name) {
        return JAXBRIContext.mangleNameToVariableName(name);
    }
    public static String getClassName(String name) {
        return JAXBRIContext.mangleNameToClassName(name);
    }
    public class XsdField extends XsdNode {

        XsdType type;

        void buildDoc(XmlSchemaAnnotation annotation) {
            if (annotation != null) {
                for (XmlSchemaAnnotationItem ai : annotation.getItems()) {
                    if (ai instanceof XmlSchemaDocumentation) {
                        NodeList docs = ((XmlSchemaDocumentation) ai).getMarkup();
                        for (int i = 0; i < docs.getLength(); ++i) {
                            Node n = docs.item(i);
                            String d = n.getNodeValue().trim();
                            if (StringUtils.isNotBlank(d))
                                getDocumentation().add(d);
                        }
                    }
                }
            }
        }
        @Override
        public String convertName(String text) {
            return getFieldName(super.convertName(text));
        }

        public String getFullName() {
            StringBuilder sb = new StringBuilder((this.parent == null ? "" : ((XsdType)this.parent).getFullName()));
            if (sb.length() > 0) {
                sb.append('.');
            }
            sb.append(this.getName());
            return sb.toString();
        }

        public XsdField(XmlSchemaObject node, XsdNode parent, XsdType type) {
            super(node, parent);
            this.type = type;
            if (parent instanceof XsdType) {
                ((XsdType)parent).addField(this);
            }
            if (node instanceof XmlSchemaAttribute) {
                buildDoc(((XmlSchemaAttribute) node).getAnnotation());
            } else if (node instanceof XmlSchemaElement) {
                buildDoc(((XmlSchemaElement) node).getAnnotation());
                
            }
        }

        @Override
        public void accept(XsdVisitor v) {
            v.visit(this);
        }
    }

    public class XsdType extends XsdNode {

        List<XsdField> fields = new ArrayList<>();
        List<XsdType> innerTypes = new ArrayList<>();
        boolean ordered = false;
        List<XsdField> propOrder = new ArrayList<>();

        XsdType getParentType() {
            XsdNode p = this.getParent();
            while (p != null) {
                if (p instanceof XsdType) {
                    return (XsdType) p;
                }
                p = p.getParent();
            }
            return null;
        }

        @Override
        public String convertName(String text) {
            return getClassName(super.convertName(text));
        }
        
        boolean isOrdered() {
            return ordered;
        }
        void setOrdered(boolean value) {
            this.ordered = value;
        }
        public List<XsdField> getPropOrder() {
            return this.propOrder;
        }
        public XsdField findField(String fieldName) {
            Optional<XsdField> ret = this.fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();
            return ret.isPresent() ? ret.get() : null;
        }
        XsdType getAttrType(org.apache.ws.commons.schema.XmlSchemaType t) {
            if (t instanceof XmlSchemaSimpleType) {
                XmlSchemaSimpleTypeContent stc = ((XmlSchemaSimpleType) t).getContent();
                return new XsdType(stc, null);
            } else if (t instanceof XmlSchemaComplexType) {

            }
            return null;
        }

        void    resolveParticleItems(Collection<? extends XmlSchemaObjectBase> items) {
            items.forEach(item -> resolveParticleItem(item));
        }
        void    resolveType(org.apache.ws.commons.schema.XmlSchemaType t) {
            if (t instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType ct = (XmlSchemaComplexType) t;
                resolveParticle(ct.getParticle());
                List<XmlSchemaAttributeOrGroupRef> attrs = ct.getAttributes();
                for (XmlSchemaAttributeOrGroupRef a : attrs) {
                    if (a instanceof XmlSchemaAttribute) {
                        XmlSchemaAttribute attr = (XmlSchemaAttribute) a;
                        XsdField f = new XsdField(attr, this, null);
                    }
                }
            } else if (t instanceof XmlSchemaSimpleType) {
                XmlSchemaSimpleType ct = (XmlSchemaSimpleType) t;

                XsdField f = new XsdField(ct, this, new XsdType(ct, null));
                for (XmlSchemaAnnotationItem ai : ct.getAnnotation().getItems()) {
                    if (ai instanceof XmlSchemaDocumentation) {
                        NodeList docs = ((XmlSchemaDocumentation) ai).getMarkup();
                        for (int i = 0; i < docs.getLength(); ++i) {
                            Node n = docs.item(i);
                            String d = n.getNodeValue();
                            f.getDocumentation().add(d);
                        }
                    }
                }
            }
        }
        void    resolveParticleItem(XmlSchemaObjectBase xso) {
            if (xso instanceof XmlSchemaElement) {
                resolveElement((XmlSchemaElement)xso);
            } else if (xso instanceof XmlSchemaParticle) {
                resolveParticle((XmlSchemaParticle)xso);
            }
        }
        
        void    resolveElement(XmlSchemaElement element) {
            String elName = element.getName();
            QName qname = element.getSchemaTypeName();
            if (qname == null) {
                org.apache.ws.commons.schema.XmlSchemaType innerType = element.getSchemaType();
                if (innerType instanceof XmlSchemaComplexType) {
                    XsdType anonType = new XsdType(element, this);
                    XsdField f = new XsdField(element, this, anonType);
                }
            } else {
                XsdField f = new XsdField(element, this, null/*getAttrType()*/);
            }
            
        }
        
        void    resolveParticle(XmlSchemaParticle particle) {
            if (particle == null)
                return;
            if (particle instanceof XmlSchemaSequence) {
                this.setOrdered(true);
                resolveParticleItems(((XmlSchemaSequence) particle).getItems());
            } else if (particle instanceof XmlSchemaChoice) {
                resolveParticleItems(((XmlSchemaChoice) particle).getItems());
            } else if (particle instanceof XmlSchemaAll) {
                resolveParticleItems(((XmlSchemaAll) particle).getItems());
            }
            
        }
        public XsdType(XmlSchemaObject node, XsdType parent) {
            super(node, parent);
            if (parent != null) {
                parent.addInnerType(this);
            }
            if (node instanceof XmlSchemaAnnotated) {
                XmlSchemaAnnotated ao = (XmlSchemaAnnotated) node;
                XmlSchemaAnnotation ant = ao.getAnnotation();
                if (ant != null) {
                    for (XmlSchemaAnnotationItem ai : ao.getAnnotation().getItems()) {
                        if (ai instanceof XmlSchemaDocumentation) {
                            NodeList docs = ((XmlSchemaDocumentation) ai).getMarkup();
                            for (int i = 0; i < docs.getLength(); ++i) {
                                Node n = docs.item(i);
                                this.getDocumentation().add(n.getNodeValue());
                            }
                        }
                    }
                }
            }
            if (node instanceof XmlSchemaElement) {
                resolveType(((XmlSchemaElement) node).getSchemaType());
            }
            if (node instanceof XmlSchemaComplexType) {
                XmlSchemaComplexType ct = (XmlSchemaComplexType) node;
                List<XmlSchemaAttributeOrGroupRef> attributes = ct.getAttributes();
                for(XmlSchemaAttributeOrGroupRef attribute : attributes) {
                    if (attribute instanceof XmlSchemaAttribute) {
                        XmlSchemaAttribute a = (XmlSchemaAttribute)attribute;
                        XsdField f = new XsdField(a, this, null);
                    }
                }
            }
        }

        public String getFullName() {
            XsdType parentType = this.getParentType();
            if (parentType != null) {
                return parentType.getFullName() + "." + getName();
            }
            return this.getName();
        }

        public String getFullNameMapped() {
            XsdType parentType = this.getParentType();
            if (parentType != null) {
                return parentType.getFullNameMapped() + "." + getMappedName();
            }
            return this.getMappedName();
        }

        public List<XsdType> getInnerTypes() {
            return this.innerTypes;
        }

        public List<XsdField> getFields() {
            return this.fields;
        }

        void addInnerType(XsdType inner) {
            this.innerTypes.add(inner);
        }

        void addField(XsdField field) {
            this.fields.add(field);
            if (ordered) {
                if (field.getSchemaObject() instanceof XmlSchemaElement) {
                    this.propOrder.add(field); 
               }
            }
        }

        @Override
        public void accept(XsdVisitor v) {
            v.visit(this);
        }

    }

    
    // Простая транслитерация RU -> LAT
    static final char[] ru = {'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
    static final String[] lat = {"a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja"};
    static final Map<Character, String> translit = new HashMap<>();
    static {
        assert(ru.length == lat.length);
        for(int i = 0; i < ru.length; ++i) {
            translit.put(ru[i], lat[i]);
        }
    }
    public static String transliterate(String message) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); ++i) {
            char ch = message.charAt(i);
            String rep = translit.get(ch);
            if (rep != null) 
                builder.append(rep);
            else
                builder.append(ch);
        }
        return builder.toString();
    }
}
