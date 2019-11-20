package com.sandy.codegen;

import java.io.File ;
import java.io.FileFilter ;
import java.io.FileInputStream ;

import org.apache.log4j.Logger ;

import com.sandy.codegen.config.CodeGenConfig ;
import com.sandy.codegen.config.ConfigLoader ;
import com.sandy.codegen.config.MkdirsConfig ;
import com.sandy.codegen.config.TransformationConfig ;

public class CodeGen {
    
    private static final Logger log = Logger.getLogger( CodeGen.class ) ;
    
    private File configFile = null ;
    
    public CodeGen( File config ) {
        this.configFile = config ;
    }
    
    public void execute() {
        FileInputStream fIs = null ;
        
        try {
            fIs = new FileInputStream( configFile ) ;
            ConfigLoader loader = new ConfigLoader() ;
            CodeGenConfig config = loader.loadConfig( fIs ) ;
            processCodeGenConfig( config ) ;
        }
        catch( Exception e ) {
            log.error( "Error executing code generator.", e ) ;
        }
        finally {
            if( fIs != null ) {
                try {
                    fIs.close() ;
                }
                catch( Exception e ) {
                    log.error( "Error closing file stream", e ) ;
                }
            }
        }
    }
    
    private void processCodeGenConfig( CodeGenConfig config ) 
        throws Exception {
        
        processMkdirsConfig( config.getMkdirsConfig() ) ;
        processTransformations( config ) ;
    }
    
    private void processMkdirsConfig( MkdirsConfig config ) {
        if( config == null ) return ;
        
        log.debug( "Making specified directories" ) ;
        
        File baseDir = null ;
        if( config.getBaseDirectory() != null ) {
            baseDir = new File( config.getBaseDirectory() ) ;
        }
        
        for( String dirName : config.getDirectories() ) {
            File dir = null ;
            if( baseDir != null ) {
                dir = new File( baseDir, dirName ) ;
            }
            else {
                dir = new File( dirName ) ;
            }
            
            log.debug( "Making directory = " + dir.getAbsolutePath() ) ;
            dir.mkdirs() ;
        }
    }
    
    private void processTransformations( CodeGenConfig config ) 
        throws Exception {
        
        TransformationExecutor executor = null ;
        for( TransformationConfig tCfg : config.getTransformations() ) {
            log.debug( "\nExecuting transformation" ) ;
            log.debug( "------------------------" ) ;
            log.debug( tCfg.getFormattedString( "" ) ) ;
            executor = new TransformationExecutor( tCfg ) ;
            executor.execute() ;
        }
    }

    public static void main( String[] args ) 
        throws Exception {
        
        if( args.length > 0 ) {
            for( String arg : args ) {
                File file = new File( arg ) ;
                if( file.isDirectory() ) {
                    launchCodeGenForConfigsInDir( file ) ;
                }
                else {
                    CodeGen codeGen = new CodeGen( file ) ;
                    codeGen.execute() ;
                }
            }
        }
        else {
            System.err.println( "CodeGen : Usage" ) ;
            System.err.println( "CodeGen <config file path>+" ) ;
        }
    }
    
    private static void launchCodeGenForConfigsInDir( File dir ) 
        throws Exception {
        
        File[] configs = dir.listFiles( new FileFilter() {
            public boolean accept( File file ) {
                if( file.isDirectory() ) return true ;
                return file.getName().endsWith( ".yaml" ) ;
            }
        } ) ;
        
        for( File config : configs ) {
            CodeGen codeGen = new CodeGen( config ) ;
            codeGen.execute() ;
        }
    }
}
