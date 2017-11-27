package com.luxoft.lxsd;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.writer.FileCodeWriter;
import com.sun.tools.xjc.api.ErrorListener;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
//import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.Configuration;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

//@Slf4j
//@SpringBootApplication
//@Configuration
public class XSDCli  // implements CommandLineRunner 
{
    

    final CommandLineParser parser = new DefaultParser();
    static final Options options = new Options();
    static {
        options.addOption(Option.builder("m").desc("Set execution mode (extract|generate)").hasArg().argName("mode").required().build());
        options.addOption(Option.builder("f").desc("Set translation file").hasArg().argName("translation_file").required(false).build());
        options.addOption(Option.builder("o").desc("Set output directory").hasArg().argName("out_dir").required(false).build());
        options.addOption(Option.builder("p").desc("Set Java package name").hasArg().argName("package_name").required(false).build());
    }

    static enum EXEC_MODE {
      EXTRACT("extract"),
      GENERATE("generate")
      ;
      final String name;
      EXEC_MODE(String name) {
          this.name = name;
      }
      String getName() {
          return name;
      }
      static EXEC_MODE fromName(String name) {
          for (EXEC_MODE v : EXEC_MODE.values()) {
              if (v.name.equals(name))
                  return v;
          }
          return null;
      }
    };

    // mode
    // - extract : extracts element&attribute names (identifiers) to specified file for later traslation
    // - generate: generates java code for types defined in XSD 
    EXEC_MODE mode;
    
    // path to XSD
    Path    schemaPath;
    // Output directory fo generated java package
    Path    outDir;
    // path to translation file
    Path    xlatPath;
    // package to generate
    String  packageName;
    
    XSDCli() {
    }
    
    static void showHelp() {
        HelpFormatter hf = new HelpFormatter();
        hf.setArgName("XSD_FILE");
        hf.printHelp(100, "lxsd", "", options, "", true);
    }
    
    boolean setMode(String modeName) {
        EXEC_MODE m = EXEC_MODE.fromName(modeName);
        // Invalid mode name 
        if (m == null)
            return false;
        
        mode = m;
        return true;
    }
    
    boolean parseArgs(String[] args) throws ParseException {
        if (args.length < 1)
            return false;
        
        CommandLine cl = this.parser.parse(options, args);

        String optMode = cl.getOptionValue('m');
        if (!setMode(optMode))
            throw new ParseException("Invalid execution mode argument: " + optMode);

        List<String> argList = cl.getArgList();
        if(argList.isEmpty())
            throw new ParseException("Missing schema file argument");
        
        String optOutDir = cl.getOptionValue('o', ".");
        String optPackage = cl.getOptionValue('p', "org.jaxb.generated");
        String optXlatFile = cl.getOptionValue('f', ".xlat");
        String optSchemaPath = argList.get(0);
        
        
        this.schemaPath = Paths.get(optSchemaPath).toAbsolutePath();
        this.outDir = Paths.get(optOutDir).toAbsolutePath();
        this.packageName = optPackage;
        this.xlatPath = Paths.get(optXlatFile);
        
        return true;
    }
    
    File getXlatFile() {
        if (this.xlatPath == null) {
            this.xlatPath = Paths.get(this.schemaPath.getParent().toString(), this.schemaPath.getFileName().toString() + ".properties"); 
        }
        return this.xlatPath.toFile();
    }
    
    File getOutDir() {
        return this.outDir.toFile();
    }
    
    // mode = generate
    void generateCode() {
        try {
            XSDSchema schema = new XSDSchema(schemaPath).withMapping(getXlatFile()).load();
            JCodeModel cm;
            S2JJAXBModel model;
            SchemaCompiler sc = XJC.createSchemaCompiler(); 
            ErrorListener el = new ErrorListener() {
                @Override
                public void error(SAXParseException saxpe) {
                    System.err.println("ERROR: " + saxpe.getMessage());
                }

                @Override
                public void fatalError(SAXParseException saxpe) {
                    System.err.println("FATAL: " + saxpe.getMessage());
                }

                @Override
                public void warning(SAXParseException saxpe) {
                    System.err.println("WARN: " + saxpe.getMessage());
                }

                @Override
                public void info(SAXParseException saxpe) {
                    System.err.println("INFO: " + saxpe.getMessage());
                }
            };
            // Parse schema and build code model using 'standard' xjc mechanism
            sc.parseSchema(new InputSource(this.schemaPath.toUri().toURL().toExternalForm()));
            sc.forcePackageName(this.packageName);
            model = sc.bind();
            cm = model.generateCode(null, el);
            
            // clone generated code model
            // the following modifications to be made during cloning:
            // - identifiers translated using either translation file or simple transliteration
            // - accessors dropped
            // - lombok anntoations added
            // - terse and useful documentation added using xs:documentation extracted from
            //   schema
            // - ObjectFactory.java skipped
            CodeGen cg = new CodeGen(schema.getRegistry());
            JCodeModel result = cg.cloneModel(cm);
            
            result.build(new FileCodeWriter(getOutDir(), "UTF-8"));
            
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // mode = extract
    void extractIdentifiers() {
        try {
            File mapping = getXlatFile();
            XSDSchema schema = new XSDSchema(schemaPath).withMapping(mapping).load();
            try( OutputStreamWriter osr = new OutputStreamWriter(
                    new FileOutputStream(mapping), Charset.forName("utf-8").newEncoder())) {
                ExtractionVisitor v = new ExtractionVisitor(osr);
                schema.accept(v);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    void go(String[] args) throws ParseException {
        if (!parseArgs(args)) {
            showHelp();
            return;
        }
        if (mode == EXEC_MODE.EXTRACT)
            extractIdentifiers();
        else if (mode == EXEC_MODE.GENERATE) 
            generateCode();
    }


    static final String charEscape = "\b\t\n\f\r\"\'\\";
    static final String charMacro  = "btnfr\"'\\";
    public static String quotifyNoEscape(char quote, String s) {
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
                // this overrides the original code in 
                // public static com.sun.codemodel.JExpr.quotify(char, String)
                // to suppress unicode escaping as we generate UTF-8 encoded files
                sb.append(c);
                // the following comment contains original code
//                // technically Unicode escape shouldn't be done here,
//                // for it's a lexical level handling.
//                // 
//                // However, various tools are so broken around this area,
//                // so just to be on the safe side, it's better to do
//                // the escaping here (regardless of the actual file encoding)
//                //
//                // see bug 
//                if( c<0x20 || 0x7E<c ) {
//                    // not printable. use Unicode escape
//                    sb.append("\\u");
//                    String hex = Integer.toHexString(((int)c)&0xFFFF);
//                    for( int k=hex.length(); k<4; k++ )
//                        sb.append('0');
//                    sb.append(hex);
//                } else {
//                    sb.append(c);
//                }
            }
        }
        sb.append(quote);
        return sb.toString();
    }
/*    
    static final MethodType mtQuotify = MethodType.methodType(String.class, char.class, String.class);
    static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    static MethodHandle mh;
    static URLClassLoader ucl;
    static Class<?> jexprClass;
    static {
        
        try {
            ucl = new URLClassLoader(new URL[]{
                new URL("file:/./com/sun/codemodel")
            }) {
                @Override
                protected Class<?> findClass(final String name) throws ClassNotFoundException {
                    Class<?> clazz = super.findClass(name);
                    Class<?> ret = (Class<?>)Proxy.newProxyInstance(this, new Class[] {clazz}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            if (method.getName().equals("quotify")) {
                                return quotifyNoEscape((char)args[0], (String)args[1]);
                            }
                            return method.invoke(proxy, args);
                        }
                    });
                    return ret;
                }
            };
            log.info("Class JExpr {} FOUND", (ucl == null ? "*NOT*" : ""));
            jexprClass = ucl.loadClass("com.sun.codemodel.JExpr");
            log.info("Class JExpr {} LOADED", (jexprClass == null ? "*NOT*" : ""));
            
        } catch (MalformedURLException ex1) {
            log.info("Method FOUND: public static String com.sun.codemodel.JExpr.quotify(char, String)");
        } catch (ClassNotFoundException ex2) {
            log.info("JExpr.class: ", ex2);
        }
        try {
            mh = lookup.findStatic(com.sun.codemodel.JExpr.class, "quotify", mtQuotify);
            log.info("Method FOUND: public static String com.sun.codemodel.JExpr.quotify(char, String)");
        } catch (NoSuchMethodException | IllegalAccessException ex) {
            log.info("Method not found: public static String com.sun.codemodel.JExpr.quotify(char, String)");
        }
    }
*/
    public static void main(String[] args) throws Exception {
        //SpringApplication.run(XSDCli.class, args);
        new XSDCli().run(args);
    }

    public void run(String... args) throws Exception {
        System.setProperty("file.encoding", "utf-8");
        try {
            go(args);
        } catch (ParseException ex) {
            System.err.println("Exception: " + ex.getMessage());
            showHelp();
        } catch (Throwable t) {
            System.err.println("Exception: " + t.getMessage());
            throw t;
        }
        return;
    }
}