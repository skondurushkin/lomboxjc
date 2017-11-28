package com.luxoft.lxsd;

import com.luxoft.lxsd.XSDSchema.XsdField;
import com.luxoft.lxsd.XSDSchema.XsdNode;
import com.luxoft.lxsd.XSDSchema.XsdType;
import com.sun.codemodel.JAnnotatable;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JDocComment;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.tools.xjc.generator.annotation.spec.XmlAccessorTypeWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlAnyElementWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlAttributeWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlElementWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlRegistryWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlRootElementWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlSchemaTypeWriter;
import com.sun.tools.xjc.generator.annotation.spec.XmlTypeWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaUse;

/**
 * Генератор кода.
 * 
 * Из JCodeModel, построенной xjc, путем клонирования создается 
 * модифицированная модель
 * 
 * @author skondurushkin
 */
public class CodeGen {

    final Map<String, XsdNode> mapNames;
    CodeGen(Map<String, XsdNode> mapNames) {
        this.mapNames = mapNames;
    }
  
    JPackage getPackage(JClassContainer cc) {
        if (cc instanceof JPackage) {
            return (JPackage)cc;
        } else if (cc instanceof JDefinedClass) {
            return ((JDefinedClass)cc)._package();
        }
        return null;
    }
    
    JDefinedClass getTargetClass(JClassContainer targetCC, JDefinedClass sourceClass) {
        XsdType type = getXsdType(sourceClass);
        if (type == null) {
            return null;
        }
        String fullyQualifiedClassName = type.getFullNameMapped();
        String fullyQualifiedClassNameWithPackage = fullyQualifiedClassName;
        String packageName = "";
        JPackage pkg = getPackage(targetCC);
        if (pkg != null) {
            packageName = pkg.name();
        }
        if (StringUtils.isNotBlank(packageName))
            fullyQualifiedClassNameWithPackage = packageName + '.' + fullyQualifiedClassNameWithPackage;
        
        JDefinedClass ret = targetCC.owner()._getClass(fullyQualifiedClassNameWithPackage);
        if (ret == null) {
            try {
                ret = targetCC._class(sourceClass.mods().getValue(), type.getMappedName(), sourceClass.getClassType());
            } catch (JClassAlreadyExistsException ex) {
                throw new RuntimeException(ex);
            }
        }
        return ret;
    }

    void cloneField(JDefinedClass toClass, JDefinedClass fromClass, String name, JFieldVar fieldVar) {
        XsdField field = getXsdField(fromClass, fieldVar);
        JFieldVar ret = toClass.field(fieldVar.mods().getValue(), fieldVar.type(), (field == null)?name:field.getMappedName());
        makeDoc(ret.javadoc(), field);
        boolean required = false;
        if (field != null) {
            XmlSchemaObject xso = field.getSchemaObject();
            if (xso instanceof XmlSchemaAttribute) {
                XmlSchemaAttribute attr = (XmlSchemaAttribute) xso;
                XmlSchemaUse use = attr.getUse();
                if (use != null) {
                    required = (use == XmlSchemaUse.REQUIRED);
                }
                QName qname = attr.getSchemaTypeName();
                if (qname != null && qname.getNamespaceURI().equals(XMLConstants.W3C_XML_SCHEMA_NS_URI)) {
                    XmlSchemaSimpleType simpleType = attr.getSchemaType();
                    ret.annotate2(XmlSchemaTypeWriter.class).name(qname.getLocalPart());
                }
            } else if (xso instanceof XmlSchemaElement) {
                XmlSchemaElement el = (XmlSchemaElement)xso;
                required = (el.getMinOccurs() != 0);
            }
            if (ret.type() instanceof JDefinedClass) {
                XmlElementWriter w = ret.annotate2(XmlElementWriter.class).name(field.getOrigName()).required(required);
            } else {
                if (field.getSchemaObject() instanceof XmlSchemaAny) {
                    XmlAnyElementWriter w = ret.annotate2(XmlAnyElementWriter.class);
                } else {
                    XmlAttributeWriter w = ret.annotate2(XmlAttributeWriter.class).name(field.getOrigName()).required(required);
                }
            }
        }
        if (fieldVar.type() instanceof JDefinedClass) {
            JType type = getTargetClass(toClass, (JDefinedClass)fieldVar.type());
            if (type != null) {
                ret.type(type);
            }
        } else if (fieldVar.type() instanceof JClass) {
            JClass type = (JClass)fieldVar.type();
            if (type.isParameterized()) {
                List<JClass> params = type.getTypeParameters();
                List<JClass> targetParams = new ArrayList<>();
                for(JClass sourceClass : params) {
                    if (sourceClass instanceof JDefinedClass) {
                        JDefinedClass targetClass = getTargetClass(toClass, (JDefinedClass)sourceClass);
                        if (targetClass != null)
                            targetParams.add(targetClass);
                        else {
                            targetParams.add(sourceClass);
                        }
                    } else 
                        targetParams.add(sourceClass);
                }
                JType erasure = type.erasure();
                try {
                    Class<?> clazz = Class.forName(erasure.fullName());
                    JClass narrowedClass = toClass.owner().ref(clazz).narrow(targetParams);
                    ret.type(narrowedClass);
                } catch (ClassNotFoundException ex) {
                    // Suppress;
                }
            }
            
        }
    }

    XsdType  guessType(JFieldVar fld) {
        JType type = fld.type();
        if (type instanceof JDefinedClass) {
            return getXsdType((JDefinedClass)type);
        }
        return null;
    }
    XsdField getXsdField(JDefinedClass toClass, JFieldVar fld) {
        XsdField ret = null;
        XsdType clsType = getXsdType(toClass);
        if (clsType != null)
            ret = clsType.findField(fld.name());
        if (ret == null) {
            XsdType type = guessType(fld);
            type = type == null? null : type.getParentType();
            ret = type == null? null : type.findField(fld.name());
        }
        return ret;
    }
    XsdType getXsdType(JDefinedClass cls) {
        String pkgName = cls._package().name();
        String clsFullName = cls.fullName();
        if (clsFullName.startsWith(pkgName+'.')) {
            clsFullName = clsFullName.substring(pkgName.length()+1);
        }
        XsdNode n = this.mapNames.get(clsFullName);
        return (n instanceof XsdType)? (XsdType)n : null;
    }
    
    String mapClassName(JDefinedClass cls) {
        XsdNode n = getXsdType(cls);
        return (n == null)? cls.name() : n.getMappedName();
    }
    
    List<String> wrapString(String longText, int width) {
        List<String> ret = new ArrayList<>();
        longText = WordUtils.wrap(longText, width);
        String parts[] = longText.split("\n");
        for (String part : parts) {
            part = part.trim();
            if (StringUtils.isNotBlank(part))
                ret.add(part);
        }
        return ret;
    }
    
    List<String> prepareDoc(List<String> docStrings) {
        List<String> ret = new ArrayList<>();
        for(String docString : docStrings) {
            docString = docString.trim();
            ret.addAll(wrapString(docString, 80));
        }
        return ret;
    }
    
    void makeDoc(JDocComment jdoc, List<String> docStrings) {
        boolean first = true;
        docStrings = prepareDoc(docStrings);
        for(String docString : docStrings) {
            if (StringUtils.isNotBlank(docString)) {
                if (first) {
                   jdoc.append("DOC: ");
                } else {
                   jdoc.append("     ");
                }
                jdoc.append(docString).append("\n");
                first = false;
            }
        }
    }
    void makeDoc(JDocComment jdoc, XsdNode n) {
        if (n == null)
            return;
        if(n != null) {
            jdoc.append("XSD: ").append(n.getName()).append("\n");
            makeDoc(jdoc, n.getDocumentation());
        }
    }
    
    boolean hasAnnotation(JAnnotatable t, Class<? extends Annotation> annClass) {
        for(JAnnotationUse au : t.annotations()){
            if (au.getAnnotationClass().fullName().equals(annClass.getName()))
                return true;
        }
        return false;
    }
    
    // NYI
    JDefinedClass generateObjectFactory(JDefinedClass factory) {
        return factory;
    }
    
    // Находим и возвращаем класс, определенный в данном контейнере (пакете или классе)
    // Если искомый класс не найдем, создаем определение и возвращаем его
    JDefinedClass findOrCreateClass(JClassContainer classContainer, int mods, String name) throws JClassAlreadyExistsException {
        Iterator<JDefinedClass> itClasses = classContainer.classes();
        while(itClasses.hasNext()) {
            JDefinedClass clazz = itClasses.next();
            if (clazz.name().equals(name))
                return clazz;
        }
        return classContainer._class(mods, name);
    }
    // Клонирование класса
    // методы класса не клонируются. Это геттеры и сеттеры, которые нам дает lombok. 
    void cloneClass(JClassContainer classContainer, JDefinedClass cls) {
        try {
            // Пока что пропускаем ObjectFactory
            if (cls.name().equals("ObjectFactory"))
                return;
            XsdType type = getXsdType(cls);
            final JDefinedClass ret = findOrCreateClass(classContainer, cls.mods().getValue(), mapClassName(cls));
            makeDoc(ret.javadoc(), type);
            if (ret.name().equals("ObjectFactory")) {
                ret.annotate2(XmlRegistryWriter.class);
                ret.javadoc().append(cls.javadoc());
                generateObjectFactory(ret);
            } else {
                // К сожалению JCodeModel не дает полного доступа к аннотациям
                // Приходится дублировать функциональность XJC для аннотирования
                // клонированного класса всеми аннотациями исходного
                ret.annotate((Class)Class.forName("lombok.Data"));
                ret.annotate2(XmlAccessorTypeWriter.class).value(XmlAccessType.FIELD);
                XmlTypeWriter xtw = ret.annotate2(XmlTypeWriter.class).name(cls.name());
                if (type != null && type.isOrdered() ) {
                    for(XsdField f : type.getPropOrder()) {
                        // As we use XmlAccessorType XmlAccesType.FIELD
                        // the names in propOrder MUST correspond to field names
                        xtw.propOrder(f.getMappedName()); 
                    }
                }
                if (hasAnnotation(cls, XmlRootElement.class))
                    ret.annotate2(XmlRootElementWriter.class).name(cls.name());
            }
            
            // клонируем поля класса
            cls.fields().forEach((k,v)-> cloneField(ret, cls, k, v));
            // клонируем внутренние классы (рекрсивно)
            cls.classes().forEachRemaining(icls -> cloneClass(ret, icls));
        } catch (JClassAlreadyExistsException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    // Клонирование пакета
    void clonePackage(JCodeModel toModel, JPackage pkg) {
        final JPackage ret = toModel._package(pkg.name());
        pkg.classes().forEachRemaining(cls -> cloneClass(ret, cls));
    }
    
    // Клонирование модели
    JCodeModel cloneModel(JCodeModel source) throws IOException {
        final JCodeModel ret = new JCodeModel();
        source.packages().forEachRemaining(pkg -> clonePackage(ret, pkg));
        return ret;
    }
}
