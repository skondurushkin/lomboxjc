package com.luxoft.lxsd;

/**
 *
 * @author skondurushkin
 * 
 * For more information, see the W3C XML Schema Part 2: Datatypes Recommendation at 
 * https://www.w3.org/TR/xmlschema-2/#rf-facets.

 */
public class XmlFacets {
    // Specified set of values. This constrains a data type to the specified values.
    public static final String FACET_ENUMERATION = "enumeration";
    
    // Value with specific maximum number of decimal digits in the fractional part. 
    public static final String FACET_FRACTIONDIGITS = "fractionDigits";
    
    // Number of units of length. Units of length depend on the data type. 
    // This value must be a nonNegativeInteger. 
    public static final String FACET_LENGTH = "length";
    
    // Upper bound value (all values are less than this value). This value must be 
    // the same data type as the inherited data type.
    public static final String FACET_MAXEXCLUSIVE = "maxExclusive";
    
    // Maximum value. This value must be the same data type as the inherited data type. 
    public static final String FACET_MAXINCLUSIVE = "maxInclusive";
    
    // Maximum number of units of length. Units of length depend on the data type. 
    // This value must be a nonNegativeInteger.
    public static final String FACET_MAXLENGTH = "maxLength";
    
    // Lower bound value (all values are greater than this value). This value must 
    // be the same data type as the inherited data type. 
    public static final String FACET_MINEXCLUSIVE = "minExclusive";
    
    // Minimum value. This value must be the same data type as the inherited data type. 
    public static final String FACET_MININCLUSIVE = "minInclusive";
    
    // Minimum number of units of length. Units of length depend on the data type. 
    // This value must be a nonNegativeInteger.
    public static final String FACET_MINLENGTH = "minLength";
    
    // Specific pattern that the data type's values must match. 
    // This constrains the data type to literals that match the specified pattern. 
    // The pattern value must be a regular expression. 
    public static final String FACET_PATTERN = "pattern";
    
    // Value with specific maximum number of decimal digits.
    public static final String FACET_TOTALDIGITS = "totalDigits";
    
    // Value must be one of preserve, replace, or collapse. 
    // preserve     | No normalization is performed; the value is not changed for 
    //              | element content as required by the W3C XML 1.0 Recommendation.
    // replace      | All occurrences of #x9 (tab), #xA (line feed) and #xD (carriage return) 
    //              | are replaced with #x20 (space).
    // collapse     | After the processing implied by replace, contiguous sequences of #x20s 
    //              | are collapsed to a single #x20, and leading and trailing #x20s are removed.    
    // The whiteSpace facet cannot be changed for most numeric data types. 
    public static final String FACET_WHITESPACE = "whiteSpace";
}
