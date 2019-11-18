package com.sandy.codegen.config ;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.InputStream ;

import org.apache.log4j.Logger ;

import com.fasterxml.jackson.databind.ObjectMapper ;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory ;

public class ConfigLoader {

    private static Logger log = Logger.getLogger( ConfigLoader.class ) ;
    
    private ObjectMapper mapper = null ; 
    
    public ConfigLoader() {
        this.mapper = new ObjectMapper( new YAMLFactory() ) ; 
        this.mapper.findAndRegisterModules() ;
    }
    
    public CodeGenConfig loadConfig( InputStream is ) throws Exception {
        CodeGenConfig config =  mapper.readValue( is, CodeGenConfig.class ) ;
        log.debug( "Config before enrichment" ) ;
        log.debug( "------------------------" ) ;
        log.debug( config ) ;
        config.enrichValues() ;
        
        log.debug( "Config after enrichment" ) ;
        log.debug( "------------------------" ) ;
        log.debug( config ) ;
        return config ;
    }
    
    public static void main( String[] args ) throws Exception {
        
        File file = new File( "/Users/sandeep/projects/source/CodeGen/doc/test.yaml" ) ;
        FileInputStream fIs = new FileInputStream( file ) ;
        ConfigLoader loader = new ConfigLoader() ;
        
        loader.loadConfig( fIs ) ;
    }
}
