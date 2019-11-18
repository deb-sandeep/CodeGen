package com.sandy.codegen;

import java.io.File ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;
import org.stringtemplate.v4.ST ;

import com.sandy.codegen.config.TransformationConfig ;
import com.sandy.common.util.StringUtil ;

public class TransformationExecutor {

    static final Logger log = Logger.getLogger( TransformationExecutor.class ) ;
    
    private TransformationConfig config = null ;
    
    public TransformationExecutor( TransformationConfig config ) {
        this.config = config ;
    }
    
    public void execute() throws Exception {
        
        ST st = getStringTemplate() ;
        String transformedContent = st.render() ;    

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
    
    private ST getStringTemplate() 
        throws Exception {
        
        String templateContents = getTemplateContents() ;
        ST stringTemplate = new ST( templateContents, '$', '$' ) ;
        
        stringTemplate.add( "envVars", config.getParentConfig().getEnvVars() ) ;
        for( String key : config.getParams().keySet() ) {
            stringTemplate.add( key, config.getParams().get( key ) ) ;
        }
        
        return stringTemplate ;
    }
    
    private String getTemplateContents() 
        throws Exception {
        
        File templateBaseDir = null ;
        File templateFile = null ;
        String baseDirStr = config.getParentConfig().getTemplateDir() ;
        
        if( baseDirStr != null ) {
            templateBaseDir = new File( baseDirStr ) ;
        }
        
        if( templateBaseDir != null ) {
            templateFile = new File( templateBaseDir, config.getTemplate() ) ;
        }
        else {
            templateFile = new File( config.getTemplate() ) ;
        }
        
        if( !templateFile.exists() ) {
            throw new RuntimeException( "Template file " + 
                                        templateFile.getAbsolutePath() + 
                                        " does not exist." ) ;
        }
        
        String fileContents = FileUtils.readFileToString( templateFile ) ;
        return fileContents ;
    }
}
