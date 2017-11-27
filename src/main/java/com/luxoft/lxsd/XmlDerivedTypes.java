package com.luxoft.lxsd;

/**
 *
 * @author skondurushkin
 */
public class XmlDerivedTypes {
    // DECRIPTION: Represents white space normalized strings. This data type is derived from string.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_NOTMALIZEDSTRING = "normalizedString";
    
    // DECRIPTION: Represents tokenized strings. This data type is derived from normalizedString.
    // FACETS: enumeration, pattern, length, minLength, maxLength, whiteSpace
    public static final String XML_TOKEN = "token";
    
    // DECRIPTION: Represents natural language identifiers (defined by RFC 1766). This data type is derived from token.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_LANGUAGE = "language";
    
    // DECRIPTION: Represents the IDREFS attribute type. Contains a set of values of type IDREF.
    // FACETS: length, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_IDREFS = "IDREFS";
    
    // DECRIPTION: Represents the ENTITIES attribute type. Contains a set of values of type ENTITY.
    // FACETS: length, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_ENTITIES = "ENTITIES";
    
    // DECRIPTION: Represents the NMTOKEN attribute type. An NMTOKEN is set of name characters (letters, digits, and other characters) in any combination. Unlike Name and NCName, NMTOKEN has no restrictions on the starting character. This data type is derived from token.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_NMTOKEN = "NMTOKEN";
    
    // DECRIPTION: Represents the NMTOKENS attribute type. Contains a set of values of type NMTOKEN.
    // FACETS: length, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_NMTOKENS = "NMTOKENS";
    
    // DECRIPTION: Represents names in XML. A Name is a token that begins with a letter, underscore, or colon and continues with name characters (letters, digits, and other characters). This data type is derived from token.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_NAME = "Name";
    
    // DECRIPTION: Represents noncolonized names. This data type is the same as Name, except it cannot begin with a colon. This data type is derived from Name.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_NCNAME = "NCName";
    
    // DECRIPTION: Represents the ID attribute type defined in the XML 1.0 Recommendation. The ID must be a no-colon-name (NCName) and must be unique within an XML document. This data type is derived from NCName.
    // FACETS: length, enumeration, pattern, maxLength, minLength, whiteSpace
    public static final String XML_ID = "ID";
    
    // DECRIPTION: Represents a reference to an element that has an ID attribute that matches the specified ID. An IDREF must be an NCName and must be a value of an element or attribute of type ID within the XML document. This data type is derived from NCName.
    // FACETS: length, enumeration, pattern, maxLength, minLength, whiteSpace
    public static final String XML_IDREF = "IDREF";
    
    // DECRIPTION: Represents the ENTITY attribute type in XML 1.0 Recommendation. This is a reference to an unparsed entity with a name that matches the specified name. An ENTITY must be an NCName and must be declared in the schema as an unparsed entity name. This data type is derived from NCName.
    // FACETS: length, enumeration, pattern, maxLength, minLength, whiteSpace
    public static final String XML_ENTITY = "ENTITY";
    
    // DECRIPTION: Represents a sequence of decimal digits with an optional leading sign (+ or -). This data type is derived from decimal.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_INTEGER = "integer";
    
    // DECRIPTION: Represents an integer that is less than or equal to zero. A nonPositiveInteger consists of a negative sign (-) and sequence of decimal digits. This data type is derived from integer.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_NONPOSITIVEINTEGER = "nonPositiveInteger";
    
    // DECRIPTION: Represents an integer that is less than zero. Consists of a negative sign (-) and sequence of decimal digits. This data type is derived from nonPositiveInteger.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_NEGATIVEINTEGER = "negativeInteger";
    
    // DECRIPTION: Represents an integer with a minimum value of -9223372036854775808 and maximum of 9223372036854775807. This data type is derived from integer.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_LONG = "long";
    
    // DECRIPTION: Represents an integer with a minimum value of -2147483648 and maximum of 2147483647. This data type is derived from long.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_INT = "int";
    
    // DECRIPTION: Represents an integer with a minimum value of -32768 and maximum of 32767. This data type is derived from int.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_SHORT = "short";
    
    // DECRIPTION: Represents an integer with a minimum value of -128 and maximum of 127. This data type is derived from short.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_BYTE = "byte";
    
    // DECRIPTION: Represents an integer that is greater than or equal to zero. This data type is derived from integer.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_NONNEGATIVEINTEGER = "nonNegativeInteger";
    
    // DECRIPTION: Represents an integer with a minimum of zero and maximum of 18446744073709551615. This data type is derived from nonNegativeInteger.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_UNSIGNEDLONG = "unsignedLong";
    
    // DECRIPTION: Represents an integer with a minimum of zero and maximum of 4294967295. This data type is derived from unsignedLong.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_UNSIGNEDINT = "unsignedInt";
    
    // DECRIPTION: Represents an integer with a minimum of zero and maximum of 65535. This data type is derived from unsignedInt.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_UNSIGNEDSHORT = "unsignedShort";
    
    // DECRIPTION: Represents an integer with a minimum of zero and maximum of 255. This data type is derived from unsignedShort.
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_UNSIGNEDBYTE = "unsignedByte";
    
    // DECRIPTION: Represents an integer that is greater than zero. This data type is derived from nonNegativeInteger.    
    // FACETS: enumeration, fractionDigits, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, totalDigits, whiteSpace
    public static final String XML_POSITIVEINTEGER = "positiveInteger";
        
}
