package com.luxoft.lxsd;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import com.sun.codemodel.JFormatter;
import com.sun.codemodel.JStringLiteral;

/**
 *
 * @author skondurushkin
 */
@Aspect
public class JExprAspect {
    @Around("execution (public void com.sun.codemodel.JStringLiteral.generate(JFormatter)) && this(me) && args(fmt)")
    public void generateNoEscape(JStringLiteral me, JFormatter fmt) {
        fmt.p(quotifyNoEscape('"', me.str));
    }

    static final String charEscape = "\b\t\n\f\r\"\'\\";
    static final String charMacro  = "btnfr\"'\\";
    static String quotifyNoEscape(char quote, String s) {
        int n = s.length();
        StringBuilder sb = new StringBuilder(n + 2);
        sb.append(quote);
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            int j = charEscape.indexOf(c);
            if(j>=0) {
                if((quote=='"' && c=='\'') || (quote=='\'' && c=='"')) {
                    sb.append(c);
                } else {
                    sb.append('\\');
                    sb.append(charMacro.charAt(j));
                }
            } else {
                sb.append(c);
            }
        }
        sb.append(quote);
        return sb.toString();
    }
            
}
