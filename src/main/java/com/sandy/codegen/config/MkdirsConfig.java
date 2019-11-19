package com.sandy.codegen.config;

import static com.sandy.codegen.config.ConfigUtils.INDENT3 ;
import static com.sandy.codegen.config.ConfigUtils.enrichList ;
import static com.sandy.codegen.config.ConfigUtils.enrichString ;
import static com.sandy.codegen.config.ConfigUtils.getFormattedListContents ;

import java.util.List ;

public class MkdirsConfig {

    private CodeGenConfig parentConfig = null ;
    private String baseDirectory = null ;
    private List<String> directories = null ;
    
    public CodeGenConfig getParentConfig() {
        return this.parentConfig ;
    }
    
    public void setParentConfig( CodeGenConfig config ) {
        this.parentConfig = config ;
    }
    
    public String getBaseDirectory() {
        return baseDirectory ;
    }
    
    public void setBaseDirectory( String baseDirectory ) {
        this.baseDirectory = baseDirectory ;
    }
    
    public List<String> getDirectories() {
        return directories ;
    }
    
    public void setDirectories( List<String> directories ) {
        this.directories = directories ;
    }
    
    public void enrichValues() 
        throws Exception {
        
        this.baseDirectory = enrichString( this.baseDirectory, parentConfig ) ;
        enrichList( this.directories, parentConfig ) ;
    }
    
    public String getFormattedString( String indent ) {
        StringBuilder builder = new StringBuilder() ;
        builder.append( indent + "MkdirsConfig -> {\n" )
               .append( indent + "   baseDirectory : " + baseDirectory + "\n" )
               .append( indent + "   directories : " )
               .append( getFormattedListContents( directories, INDENT3 ) )
               .append( "\n" + indent + "}\n" ) ;
        return builder.toString() ;
    }
}
