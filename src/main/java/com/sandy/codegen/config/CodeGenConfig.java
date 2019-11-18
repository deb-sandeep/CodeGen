package com.sandy.codegen.config;

import static com.sandy.codegen.config.ConfigUtils.INDENT1 ;
import static com.sandy.codegen.config.ConfigUtils.enrichString ;
import static com.sandy.codegen.config.ConfigUtils.getFormattedMapContents ;

import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;

public class CodeGenConfig {
    
    static final Logger log = Logger.getLogger( CodeGenConfig.class ) ;

    private Map<String, String> envVars = null ;
    private String templateDir = null ;
    private MkdirsConfig mkdirsConfig = null ;
    private List<TransformationConfig> transformations = null ;

    public Map<String, String> getEnvVars() {
        return envVars ;
    }

    public void setEnvVars( Map<String, String> envVars ) {
        this.envVars = envVars ;
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
        for( String key : envVars.keySet() ) {
            String value = envVars.get( key ) ;
            value = enrichString( value, this ) ;
            envVars.put( key, value ) ;
        }
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
        builder.append( getFormattedMapContents( "baseAttributes", envVars, INDENT1 ) )
               .append( INDENT1 + "templateDir : " + templateDir + "\n" )
               .append( mkdirsConfig.getFormattedString( INDENT1 ) ) ;
        
        for( TransformationConfig tCfg : transformations ) {
            builder.append( tCfg.getFormattedString( INDENT1 ) + "\n" ) ; 
        }
        builder.append( "}\n" ) ;
        
        return builder.toString() ;
    }
}
