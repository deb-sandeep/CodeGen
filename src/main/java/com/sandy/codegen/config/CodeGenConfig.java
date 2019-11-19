package com.sandy.codegen.config;

import static com.sandy.codegen.config.ConfigUtils.*  ;

import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;

public class CodeGenConfig {
    
    static final Logger log = Logger.getLogger( CodeGenConfig.class ) ;

    private Map<String, Object> env = null ;
    private String templateDir = null ;
    private MkdirsConfig mkdirsConfig = null ;
    private List<TransformationConfig> transformations = null ;

    public Map<String, Object> getEnv() {
        return env ;
    }

    public void setEnv( Map<String, Object> envVars ) {
        this.env = envVars ;
    }
    
    public String getTemplateDir() {
        return templateDir ;
    }

    public void setTemplateDir( String templateDir ) {
        this.templateDir = templateDir ;
    }

    public MkdirsConfig getMkdirsConfig() {
        return mkdirsConfig ;
    }

    public void setMkdirsConfig( MkdirsConfig mkdirsConfig ) {
        this.mkdirsConfig = mkdirsConfig ;
    }

    public List<TransformationConfig> getTransformations() {
        return transformations ;
    }

    public void setTransformations( List<TransformationConfig> transformations ) {
        this.transformations = transformations ;
    }

    public void enrichValues() throws Exception {
        enrichMap( env, this ) ;
        templateDir = enrichString( templateDir, this ) ;
        
        mkdirsConfig.setParentConfig( this ) ;
        mkdirsConfig.enrichValues() ;
        
        for( TransformationConfig tCfg : transformations ) {
            tCfg.setParentConfig( this ) ;
            tCfg.enrichValues() ;
        }
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder( "CodeGenConfig -> {\n" ) ;
        builder.append( getFormattedMapContents( "baseAttributes", env, INDENT1 ) )
               .append( INDENT1 + "templateDir : " + templateDir + "\n" )
               .append( mkdirsConfig.getFormattedString( INDENT1 ) ) ;
        
        for( TransformationConfig tCfg : transformations ) {
            builder.append( tCfg.getFormattedString( INDENT1 ) + "\n" ) ; 
        }
        builder.append( "}\n" ) ;
        
        return builder.toString() ;
    }
}
