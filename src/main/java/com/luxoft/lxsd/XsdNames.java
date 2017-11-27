package com.luxoft.lxsd;

/**
 *
 * @author skondurushkin
 */
public class XsdNames {

    // ROOT ELEMENT
    public static final String XSD_SCHEMA = "schema";
    
    // TOP LEVEL ELEMENTS
    // <xsd:annotation> Element | Defines an annotation.
    public static final String XSD_ANNOTATION = "annotation";
    // <xsd:attribute> Element | Declares an attribute.
    public static final String XSD_ATTRIBUTE = "attribute";
    // <xsd:attributeGroup> Element | Groups a set of attribute declarations so that they can be incorporated as a group for complex type definitions.
    // <xsd:complexType> Element | Defines a complex type, which determines the set of attributes and the content of an element.
    public static final String XSD_COMPLEXTYPE = "complexType";
    // <xsd:element> Element | Declares an element.
    public static final String XSD_ELEMENT = "element";
    // <xsd:group> Element | Groups a set of element declarations so that they can be incorporated as a group into complex type definitions.
    public static final String XSD_GROUP = "group";
    // <xsd:import> Element | Identifies a namespace whose schema components are referenced by the containing schema.
    public static final String XSD_IMPORT = "import";
    // <xsd:include> Element | Includes the specified schema document in the target namespace of the containing schema.
    public static final String XSD_INCLUDE = "include";
    // <xsd:notation> Element | Contains the definition of a notation to describe the format of non-XML data within an XML document. An XML Schema notation declaration is a reconstruction of XML 1.0 NOTATION declarations.
    public static final String XSD_NOTATION = "notation";
    // <xsd:redefine> Element | Allows simple and complex types, groups, and attribute groups that are obtained from external schema files to be redefined in the current schema.
    public static final String XSD_REDEFINE = "redefine";
    // <xsd:simpleType> Element | Defines a simple type, which determines the constraints on and information about the values of attributes or elements with text-only content.
    public static final String XSD_SIMPLETYPE = "simpleType";
        
    // PARTICLES
    // The following are elements that can have minOccurs and maxOccurs attributes. Such elements always 
    // appear as part of a complex type definition or as part of a named model group.
    
    // <xsd:all> Element | Allows the elements in the group to appear (or not appear) in any order in the containing element.
    public static final String XSD_ALL = "all";
    // <xsd:any> Element | Enables any element from the specified namespace(s) to appear in the containing sequence or choice element.
    public static final String XSD_ANY = "any";
    // <xsd:choice> Element | Allows one and only one of the elements contained in the selected group to be present within the containing element.
    public static final String XSD_CHOICE = "choice";
    // <xsd:element> Element | Declares an element.
    // see XSD_ELEMENT
    
    // <xsd:group> Element | Groups a set of element declarations so that they can be incorporated as a group into complex type definitions.
    // see XSD_GROUP
    // <xsd:sequence> Element | Requires the elements in the group to appear in the specified sequence within the containing element.    
    public static final String XSD_SEQUENCE = "sequence";
    
    // IDENTITY CONSTRAINTS
    // <xsd:field> Element | Specifies an XML Path Language (XPath) expression that specifies the value (or one of the values) used to define an identity constraint (unique, key, and keyref elements).
    // <xsd:key> Element | Specifies that an attribute or element value (or set of values) must be a key within the specified scope. The scope of a key is the containing element in an instance document. A key must be unique, non-nillable, and always present.
    // <xsd:keyref> Element | Specifies that an attribute or element value (or set of values) correspond to those of the specified key or unique element.
    // <xsd:selector> Element | Specifies an XPath expression that selects a set of elements for an identity constraint (unique, key, and keyref elements).
    // <xsd:unique> Element | Specifies that an attribute or element value (or a combination of attribute or element values) must be unique within the specified scope. The value must be unique or nil.    
    
    
    // ATTRIBUTES
    // <xsd:anyAttribute> Element | Enables any attribute from the specified namespace(s) to appear in the containing complexType element or in the containing attributeGroup element.
    // <xsd:attribute> Element | Declares an attribute.
    // <xsd:attributeGroup> Element | Groups a set of attribute declarations so that they can be incorporated as a group for complex type definitions.
    
    // NAMED SCHEMA OBJECTS
    // The following are elements that define named constructs in schemas. 
    // Named constructs are referred to with a QName by other schema elements.
    
    // <xsd:attribute> Element | Declares an attribute.
    // <xsd:attributeGroup> Element | Groups a set of attribute declarations so that they can be incorporated as a group for complex type definitions.
    // <xsd:complexType> Element | Defines a complex type, which determines the set of attributes and the content of an element.
    // <xsd:element> Element | Declares an element.
    // <xsd:group> Element | Groups a set of element declarations so that they can be incorporated as a group into complex type definitions.
    // <xsd:key> Element | Specifies that an attribute or element value (or set of values) must be a key within the specified scope. The scope of a key is the containing element in an instance document. A key must be unique, non-nillable, and always present.
    // <xsd:keyref> Element | Specifies that an attribute or element value (or set of values) correspond to those of the specified key or unique element.
    // <xsd:notation> Element | Contains the definition of a notation to describe the format of non-XML data within an XML document. An XML Schema notation declaration is a reconstruction of XML 1.0 NOTATION declarations.
    // <xsd:simpleType> Element | Defines a simple type, which determines the constraints on and information about the values of attributes or elements with text-only content.
    // <xsd:unique> Element | Specifies that an attribute or element value (or a combination of attribute or element values) must be unique within the specified scope. The value must be unique or nil.    

    // COMPLEX TYPE DEFINITIONS
    // <xsd:all> Element | Allows the elements in the group to appear (or not appear) in any order in the containing element.
    // <xsd:annotation> Element | Defines an annotation.
    // <xsd:any> Element | Enables any element from the specified namespace(s) to appear in the containing sequence or choice element.
    // <xsd:anyAttribute> Element | Enables any attribute from the specified namespace(s) to appear in the containing complexType element or in the containing attributeGroup element.
    // <xsd:appinfo> Element | Specifies information to be used by applications within an annotation element.
    // <xsd:attribute> Element | Declares an attribute.
    // <xsd:attributeGroup> Element | Groups a set of attribute declarations so that they can be incorporated as a group for complex type definitions.
    // <xsd:choice> Element | Allows one and only one of the elements contained in the selected group to be present within the containing element.
    // <xsd:complexContent> Element | Contains extensions or restrictions on a complex type that contains mixed content or elements only.
    // <xsd:documentation> Element | Specifies information to be read or used by users within an annotation element.
    // <xsd:element> Element | Declares an element.
    // <xsd:extension> Element (simpleContent) | Contains extensions on simpleContent. This extends a simple type or a complex type that has simple content by adding specified attribute(s), attribute groups(s) or anyAttribute.
    // <xsd:extension> Element (complexContent) | Contains extensions on complexContent.
    // <xsd:group> Element | Groups a set of element declarations so that they can be incorporated as a group into complex type definitions.
    // <xsd:restriction> Element (simpleContent) | Defines constraints on a simpleContent definition.
    // <xsd:restriction> Element (complexContent) | Defines constraints on a complexContent definition.
    // <xsd:sequence> Element | Requires the elements in the group to appear in the specified sequence within the containing element.
    // <xsd:simpleContent> Element | Contains extensions or restrictions on a complexType element with character data or a simpleType element as content and contains no elements.

    // SIMPLE TYPES DEFINITIONS
    // <xsd:annotation> Element | Defines an annotation.
    // <xsd:appinfo> Element | Specifies information to be used by applications within an annotation element.
    // <xsd:documentation> Element | Specifies information to be read or used by users within an annotation element.
    // <xsd:element> Element | Declares an element.
    // <xsd:list> Element | Defines a collection of a single simpleType definition.
    // <xsd:restriction> Element (simpleType) | Defines constraints on a simpleType definition
    // <xsd:union> Element | Defines a collection of multiple simpleType definitions.

    public static final String XSD_APPINFO = "appinfo";
    public static final String XSD_DOCUMENTATION = "documentation";
    
    public static final String XSD_RESTRICTION = "restriction";
    public static final String XSD_LENGTH = "length";
    public static final String XSD_ENUMERATION = "enumeration";
    public static final String XSD_MINLENGTH = "minLength";
    public static final String XSD_MAXLENGTH = "maxLength";
    public static final String XSD_PATTERN = "pattern";
    public static final String XSD_TOTALDIGITS = "totalDigits";
    public static final String XSD_FRACTIONDIGITS = "fractionDigits";
    
    public static final String XSD_STRING = "string";
    public static final String XSD_INTEGER = "integer";
    public static final String XSD_DECIMAL = "decimal";
    public static final String XSD_BOOLEAN = "boolean";
    
}
