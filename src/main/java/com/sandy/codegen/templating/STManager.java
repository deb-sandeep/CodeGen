package com.sandy.codegen.templating;

import java.io.File ;

import org.antlr.stringtemplate.StringTemplate ;
import org.antlr.stringtemplate.StringTemplateGroup ;
import org.apache.commons.io.FileUtils ;

import com.sandy.codegen.config.CodeGenConfig ;

public class STManager {

    private StringTemplateGroup stGroup = null ;
    
    private static STManager instance = null ;
    
    private STManager() {
    }
    
    public static STManager instance() {
        if( instance == null ) {
            instance = new STManager() ;
        }
        return instance ;
    }
    
    public void initialize( CodeGenConfig config ) {
        
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
}
