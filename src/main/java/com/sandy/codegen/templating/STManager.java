package com.sandy.codegen.templating;

import java.io.File ;
import java.util.List ;

import org.antlr.stringtemplate.StringTemplate ;
import org.antlr.stringtemplate.StringTemplateGroup ;
import org.apache.commons.io.FileUtils ;

import com.sandy.codegen.config.CodeGenConfig ;

public class STManager {

    private StringTemplateGroup stGroup = null ;
    
    private static STManager instance = null ;
    private CodeGenConfig codeGenConfig = null ;
    
    private STManager() {
    }
    
    public static STManager instance() {
        if( instance == null ) {
            instance = new STManager() ;
        }
        return instance ;
    }
    
    public void initialize( CodeGenConfig config ) {
        
        this.codeGenConfig = config ;
        if( config.getTemplateDir() != null ) {
            stGroup = new StringTemplateGroup( "codegen", config.getTemplateDir() ) ;
        }
    }
    
    public StringTemplate getTemplate( String templatePath ) {
        StringTemplate template = null ;
        
        if( templatePath.charAt( 0 ) != '/' ) {
            templatePath = "/" + templatePath ;
        }
        
        if( stGroup != null ) {
            template = stGroup.getInstanceOf( templatePath ) ;
        }
        else {
            File file = new File( templatePath ) ;
            if( !file.exists() ) {
                throw new RuntimeException( "Template " + templatePath + 
                                            " does not exist." ) ;
            }
            else {
                try {
                    String contents = FileUtils.readFileToString( file ) ;
                    template = new StringTemplate( contents ) ;
                }
                catch( Exception e ) {
                    throw new RuntimeException( "Error getting template " + 
                                                templatePath, e ) ;
                }
            }
        }
        return template ;
    }

    public String getEmbeddedExtHandler( String template ) 
        throws Exception {
        
        File baseDir = null ;
        File templateFile = null ;
        
        if( !template.endsWith( ".st" ) ) {
            template += ".st" ;
        }
        
        if( codeGenConfig.getTemplateDir() != null ) {
            baseDir = new File( codeGenConfig.getTemplateDir() ) ;
            templateFile = new File( baseDir, template ) ;
        }
        else {
            templateFile = new File( template ) ;
        }
        
        List<String> lines = FileUtils.readLines( templateFile ) ;
        String firstLine = lines.get( 0 ).trim() ;
        
        if( firstLine.startsWith( "$!" ) && firstLine.endsWith( "!$" ) ) {
            firstLine = firstLine.substring( "$!".length() ) ;
            firstLine = firstLine.substring( 0, firstLine.length()-"!$".length() ) ;
            
            if( firstLine.contains( ":" ) ) {
                String[] parts = firstLine.split( ":" ) ;
                if( parts[0].trim().equals( "extHandler" ) ) {
                    return parts[1].trim() ;
                }
            }
        }
        
        return null ;
    }
}
