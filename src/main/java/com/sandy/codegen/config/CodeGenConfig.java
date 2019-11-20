package com.sandy.codegen.config;

import static com.sandy.codegen.config.ConfigUtils.*  ;

import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;

public class CodeGenConfig {
    
    static final Logger log = Logger.getLogger( CodeGenConfig.class ) ;

    private Map<String, Object> env = null ;
    private String templateDir = null ;
    private String extHandlerDir = null ;
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

    public String getExtHandlerDir() {
        return extHandlerDir ;
    }

    public void setExtHandlerDir( String extHandlerDir ) {
        this.extHandlerDir = extHandlerDir ;
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
        extHandlerDir = enrichString( extHandlerDir, this ) ;
        
        if( mkdirsConfig != null ) {
            mkdirsConfig.setParentConfig( this ) ;
            mkdirsConfig.enrichValues() ;
        }

        if( transformations != null ) {
            for( TransformationConfig tCfg : transformations ) {
                tCfg.setParentConfig( this ) ;
                tCfg.enrichValues() ;
            }
        }
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder( "CodeGenConfig -> {\n" ) ;
        builder.append( getFormattedMapContents( "baseAttributes", env, INDENT1 ) )
               .append( INDENT1 + "templateDir : " + templateDir + "\n" ) 
               .append( INDENT1 + "extHandlerDir : " + extHandlerDir + "\n" ) ;
        
        if( mkdirsConfig != null ) {
            builder.append( mkdirsConfig.getFormattedString( INDENT1 ) ) ;
        }
        
        if( transformations != null ) {
            for( TransformationConfig tCfg : transformations ) {
                builder.append( tCfg.getFormattedString( INDENT1 ) + "\n" ) ; 
            }
        }
        
        builder.append( "}\n" ) ;
        
        return builder.toString() ;
    }
}
