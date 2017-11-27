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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class XSDCli {
    

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


    public static void main(String[] args) throws Exception {
        new XSDCli().run(args);
    }

    public void run(String... args) throws Exception {
        try {
            go(args);
        } catch (ParseException ex) {
            System.err.println("Exception: " + ex.getMessage());
            showHelp();
        } catch (Throwable t) {
            System.err.println("Exception: " + t.getMessage());
            throw t;
        }
    }
}