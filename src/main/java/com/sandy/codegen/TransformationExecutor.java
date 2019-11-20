package com.sandy.codegen;

import java.io.File ;

import org.antlr.stringtemplate.StringTemplate ;
import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.codegen.config.CodeGenConfig ;
import com.sandy.codegen.config.TransformationConfig ;
import com.sandy.codegen.templating.STManager ;
import com.sandy.common.util.StringUtil ;

public class TransformationExecutor {

    static final Logger log = Logger.getLogger( TransformationExecutor.class ) ;
    
    private TransformationConfig config = null ;
    private STManager stManager = null ;
    
    public TransformationExecutor( TransformationConfig config ) {
        this.config = config ;
        this.stManager = STManager.instance() ;
        this.stManager.initialize( this.config.getParentConfig() ) ;
    }
    
    public void execute() throws Exception {
        
        StringTemplate st = getContextualizedStringTemplate() ;
        String transformedContent = st.toString() ;    

        if( StringUtil.isEmptyOrNull( config.getDestination() ) ) {
            log.debug( "Transformed content -> \n" ) ;
            log.debug( transformedContent ) ;
        }
        else {
            File destFile = getDestFile() ;
            FileUtils.writeStringToFile( destFile, transformedContent ) ;
        }
    }
    
    private File getDestFile() {
        File destFile = new File( config.getDestination() ) ;
        File destFileDir = destFile.getParentFile() ;
        if( !destFileDir.exists() ) {
            destFileDir.mkdirs() ;
        }
        return destFile ;
    }
    
    private StringTemplate getContextualizedStringTemplate() 
        throws Exception {
        
        try {
            StringTemplate stringTemplate = this.stManager.getTemplate( config.getTemplate() ) ;
            
            CodeGenConfig parentConfig = config.getParentConfig() ;
            if( parentConfig.getEnv() != null ) {
                stringTemplate.setAttribute( "env", parentConfig.getEnv() ) ;
            }
            
            if( config.getParams() != null ) {
                for( String key : config.getParams().keySet() ) {
                    stringTemplate.setAttribute( key, config.getParams().get( key ) ) ;
                }
            }
            
            stringTemplate.setAttribute( "config", parentConfig ) ;
            
            return stringTemplate ;
        }
        catch( Exception e ) {
            throw new Exception( "Error processing template - " + 
                                 config.getTemplate(), e ) ;
        }
    }
}
