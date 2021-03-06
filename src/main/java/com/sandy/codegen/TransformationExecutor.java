package com.sandy.codegen;

import java.io.File ;
import java.io.FileReader ;
import java.util.ArrayList ;
import java.util.List ;

import javax.script.Invocable ;
import javax.script.ScriptEngine ;
import javax.script.ScriptEngineManager ;

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
    private List<TransformationConfig> nestedConfigs = null ;
    private STManager stManager = null ;
    
    public TransformationExecutor( TransformationConfig config ) {
        this.config = config ;
        this.stManager = STManager.instance() ;
        this.stManager.initialize( this.config.getParentConfig() ) ;
        this.nestedConfigs = new ArrayList<>() ;
    }
    
    public void execute() throws Exception {
        log.debug( "\nExecuting transformation" ) ;
        log.debug( "------------------------" ) ;
        log.debug( config.getFormattedString( "" ) ) ;
        executeConfig( config ) ;
        for( TransformationConfig cfg : nestedConfigs ) {
            executeConfig( cfg ) ;
        }
    }
    
    private void executeConfig( TransformationConfig cfg ) 
        throws Exception {
        
        enrichExtHandler( cfg ) ;
        
        if( cfg.getExtHandler() != null ) {
            executeExtHandler( config ) ;
        }

        StringTemplate st = getContextualizedStringTemplate( cfg ) ;
        String transformedContent = st.toString() ;    

        if( StringUtil.isEmptyOrNull( cfg.getDestination() ) ) {
            log.debug( "Transformed content -> \n" ) ;
            log.debug( transformedContent ) ;
        }
        else {
            File destFile = getDestFile( cfg ) ;
            FileUtils.writeStringToFile( destFile, transformedContent ) ;
        }
    }
    
    private void enrichExtHandler( TransformationConfig cfg ) 
        throws Exception {
        
        // If an extension handler is specified as a part of the
        // transformation config, it takes precedence
        if( cfg.getExtHandler() != null ) return ;
        
        // If an extension handler is not specified in the configuration,
        // we check the first line in the template and see if the template
        // specifies an embedded handler
        String extHandler = stManager.getEmbeddedExtHandler( cfg.getTemplate() ) ;
        
        if( StringUtil.isNotEmptyOrNull( extHandler ) ) {
            log.debug( "Found embedded ext handler = " + extHandler ) ;
            cfg.setExtHandler( extHandler ) ;
        }
    }
    
    private File getDestFile( TransformationConfig cfg ) {
        File destFile = new File( cfg.getDestination() ) ;
        File destFileDir = destFile.getParentFile() ;
        if( !destFileDir.exists() ) {
            destFileDir.mkdirs() ;
        }
        return destFile ;
    }
    
    private StringTemplate getContextualizedStringTemplate( TransformationConfig cfg ) 
        throws Exception {
        
        try {
            StringTemplate stringTemplate = this.stManager.getTemplate( cfg.getTemplate() ) ;
            
            CodeGenConfig parentConfig = cfg.getParentConfig() ;
            if( parentConfig.getEnv() != null ) {
                stringTemplate.setAttribute( "env", parentConfig.getEnv() ) ;
            }
            
            if( cfg.getParams() != null ) {
                for( String key : cfg.getParams().keySet() ) {
                    stringTemplate.setAttribute( key, cfg.getParams().get( key ) ) ;
                }
            }
            
            stringTemplate.setAttribute( "config", parentConfig ) ;
            
            return stringTemplate ;
        }
        catch( Exception e ) {
            throw new Exception( "Error processing template - " + 
                                 cfg.getTemplate(), e ) ;
        }
    }
    
    @SuppressWarnings( "unchecked" )
    private void executeExtHandler( TransformationConfig cfg ) 
        throws Exception {
        
        File scriptFile = getExtHandlerScriptFile( cfg.getExtHandler() ) ;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn") ;
        
        engine.eval( new FileReader( scriptFile ) ) ;
        Invocable invocable = ( Invocable )engine ;

        List<TransformationConfig> handlerOutput = null ;
        handlerOutput = ( List<TransformationConfig> )
                        invocable.invokeFunction( "execute", cfg ) ;
        
        if( handlerOutput != null ) {
            for( TransformationConfig tCfg : handlerOutput ) {
                tCfg.setParentConfig( cfg.getParentConfig() ) ;
                tCfg.enrichValues() ;
                this.nestedConfigs.add( tCfg ) ;
            }
        }
    }
    
    private File getExtHandlerScriptFile( String extHandlerFileName ) {
        
        File baseDir = null ;
        File extHandlerFile = null ;
        CodeGenConfig parentCfg = config.getParentConfig() ;
        
        if( parentCfg.getExtHandlerDir() != null ) {
            baseDir = new File( parentCfg.getExtHandlerDir() ) ;
            if( !baseDir.exists() ) {
                throw new RuntimeException( "Ext handler base dir doesn't exist. " + 
                                            baseDir.getAbsolutePath() ) ;
            }
        }
        
        if( !extHandlerFileName.endsWith( ".js" ) ) {
            extHandlerFileName += ".js" ;
        }
        
        if( baseDir == null ) {
            extHandlerFile = new File( extHandlerFileName ) ;
        }
        else {
            extHandlerFile = new File( baseDir, extHandlerFileName ) ;
        }
        
        if( !extHandlerFile.exists() ) {
            throw new RuntimeException( "Ext handler file doesn't exist. " +
                                         extHandlerFile.getAbsolutePath() ) ;
        }
        
        return extHandlerFile ;
    }
}
