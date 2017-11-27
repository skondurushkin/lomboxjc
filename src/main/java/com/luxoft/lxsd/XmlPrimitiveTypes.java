package com.luxoft.lxsd;

/**
 *
 * @author skondurushkin
 */
public class XmlPrimitiveTypes {

    // DESCRIPTION: Represents character strings.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_STRING = "string";

    // DESCRIPTION: Represents Boolean values, which are either true or false.
    // FACETS: pattern, whiteSpace
    public static final String XML_BOOLEAN = "boolean";

    // DESCRIPTION: Represents arbitrary precision numbers.
    // FACETS: enumeration, pattern, totalDigits, fractionDigits, minInclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_DECIMAL = "decimal";

    // DESCRIPTION: Represents single-precision 32-bit floating-point numbers.
    // FACETS: pattern, enumeration, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_FLOAT = "float";

    // DESCRIPTION: Represents double-precision 64-bit floating-point numbers.
    // FACETS: pattern, enumeration, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_DOUBLE = "double";

    // DESCRIPTION: Represents a duration of time.
    //              The pattern for duration is PnYnMnDTnHnMnS, where nY represents the number of years, 
    //              nM the number of months, nD the number of days, T the date/time separator, 
    //              nH the number of hours, nM the number of minutes, and nS the number of seconds.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_DURATION = "duration";

    // DESCRIPTION: Represents a specific instance of time.
    //              The pattern for dateTime is CCYY-MM-DDThh:mm:ss where CC represents the century, 
    //              YY the year, MM the month, and DD the day, preceded by an optional leading negative (-) character to indicate a negative number.
    //              If the negative character is omitted, positive (+) is assumed. The T is the date/time separator and hh, mm, and ss represent hour, minute, and second respectively. 
    //              Additional digits can be used to increase the precision of fractional seconds if desired. For example, the format ss.ss...
    //              with any number of digits after the decimal point is supported. The fractional seconds part is optional.
    //              This representation may be immediately followed by a "Z" to indicate Coordinated Universal Time (UTC) or 
    //              to indicate the time zone. For example, the difference between the local time and Coordinated Universal Time, 
    //              immediately followed by a sign, + or -, followed by the difference from UTC represented as hh:mm 
    //              (minutes is required). If the time zone is included, both hours and minutes must be present.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_DATETIME = "dateTime";

    // DESCRIPTION: Represents an instance of time that recurs every day.
    //              The pattern for time is hh:mm:ss.sss with optional time zone indicator.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_TIME = "time";

    // DESCRIPTION: Represents a calendar date.
    //              The pattern for date is CCYY-MM-DD with optional time zone indicator as allowed for dateTime.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_DATE = "date";

    // DESCRIPTION: Represents a specific Gregorian month in a specific Gregorian year. A set of one-month long, nonperiodic instances.
    //              The pattern for gYearMonth is CCYY-MM with optional time zone indicator.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_GYEARMONTH = "gYearMonth";

    // DESCRIPTION: Represents a Gregorian year. A set of one-year long, nonperiodic instances.
    //              The pattern for gYear is CCYY with optional time zone indicator as allowed for dateTime.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_GYEAR = "gYear";

    // DESCRIPTION: Represents a specific Gregorian date that recurs, specifically a day of the year such as the third of May. A gMonthDay is the set of calendar dates. Specifically, it is a set of one-day long, annually periodic instances.
    //              The pattern for gMonthDay is --MM-DD with optional time zone indicator as allowed for date.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_GMONTHDAY = "gMonthDay";

    // DESCRIPTION: Represents a Gregorian day that recurs, specifically a day of the month such as the fifth day of the month. A gDay is the space of a set of calendar dates. Specifically, it is a set of one-day long, monthly periodic instances.
    //              The pattern for gDay is ---DD with optional time zone indicator as allowed for date.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_GDAY = "gDay";

    // DESCRIPTION: Represents a Gregorian month that recurs every year. A gMonth is the space of a set of calendar months. Specifically, it is a set of one-month long, yearly periodic instances.
    //              The pattern for gMonth is --MM-- with optional time zone indicator as allowed for date.
    // FACETS: enumeration, pattern, minInclusive, minExclusive, maxInclusive, maxExclusive, whiteSpace
    public static final String XML_GMONTH = "gMonth";

    // DESCRIPTION: Represents arbitrary hex-encoded binary data. A hexBinary is the set of finite-length sequences of binary octets. Each binary octet is encoded as a character tuple, consisting of two hexadecimal digits ([0-9a-fA-F]) representing the octet code.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_HEXBINARY = "hexBinary";

    // DESCRIPTION: Represents Base64-encoded arbitrary binary data. A base64Binary is the set of finite-length sequences of binary octets.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_BASE64BINARY = "base64Binary";

    // DESCRIPTION: Represents a URI as defined by RFC 2396. An anyURI value can be absolute or relative, and may have an optional fragment identifier.
    // FACETS: length, pattern, maxLength, minLength, enumeration, whiteSpace
    public static final String XML_ANYURI = "anyURI";

    // DESCRIPTION: Represents a qualified name. A qualified name is composed of a prefix and a local name separated by a colon. Both the prefix and local names must be an NCName. The prefix must be associated with a namespace URI reference, using a namespace declaration.
    // FACETS: length, enumeration, pattern, maxLength, minLength, whiteSpace
    public static final String XML_QNAME = "QName";

    // DESCRIPTION: Represents a NOTATION attribute type. A set of QNames.
    // FACETS: length, enumeration, pattern, maxLength, minLength, whiteSpace
    public static final String XML_NOTATION = "NOTATION";

}
