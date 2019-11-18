package com.sandy.codegen.config;

import static com.sandy.codegen.config.ConfigUtils.* ;

import java.util.Map ;

public class TransformationConfig {

    private CodeGenConfig parentConfig = null ;
    
    private String template = null ;
    private String destination = null ;
    private Map<String, String> params = null ;
    
    public CodeGenConfig getParentConfig() {
        return this.parentConfig ;
    }
    
    public void setParentConfig( CodeGenConfig config ) {
        this.parentConfig = config ;
    }
    
    public String getTemplate() {
        return template ;
    }
    public void setTemplate( String template ) {
        this.template = template ;
    }
    
    public String getDestination() {
        return destination ;
    }
    public void setDestination( String destination ) {
        this.destination = destination ;
    }
    
    public Map<String, String> getParams() {
        return params ;
    }
    public void setParams( Map<String, String> params ) {
        this.params = params ;
    }
    
    public void enrichValues() 
        throws Exception {
        
        template = enrichString( template, parentConfig ) ;
        destination = enrichString( destination, parentConfig ) ;
        for( String key : params.keySet() ) {
            String value = params.get( key ) ;
            value = enrichString( value, parentConfig ) ;
            params.put( key, value ) ;
        }
    }

    public String getFormattedString( String indent ) {
        StringBuilder builder = new StringBuilder() ;
        builder.append( indent + "Transformation -> {\n" )
               .append( indent + INDENT1 + "template : " + template + "\n" )
               .append( indent + INDENT1 + "destination : " + destination + "\n" )
               .append( getFormattedMapContents( "params", params, indent + INDENT1 ) )
               .append( indent + "}" ) ;
        return builder.toString() ;
    }
}
